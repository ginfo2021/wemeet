package com.wemeet.dating.api;

import com.wemeet.dating.model.request.FileUploadRequest;
import com.wemeet.dating.model.response.ApiResponse;
import com.wemeet.dating.model.response.ProfilePhotoResponse;
import com.wemeet.dating.model.user.UserResult;
import com.wemeet.dating.service.StorageService;
import com.wemeet.dating.util.validation.constraint.ActiveUser;
import com.wemeet.dating.util.validation.constraint.NotSuspendedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("v1/image")
@Validated
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @NotSuspendedUser(message = "User is suspended")
    @ActiveUser(message = "User not active")
    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse uploadProfilePhoto(
            @AuthenticationPrincipal UserResult userResult,
            @RequestParam(value = "imageType") String imageType,
            MultipartFile file
    ) throws Exception{
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        fileUploadRequest.setImageType(imageType);

        ProfilePhotoResponse response = storageService.storeFiles(userResult, fileUploadRequest);
        return ApiResponse.builder()
                .message("File(s) Uploaded Successfully")
                .data(response)
                .build();
    }
}
