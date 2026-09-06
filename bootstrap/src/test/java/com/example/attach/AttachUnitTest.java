package com.example.attach;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.attach.exception.AttachCustomExceptionHandler;
import com.example.events.spring.AttachCreatedEvent;
import com.example.model.attach.AttachModel;
import com.example.outbound.attach.AmazonS3OutConnector;
import com.example.outbound.attach.AttachOutConnector;
import com.example.outbound.attach.FailedThumbnailOutConnector;
import com.example.s3.utile.FileUtile;
import com.example.service.attach.AttachService;
import com.example.service.attach.ThumbnailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AttachService/ThumbnailService는 raw AWS SDK(AmazonS3)가 아니라 자체 포트
 * (AmazonS3Port / 구현체 AmazonS3OutConnector)를 통해 S3와 통신하도록 리팩터링되어 있다.
 * mock 타입/메서드명을 실제 포트 시그니처에 맞춰 전면 재작성한다.
 */
@ExtendWith(MockitoExtension.class)
public class AttachUnitTest {

    @InjectMocks
    private AttachService attachService;

    @InjectMocks
    private ThumbnailService thumbnailService;

    @Mock
    private AttachOutConnector attachOutConnector;

    // AmazonS3OutConnector가 AmazonS3Port를 구현하므로, mock 하나로
    // AttachService(AmazonS3Port 필드)와 ThumbnailService(AmazonS3OutConnector 필드) 둘 다 주입된다.
    @Mock
    private AmazonS3OutConnector amazonS3;

    @Mock
    private FailedThumbnailOutConnector failedThumbnailOutConnector;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() throws Exception {
        // bucketName 필드 리플렉션 주입
        Field bucketField = AttachService.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(attachService, bucketName);

        // 썸네일 크기 값도 설정해주자 (디폴트 없으면 NPE 날 수 있음)
        Field widthField = AttachService.class.getDeclaredField("thumbnailWidth");
        widthField.setAccessible(true);
        widthField.set(attachService, 200);

        Field heightField = AttachService.class.getDeclaredField("thumbnailHeight");
        heightField.setAccessible(true);
        heightField.set(attachService, 200);

        // ThumbnailService도 동일하게 @Value 필드가 있는데, 순수 Mockito 테스트라
        // 스프링 컨텍스트 없이는 주입되지 않아 기본값(0)으로 남아 Thumbnails.size(0,0)에서
        // 예외가 나던 부분 - 여기도 리플렉션으로 값을 넣어준다.
        Field thumbWidthField = ThumbnailService.class.getDeclaredField("thumbnailWidth");
        thumbWidthField.setAccessible(true);
        thumbWidthField.set(thumbnailService, 200);

        Field thumbHeightField = ThumbnailService.class.getDeclaredField("thumbnailHeight");
        thumbHeightField.setAccessible(true);
        thumbHeightField.set(thumbnailService, 200);
    }

    @Test
    @DisplayName("이미지 확장자 테스트")
    void ImageTypeTest() {
        assertTrue(FileUtile.isSupportedImageExtension("test.jpg"));
        assertTrue(FileUtile.isSupportedImageExtension("test.PNG"));
        assertFalse(FileUtile.isSupportedImageExtension("test.txt"));
    }

    @Test
    @DisplayName("이미지 MINE 타입 검사")
    void ImageMineTypeTest() throws IOException {
        BufferedImage dummy = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummy, "png", baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        assertTrue(FileUtile.isImageMimeType(bais));
    }

    @Test
    @DisplayName("섬네일 생성 테스트")
    void ThumbNailCreateTest() throws IOException {
        // given
        String storedFileName = "final/test-image.jpg";
        AttachModel attachModel = AttachModel.builder()
                .id(1L)
                .storedFileName(storedFileName)
                .build();

        BufferedImage dummyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "jpg", baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        when(amazonS3.getObjectInputStream(storedFileName)).thenReturn(bais);
        when(amazonS3.getFileUrl(anyString())).thenReturn("https://dummy-url.com/thumb_test-image.jpg");

        // when
        thumbnailService.createAndUploadThumbnail(attachModel);

        // then
        verify(amazonS3, times(1)).upload(startsWith("thumb_"), any(InputStream.class), any(ObjectMetadata.class));
        verify(attachOutConnector, times(1)).updateAttach(eq(1L), any(AttachModel.class));
    }

    @Test
    @DisplayName("createAttach 성공 - S3 복사/URL 발급/저장 및 이벤트 발행")
        // 참고: 썸네일 생성은 이제 createAttach()가 발행하는 이벤트를 구독하는 별도 리스너가
        // 비동기로 처리하므로(여기선 eventPublisher가 mock이라 실제 발행되지 않음),
        // createAttach() 자체의 책임(S3 복사/URL/사이즈/저장/이벤트 발행)만 검증한다.
    void createAttachTest() {
        // given
        List<String> uploadedFiles = List.of("temp/test1.jpg", "temp/test2.jpg");

        for (String tempFile : uploadedFiles) {
            String finalFile = tempFile.replaceFirst("^temp/", "final/");
            when(amazonS3.fileSize(finalFile)).thenReturn(123L);
            when(amazonS3.getFileUrl(finalFile)).thenReturn("https://dummy.com/" + finalFile);
        }

        when(attachOutConnector.createAttach(any())).thenAnswer(invocation -> {
            AttachModel model = invocation.getArgument(0);
            model.setId(new Random().nextLong());
            return model;
        });

        // when
        List<AttachModel> result = attachService.createAttach(uploadedFiles);

        // then
        assertEquals(2, result.size());
        for (String tempFile : uploadedFiles) {
            String finalFile = tempFile.replaceFirst("^temp/", "final/");
            verify(amazonS3).copy(tempFile, finalFile);
            verify(amazonS3).delete(tempFile);
            verify(amazonS3).fileSize(finalFile);
        }
        verify(attachOutConnector, times(2)).createAttach(any());
        // publishEvent가 오버로드(ApplicationEvent/Object) 메서드라, 타입을 명시해야
        // Mockito가 실제 호출된 오버로드를 정확히 매칭한다.
        verify(eventPublisher, times(2)).publishEvent(any(AttachCreatedEvent.class));
    }

    @Test
    @DisplayName("PreSignedUrl 발급 테스트")
    void generatePreSignedUrlTest() throws Exception {
        // given
        List<String> fileNames = List.of("test1.jpg", "test2.jpg");

        when(amazonS3.generatePresignedUrl(anyString(), eq(HttpMethod.PUT), anyLong()))
                .thenAnswer(invocation -> new URL("https://dummy.com/" + invocation.getArgument(0)));

        // when
        List<String> result = attachService.generatePreSignedUrls(fileNames);

        // then
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("temp/test1.jpg"));
        assertTrue(result.get(1).contains("temp/test2.jpg"));
    }

    @Test
    @DisplayName("파일 + Attach 삭제 테스트")
    void deleteAttachAndFile_정상_삭제_테스트() {
        // given
        AttachModel attachModel = AttachModel.builder()
                .id(1L)
                .storedFileName("final/test-image.jpg")
                .build();

        when(attachOutConnector.findById(1L)).thenReturn(attachModel);

        // when
        attachService.deleteAttachAndFile(1L);

        // then
        verify(amazonS3).delete("final/test-image.jpg");
        verify(amazonS3).delete("thumb_final/test-image.jpg");
        verify(attachOutConnector).deleteAttach(1L);
    }

    @Test
    @DisplayName("createAttach 실패 시 AttachCustomExceptionHandler 발생")
    void createAttach_S3_예외_발생_테스트() {
        // given
        List<String> files = List.of("temp/test1.jpg");
        // S3 복사 단계에서 예외 발생 유도
        doThrow(new RuntimeException("S3 복사 실패"))
                .when(amazonS3).copy(anyString(), anyString());

        // when & then
        assertThrows(AttachCustomExceptionHandler.class, () -> attachService.createAttach(files));
    }

    @Test
    @DisplayName("deleteFileFromS3 예외 발생 시 RuntimeException 처리")
    void deleteFileFromS3_예외_처리_테스트() {
        // given
        String fileName = "final/test.jpg";
        doThrow(new RuntimeException("S3 삭제 실패"))
                .when(amazonS3).delete(fileName);

        // when & then
        assertThrows(RuntimeException.class, () -> attachService.deleteFileFromS3(fileName));
    }
}
