package com.example.inconnector.attach;

import static com.example.apimodel.attach.AttachApiModel.AttachResponse;
import com.example.interfaces.attach.AttachInterfaces;
import com.example.model.attach.AttachModel;
import com.example.service.attach.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttachInConnector implements AttachInterfaces {

    private final AttachService attachService;

    @Override
    public List<AttachResponse> findAll() {
        return attachService.findAll().stream().map(this::toApiModel).toList();
    }

    @Override
    public AttachResponse findById(Long attachId) {
        AttachModel attachModel = attachService.findById(attachId);
        return toApiModel(attachModel);
    }

    @Override
    public AttachResponse findByOriginFileName(String originFileName) {
        AttachModel attachModel = attachService.findByOriginFileName(originFileName);
        return toApiModel(attachModel);
    }

    @Override
    public List<AttachResponse> findByIds(List<Long> attachIds) throws IOException {
        List<AttachModel> attachModel = attachService.findByIds(attachIds);
        return attachModel
                .stream()
                .map(this::toApiModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachResponse> createdAttach(List<MultipartFile> fileList) throws IOException {
        List<AttachModel>createResult = attachService.createAttach(fileList);
        return createResult
                .stream()
                .map(this::toApiModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachResponse> updateAttach(List<MultipartFile>uploadFiles,Long scheduleId) throws IOException {
        List<AttachModel>updatedResult = attachService.updateAttach(scheduleId,uploadFiles);
        return updatedResult
                .stream()
                .map(this::toApiModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttach(Long attachId) {
        attachService.deleteAttach(attachId);
    }

    @Override
    public void updateScheduleId(List<Long> fileIds, Long scheduleId) {
        attachService.updateScheduleId(fileIds,scheduleId);
    }

    @Override
    public String generatePreSignedUrl(Long attachId) throws Exception {
        return attachService.generatePreSignedUrl(attachId);
    }

    @Override
    public boolean validatePareSignedUrl(String fileName, long expiration, String signature) throws Exception {
        return attachService.validatePreSignedUrl(fileName,expiration,signature);
    }

    //model <-> api model
    private AttachResponse toApiModel(AttachModel attachModel) {
        return AttachResponse.builder()
                .id(attachModel.getId())
                .originFileName(attachModel.getOriginFileName())
                .storedFileName(attachModel.getStoredFileName())
                .thumbnailFilePath(attachModel.getThumbnailFilePath())
                .fileSize(attachModel.getFileSize())
                .filePath(attachModel.getFilePath())
                .isDeletedAttach(attachModel.isDeletedAttached())
                .build();
    }
}
