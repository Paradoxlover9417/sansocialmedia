package com.sanproject.sansocialmedia.s3;

import com.sanproject.sansocialmedia.dto.S3FileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/api/s3")
    public List<S3FileDTO> getS3FileList() {
        return s3Service.getFileList();
    }

    @PostMapping("api/s3")
    public ResponseEntity<S3FileDTO> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(s3Service.uploadFile(file));
    }

    @GetMapping("api/s3/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        InputStreamResource resource = s3Service.downloadFile(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/api/s3/{fileName}")
    public ResponseEntity<S3FileDTO> deleteFile(@PathVariable String fileName) {
        return ResponseEntity.ok(s3Service.deleteFile(fileName));
    }

    @GetMapping("/api/count")
    public Map<String, Integer> getCount() {
        return s3Service.getCount();
    }
}
