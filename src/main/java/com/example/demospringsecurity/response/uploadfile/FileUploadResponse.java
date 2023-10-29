package com.example.demospringsecurity.response.uploadfile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {
    private String fileName;
    private String downloadUri;
    private long size;
}
