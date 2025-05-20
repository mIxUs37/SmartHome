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

    public void goodMorning() {
        livingRoom.turnOn("Light");
        livingRoom.turnOn("AC");
        kitchen.turnOn("Light");
        musicPlayer.play();

        logger.log("Гостиная", "Сценарий 'Утро': свет и кондиционер включены");
        logger.log("Кухня", "Сценарий 'Утро': свет включён");
        logger.log("Плеер", "Воспроизведение музыки");
    }

    public void leaveHome() {
        livingRoom.turnOff("Light");
        livingRoom.turnOff("AC");
        livingRoom.turnOff("Speaker");

        kitchen.turnOff("Light");
        kitchen.turnOff("AC");
        kitchen.turnOff("Speaker");

        musicPlayer.stop();

        logger.log("Гостиная", "Сценарий 'Ушел': все выключено");
        logger.log("Кухня", "Сценарий 'Ушел': все выключено");
        logger.log("Плеер", "Остановлен");
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
