package com.wemeet.dating.service;

import com.wemeet.dating.exception.InvalidFileTypeException;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.entity.UserImage;
import com.wemeet.dating.model.request.FileUploadRequest;
import com.wemeet.dating.model.response.ProfilePhotoResponse;
import com.wemeet.dating.model.user.UserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    private S3Service s3Service;
    private UserService userService;
    private UserImageService userImageService;

    @Autowired
    public StorageService(
            S3Service s3Service,
            UserService userService,
            UserImageService userImageService
    ) {
        this.s3Service = s3Service;
        this.userService = userService;
        this.userImageService = userImageService;
    }

    public ProfilePhotoResponse storeFiles(UserResult userResult, FileUploadRequest request) throws Exception {

        if(request.getFile().isEmpty()){
            throw new InvalidFileTypeException("Invalid file found");
        }

        String imageUrl = s3Service.putObject(request.getFile().getInputStream());

        User user = userService.findById(userResult.getUser().getId());

        if(request.getFileType().getName().equals("PROFILE_IMAGE")){
            user.setProfileImage(imageUrl);
            userService.createOrUpdateUser(user);
        }else if(request.getFileType().getName().equals("ADDITIONAL_IMAGE")){
            UserImage userImage = new UserImage();
            userImage.setUser(user);
            userImage.setImageUrl(imageUrl);
            userImageService.saveUserImage(userImage);
        }else {
            return null;
        }

        return ProfilePhotoResponse
                .builder()
                .imageUrl(imageUrl)
                .build();
    }

}
