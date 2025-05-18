package app.LMS.controller;

import app.LMS.dto.upload.FileUploadProgressDTO;
import app.LMS.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<String> initiateUpload(
            @RequestParam("fileName") String fileName,
            @RequestParam("fileSize") long fileSize) {
        String uploadId = fileUploadService.initiateUpload(fileName, fileSize);
        return ResponseEntity.ok(uploadId);
    }

    @GetMapping("/progress/{uploadId}")
    public ResponseEntity<FileUploadProgressDTO> getUploadProgress(
            @PathVariable String uploadId) {
        FileUploadProgressDTO progress = fileUploadService.getUploadProgress(uploadId);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/progress/{uploadId}")
    public ResponseEntity<Void> updateUploadProgress(
            @PathVariable String uploadId,
            @RequestParam("bytesUploaded") long bytesUploaded) {
        fileUploadService.updateUploadProgress(uploadId, bytesUploaded);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/complete/{uploadId}")
    public ResponseEntity<Void> completeUpload(
            @PathVariable String uploadId,
            @RequestParam("file") MultipartFile file) {
        fileUploadService.completeUpload(uploadId, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel/{uploadId}")
    public ResponseEntity<Void> cancelUpload(@PathVariable String uploadId) {
        fileUploadService.cancelUpload(uploadId);
        return ResponseEntity.ok().build();
    }
} 