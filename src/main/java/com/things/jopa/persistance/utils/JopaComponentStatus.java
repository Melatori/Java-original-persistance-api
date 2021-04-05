package com.things.jopa.persistance.utils;

public enum JopaComponentStatus {
    NOT_INITIALIZED(true),
    INITIALIZED(true),
    READY(true);
    private final boolean isLocked;
    JopaComponentStatus(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return this.isLocked;
    }
}
