package com.sanproject.sansocialmedia.util;

import com.sanproject.sansocialmedia.enumeration.FileExtension;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidation {
    public void validateFile(MultipartFile file) {
        checkNull(file);
        checkFileExtension(file);
        checkFileSize(file);
    }

    private void checkNull(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }
    }

    // 남은 횟수 체크
    public void checkUploadCount(int uploadCount) {
        if (uploadCount <= 0) {
            throw new RuntimeException("업로드 횟수가 남아있지 않습니다.");
        }
    }

    public void checkDownloadCount(int downloadCount) {
        if (downloadCount <= 0) {
            throw new RuntimeException("다운로드 횟수가 남아있지 않습니다.");
        }
    }

    // 확장자 체크
    private void checkFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!FileExtension.isValidExtension(filename)) {
            throw new RuntimeException("허용되지 않는 파일 확장자입니다.");
        }
    }

    // 용량 체크
    private void checkFileSize(MultipartFile file) {
        long MAX_SIZE = 5 * 1024 * 1024;
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("파일 용량을 초과하였습니다.");
        }
    }
}
