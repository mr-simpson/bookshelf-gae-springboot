package com.example.bookshelf.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CloudStorageHelperTests {

    @Test
    public void isCloudStorageConfiguredBookShelfBucketIsNull() {
        CloudStorageHelper cloudStorageHelper = new CloudStorageHelper(null, null);
        assertFalse(cloudStorageHelper.isCloudStorageConfigured());
    }
    
    @Test
    public void isCloudStorageConfiguredBookShelfBucketIsBlank() {
        CloudStorageHelper cloudStorageHelper = new CloudStorageHelper("", null);
        assertFalse(cloudStorageHelper.isCloudStorageConfigured());
    }
    
    @Test
    public void isCloudStorageConfiguredBookShelfBucketIsValid() {
        CloudStorageHelper cloudStorageHelper = new CloudStorageHelper("test", null);
        assertTrue(cloudStorageHelper.isCloudStorageConfigured());
    }
}
