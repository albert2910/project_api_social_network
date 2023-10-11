package com.example.demospringsecurity.service;

import com.example.demospringsecurity.repository.UserInfoRepository;
import com.example.demospringsecurity.response.FileUploadResponse;
import com.example.demospringsecurity.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.util.StringUtils.cleanPath;

@Service
public class FileService {

    public FileUploadResponse uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        String fileCode = FileUploadUtil.saveFile(fileName, multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/downloadFile/" + fileCode);
        return response;
    }
}
