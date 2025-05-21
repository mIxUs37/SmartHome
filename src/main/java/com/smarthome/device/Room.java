package com.smarthome.device;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private final String name;
    private final Map<String, Device> devices = new HashMap<>();

    public Room(String name) {
        this.name = name;
    }

    public void addDevice(String type, Device device) {
        devices.put(type, device);
    }

    public void turnOn(String type) {
        if (devices.containsKey(type)) {
            devices.get(type).turnOn();
        }
    }

    public void turnOff(String type) {
        if (devices.containsKey(type)) {
            devices.get(type).turnOff();
        }
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder(name + ": ");
        for (Map.Entry<String, Device> entry : devices.entrySet()) {
            sb.append(entry.getKey()).append(" = ").append(entry.getValue().getStatus()).append("; ");
        }
        return sb.toString();
    }

    public String getName() {
        return name;
    }
}