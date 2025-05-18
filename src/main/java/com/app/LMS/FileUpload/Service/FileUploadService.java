package com.app.LMS.FileUpload.Service;

import com.app.LMS.DTO.FileUploadProgressDTO;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String initiateUpload(String fileName, long fileSize);
    FileUploadProgressDTO getUploadProgress(String uploadId);
    void updateUploadProgress(String uploadId, long bytesUploaded);
    void completeUpload(String uploadId, MultipartFile file);
    void cancelUpload(String uploadId);
} 