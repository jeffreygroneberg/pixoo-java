# Getting Started with Pixoo Java Library

This guide will help you get started with the Pixoo Java library quickly.

## Prerequisites

- Java 11 or higher
- Maven (for building)
- A Divoom Pixoo device connected to your network

## Quick Setup

### 1. Add to Your Project

If using Maven, add this dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.pixoo</groupId>
    <artifactId>pixoo-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Find Your Device IP

First, you need to find your Pixoo device's IP address. You can:
- Check your router's device list
- Use network scanning tools
- Check the device settings if available

### 3. Basic Usage

```java
import com.pixoo.objects.Pixoo;
import com.pixoo.constants.Palette;

public class MyFirstPixooApp {
    public static void main(String[] args) {
        // Replace with your device's IP address
        Pixoo pixoo = new Pixoo("192.168.1.137");
        
        // Clear the screen
        pixoo.clear(Palette.BLACK);
        
        // Draw a red pixel
        pixoo.drawPixel(32, 32, Palette.RED);
        
        // Draw some text
        pixoo.drawText("Hello!", 10, 10, Palette.BLUE);
        
        // Push changes to display
        pixoo.push();
    }
}
```

## Running the Demo

The library includes a demo application:

```bash
# Compile the project
mvn compile

# Run the demo (replace IP with your device's IP)
mvn exec:java -Dexec.mainClass="com.pixoo.demo.PixooDemo" -Dexec.args="192.168.1.137"
```

## Common Operations

### Drawing Shapes

```java
// Clear screen
pixoo.clear(Palette.BLACK);

// Draw pixels
pixoo.drawPixel(x, y, Palette.RED);
pixoo.drawPixelAtLocationRgb(x, y, 255, 0, 0);

// Draw lines
pixoo.drawLine(0, 0, 63, 63, Palette.WHITE);

// Draw rectangles
pixoo.drawFilledRectangle(10, 10, 20, 20, Palette.BLUE);
```

### Text and Characters

```java
// Draw text using built-in font
pixoo.drawText("Hello World", 0, 0, Palette.GREEN);

// Draw individual characters
pixoo.drawCharacter('A', 10, 10, Palette.YELLOW);

// Send scrolling text to device
pixoo.sendText("Scrolling Text", 0, 32, Palette.WHITE, 1, 2, 64, 1, TextScrollDirection.LEFT);
```

### Device Control

```java
// Control brightness (0-100)
pixoo.setBrightness(75);

// Control screen
pixoo.setScreenOff();
pixoo.setScreenOn();

// Play sounds
pixoo.soundBuzzer(500, 500, 3000);
```

### Animation

```java
for (int i = 0; i < 64; i++) {
    pixoo.clear(Palette.BLACK);
    pixoo.drawPixel(i, 32, Palette.RED);
    pixoo.push();
    
    try {
        Thread.sleep(50);
    } catch (InterruptedException e) {
        break;
    }
}
```

## Important Notes

1. **Always call `push()`** after drawing operations to display changes
2. **Don't call `push()` too frequently** - limit to once per second to avoid overwhelming the device
3. **Handle exceptions** when working with network operations
4. **Use simulation mode** for testing without a physical device

## Simulation Mode

For development and testing without a physical device:

```java
Pixoo pixoo = new Pixoo(null, 64, true, true, true, new SimulatorConfiguration(4));
```

## Error Handling

```java
try {
    Pixoo pixoo = new Pixoo("192.168.1.137", 64, true, true, false, null);
    
    pixoo.clear(Palette.BLACK);
    pixoo.drawText("Hello", 0, 0, Palette.WHITE);
    pixoo.push();
    
} catch (Exception e) {
    System.err.println("Error communicating with Pixoo: " + e.getMessage());
}
```

## Next Steps

- Check out the full [README.md](README.md) for complete API documentation
- Look at the [PixooExample.java](src/main/java/com/pixoo/examples/PixooExample.java) for more examples
- Run the [PixooDemo.java](src/main/java/com/pixoo/demo/PixooDemo.java) to see the library in action
- Explore the test files for additional usage patterns

## Troubleshooting

### "Connection error"
- Verify your device IP address
- Ensure the device is on the same network
- Check that the device is powered on

### "No response"
- Try reducing the frequency of `push()` calls
- Enable debug mode to see detailed error messages
- Consider using the auto-refresh feature

### "Class not found"
- Ensure all dependencies are correctly installed
- Check your classpath includes the Pixoo JAR file

For more help, check the issues in the project repository or refer to the main documentation.
