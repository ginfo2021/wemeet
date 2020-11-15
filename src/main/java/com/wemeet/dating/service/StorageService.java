package com.wemeet.dating.service;

import com.wemeet.dating.dao.MusicRepository;
import com.wemeet.dating.dao.PlaylistRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidFileTypeException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Music;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.FileType;
import com.wemeet.dating.model.request.FileUploadRequest;
import com.wemeet.dating.model.request.MusicUploadRequest;
import com.wemeet.dating.model.response.ProfilePhotoResponse;
import com.wemeet.dating.model.user.UserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

    private S3Service s3Service;
    private UserService userService;
    private UserImageService userImageService;
    private MusicService musicService;
    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;

    @Autowired
    public StorageService(
            S3Service s3Service,
            MusicRepository musicRepository, PlaylistRepository playlistRepository) {
        this.s3Service = s3Service;
        this.musicRepository = musicRepository;
        this.playlistRepository = playlistRepository;
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


    public void storeMusicFiles(UserResult user, MusicUploadRequest request) throws Exception {
        if (user == null || user.getUser().getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        long musicCount = musicRepository.count();

        if (musicCount >= 100){
            throw new BadRequestException("Song upload limit exceeded");
        }

        if (request.getSong().isEmpty()){
            throw new InvalidFileTypeException("Song is required");
        }

        if (request.getSongArt().isEmpty()){
            throw new InvalidFileTypeException("Song Art is required");
        }

        if (!isSupportedMusicContentType(request.getSong().getContentType())){
            throw new InvalidFileTypeException("Invalid song filetype");
        }

        if (!isSupportedContentType(request.getSongArt().getContentType())){
            throw new InvalidFileTypeException("Invalid song art filetype");
        }

        try {
            Music music = new Music();
            music.setTitle(request.getTitle().toLowerCase());
            music.setArtist(request.getArtist());
            music.setUploadedBy(user.getUser());

            FileUploadRequest uploadRequest = new FileUploadRequest();
            uploadRequest.setFileType(FileType.valueOf("MUSIC"));
            uploadRequest.setFile(request.getSong());
            music.setSongUrl(s3Service.putObject(user.getUser(), uploadRequest));

            FileUploadRequest artworkRequest = new FileUploadRequest();
            artworkRequest.setFile(request.getSongArt());
            artworkRequest.setFileType(FileType.valueOf("ARTWORK"));
            music.setArtworkURL(s3Service.putObject(user.getUser(), artworkRequest));

            musicRepository.save(music);

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/jpeg");
    }

    private boolean isSupportedMusicContentType(String contentType) {
        return contentType.equals("audio/mpeg");
    }

}
