package com.wemeet.dating.service;

import com.wemeet.dating.dao.MusicRepository;
import com.wemeet.dating.dao.PlaylistRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Music;
import com.wemeet.dating.model.entity.Playlist;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.CreatePlaylistRequest;
import com.wemeet.dating.model.request.MusicCreateRequest;
import com.wemeet.dating.model.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MusicService {
    private final MusicRepository musicRepository;
    private final PlaylistRepository playlistRepository;
    private final UserService userService;

    @Autowired
    public MusicService(MusicRepository musicRepository, PlaylistRepository playlistRepository, UserService userService) {
        this.musicRepository = musicRepository;
        this.playlistRepository = playlistRepository;
        this.userService = userService;
    }

    public PageResponse<Music> getMusicList(User user, int pageNum, int pageSize) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        return new PageResponse<>(musicRepository.findAll(PageRequest.of(pageNum, pageSize)));
    }

    public Music findMusicById(Long id) {
        Music music = musicRepository.findById(id).orElse(null);
        if (music != null && music.isDeleted()) {
            return null;
        }
        return music;
    }

    public Playlist findPlaylistById(Long id) {
        Playlist playlist = playlistRepository.findById(id).orElse(null);

        if (playlist != null && playlist.isDeleted()) {
            return null;
        }
        return playlist;
    }

    public void createOrUpdatePlaylist(User user, CreatePlaylistRequest playlistRequest) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        Music music = this.findMusicById(playlistRequest.getSongId());

        if (music == null || music.getId() <= 0) {
            throw new BadRequestException("Music not found");
        }

        Playlist playlist = new Playlist();
        playlist.setTitle(playlistRequest.getName());
        playlist.setUploadedBy(user);
        playlist.setDeleted(false);
        playlist.setSongId(music);
        playlistRepository.save(playlist);
    }

    public void createMusic(MusicCreateRequest request) throws  Exception{
        Music music = new Music();

        request.setArtist(request.getArtist());
        request.setSongUrl(request.getSongUrl());
        request.setTitle(request.getTitle());

        musicRepository.save(music);
    }
}
