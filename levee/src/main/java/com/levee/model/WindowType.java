package com.levee.model;

public enum WindowType {
    PER_MINUTE("PER_MINUTE"),
    PER_HOUR("PER_HOUR"),
    PER_DAY("PER_DAY");

    private final String windowType;

    WindowType(String windowType) {
        this.windowType = windowType;
    }

    public String getWindowType() {
        return this.windowType;
    }
}
