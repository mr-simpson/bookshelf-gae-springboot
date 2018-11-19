package com.example.bookshelf.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        if (multipartFile.getSize() == 0 && multipartFile.getOriginalFilename().length() == 0) {
            return true;
        }
        
        // 0バイトファイルは不可
        if (multipartFile.getSize() == 0 && multipartFile.getOriginalFilename().length() != 0) {
            return false;
        }
        
        return checkFileExtension(multipartFile.getOriginalFilename());
    }

    private boolean checkFileExtension(String fileName) {
        if (fileName != null && !fileName.isEmpty() && fileName.contains(".")) {
            String[] allowedExt = { ".jpg", ".jpeg", ".png", ".gif" };
            for (String ext : allowedExt) {
                if (fileName.endsWith(ext)) {
                    return true;
                }
            }
        }
        return false;
    }
}
