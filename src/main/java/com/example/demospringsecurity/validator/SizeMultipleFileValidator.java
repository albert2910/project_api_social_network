package com.example.demospringsecurity.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class SizeMultipleFileValidator implements ConstraintValidator<ValidSizeMultipleFile, MultipartFile[]> {

    @Override
    public void initialize(ValidSizeMultipleFile constraintAnnotation) {

    }

    @Override
    public boolean isValid(MultipartFile[] multipartFiles, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = false;
        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                result = multipartFile.getSize() <= 1048576;
                if (result == false) {
                    break;
                }
            }
        } else {
            result = true;
        }

        return result;
    }
}
