package com.example.apimodel.attach;

import lombok.Builder;

public class AttachApiModel {

    @Builder
    public record AttachResponse(  Long id,
                                   String originFileName,
                                   String storedFileName,
                                   long fileSize,
                                   String filePath) {}
}
