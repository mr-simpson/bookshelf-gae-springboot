package com.example.bookshelf.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

@Component
public class CloudStorageHelper {
    
    private String bookshelfBucket;
    
    private Storage storage;
    
    public CloudStorageHelper(@Value("${bookshelf.bucket:#{null}}") String bookshelfBucket,
            Storage storage) {
        this.bookshelfBucket = bookshelfBucket;
        this.storage = storage;
        
    }

    public String save(String fileName, byte[] bytes) throws IOException {

        if (!isCloudStorageConfigured()) {
            return null;
        }
        
        DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
        DateTime dt = DateTime.now(DateTimeZone.UTC);
        String dtString = dt.toString(dtf);
        final String saveFileName = fileName + dtString;
        
        BlobInfo blobInfo =
                storage.create(
                        BlobInfo
                        .newBuilder(bookshelfBucket, saveFileName)
                        .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                        .build(),
                        bytes);
        
        return blobInfo.getMediaLink();
      }
    
    public boolean isCloudStorageConfigured() {
        if (bookshelfBucket == null || bookshelfBucket.length() == 0) {
            return false;
        }
        return true;
    }
}
