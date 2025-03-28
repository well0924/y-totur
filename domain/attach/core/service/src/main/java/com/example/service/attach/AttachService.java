package com.example.service.attach;

import com.example.model.attach.AttachModel;
import com.example.outconnector.attach.AttachOutConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class AttachService {

    private final AttachOutConnector attachOutConnector;

    private final String fileUploadPath;

    private final String signedKey;

    private final Long expirationTime;

    public AttachService(
            AttachOutConnector attachOutConnector,
            @Value("${server.file.upload}") String fileUploadPath,
            @Value("${upload.key}") String signedKey,
            @Value("${upload.expiration}") Long expirationTime) {
        this.attachOutConnector = attachOutConnector;
        this.fileUploadPath = fileUploadPath;
        this.signedKey = signedKey;
        this.expirationTime = expirationTime;
    }

    @Transactional(readOnly = true)
    public List<AttachModel> findAll() {
        return attachOutConnector.findAll();
    }

    @Transactional(readOnly = true)
    public AttachModel findById(Long attachId) {
        return attachOutConnector.findById(attachId);
    }

    @Transactional(readOnly = true)
    public List<AttachModel> findByIds(List<Long>attachIds) {
        return attachOutConnector.findByIdIn(attachIds);
    }

    @Transactional(readOnly = true)
    public AttachModel findByOriginFileName(String originFileName) {
        return attachOutConnector.findByOriginFileName(originFileName);
    }

    public List<AttachModel> createAttach(List<MultipartFile> files) throws IOException {

        List<AttachModel> attachModels = AttachModel.uploadMultipleFiles(files, fileUploadPath);
        List<AttachModel> savedAttachModels = new ArrayList<>();

        for (AttachModel attachModel : attachModels) {
            savedAttachModels.add(attachOutConnector.createAttach(attachModel));
        }
        log.info("result::"+savedAttachModels);
        return savedAttachModels;
    }

    public List<AttachModel> updateAttach(Long scheduleId, List<MultipartFile> newFiles) throws IOException {

        List<AttachModel> existFile = attachOutConnector.findAllByScheduleId(scheduleId);
        log.info("attachment:"+existFile);
        List<AttachModel> updatedFiles = AttachModel.updateMultipleFiles(existFile, newFiles, fileUploadPath);

        List<AttachModel> savedAttachModels = new ArrayList<>();

        for (AttachModel updatedFile : updatedFiles) {
            savedAttachModels.add(attachOutConnector.updateAttach(updatedFile.getId(), updatedFile));
        }
        log.info("updatedInfo::"+savedAttachModels);
        return savedAttachModels;
    }

    public void deleteAttach(Long attachId) {
        AttachModel attachModel = attachOutConnector.findById(attachId);
        attachModel.deleteFile(attachModel.getFilePath());
        attachOutConnector.deleteAttach(attachId);
    }

    public void updateScheduleId(List<Long> fileIds, Long scheduleId) {
        attachOutConnector.updateScheduleId(fileIds, scheduleId);
    }

    public String generatePreSignedUrl(Long attachId) throws Exception {
        AttachModel attachModel = attachOutConnector.findById(attachId);
        return attachModel.generatePreSignedUrl(signedKey,expirationTime);
    }

    public boolean validatePreSignedUrl(String fileName, long expiration, String signature) throws Exception {
        AttachModel attachModel = attachOutConnector.findByOriginFileName(fileName);
        return attachModel.validatePreSignedUrl(signedKey, expiration, signature);
    }
}
