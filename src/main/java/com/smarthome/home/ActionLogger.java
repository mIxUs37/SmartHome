package com.smarthome.home;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class ActionLogger {
    private final List<String> logs = new LinkedList<>();

    public void log(String room, String action) {
        logs.add("[" + LocalDateTime.now() + "] " + room + ": " + action);
    }

    public List<String> getLogs() {
        return logs;
    }
}