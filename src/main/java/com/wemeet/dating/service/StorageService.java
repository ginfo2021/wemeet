package com.wemeet.dating.service;

import com.wemeet.dating.exception.InvalidFileTypeException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.FileUploadRequest;
import com.wemeet.dating.model.request.MusicUploadRequest;
import com.wemeet.dating.model.response.MusicUploadResponse;
import com.wemeet.dating.model.response.ProfilePhotoResponse;
import com.wemeet.dating.model.user.UserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StorageService {

    private S3Service s3Service;
    private UserService userService;
    private UserImageService userImageService;
    private MusicService musicService;

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

        System.out.println("filetyoe"+ request.getFile().getContentType());
        if (!isSupportedContentType(request.getFile().getContentType())){
            throw new InvalidFileTypeException("Only PNG or JPG images are allowed!");
        }

        String imageUrl = s3Service.putObject(user, request);

        return ProfilePhotoResponse
                .builder()
                .imageUrl(imageUrl)
                .build();
    }


    public MusicUploadResponse storeMusicFiles(UserResult user, MusicUploadRequest request) throws Exception {
        if (user == null || user.getUser().getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        if(!request.getFileType().getName().equals("MUSIC")){
            throw new InvalidFileTypeException("Endpoint accepts only music files");
        }

        if(request.getFile().isEmpty()){
            throw new InvalidFileTypeException("Invalid file found");
        }

        String[] allowedContentTypes = new String[]{"audio/mp3"};
        List<String> list = Arrays.asList(allowedContentTypes);

        if (!list.contains(request.getFile().getContentType())){
            throw new InvalidFileTypeException("Invalid file type!");
        }

        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFileType(request.getFileType());
        fileUploadRequest.setFile(request.getFile());

        String musicUrl = s3Service.putObject(user.getUser(), fileUploadRequest);

        return MusicUploadResponse
                .builder()
                .musicUrl(musicUrl)
                .build();
    }


    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }

}
