package com.example.demospringsecurity.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MultipleFileValidator implements ConstraintValidator<ValidMultipleFile, MultipartFile[]> {
    @Override
    public void initialize(ValidMultipleFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile[] multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = true;
        if(multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                String contentType = multipartFile.getContentType();
                if (!isSupportedContentType(contentType)) {
                    result = false;
                }
            }
        }
        return result;
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("application/pdf")
                || contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }
}
