package com.smarthome.home;

import com.smarthome.device.MusicPlayer;
import com.smarthome.device.Room;

public class SmartHomeFacade {

    private final Room livingRoom;
    private final Room kitchen;
    private final MusicPlayer musicPlayer;
    private final ActionLogger logger;

    public SmartHomeFacade(Room livingRoom, Room kitchen, MusicPlayer musicPlayer, ActionLogger logger) {
        this.livingRoom = livingRoom;
        this.kitchen = kitchen;
        this.musicPlayer = musicPlayer;
        this.logger = logger;
    }

    public void turnAllOn() {
        livingRoom.turnOn("Light");
        livingRoom.turnOn("AC");
        livingRoom.turnOn("Speaker");

        kitchen.turnOn("Light");
        kitchen.turnOn("AC");
        kitchen.turnOn("Speaker");

        musicPlayer.play();

        logger.log("Гостиная", "включены все устройства");
        logger.log("Кухня", "включены все устройства");
        logger.log("Плеер", "музыка запущена");
    }

    public void turnAllOff() {
        livingRoom.turnOff("Light");
        livingRoom.turnOff("AC");
        livingRoom.turnOff("Speaker");

        kitchen.turnOff("Light");
        kitchen.turnOff("AC");
        kitchen.turnOff("Speaker");

        musicPlayer.stop();

        logger.log("Гостиная", "всё выключено");
        logger.log("Кухня", "всё выключено");
        logger.log("Плеер", "музыка остановлена");
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public Room getLivingRoom() {
        return livingRoom;
    }

    public Room getKitchen() {
        return kitchen;
    }

    public ActionLogger getLogger() {
        return logger;
    }
}
