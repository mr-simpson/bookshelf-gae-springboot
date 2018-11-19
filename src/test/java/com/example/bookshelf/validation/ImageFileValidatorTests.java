package com.example.bookshelf.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ImageFileValidatorTests {
    
    private ImageFileValidator imageFileValidator;
    
    @BeforeEach
    void setUp() {
        this.imageFileValidator = new ImageFileValidator();
    }
    
    @Test
    void isValidFileNotUploaded() {
        MultipartFile multipartFile = new MockMultipartFile("file-name", "".getBytes());
        assertTrue(imageFileValidator.isValid(multipartFile, null));
    }
    
    @Test
    void isValidZeroByteFileUploaded() {
        MultipartFile multipartFile = new MockMultipartFile("file-name", "original-file-name", "test-content", "".getBytes());
        assertFalse(imageFileValidator.isValid(multipartFile, null));
    }
    
    @Test
    void isValidFileExtensionNotImage() {
        MultipartFile multipartFile = new MockMultipartFile("file-name", "test.csv", "test-content", "test".getBytes());
        assertFalse(imageFileValidator.isValid(multipartFile, null));
    }
    
    @Test
    void isValidFileExtensionIsImage() {
        MultipartFile multipartFile = new MockMultipartFile("file-name", "test.png", "test-content", "test".getBytes());
        assertTrue(imageFileValidator.isValid(multipartFile, null));
    }
}
