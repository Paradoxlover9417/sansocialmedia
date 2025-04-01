package com.sanproject.sansocialmedia.util;

import com.sanproject.sansocialmedia.dto.S3FileDTO;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class S3Converter {

    public static List<S3FileDTO> objectToDTO(List<S3Object> s3ObjectList) {
        return s3ObjectList.stream().map(s3Object -> {
            String fileKey = s3Object.key();
            return S3FileDTO.builder()
                    .fileName(fileKey.substring(fileKey.lastIndexOf("/") + 1))
                    .fileSize(s3Object.size())
                    .filePath(fileKey.substring(0, fileKey.lastIndexOf("/") + 1))
                    .modifiedDate(LocalDateTime.ofInstant(s3Object.lastModified(), ZoneId.of("Asia/Seoul")))
                    .build();
        }).collect(Collectors.toList());
    }
}
