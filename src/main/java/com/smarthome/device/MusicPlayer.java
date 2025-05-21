package com.smarthome.device;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayer implements Device {

    private MediaPlayer mediaPlayer;
    private List<File> playlist;
    private int currentIndex = 0;
    private boolean isOn = false;

    public void loadPlaylist(List<File> files) {
        if (playlist == null) {
            playlist = new ArrayList<>();
        }
        playlist.addAll(files);
        currentIndex = 0;
    }


    public void play() {
        if (playlist != null && !playlist.isEmpty()) {
            stop();
            File track = playlist.get(currentIndex);
            Media media = new Media(track.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            isOn = true;
        }
    }

    public void play(int index) {
        if (playlist == null || playlist.isEmpty()) return;
        if (index < 0 || index >= playlist.size()) return;
        currentIndex = index;
        play();
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isOn = false;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isOn = false;
        }
    }

    public void next() {
        if (playlist == null || playlist.isEmpty()) return;
        currentIndex = (currentIndex + 1) % playlist.size();
        play();
    }

    public void prev() {
        if (playlist == null || playlist.isEmpty()) return;
        currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
        play();
    }

    public String getCurrentTrackName() {
        return (playlist == null || playlist.isEmpty())
                ? "-" : playlist.get(currentIndex).getName();
    }

    public List<File> getPlaylist() {
        return playlist;
    }

    public void savePlaylistToFile(File saveFile) {
        if (playlist == null || playlist.isEmpty()) return;
        try (PrintWriter out = new PrintWriter(saveFile)) {
            for (File file : playlist) {
                out.println(file.getAbsolutePath());
            }
            System.out.println("[SAVE] " + saveFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPlaylistFromFile(File loadFile) {
        if (!loadFile.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(loadFile))) {
            List<File> loaded = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                File f = new File(line);
                if (f.exists()) loaded.add(f);
            }
            loadPlaylist(loaded);
            System.out.println("[LOAD] " + loadFile.getAbsolutePath());
            System.out.println("[LOAD] loaded " + loaded.size() + " tracks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Duration getCurrentTime() {
        return mediaPlayer != null ? mediaPlayer.getCurrentTime() : Duration.ZERO;
    }

    public Duration getTotalDuration() {
        return mediaPlayer != null ? mediaPlayer.getTotalDuration() : Duration.UNKNOWN;
    }

    public void turnOn() {
        isOn = true;
        play();
    }

    public void turnOff() {
        isOn = false;
        stop();
    }

    public String getStatus() {
        return isOn ? "Playing" : "Stopped";
    }
}