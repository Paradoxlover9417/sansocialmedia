package com.sanproject.sansocialmedia.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
public class S3FileDTO {

    private String fileName;

    private long fileSize;

    private MultipartFile file;

    private String filePath;

    private LocalDateTime modifiedDate;

}

