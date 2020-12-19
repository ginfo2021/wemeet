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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public MusicService(MusicRepository musicRepository, PlaylistRepository playlistRepository, UserService userService) {
        this.musicRepository = musicRepository;
        this.playlistRepository = playlistRepository;
        this.userService = userService;
    }

    public PageResponse<Music> getMusicList(String title, User user, int pageNum, int pageSize) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        PageResponse<Music> response = new PageResponse<>();
        List<Music> songs = new ArrayList<>();
        Page<Music> musicPage = null;

        if (title != null){
            musicPage = musicRepository.findByTitle(title.toLowerCase(), PageRequest.of(pageNum, pageSize));
        }else {
            musicPage = musicRepository.getAllSongs(PageRequest.of(pageNum, pageSize));
        }

        musicPage.forEach(songs::add);
        //remove deleted songs
        songs = songs
                .stream()
                .collect(Collectors.toList());

        response.setContent(songs);
        response.setPageNum(musicPage.getNumber());
        response.setNumberOfElements(musicPage.getNumberOfElements());
        response.setPageSize(musicPage.getSize());
        response.setTotalElements(musicPage.getTotalElements());
        response.setTotalPages(musicPage.getTotalPages());

        return response;
    }

    public PageResponse<Playlist> getPlaylist(String title, User user, int pageNum, int pageSize) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        PageResponse<Playlist> response = new PageResponse<>();
        List<Playlist> playlists = new ArrayList<>();
        Page<Playlist> playlistPage = null;

        if (title != null){
            playlistPage = playlistRepository.findByTitle(title.toLowerCase(), PageRequest.of(pageNum, pageSize));
        }else{
           playlistPage = playlistRepository.getAllSongsOnPlaylist(PageRequest.of(pageNum, pageSize));
        }

        try {
            playlistPage.forEach(playlists::add);
        }catch (Exception exception){
            logger.error("Error fetching playlist", exception);
        }

        //remove deleted playlists
        playlists = playlists.stream()
                .filter(playlist -> !playlist.isDeleted())
                .collect(Collectors.toList());

        response.setContent(playlists);
        response.setPageNum(playlistPage.getNumber());
        response.setPageSize(playlistPage.getSize());
        response.setNumberOfElements(playlistPage.getNumberOfElements());
        response.setTotalElements(playlistPage.getTotalElements());
        response.setTotalPages(playlistPage.getTotalPages());

        return response;

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

        long playlistCount = playlistRepository.countByTitle(playlistRequest.getName().toLowerCase());

        if (playlistCount >= 6){
            throw new BadRequestException("Playlist upload limit reached");
        }

        for (long song: playlistRequest.getSongs()){
            Music music = musicRepository.findById(song).orElse(null);
            if (music == null ){
                throw new BadRequestException("Invalid song id");
            }else {

                Playlist playlist = new Playlist();
                playlist.setTitle(playlistRequest.getName().toLowerCase());
                playlist.setUploadedBy(user);
                playlist.setDeleted(false);

                playlist.setSongId(music);
                playlistRepository.save(playlist);

            }
        }

    }

    public void deleteSongFromPlaylist(User user, DeleteSongFromPlaylist request) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does Not exist");
        }

        List<Playlist> playlists = (List<Playlist>) playlistRepository.findAllById(request.getSongs());

        if (playlists.isEmpty()){
            throw new BadRequestException("Song does not exist in playlist");
        }

        playlists.forEach(playlist -> {
            playlist.setDeleted(true);
            playlistRepository.save(playlist);
        });

    }

    public void deleteMusic(User user, DeleteMusicRequest request) throws Exception {
        if (user == null || user.getId() <= 0) {
            throw new InvalidJwtAuthenticationException("User with token does not exist");
        }

        List<Music> songs = (List<Music>) musicRepository.findAllById(request.getSongs());

        if (songs.isEmpty()){
            throw new BadRequestException("Song does not exist in playlist");
        }

        songs.forEach(song -> {
            song.setDeleted(true);
            musicRepository.save(song);
        });
    }

    public long countSongs() {
        return musicRepository.count();
    }
}
