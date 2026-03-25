package com.cognizant.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cognizant.dto.AddCommentRequest;
import com.cognizant.dto.AttachmentDTO;
import com.cognizant.dto.CommentDTO;
import com.cognizant.entities.Attachment;
import com.cognizant.entities.Comment;
import com.cognizant.entities.Defect;
import com.cognizant.repositries.AttachmentRepository;
import com.cognizant.repositries.CommentRepository;
import com.cognizant.repositries.DefectEntityRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = "*")
@Tag(name = "Comments & Attachments", description = "Bug discussion and file upload API")
public class CommentAndAttachmentController {

    private final Logger logger = LoggerFactory.getLogger(CommentAndAttachmentController.class);

    private final CommentRepository    commentRepository;
    private final AttachmentRepository attachmentRepository;
    private final DefectEntityRepository defectRepository;

    // Where uploaded files are saved on disk
    // Set this in application.properties: app.upload.dir=./uploads
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public CommentAndAttachmentController(
            CommentRepository    commentRepository,
            AttachmentRepository attachmentRepository,
            DefectEntityRepository defectRepository) {
        this.commentRepository    = commentRepository;
        this.attachmentRepository = attachmentRepository;
        this.defectRepository     = defectRepository;
    }

    // ════════════════════════════════════════════════════════════════
    //  COMMENTS
    // ════════════════════════════════════════════════════════════════

    // GET /api/defects/{defectId}/comments  — list all comments on a bug
    @Operation(description = "Get all comments for a defect (oldest first)")
    @GetMapping("/defects/{defectId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Integer defectId) {
        Optional<Defect> opt = defectRepository.findById(defectId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Defect not found\"}");
        }
        List<Comment> comments = commentRepository
                .findByDefectOrderByCreatedAtAsc(opt.get());

        List<CommentDTO> dtos = comments.stream().map(c -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(c.getId());
            dto.setDefectId(defectId);
            dto.setAuthor(c.getAuthor());
            dto.setAuthorRole(c.getAuthorRole());
            dto.setContent(c.getContent());
            dto.setCreatedAt(c.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // POST /api/defects/{defectId}/comments  — add a comment
    @Operation(description = "Add a comment to a defect (tester or developer)")
    @PostMapping("/defects/{defectId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Integer defectId,
            @RequestBody  AddCommentRequest request) {

        logger.info("Adding comment to defect #{} by {}", defectId, request.getAuthor());

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Comment content cannot be empty\"}");
        }

        Optional<Defect> opt = defectRepository.findById(defectId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Defect not found\"}");
        }

        Comment comment = new Comment();
        comment.setDefect(opt.get());
        comment.setAuthor(request.getAuthor());
        comment.setAuthorRole(request.getAuthorRole());
        comment.setContent(request.getContent().trim());

        Comment saved = commentRepository.save(comment);

        CommentDTO dto = new CommentDTO();
        dto.setId(saved.getId());
        dto.setDefectId(defectId);
        dto.setAuthor(saved.getAuthor());
        dto.setAuthorRole(saved.getAuthorRole());
        dto.setContent(saved.getContent());
        dto.setCreatedAt(saved.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // DELETE /api/comments/{commentId}  — delete a comment (author only)
    @Operation(description = "Delete a comment by ID")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
        if (!commentRepository.existsById(commentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Comment not found\"}");
        }
        commentRepository.deleteById(commentId);
        return ResponseEntity.ok("{\"message\": \"Comment deleted\"}");
    }

    // ════════════════════════════════════════════════════════════════
    //  ATTACHMENTS
    // ════════════════════════════════════════════════════════════════

    // GET /api/defects/{defectId}/attachments  — list attachments
    @Operation(description = "List all attachments for a defect")
    @GetMapping("/defects/{defectId}/attachments")
    public ResponseEntity<?> getAttachments(@PathVariable Integer defectId) {
        Optional<Defect> opt = defectRepository.findById(defectId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Defect not found\"}");
        }
        List<Attachment> attachments = attachmentRepository.findByDefect(opt.get());
        List<AttachmentDTO> dtos = attachments.stream().map(a -> toDTO(a, defectId)).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // POST /api/defects/{defectId}/attachments  — upload a file
    @Operation(description = "Upload a file attachment to a defect (screenshot, log, etc.)")
    @PostMapping(value = "/defects/{defectId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAttachment(
            @PathVariable Integer          defectId,
            @RequestParam  MultipartFile   file,
            @RequestParam  String          uploadedBy) {

        logger.info("Uploading attachment '{}' for defect #{} by {}",
                file.getOriginalFilename(), defectId, uploadedBy);

        // 1. Validate defect exists
        Optional<Defect> opt = defectRepository.findById(defectId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Defect not found\"}");
        }

        // 2. Validate file not empty
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"File is empty\"}");
        }

        // 3. Validate file size (max 10 MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"File too large. Max 10MB.\"}");
        }

        // 4. Save file to disk
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            // Generate a unique filename to avoid collisions
            String original   = file.getOriginalFilename();
            String extension  = original != null && original.contains(".")
                    ? original.substring(original.lastIndexOf('.'))
                    : "";
            String storedName = UUID.randomUUID().toString().replace("-", "") + extension;

            Path targetPath = uploadPath.resolve(storedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 5. Save metadata to DB
            Attachment attachment = new Attachment();
            attachment.setDefect(opt.get());
            attachment.setOriginalName(original);
            attachment.setStoredName(storedName);
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setUploadedBy(uploadedBy);

            Attachment saved = attachmentRepository.save(attachment);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(saved, defectId));

        } catch (IOException e) {
            logger.error("Failed to save file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to save file: " + e.getMessage() + "\"}");
        }
    }

    // GET /api/attachments/download/{storedName}  — download/view file
    @Operation(description = "Download or view an attachment by its stored filename")
    @GetMapping("/attachments/download/{storedName}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String storedName) {
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(storedName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/attachments/{attachmentId}  — delete an attachment
    @Operation(description = "Delete an attachment by ID (also removes file from disk)")
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Integer attachmentId) {
        Optional<Attachment> opt = attachmentRepository.findById(attachmentId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Attachment not found\"}");
        }
        // Delete file from disk
        try {
            Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize()
                    .resolve(opt.get().getStoredName());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.warn("Could not delete file from disk: {}", e.getMessage());
        }
        attachmentRepository.deleteById(attachmentId);
        return ResponseEntity.ok("{\"message\": \"Attachment deleted\"}");
    }

    // Helper — entity to DTO
    private AttachmentDTO toDTO(Attachment a, Integer defectId) {
        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(a.getId());
        dto.setDefectId(defectId);
        dto.setOriginalName(a.getOriginalName());
        dto.setStoredName(a.getStoredName());
        dto.setFileType(a.getFileType());
        dto.setFileSize(a.getFileSize());
        dto.setUploadedBy(a.getUploadedBy());
        dto.setUploadedAt(a.getUploadedAt());
        dto.setDownloadUrl("/api/attachments/download/" + a.getStoredName());
        return dto;
    }
}
