package com.wemeet.dating.service;

import com.wemeet.dating.dao.MusicRepository;
import com.wemeet.dating.dao.PlaylistRepository;
import com.wemeet.dating.exception.BadRequestException;
import com.wemeet.dating.exception.InvalidJwtAuthenticationException;
import com.wemeet.dating.model.entity.Music;
import com.wemeet.dating.model.entity.Playlist;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.request.CreatePlaylistRequest;
import com.wemeet.dating.model.request.DeleteMusicRequest;
import com.wemeet.dating.model.request.DeleteSongFromPlaylist;
import com.wemeet.dating.model.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        List<Music> songs = new ArrayList<>();
        musicRepository.findAll(PageRequest.of(pageNum, pageSize)).forEach(songs::add);
        //remove deleted songs
        songs = songs.stream()
                .filter(song -> !song.isDeleted())
                .collect(Collectors.toList());

        return new PageResponse<>((Page<Music>) songs);
    }

    public List<Playlist> getPlaylist(User user) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        List<Playlist> playlists = new ArrayList<>();
        playlistRepository.findAll().forEach(playlists::add);
        //remove deleted playlists
        playlists = playlists.stream()
                .filter(playlist -> !playlist.isDeleted())
                .collect(Collectors.toList());

        return playlists;

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

        if (playlistRequest.getSongs().isEmpty()){
            throw new BadRequestException("Song array is empty!");
        }

        long playlistCount = playlistRepository.countByName(playlistRequest.getName().toLowerCase());

        if (playlistCount == 6){
            throw new Exception("Playlist upload limit reached");
        }

        for (long song: playlistRequest.getSongs()){
            Music music = musicRepository.findById(song).orElse(null);
            if (music == null ){
                throw new BadRequestException("Invalid song id");
            }else {
                Playlist playlist = playlistRepository.findByTitle(playlistRequest.getName());

                if (playlist == null){
                    playlist = new Playlist();
                    playlist.setTitle(playlistRequest.getName().toLowerCase());
                    playlist.setUploadedBy(user);
                    playlist.setDeleted(false);

                    playlist.setSongId(music);
                    playlistRepository.save(playlist);
                }
            }
        }

    }

    public void deleteSongFromPlaylist(User user, DeleteSongFromPlaylist request) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        for (long song: request.getSongs()){
            Music music = musicRepository.findById(song).orElse(null);

            if (music == null){
                throw new BadRequestException("Invalid song id");
            }

            Playlist playlist = playlistRepository.findBySongId(music);

            if (playlist == null){
                throw new BadRequestException("Song does not exist in playlist");
            }

            playlist.setDeleted(true);
            playlistRepository.save(playlist);

        }

    }

    public void deleteMusic(User user, DeleteMusicRequest request) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does not exist");
        }

        for (long song: request.getSongs()){
            Music music = musicRepository.findById(song).orElse(null);

            if (music == null){
                throw new BadRequestException("Invalid Song ID");
            }

            music.setDeleted(true);
            musicRepository.save(music);
        }
    }
}
