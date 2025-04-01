package com.sanproject.sansocialmedia.s3;

import com.sanproject.sansocialmedia.dto.S3FileDTO;
import com.sanproject.sansocialmedia.util.FileValidation;
import com.sanproject.sansocialmedia.util.S3Converter;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Getter
public class S3Service {


    @Value("${cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-access-key:}")
    private String secretAccessKey;

    @Value("${cloud.aws.region}")
    private String region;

    @Value("${cloud.aws.bucket-name}")
    private String bucketName;

    @Value("${cloud.aws.s3.prefix}")
    private String prefix;

    private int uploadCount = 5;
    private int downloadCount = 5;

    private S3Client s3Client;

    private final FileValidation fileValidation;

    @PostConstruct
    private void init() {
        AwsCredentialsProvider credentialsProvider;

        if (accessKey.isEmpty()) {
            credentialsProvider = DefaultCredentialsProvider.create();
        } else {
            credentialsProvider = StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretAccessKey)
            );
        }

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    public List<S3FileDTO> getFileList() {
        ListObjectsV2Request listObjects = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
        List<S3Object> contents = s3Client.listObjectsV2(listObjects).contents();
        return S3Converter.objectToDTO(contents);
    }

    public S3FileDTO uploadFile(MultipartFile file) {

        fileValidation.validateFile(file);
        fileValidation.checkUploadCount(uploadCount);

        String originalFileName = file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefix + originalFileName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            uploadCount--;



            return S3FileDTO.builder().fileName(originalFileName).build();
        } catch (IOException e) {
            throw new RuntimeException(originalFileName + " 파일 업로드 실패");
        }
    }

    public InputStreamResource downloadFile(String fileName) {

        fileValidation.checkDownloadCount(downloadCount);

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefix + fileName)
                    .build();

            downloadCount--;

            return new InputStreamResource(s3Client.getObject(getObjectRequest));

        } catch (S3Exception e) {
            throw new RuntimeException("파일 다운로드 실패: ");
        }
    }

    public S3FileDTO deleteFile(String fileName) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(prefix + fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        return S3FileDTO.builder().fileName(fileName).build();
    }

    @Scheduled(cron = "0 0 */6 * * *")
    private void resetCount() {
        uploadCount = 5;
        downloadCount= 5;
    }

    public Map<String, Integer> getCount() {
        return Map.of(
                "uploadCount", uploadCount,
                "downloadCount", downloadCount
        );
    }
}
