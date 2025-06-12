package de.jeffreygroneberg.pixooj.enums;

/**
 * Enumeration for channel types on Pixoo devices.
 */
public enum Channel {
    FACES(0),
    CLOUD(1),
    VISUALIZER(2),
    CUSTOM(3);
    
    private final int value;
    
    Channel(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
