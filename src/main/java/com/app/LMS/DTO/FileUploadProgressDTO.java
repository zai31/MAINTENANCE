package com.app.LMS.DTO;

public class FileUploadProgressDTO {
    private String uploadId;
    private String fileName;
    private long bytesUploaded;
    private long totalBytes;
    private String status;
    private String errorMessage;

    public FileUploadProgressDTO() {
    }

    public FileUploadProgressDTO(String uploadId, String fileName, long bytesUploaded, long totalBytes, String status) {
        this.uploadId = uploadId;
        this.fileName = fileName;
        this.bytesUploaded = bytesUploaded;
        this.totalBytes = totalBytes;
        this.status = status;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getBytesUploaded() {
        return bytesUploaded;
    }

    public void setBytesUploaded(long bytesUploaded) {
        this.bytesUploaded = bytesUploaded;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public double getProgressPercentage() {
        if (totalBytes == 0) return 0;
        return (bytesUploaded * 100.0) / totalBytes;
    }
} 