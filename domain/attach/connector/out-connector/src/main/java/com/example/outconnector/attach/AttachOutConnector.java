package com.example.outconnector.attach;

import com.example.attach.dto.AttachErrorCode;
import com.example.attach.exception.AttachCustomExceptionHandler;
import com.example.model.attach.AttachModel;
import com.example.rdbrepository.Attach;
import com.example.rdbrepository.AttachRepository;
import com.example.rdbrepository.ScheduleRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
public class AttachOutConnector {

    private final AttachRepository attachRepository;

    private final ScheduleRepository scheduleRepository;

    public List<AttachModel> findAll() {
        return attachRepository
                .findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    public AttachModel findById(Long attachId) {
        return attachRepository.findById(attachId)
                .map(this::toModel)
                .orElseThrow(()-> new AttachCustomExceptionHandler(AttachErrorCode.NOT_FOUND_ATTACH));
    }

    public AttachModel findByOriginFileName(String originFileName) {
        return attachRepository.findByOriginFileName(originFileName)
                .map(this::toModel)
                .orElseThrow(()-> new AttachCustomExceptionHandler(AttachErrorCode.NOT_FOUND_ATTACH));
    }

    public AttachModel createAttach(AttachModel attachModel) {

        if (attachModel.getScheduledId() == null) {
            //Schedules testSchedule = scheduleRepository.save(new Schedules("테스트 스케줄"));
            attachModel.setScheduledId(0L);  // 테스트용 임시 ID (0L로 설정)
        }

        Attach attach = Attach
                .builder()
                .scheduledId(attachModel.getId())
                .originFileName(attachModel.getOriginFileName())
                .storedFileName(attachModel.getStoredFileName())
                .fileSize(attachModel.getFileSize())
                .filePath(attachModel.getFilePath())
                .build();

        return toModel(attachRepository.save(attach));
    }

    public AttachModel updateAttach(Long attachId,AttachModel attachModel) {
        Attach attach = findByOne(attachId);
        attach.update(attachModel.getOriginFileName(), attachModel.getStoredFileName());
        return toModel(attach);
    }

    public void deleteAttach(Long attachId) {
        Attach attach = findByOne(attachId);

        if(attach!=null) {
            attachRepository.deleteById(attachId);
        }
    }

    private Attach findByOne(Long attachId) {
        return attachRepository
                .findById(attachId)
                .orElseThrow(()->new AttachCustomExceptionHandler(AttachErrorCode.NOT_FOUND_ATTACH));
    }

    private AttachModel toModel(Attach attach) {
        return AttachModel
                .builder()
                .id(attach.getId())
                .fileSize(attach.getFileSize())
                .originFileName(attach.getOriginFileName())
                .storedFileName(attach.getStoredFileName())
                .filePath(attach.getFilePath())
                .scheduledId(attach.getScheduledId())
                .createdBy(attach.getCreatedBy())
                .createdTime(attach.getCreatedTime())
                .updatedBy(attach.getUpdatedBy())
                .updatedTime(attach.getUpdatedTime())
                .build();
    }
}
