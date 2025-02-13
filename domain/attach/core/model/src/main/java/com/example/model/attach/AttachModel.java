package com.example.model.attach;

import lombok.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachModel {

    private Long id;
    private String originFileName;
    private String storedFileName;
    private String filePath;
    private Long fileSize;
    private Long scheduledId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    //presigned url 생성
    public String generatePreSignedUrl(String secretKey, long expirationTime) throws Exception {
        long expirationTimestamp = System.currentTimeMillis() + expirationTime;
        String dataToSign = originFileName + ":" + expirationTimestamp;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String signature = Base64.getEncoder().encodeToString(mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8)));

        return "/files/download?fileName=" + originFileName
                + "&expiration=" + expirationTimestamp
                + "&signature=" + signature;
    }
    
    // presigned url 유효성 검사
    public boolean validatePreSignedUrl(String secretKey, long expiration, String signature) throws Exception {
        if (System.currentTimeMillis() > expiration) {
            System.out.println(expiration);
            return false;
        }

        String dataToSign = originFileName + ":" + expiration;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        String expectedSignature = Base64.getEncoder().encodeToString(mac.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8)));
        System.out.println(expectedSignature);
        System.out.println(signature);
        return expectedSignature.equals(signature);
    }

    //파일 업로드
    public static List<AttachModel> uploadMultipleFiles(List<MultipartFile> fileList, String uploadDir) throws IOException {
        List<AttachModel> uploadedFiles = new ArrayList<>();

        if(!CollectionUtils.isEmpty(fileList)){
            String absolutePath = new File(uploadDir).getAbsolutePath() + File.separator + File.separator;

            File file = new File(absolutePath);

            // 디렉터리가 존재하지 않을 경우
            if(!file.exists()) {
                boolean wasSuccessful = file.mkdirs();
                System.out.println(wasSuccessful);
                // 디렉터리 생성에 실패했을 경우
                if(!wasSuccessful)
                    System.out.println("file: was not successful");
            }

            //다중 파일 처리
            for(MultipartFile multipartFile : fileList){
                if(!multipartFile.isEmpty()) {
                    String originalFileExtension;
                    String originFileName = multipartFile.getOriginalFilename();
                    String storedFileName = UUID.randomUUID() +"."+originFileName;
                    String contentType = multipartFile.getContentType();
                    String ext = Objects.requireNonNull(originFileName).substring(originFileName.lastIndexOf(".")+1);

                    // 확장자명이 존재하지 않을 경우 처리 x
                    if(ObjectUtils.isEmpty(contentType)) {
                        break;
                    }
                    else {
                        if(contentType.contains("image/jpeg"))
                            originalFileExtension = ".jpg";
                        else if(contentType.contains("image/png"))
                            originalFileExtension = ".png";
                        else
                            originalFileExtension= ext;
                    }
                    // 파일명 중복 피하고자 나노초까지 얻어와 지정
                    String new_file_name = System.nanoTime() +"."+originalFileExtension;

                    AttachModel attachModel = AttachModel
                            .builder()
                            .originFileName(originFileName)
                            .storedFileName(storedFileName)
                            .fileSize(multipartFile.getSize())
                            .filePath(uploadDir+File.separator+new_file_name)
                            .build();

                    uploadedFiles.add(attachModel);

                    // 업로드 한 파일 데이터를 지정한 파일에 저장
                    file = new File( absolutePath + File.separator + new_file_name);
                    multipartFile.transferTo(file);
                    System.out.println(file);
                    // 파일 권한 설정(쓰기, 읽기)
                    file.setWritable(true);
                    file.setReadable(true);
                }
            }
        }
        return uploadedFiles;
    }

    //파일 업로드 수정
    public static List<AttachModel> updateMultipleFiles(List<AttachModel> existingFiles, List<MultipartFile> newFiles, String uploadDir) throws IOException {
        List<AttachModel> updatedFiles;

        // 기존 파일 삭제
        for (AttachModel existingFile : existingFiles) {
            File oldFile = new File(uploadDir, existingFile.getStoredFileName());
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
        // 새 파일 저장 (기존 업로드 메서드와 동일한 로직 사용)
        updatedFiles = uploadMultipleFiles(newFiles, uploadDir);

        return updatedFiles;
    }

    //파일 업로드 삭제
    public void deleteFile(String uploadDir) {
        File fileToDelete = new File(uploadDir, this.storedFileName);
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }
    }
}
