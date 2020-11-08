package com.wemeet.dating.service;

import com.wemeet.dating.dao.MusicRepository;
import com.wemeet.dating.dao.PlaylistRepository;
import com.wemeet.dating.exception.InvalidFileTypeException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Music;
import com.wemeet.dating.model.entity.Playlist;
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

        if (request.getFiles().isEmpty()){
            throw new InvalidFileTypeException("Files not found");
        }

        request.getFiles().forEach(multipartFile -> {
            try {
                if (isSupportedMusicContentType(multipartFile.getContentType())
                        || isSupportedContentType(multipartFile.getContentType())){
                    throw new InvalidFileTypeException("Invalid filetype");
                }

                FileUploadRequest fileUploadRequest  = new FileUploadRequest();
                fileUploadRequest.setFile(multipartFile);
                fileUploadRequest.setFileType(FileType.valueOf(request.getMusicType().getName()));

                Music music = new Music();
                music.setArtist(request.getArtist());
                music.setTitle(request.getTitle());
                music.setUploadedBy(user.getUser());

                music = musicRepository.save(music);

                if (request.getMusicType().getName().equals("PLAYLIST")){
                    Playlist playlist = new Playlist();
                    playlist.setSongId(music);
                    playlist.setUploadedBy(user.getUser());
                    playlist.setTitle(request.getTitle());
                    playlist.setArtist(playlist.getArtist());
                }

                if (isSupportedContentType(multipartFile.getContentType())){
                    String fileUrl = s3Service.putObject(user.getUser(), fileUploadRequest);
                    Playlist playlist = playlistRepository.findBySongId(music);
                    if (playlist != null){
                        playlist.setArtwork(fileUrl);
                    }

                }else {
                    String musicUrl = s3Service.putObject(user.getUser(), fileUploadRequest);
                    music.setSongUrl(musicUrl);
                    musicRepository.save(music);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
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
