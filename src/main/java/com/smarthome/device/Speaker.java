package com.smarthome.device;

public class Speaker implements Device {
    private boolean isOn = false;

    public void turnOn() { isOn = true; }

    public void turnOff() { isOn = false; }

    public String getStatus() {
        return isOn ? "ON" : "OFF";
    }
}