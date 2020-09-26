package com.wemeet.dating.service;

import com.wemeet.dating.exception.InvalidFileTypeException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.FileUploadRequest;
import com.wemeet.dating.model.response.ProfilePhotoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    private S3Service s3Service;
    private UserService userService;
    private UserImageService userImageService;

    @Autowired
    public StorageService(
            S3Service s3Service
    ) {
        this.s3Service = s3Service;
    }

    public ProfilePhotoResponse storeFiles(User user, FileUploadRequest request) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        if(request.getFile().isEmpty()){
            throw new InvalidFileTypeException("Invalid file found");
        }

        String imageUrl = s3Service.putObject(user, request);

        return ProfilePhotoResponse
                .builder()
                .imageUrl(imageUrl)
                .build();
    }

}
