package de.jeffreygroneberg.pixooj.enums;

/**
 * Enumeration for text scroll directions on Pixoo devices.
 */
public enum TextScrollDirection {
    LEFT(0),
    RIGHT(1);
    
    private final int value;
    
    TextScrollDirection(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
