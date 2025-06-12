package de.jeffreygroneberg.pixooj.constants;

/**
 * Color palette constants for Pixoo devices.
 */
public class Palette {
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color MAGENTA = new Color(255, 0, 255);
    
    /**
     * RGB Color class
     */
    public static class Color {
        private final int red;
        private final int green;
        private final int blue;
        
        public Color(int red, int green, int blue) {
            this.red = clamp(red);
            this.green = clamp(green);
            this.blue = clamp(blue);
        }
        
        public int getRed() { return red; }
        public int getGreen() { return green; }
        public int getBlue() { return blue; }
        
        public int[] toArray() {
            return new int[]{red, green, blue};
        }
        
        public String toHex() {
            return String.format("#%02X%02X%02X", red, green, blue);
        }
        
        public int toInt() {
            return (red << 16) | (green << 8) | blue;
        }
        
        private int clamp(int value) {
            return Math.max(0, Math.min(255, value));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Color)) return false;
            Color other = (Color) obj;
            return red == other.red && green == other.green && blue == other.blue;
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(red, green, blue);
        }
        
        @Override
        public String toString() {
            return String.format("Color(r=%d, g=%d, b=%d)", red, green, blue);
        }
    }
}
