package com.example.demospringsecurity.service;

import com.example.demospringsecurity.response.uploadfile.FileUploadResponse;
import com.example.demospringsecurity.util.FileUploadUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @InjectMocks
    FileService fileService;


    @Test
    public void testUploadFile() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("data", "testFile.txt", "text/plain", "some xml".getBytes());
        MockedStatic fileUploadUtil = Mockito.mockStatic(FileUploadUtil.class);
        // Mock the behavior of the MultipartFile
//        when(multipartFile.getOriginalFilename()).thenReturn("testFile.txt");
//        when(multipartFile.getSize()).thenReturn(100L);

        // Mock the behavior of your utility class, assuming you have a utility class
        fileUploadUtil.when(() -> FileUploadUtil.saveFile(Mockito.anyString(), eq(multipartFile))).thenReturn("12345");

        // Call the method you want to test
        FileUploadResponse response = fileService.uploadFile(multipartFile);

        // Verify the expected behavior
        assertEquals("testFile.txt", response.getFileName());
        assertEquals(8, response.getSize());
        assertEquals("/downloadFile/12345", response.getDownloadUri());

        // Verify that the mocked methods were called
//        verify(multipartFile).getOriginalFilename();
//        verify(multipartFile).getSize();
//        verify(FileUploadUtil).saveFile(any(), multipartFile);
    }

}