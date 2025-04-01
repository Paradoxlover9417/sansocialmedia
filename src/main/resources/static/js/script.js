document.getElementById("uploadButton").addEventListener("click", uploadFile);

const fileListContainer = document.getElementById('file-list');
const dbVersionContainer = document.getElementById('db-version-list');

function fetchProfile() {
    fetch("/api/profile")
        .then(response => response.text())  // 단순 텍스트 응답 받기
        .then(profile => {
            const titleElement = document.querySelector("h1");
            const bucketNameElement = document.getElementById("bucket-name");
            const dbNameElement = document.getElementById("db-name");
            let prefixText = "";

            switch (profile) {
                case "local/": prefixText = "로컬";
                    break;
                case "dev/": prefixText = "개발";
                    break;
                case "prod/": prefixText = "운영";
                    break;
                default: prefixText = "";
            }

            if (prefixText) {
                titleElement.textContent = `${prefixText}서버 페이지`;
                bucketNameElement.textContent = `s3://${profile}`;
                dbNameElement.textContent = `${profile}mariaDB`;
            }
        })
        .catch(error => console.error("환경 정보를 가져오는 데 실패했습니다:", error));
}

function fetchCount() {
    fetch("/api/count")
        .then(response => response.json())  // JSON 응답 받기
        .then(data => {
            const uploadCountElement = document.getElementById("upload-count");
            const downloadCountElement = document.getElementById("download-count");

            if (uploadCountElement) {
                uploadCountElement.textContent = data.uploadCount;
            }

            if (downloadCountElement) {
                downloadCountElement.textContent = data.downloadCount;
            }
        })
        .catch(error => console.error("카운트 정보를 가져오는 데 실패했습니다:", error));
}

function fetchS3FileList() {
    // Show loading state
    fileListContainer.innerHTML = '<div class="loading-message">파일 목록을 불러오는 중...</div>';

    fetch("/api/s3", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(data => {
            displayFileList(data);
        })
        .catch(error => {
            fileListContainer.innerHTML = '<div class="error-message">파일 목록을 불러오는데 실패했습니다.</div>';
        });
}

function displayFileList(data) {
    if (!data || data.length === 0) {
        fileListContainer.innerHTML = '<div class="empty-message">파일이 없습니다</div>';
        return;
    }

    fileListContainer.innerHTML = '';

    data.forEach(fileItem => {
        const fileRow = document.createElement('div');
        fileRow.className = 'file-row';

        // Format the date
        const modifiedDate = new Date(fileItem.modifiedDate);
        const formattedDate = modifiedDate.toLocaleString('ko-KR');

        const formattedSize = formatFileSize(fileItem.fileSize);

        fileRow.innerHTML = `
                <div class="file-path">${fileItem.filePath || '-'}</div>
                <div class="file-name">${fileItem.fileName || '-'}</div>
                <div class="file-date">${formattedDate}</div>
                <div class="file-size">${formattedSize}</div>
                <div class="file-action">
                    <button class="download-btn" data-filename="${fileItem.fileName}">다운로드</button>
                </div>
                <div class="file-action">
                    <button class="delete-btn" data-filename="${fileItem.fileName}">삭제</button>
                </div>
            `;

        fileListContainer.appendChild(fileRow);

        const downloadBtn = fileRow.querySelector('.download-btn');
        downloadBtn.addEventListener('click', function() {
            downloadFile(fileItem.fileName);
        });

        const deleteBtn = fileRow.querySelector('.delete-btn');
        deleteBtn.addEventListener('click', function() {
            deleteFile(fileItem.fileName);
        });
    });
}

function uploadFile() {
    const fileInput = document.getElementById("fileInput");
    const file = fileInput.files[0];
    const validation = validateFile(file);

    if (!validation.isValid) {
        alert(validation.message);
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch("/api/s3", {
        method: "POST",
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || "알 수 없는 오류 발생");
                });
            }
            return response.json(); // JSON 응답 처리
        })
        .then(async (data) => {
            fileInput.value = "";
            await fetchS3FileList();
            await fetchCount();
            alert("파일이 성공적으로 업로드되었습니다.");
        })
        .catch(error => {
            alert(error.message);
        });
}

function downloadFile(fileName) {
    const url = `/api/s3/${fileName}`;

    const isConfirmed = confirm("파일을 다운로드하시겠습니까?");

    if (isConfirmed) {
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.message || "알 수 없는 오류 발생");
                    });
                }
                return response.blob();
            })
            .then(blob => {
                const blobUrl = URL.createObjectURL(blob);
                const link = document.createElement("a");
                link.href = blobUrl;
                link.download = fileName;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                URL.revokeObjectURL(blobUrl);
                fetchCount();
            })
            .catch(error => {
                alert(error.message);
            });
    }
}



function deleteFile(fileName) {
    if (confirm(`정말로 '${fileName}' 파일을 삭제하시겠습니까?`)) {
        fetch(`/api/s3/${fileName}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.message || "알 수 없는 오류 발생");
                    });
                }
                return response.json()
            })
            .then(data => {
                fetchS3FileList();
                alert('파일이 삭제되었습니다.');
            })
            .catch(error => {
                alert('파일 삭제 중 오류가 발생했습니다: ' + error.message);
            });
    }
}


function fetchDBVersionList() {
    // Show loading state
    dbVersionContainer.innerHTML = '<div class="loading-message">버전 정보를 불러오는 중...</div>';

    fetch("/api/DBVersionHistory", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => response.json())
        .then(data => {
            displayDBVersionHistory(data);
        })
        .catch(error => {
            dbVersionContainer.innerHTML = '<div class="error-message">버전 정보 목록을 불러오는데 실패했습니다.</div>';
        });
}

function displayDBVersionHistory(data) {
    if (!data || data.length === 0) {
        dbVersionContainer.innerHTML = '<div class="empty-message">기록이 없습니다</div>';
        return;
    }

    dbVersionContainer.innerHTML = '';

    data.forEach(dbItem => {
        const dbRow = document.createElement('div');
        dbRow.className = 'db-row';

        const modifiedDate = new Date(dbItem.installedOn);
        const formattedDate = modifiedDate.toLocaleString('ko-KR');

        dbRow.innerHTML = `
                <div class="db-version">${dbItem.script}</div>
                <div class="file-name">${formattedDate}</div>
            `;

        dbVersionContainer.appendChild(dbRow);
    });
}

function formatFileSize(fileSize) {
    switch (true) {
        case (fileSize <= 1024):
            return fileSize + " Byte"; // 1024 이하
        case (fileSize <= 1024 * 1024):
            return (fileSize / 1024).toFixed(1) + " KB";
        default:
            return (fileSize / (1024 * 1024)).toFixed(1) + " MB";
    }
}

fetchProfile();
fetchCount();
fetchS3FileList();
fetchDBVersionList();