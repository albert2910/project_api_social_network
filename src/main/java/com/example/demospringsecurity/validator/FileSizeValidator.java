package com.example.demospringsecurity.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<ValidSizeFile, MultipartFile> {
    @Override
    public void initialize(ValidSizeFile constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
//        1MB = 1048576
        return multipartFile.getSize() <= 1048576;
    }

}
