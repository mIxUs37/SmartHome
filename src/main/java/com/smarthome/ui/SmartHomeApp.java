package com.smarthome.ui;

import com.smarthome.device.*;
import com.smarthome.home.ActionLogger;
import com.smarthome.home.SmartHomeFacade;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class SmartHomeApp extends Application {

    private SmartHomeFacade smartHome;
    private ActionLogger logger;
    private TextArea logArea;
    private File playlistFile = new File("playlist.txt");

    private final Label songLabel = new Label("Current song: -");
    private final Label timeLabel = new Label("00:00 / 00:00");
    private final ListView<String> songList = new ListView<>();
    private Timeline timer;
    private MusicPlayer musicPlayer;

    @Override
    public void start(Stage stage) {
        logger = new ActionLogger();
        Room livingRoom = new Room("Living Room");
        Room kitchen = new Room("Kitchen");

        livingRoom.addDevice("Light", new Light());
        livingRoom.addDevice("AC", new AC());
        livingRoom.addDevice("Speaker", new Speaker());

        kitchen.addDevice("Light", new Light());
        kitchen.addDevice("AC", new AC());
        kitchen.addDevice("Speaker", new Speaker());

        musicPlayer = new MusicPlayer();
        musicPlayer.loadPlaylistFromFile(playlistFile);

        smartHome = new SmartHomeFacade(livingRoom, kitchen, musicPlayer, logger);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Smart Home");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.web("#333333"));
        root.getChildren().add(titleLabel);

        VBox roomControlPane = new VBox(10);
        roomControlPane.setPadding(new Insets(15));
        roomControlPane.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 10;");

        Label roomLabel = new Label("Rooms management:");
        roomLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        roomControlPane.getChildren().add(roomLabel);

        // --- Кнопки включить/выключить всё
        HBox quickButtons = new HBox(10);
        Button btnAllOn = createControlButton("Turn All ON", "#2ecc71");
        Button btnAllOff = createControlButton("Turn All OFF", "#e74c3c");

        btnAllOn.setOnAction(e -> {
            smartHome.turnAllOn();
            updateLog();
        });

        btnAllOff.setOnAction(e -> {
            smartHome.turnAllOff();
            updateLog();
        });

        quickButtons.getChildren().addAll(btnAllOn, btnAllOff);
        roomControlPane.getChildren().add(quickButtons);

        GridPane roomGrid = new GridPane();
        roomGrid.setHgap(10);
        roomGrid.setVgap(5);
        roomGrid.setPadding(new Insets(5));

        int row = 0;
        for (Room room : List.of(livingRoom, kitchen)) {
            String name = room.getName();

            Button lightOn = createControlButton("Light ON", "#335c67");
            Button lightOff = createControlButton("Light OFF", "#5d635f");
            Button acOn = createControlButton("AC ON", "#335c67");
            Button acOff = createControlButton("AC OFF", "#5d635f");
            Button spkOn = createControlButton("Speaker ON", "#335c67");
            Button spkOff = createControlButton("Speaker OFF", "#5d635f");

            lightOn.setOnAction(e -> { room.turnOn("Light"); logger.log(name, "The Lights are on"); updateLog(); });
            lightOff.setOnAction(e -> { room.turnOff("Light"); logger.log(name, "The Lights are off"); updateLog(); });
            acOn.setOnAction(e -> { room.turnOn("AC"); logger.log(name, "The AC is turned on"); updateLog(); });
            acOff.setOnAction(e -> { room.turnOff("AC"); logger.log(name, "The AC is turned off"); updateLog(); });
            spkOn.setOnAction(e -> { room.turnOn("Speaker"); logger.log(name, "The Speaker is on"); updateLog(); });
            spkOff.setOnAction(e -> { room.turnOff("Speaker"); logger.log(name, "The Speaker is off"); updateLog(); });

            roomGrid.add(new Label(name), 0, row);
            roomGrid.addRow(++row, lightOn, lightOff, acOn, acOff, spkOn, spkOff);
            row++;
        }

        roomControlPane.getChildren().add(roomGrid);
        root.getChildren().add(roomControlPane);

        VBox logPane = new VBox(10);
        logPane.setPadding(new Insets(15));
        logPane.setStyle("-fx-background-color: #335c67; -fx-background-radius: 10;");

        Label logLabel = new Label("Action history:");
        logLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        logLabel.setStyle("-fx-text-fill: white;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(120);
        logArea.setStyle("-fx-control-inner-background: #f9f9f9;");

        logPane.getChildren().addAll(logLabel, logArea);
        root.getChildren().add(logPane);

        root.getChildren().add(buildMusicPlayerUI(stage));

        Scene scene = new Scene(root, 650, 750);
        stage.setScene(scene);
        stage.setTitle("Smart Home");
        stage.setOnCloseRequest(e -> {
            musicPlayer.savePlaylistToFile(playlistFile);
            if (timer != null) timer.stop();
        });
        stage.show();
    }

    private BorderPane buildMusicPlayerUI(Stage stage) {
        BorderPane musicUI = new BorderPane();
        musicUI.setPadding(new Insets(15));
        musicUI.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");

        VBox playerPane = new VBox(15);
        playerPane.setAlignment(Pos.TOP_CENTER);

        Label header = new Label("Music Player");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        header.setTextFill(Color.web("#2c3e50"));

        HBox fileButtons = new HBox(10);
        fileButtons.setAlignment(Pos.CENTER);

        Button btnLoad = createStyledButton("Upload Musics", "#335c67");
        Button btnDelete = createStyledButton("Delete Selected", "#e74c3c");

        fileButtons.getChildren().addAll(btnLoad, btnDelete);

        VBox trackInfo = new VBox(5);
        trackInfo.setAlignment(Pos.CENTER);
        songLabel.setMaxWidth(Double.MAX_VALUE);
        songLabel.setAlignment(Pos.CENTER);
        trackInfo.getChildren().addAll(songLabel, timeLabel);

        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);

        Button btnPrev = createIconButton("◀◀", "#335c67", 30);
        Button btnPlay = createIconButton("▶", "#335c67", 40);
        Button btnPause = createIconButton("⏸", "#335c67", 40);
        Button btnNext = createIconButton("▶▶", "#335c67", 30);

        controls.getChildren().addAll(btnPrev, btnPlay, btnPause, btnNext);

        playerPane.getChildren().addAll(header, fileButtons, new Separator(), trackInfo, controls);
        musicUI.setLeft(playerPane);

        songList.setPrefWidth(250);
        songList.setPrefHeight(220);
        songList.setStyle("-fx-control-inner-background: #f9f9f9; -fx-border-color: #eeeeee; -fx-border-radius: 5;");

        VBox playlistPane = new VBox(10, new Label("Music List"), songList);
        playlistPane.setAlignment(Pos.TOP_LEFT);
        playlistPane.setPadding(new Insets(10));
        playlistPane.setStyle("-fx-background-color: #f4f4f4; -fx-border-radius: 10;");
        musicUI.setRight(playlistPane);
        BorderPane.setMargin(playlistPane, new Insets(0, 0, 0, 15));

        btnLoad.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose mp3 files");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
            List<File> files = chooser.showOpenMultipleDialog(stage);
            if (files != null) {
                musicPlayer.loadPlaylist(files);
                musicPlayer.savePlaylistToFile(playlistFile);
                refreshPlaylistDisplay();
            }
        });

        btnPlay.setOnAction(e -> {
            musicPlayer.play();
            songLabel.setText("▶ " + musicPlayer.getCurrentTrackName());
        });

        btnPause.setOnAction(e -> {
            musicPlayer.pause();
            songLabel.setText("⏸ " + musicPlayer.getCurrentTrackName());
        });

        btnNext.setOnAction(e -> {
            musicPlayer.next();
            songLabel.setText("▶ " + musicPlayer.getCurrentTrackName());
        });

        btnPrev.setOnAction(e -> {
            musicPlayer.prev();
            songLabel.setText("▶ " + musicPlayer.getCurrentTrackName());
        });

        btnDelete.setOnAction(e -> {
            int selectedIndex = songList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && musicPlayer.getPlaylist() != null && selectedIndex < musicPlayer.getPlaylist().size()) {
                musicPlayer.getPlaylist().remove(selectedIndex);
                musicPlayer.savePlaylistToFile(playlistFile);
                refreshPlaylistDisplay();
                songLabel.setText("Current Song: " + musicPlayer.getCurrentTrackName());
            }
        });

        refreshPlaylistDisplay();
        startTimer();

        return musicUI;
    }

    private Button createControlButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10;");
        return button;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15;");
        return button;
    }

    private Button createIconButton(String text, String color, double size) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: " + (size / 2) + "px; " +
                "-fx-min-width: " + size + "px; -fx-min-height: " + size + "px; -fx-background-radius: " + (size / 2) + "px;");
        return button;
    }

    private void refreshPlaylistDisplay() {
        songList.getItems().clear();
        if (musicPlayer.getPlaylist() != null) {
            musicPlayer.getPlaylist().forEach(f -> songList.getItems().add(f.getName()));
        }
        songLabel.setText("Current song: " + musicPlayer.getCurrentTrackName());
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimeLabel()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimeLabel() {
        Duration current = musicPlayer.getCurrentTime();
        Duration total = musicPlayer.getTotalDuration();
        String currentStr = formatDuration(current);
        String totalStr = formatDuration(total);
        timeLabel.setText(currentStr + " / " + totalStr);
    }

    private String formatDuration(Duration dur) {
        if (dur == null || dur.isUnknown() || dur.lessThan(Duration.ZERO)) {
            return "00:00";
        }
        int totalSeconds = (int) dur.toSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void updateLog() {
        logArea.clear();
        smartHome.getLogger().getLogs().forEach(line -> logArea.appendText(line + "\n"));
    }
}
