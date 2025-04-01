package com.sanproject.sansocialmedia.enumeration;

import lombok.Getter;

@Getter
public enum FileExtension {
    JPG(".jpg"),
    PNG(".png"),
    PDF(".pdf"),
    TXT(".txt"),
    CSV(".csv");

    // 확장자 반환
    private final String extension;

    // 생성자
    FileExtension(String extension) {
        this.extension = extension;
    }

    // 유효한 확장자 검사
    public static boolean isValidExtension(String fileName) {
        for (FileExtension ext : values()) {
            if (fileName.toLowerCase().endsWith(ext.getExtension())) {
                return true;
            }
        }
        return false;
    }
}

