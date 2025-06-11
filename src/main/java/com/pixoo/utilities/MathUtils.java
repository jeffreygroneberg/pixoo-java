package com.pixoo.utilities;

/**
 * Utility class for mathematical operations and color handling.
 */
public class MathUtils {
    
    /**
     * Clamps a value between minimum and maximum bounds.
     * @param value The value to clamp
     * @param minimum The minimum bound (default 0)
     * @param maximum The maximum bound (default 255)
     * @return The clamped value
     */
    public static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
    
    /**
     * Clamps a value between 0 and 255 (for RGB color components).
     * @param value The value to clamp
     * @return The clamped value
     */
    public static int clamp(int value) {
        return clamp(value, 0, 255);
    }
    
    /**
     * Clamps RGB color values.
     * @param rgb Array of RGB values
     * @return Array of clamped RGB values
     */
    public static int[] clampColor(int[] rgb) {
        return new int[]{clamp(rgb[0]), clamp(rgb[1]), clamp(rgb[2])};
    }
    
    /**
     * Linear interpolation between two values.
     * @param start The starting value
     * @param end The ending value
     * @param interpolant The interpolation factor (0.0 to 1.0)
     * @return The interpolated value
     */
    public static double lerp(double start, double end, double interpolant) {
        return start + interpolant * (end - start);
    }
    
    /**
     * Linear interpolation between two 2D points.
     * @param xy1 The starting point
     * @param xy2 The ending point
     * @param interpolant The interpolation factor (0.0 to 1.0)
     * @return The interpolated point
     */
    public static Point lerpLocation(Point xy1, Point xy2, double interpolant) {
        return new Point(
            lerp(xy1.x, xy2.x, interpolant),
            lerp(xy1.y, xy2.y, interpolant)
        );
    }
    
    /**
     * Calculates the minimum number of steps needed to draw a line between two points.
     * @param xy1 The starting point
     * @param xy2 The ending point
     * @return The minimum number of steps
     */
    public static int minimumAmountOfSteps(Point xy1, Point xy2) {
        return Math.max(Math.abs((int)(xy1.x - xy2.x)), Math.abs((int)(xy1.y - xy2.y)));
    }
    
    /**
     * Converts RGB values to a hex color string.
     * @param rgb Array of RGB values
     * @return Hex color string (e.g., "#FF0000")
     */
    public static String rgbToHexColor(int[] rgb) {
        return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }
    
    /**
     * Rounds a 2D point to integer coordinates.
     * @param xy The point to round
     * @return The rounded point
     */
    public static Point roundLocation(Point xy) {
        return new Point(Math.round((float)xy.x), Math.round((float)xy.y));
    }
    
    /**
     * Simple 2D point class.
     */
    public static class Point {
        public final double x;
        public final double y;
        
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return String.format("Point(%.2f, %.2f)", x, y);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Point)) return false;
            Point other = (Point) obj;
            return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y);
        }
    }
}
