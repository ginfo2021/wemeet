package com.wemeet.dating.exception;

public class S3KeyDoesNotExistException extends RuntimeException {
    public S3KeyDoesNotExistException(String bucketName, String key) {
        super(String.format("The key %s does not exist in bucket %s", bucketName, key));
    }
}
