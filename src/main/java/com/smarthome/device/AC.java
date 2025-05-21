package com.smarthome.device;

public class AC implements Device {
    private boolean isOn = false;

    @Override
    public void turnOn() { isOn = true; }

    @Override
    public void turnOff() { isOn = false; }

    @Override
    public String getStatus() {
        return isOn ? "ON" : "OFF";
    }
}