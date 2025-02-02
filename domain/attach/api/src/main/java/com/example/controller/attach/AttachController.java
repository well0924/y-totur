package com.example.controller.attach;

import static com.example.apimodel.attach.AttachApiModel.AttachResponse;
import com.example.inconnector.attach.AttachInConnector;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/attach")
public class AttachController {

    private final AttachInConnector attachInConnector;

    @GetMapping("/")
    public ResponseEntity<List<AttachResponse>> findAll() {
        List<AttachResponse> attachResponseList = attachInConnector.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(attachResponseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachResponse> findById(@PathVariable("id") Long attachId) {
        return ResponseEntity.status(HttpStatus.OK).body(attachInConnector.findById(attachId));
    }

    //test
    @PostMapping("/upload")
    public ResponseEntity<List<AttachResponse>> uploadFiles(@RequestPart("files")List<MultipartFile>fileList) {
        try {
            List<AttachResponse> attachModels = attachInConnector.createdAttach(fileList);
            return ResponseEntity.ok(attachModels);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //test
    @PutMapping("/{id}/update")
    public ResponseEntity<List<AttachResponse>> updateFiles(
            @PathVariable("id") Long attachId,
            @RequestPart("files") List<MultipartFile> request) {
        try {
            List<AttachResponse> updatedFiles = attachInConnector.updateAttach(request,attachId);
            return ResponseEntity.ok(updatedFiles);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    //test
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") Long attachId) {
        attachInConnector.deleteAttach(attachId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/presigned-url")
    public ResponseEntity<String> getPreSignedUrl(@PathVariable("id") Long attachId) {
        try {
            String preSignedUrl = attachInConnector.generatePreSignedUrl(attachId);
            return ResponseEntity.ok(preSignedUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Presigned URL 생성 실패");
        }
    }

    @GetMapping("/file/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String fileName,
            @RequestParam Long expiration,
            @RequestParam String signature) {

        try {
            boolean isValid = attachInConnector.validatePareSignedUrl(fileName, expiration, signature);
            log.info("result::"+isValid);
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            } else {
                AttachResponse attachModel = attachInConnector.findByOriginFileName(fileName);
                log.info("????"+attachModel);
                Path filePath = Paths.get(attachModel.filePath());
                Resource resource = new UrlResource(filePath.toUri());

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
