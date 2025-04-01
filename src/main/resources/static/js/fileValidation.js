// 파일 크기와 확장자 검사 함수
function validateFile(file) {
    const maxFileSize = 5 * 1024 * 1024; // 5MB를 바이트로 계산

    // 파일이 선택되지 않은 경우
    if (!file) {
        return { isValid: false, message: "파일을 선택하세요." };
    }

    // 파일 크기 검사
    if (file.size > maxFileSize) {
        return { isValid: false, message: "파일 크기가 5MB를 초과합니다. 다시 선택해주세요." };
    }

    // 허용된 확장자 목록
    const allowedExtensions = ['jpg', 'png', 'pdf', 'txt', 'csv'];

    // 파일 확장자 추출
    const fileExtension = file.name.split('.').pop().toLowerCase();

    // 확장자 검사
    if (!allowedExtensions.includes(fileExtension)) {
        return { isValid: false, message: "허용되지 않는 파일 확장자입니다. JPG, PNG, PDF, TXT, CSV 파일만 업로드할 수 있습니다." };
    }

    // 모든 검사 통과
    return { isValid: true, message: "" };
}
