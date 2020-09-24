package com.wemeet.dating.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.wemeet.dating.exception.S3KeyDoesNotExistException;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.FileUploadRequest;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.core.io.s3.SimpleStorageResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class S3Service {
    private SimpleStorageResourceLoader simpleStorageResourceLoader;
    private AmazonS3 amazonS3;
    private final String S3_BUCKET_IMAGE_PATH = "images/";

    @Value("${aws.s3.bucket}") // Default S3 bucket name. You need to create the bucket manually on AWS S3
            String defaultBucketName;

    public S3Service(AmazonS3 amazonS3, SimpleStorageResourceLoader simpleStorageResourceLoader) {
        this.simpleStorageResourceLoader = simpleStorageResourceLoader;
        this.amazonS3 = amazonS3;
    }

    public Resource getObject(String bucketName, String key) throws S3KeyDoesNotExistException {
        final Resource resource = simpleStorageResourceLoader.getResource(String.format("s3://%s/%s", bucketName, key));
        if (resource.exists()) {
            return resource;
        }
        throw new S3KeyDoesNotExistException(bucketName, key);
    }

    public String putObject(User user, FileUploadRequest uploadRequest) throws IOException {
        String uploadKey = UUID.randomUUID().toString();
        InputStream inputStream = uploadRequest.getFile().getInputStream();
        uploadKey = uploadRequest.getFileType().getName() + "_" + user.getId() + "_" + uploadKey;

        PutObjectRequest putObjectRequest = new PutObjectRequest(defaultBucketName, S3_BUCKET_IMAGE_PATH + uploadKey, inputStream, new ObjectMetadata());
        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(putObjectRequest);
        String imageUrl = amazonS3.getUrl(defaultBucketName, S3_BUCKET_IMAGE_PATH + uploadKey).toString();

        IOUtils.closeQuietly(inputStream);

        return imageUrl;
    }

    public void removeObject(String bucketName, String key) throws S3KeyDoesNotExistException {
        if (amazonS3.doesObjectExist(bucketName, key)) {
            amazonS3.deleteObject(bucketName, key);
        } else {
            throw new S3KeyDoesNotExistException(bucketName, key);
        }
    }
}
