package com.smarthome.ui;

import com.smarthome.device.*;
import com.smarthome.home.ActionLogger;
import com.smarthome.home.SmartHomeFacade;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class SmartHomeApp extends Application {

    private SmartHomeFacade smartHome;
    private ActionLogger logger;
    private TextArea logArea;
    private Label songLabel;
    private File playlistFile = new File("playlist.txt");

    @Override
    public void start(Stage stage) {
        logger = new ActionLogger();

        Room livingRoom = new Room("Гостиная");
        Room kitchen = new Room("Кухня");

        livingRoom.addDevice("Light", new Light());
        livingRoom.addDevice("AC", new AC());
        livingRoom.addDevice("Speaker", new Speaker());

        kitchen.addDevice("Light", new Light());
        kitchen.addDevice("AC", new AC());
        kitchen.addDevice("Speaker", new Speaker());

        MusicPlayer musicPlayer = new MusicPlayer();
        musicPlayer.loadPlaylistFromFile(playlistFile);

        smartHome = new SmartHomeFacade(livingRoom, kitchen, musicPlayer, logger);

        VBox topPane = new VBox(10);
        topPane.setPadding(new Insets(10));
        Label roomLabel = new Label("Управление комнатами:");

        // ВКЛ / ВЫКЛ ВСЁ
        HBox quickButtons = new HBox(10);
        Button btnAllOn = new Button("Включить всё");
        Button btnAllOff = new Button("Выключить всё");

        btnAllOn.setOnAction(e -> {
            smartHome.turnAllOn();
            updateLog();
        });

        btnAllOff.setOnAction(e -> {
            smartHome.turnAllOff();
            updateLog();
        });

        quickButtons.getChildren().addAll(btnAllOn, btnAllOff);

        GridPane roomControl = new GridPane();
        roomControl.setHgap(10);
        roomControl.setVgap(5);

        int row = 0;
        for (Room room : List.of(livingRoom, kitchen)) {
            String name = room.getName();

            Button lightOn = new Button("Свет ON");
            Button lightOff = new Button("Свет OFF");
            Button acOn = new Button("AC ON");
            Button acOff = new Button("AC OFF");
            Button spkOn = new Button("Динамик ON");
            Button spkOff = new Button("Динамик OFF");

            lightOn.setOnAction(e -> { room.turnOn("Light"); logger.log(name, "включен свет"); updateLog(); });
            lightOff.setOnAction(e -> { room.turnOff("Light"); logger.log(name, "выключен свет"); updateLog(); });
            acOn.setOnAction(e -> { room.turnOn("AC"); logger.log(name, "включен кондиционер"); updateLog(); });
            acOff.setOnAction(e -> { room.turnOff("AC"); logger.log(name, "выключен кондиционер"); updateLog(); });
            spkOn.setOnAction(e -> { room.turnOn("Speaker"); logger.log(name, "включен динамик"); updateLog(); });
            spkOff.setOnAction(e -> { room.turnOff("Speaker"); logger.log(name, "выключен динамик"); updateLog(); });

            roomControl.add(new Label(name), 0, row);
            roomControl.addRow(++row, lightOn, lightOff, acOn, acOff, spkOn, spkOff);
            row++;
        }

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(120);

        topPane.getChildren().addAll(roomLabel, quickButtons, roomControl, new Label("История действий:"), logArea);

        VBox bottomPane = new VBox(10);
        bottomPane.setPadding(new Insets(10));

        Button btnLoad = new Button("Загрузить треки");
        Button btnPlay = new Button("▶");
        Button btnPause = new Button("⏸");
        Button btnNext = new Button("▶▶");
        Button btnPrev = new Button("◀◀");

        songLabel = new Label("Текущий трек: -");
        ListView<String> songList = new ListView<>();
        songList.setPrefHeight(120);

        btnLoad.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Выберите MP3 файлы");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
            List<File> files = chooser.showOpenMultipleDialog(stage);
            if (files != null) {
                musicPlayer.loadPlaylist(files);
                musicPlayer.savePlaylistToFile(playlistFile);
                songList.getItems().clear();
                musicPlayer.getPlaylist().forEach(f -> songList.getItems().add(f.getName()));
                songLabel.setText("Текущий трек: " + musicPlayer.getCurrentTrackName());
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

        songList.setOnMouseClicked(e -> {
            int index = songList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                musicPlayer.play(index);
                songLabel.setText("▶ " + musicPlayer.getCurrentTrackName());
            }
        });

        songList.getItems().clear();
        if (musicPlayer.getPlaylist() != null) {
            musicPlayer.getPlaylist().forEach(f -> songList.getItems().add(f.getName()));
            songLabel.setText("Текущий трек: " + musicPlayer.getCurrentTrackName());
        }

        HBox controls = new HBox(10, btnPrev, btnPlay, btnPause, btnNext);
        bottomPane.getChildren().addAll(new HBox(10, btnLoad), songLabel, songList, controls);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        splitPane.getItems().addAll(topPane, bottomPane);
        splitPane.setDividerPositions(0.5);

        Scene scene = new Scene(splitPane, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Умный Дом + Музыка");

        stage.setOnCloseRequest(e -> musicPlayer.savePlaylistToFile(playlistFile));
        stage.show();
    }

    private void updateLog() {
        logArea.clear();
        smartHome.getLogger().getLogs().forEach(line -> logArea.appendText(line + "\n"));
    }
}
