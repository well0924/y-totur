package com.example.interfaces.attach;

import org.springframework.web.multipart.MultipartFile;

import static com.example.apimodel.attach.AttachApiModel.AttachResponse;

import java.io.IOException;
import java.util.List;

public interface AttachInterfaces {
    List<AttachResponse> findAll();
    AttachResponse findById(Long attachId);
    AttachResponse findByOriginFileName(String originFileName);
    List<AttachResponse> createdAttach(List<MultipartFile>uploadFiles) throws IOException;
    List<AttachResponse> updateAttach(List<MultipartFile>uploadFiles,Long attachId) throws IOException;
    void deleteAttach(Long attachId);
    String generatePreSignedUrl(Long attachId) throws Exception;
    boolean validatePareSignedUrl(String fileName,long expiration,String signature) throws Exception;
}
