package de.jeffreygroneberg.pixooj.examples;

import de.jeffreygroneberg.pixooj.constants.Palette;
import de.jeffreygroneberg.pixooj.enums.Channel;
import de.jeffreygroneberg.pixooj.enums.ImageResampleMode;
import de.jeffreygroneberg.pixooj.enums.TextScrollDirection;
import de.jeffreygroneberg.pixooj.objects.Pixoo;

import java.io.IOException;

/**
 * Example usage of the Pixoo Java library.
 */
public class PixooExample {
    
    public static void main(String[] args) {
        // Create a Pixoo instance - replace with your device's IP address
        Pixoo pixoo = new Pixoo("192.168.1.137", 64, true, true, false, null);
        
        // Example 1: Basic drawing
        basicDrawingExample(pixoo);
        
        // Example 2: Text rendering
        textExample(pixoo);
        
        // Example 3: Image drawing (if you have an image file)
        imageExample(pixoo);
        
        // Example 4: Device control
        deviceControlExample(pixoo);
    }
    
    private static void basicDrawingExample(Pixoo pixoo) {
        System.out.println("Running basic drawing example...");
        
        // Clear the screen with black
        pixoo.clear(Palette.BLACK);
        
        // Draw some pixels
        pixoo.drawPixel(10, 10, Palette.RED);
        pixoo.drawPixel(11, 10, Palette.GREEN);
        pixoo.drawPixel(12, 10, Palette.BLUE);
        
        // Draw a line
        pixoo.drawLine(0, 0, 20, 20, Palette.WHITE);
        
        // Draw a filled rectangle
        pixoo.drawFilledRectangle(30, 30, 40, 40, Palette.YELLOW);
        
        // Push the buffer to display
        pixoo.push();
        
        // Wait a bit
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void textExample(Pixoo pixoo) {
        System.out.println("Running text example...");
        
        // Clear the screen
        pixoo.clear(Palette.BLACK);
        
        // Draw some text
        pixoo.drawText("Hello", 5, 5, Palette.RED);
        pixoo.drawText("World!", 5, 15, Palette.BLUE);
        
        // Draw individual characters
        pixoo.drawCharacter('A', 5, 25, Palette.GREEN);
        pixoo.drawCharacter('B', 10, 25, Palette.CYAN);
        pixoo.drawCharacter('C', 15, 25, Palette.MAGENTA);
        
        // Push to display
        pixoo.push();
        
        // Wait a bit
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void imageExample(Pixoo pixoo) {
        System.out.println("Running image example...");
        
        try {
            // Clear the screen
            pixoo.clear(Palette.BLACK);
            
            // Draw an image (you'll need to provide a valid image path)
            pixoo.drawImage("path/to/your/image.png", 0, 0, ImageResampleMode.PIXEL_ART);
            
            // Push to display
            pixoo.push();
            
            // Wait a bit
            Thread.sleep(3000);
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void deviceControlExample(Pixoo pixoo) {
        System.out.println("Running device control example...");
        
        // Set brightness
        pixoo.setBrightness(50);
        
        // Send scrolling text
        pixoo.sendText("Java Pixoo!", 0, 32, Palette.WHITE, 1, 2, 64, 1, TextScrollDirection.LEFT);
        
        // Wait a bit
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Set channel to faces
        pixoo.setChannel(Channel.FACES.getValue());
        
        // Wait a bit more
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Turn screen off and on
        pixoo.setScreenOff();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        pixoo.setScreenOn();
    }
    
    /**
     * Animation example showing a moving pixel.
     */
    public static void animationExample(Pixoo pixoo) {
        System.out.println("Running animation example...");
        
        int x = 0;
        int y = 32;
        
        for (int i = 0; i < 64; i++) {
            // Clear screen
            pixoo.clear(Palette.BLACK);
            
            // Draw moving pixel
            pixoo.drawPixel(x, y, Palette.RED);
            
            // Add a trail
            if (x > 0) pixoo.drawPixel(x - 1, y, new Palette.Color(64, 0, 0));
            if (x > 1) pixoo.drawPixel(x - 2, y, new Palette.Color(32, 0, 0));
            
            // Push to display
            pixoo.push();
            
            // Move pixel
            x++;
            if (x >= 64) x = 0;
            
            // Wait
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
