package com.pixoo.demo;

import com.pixoo.constants.Palette;
import com.pixoo.enums.TextScrollDirection;
import com.pixoo.objects.Pixoo;

/**
 * Simple demo application for the Pixoo Java library.
 * Replace the IP address with your actual Pixoo device IP.
 */
public class PixooDemo {
    
    public static void main(String[] args) {
        // Replace this IP with your actual Pixoo device IP
        String pixooIp = "192.168.1.137";
        
        if (args.length > 0) {
            pixooIp = args[0];
        }
        
        System.out.println("Connecting to Pixoo at: " + pixooIp);
        
        // Create Pixoo instance
        Pixoo pixoo = new Pixoo(pixooIp, 64, true, true, false, null);
        
        // Run demos
        try {
            System.out.println("Running basic drawing demo...");
            basicDrawingDemo(pixoo);
            
            Thread.sleep(3000);
            
            System.out.println("Running text demo...");
            textDemo(pixoo);
            
            Thread.sleep(3000);
            
            System.out.println("Running animation demo...");
            animationDemo(pixoo);
            
            System.out.println("Demo completed!");
            
        } catch (InterruptedException e) {
            System.err.println("Demo interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    private static void basicDrawingDemo(Pixoo pixoo) {
        // Clear screen
        pixoo.clear(Palette.BLACK);
        
        // Draw some colored pixels
        pixoo.drawPixel(10, 10, Palette.RED);
        pixoo.drawPixel(11, 10, Palette.GREEN);
        pixoo.drawPixel(12, 10, Palette.BLUE);
        
        // Draw a diagonal line
        pixoo.drawLine(0, 0, 20, 20, Palette.WHITE);
        
        // Draw a filled rectangle
        pixoo.drawFilledRectangle(30, 30, 40, 40, Palette.YELLOW);
        
        // Draw another rectangle with RGB values
        pixoo.drawFilledRectangleFromTopLeftToBottomRightRgb(45, 30, 55, 40, 255, 0, 255);
        
        // Push to display
        pixoo.push();
    }
    
    private static void textDemo(Pixoo pixoo) {
        // Clear screen
        pixoo.clear(Palette.BLACK);
        
        // Draw text using built-in font
        pixoo.drawText("Hello", 5, 5, Palette.RED);
        pixoo.drawText("Java!", 5, 15, Palette.BLUE);
        pixoo.drawText("Pixoo", 5, 25, Palette.GREEN);
        
        // Draw individual characters
        pixoo.drawCharacter('A', 5, 35, Palette.CYAN);
        pixoo.drawCharacter('B', 10, 35, Palette.MAGENTA);
        pixoo.drawCharacter('C', 15, 35, Palette.YELLOW);
        
        // Send scrolling text (this uses the device's built-in text rendering)
        pixoo.sendText("Java Pixoo Library!", 0, 50, Palette.WHITE, 1, 2, 64, 1, TextScrollDirection.LEFT);
        
        // Push static elements to display
        pixoo.push();
    }
    
    private static void animationDemo(Pixoo pixoo) {
        // Animate a bouncing pixel
        int x = 0;
        int y = 32;
        int dx = 1;
        int dy = 1;
        
        for (int frame = 0; frame < 200; frame++) {
            // Clear screen
            pixoo.clear(Palette.BLACK);
            
            // Draw bouncing pixel
            pixoo.drawPixel(x, y, Palette.RED);
            
            // Add a trail
            if (x > 0 && y > 0) {
                pixoo.drawPixel(x - dx, y - dy, new Palette.Color(128, 0, 0));
            }
            
            // Update position
            x += dx;
            y += dy;
            
            // Bounce off edges
            if (x <= 0 || x >= 63) dx = -dx;
            if (y <= 0 || y >= 63) dy = -dy;
            
            // Keep in bounds
            x = Math.max(0, Math.min(63, x));
            y = Math.max(0, Math.min(63, y));
            
            // Push to display
            pixoo.push();
            
            // Wait a bit
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
