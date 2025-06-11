# Pixoo Java Library

A Java library for communicating with Divoom Pixoo devices (Pixoo 16, 32, and 64).

This is a port of the original Python Pixoo library, providing the same functionality for Java applications.

## Features

- **Drawing Operations**: Draw pixels, lines, rectangles, text, and images
- **Device Control**: Control brightness, channels, clocks, and other device settings
- **Text Rendering**: Built-in PICO-8 style font with support for multiple character sets
- **Image Support**: Load and display images with automatic scaling
- **Animation Support**: Create smooth animations by updating the display buffer
- **Device Management**: Control device settings like brightness, channels, and more

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.pixoo</groupId>
    <artifactId>pixoo-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Build from Source

1. Clone this repository
2. Navigate to the `java` directory
3. Run `mvn clean install`

## Quick Start

```java
import com.pixoo.objects.Pixoo;
import com.pixoo.constants.Palette;

public class MyPixooApp {
    public static void main(String[] args) {
        // Create a Pixoo instance (replace with your device's IP)
        Pixoo pixoo = new Pixoo("192.168.1.137");
        
        // Clear the screen
        pixoo.clear(Palette.BLACK);
        
        // Draw some pixels
        pixoo.drawPixel(10, 10, Palette.RED);
        pixoo.drawLine(0, 0, 20, 20, Palette.WHITE);
        
        // Draw text
        pixoo.drawText("Hello Java!", 5, 30, Palette.BLUE);
        
        // Push the buffer to display the changes
        pixoo.push();
    }
}
```

## API Documentation

### Creating a Pixoo Instance

```java
// Basic constructor (auto-discover device)
Pixoo pixoo = new Pixoo();

// With IP address
Pixoo pixoo = new Pixoo("192.168.1.137");

// Full constructor with all options
Pixoo pixoo = new Pixoo(
    "192.168.1.137",  // IP address
    64,               // Screen size (16, 32, or 64)
    true,             // Debug mode
    true,             // Auto-refresh connection
    false,            // Simulation mode
    new SimulatorConfiguration(4)  // Simulator config
);
```

### Drawing Operations

```java
// Clear screen
pixoo.clear(Palette.BLACK);
pixoo.clearRgb(0, 0, 0);

// Draw pixels
pixoo.drawPixel(x, y, Palette.RED);
pixoo.drawPixelAtLocationRgb(x, y, 255, 0, 0);

// Draw lines
pixoo.drawLine(startX, startY, endX, endY, Palette.WHITE);
pixoo.drawLineFromStartToStopRgb(0, 0, 10, 10, 255, 255, 255);

// Draw rectangles
pixoo.drawFilledRectangle(x1, y1, x2, y2, Palette.BLUE);
pixoo.drawFilledRectangleFromTopLeftToBottomRightRgb(0, 0, 10, 10, 0, 0, 255);

// Draw text
pixoo.drawText("Hello", x, y, Palette.GREEN);
pixoo.drawTextAtLocationRgb("World", x, y, 0, 255, 0);

// Draw characters
pixoo.drawCharacter('A', x, y, Palette.YELLOW);
pixoo.drawCharacterAtLocationRgb('B', x, y, 255, 255, 0);
```

### Image Operations

```java
// Draw image from file
pixoo.drawImage("path/to/image.png", x, y, ImageResampleMode.PIXEL_ART);

// Draw BufferedImage
BufferedImage img = ImageIO.read(new File("image.png"));
pixoo.drawImage(img, new MathUtils.Point(x, y), ImageResampleMode.SMOOTH, false);
```

### Device Control

```java
// Control display
pixoo.setBrightness(50);           // Set brightness (0-100)
pixoo.setScreen(true);             // Turn screen on/off
pixoo.setScreenOn();               // Turn screen on
pixoo.setScreenOff();              // Turn screen off

// Channels and content
pixoo.setChannel(Channel.FACES.getValue());  // Switch to faces channel
pixoo.setClock(1);                 // Set clock face
pixoo.setVisualizer(0);            // Set audio visualizer

// Text scrolling
pixoo.sendText(
    "Scrolling Text!",             // Text
    0, 32,                         // Position
    Palette.WHITE,                 // Color
    1,                             // Identifier (0-19)
    2,                             // Font size
    64,                            // Width
    1,                             // Speed
    TextScrollDirection.LEFT       // Direction
);

// Other controls
pixoo.soundBuzzer(500, 500, 3000); // Play buzzer
pixoo.setWhiteBalance(100, 100, 100); // Set white balance
pixoo.reboot();                    // Reboot device
```

### Buffer Management

**Important**: Always call `push()` after drawing operations to display your changes!

```java
// Draw something
pixoo.drawPixel(10, 10, Palette.RED);
pixoo.drawText("Hello", 0, 0, Palette.WHITE);

// Push changes to display
pixoo.push();
```

## Colors

The library includes a built-in color palette:

```java
// Predefined colors
Palette.BLACK
Palette.WHITE
Palette.RED
Palette.GREEN
Palette.BLUE
Palette.YELLOW
Palette.CYAN
Palette.MAGENTA

// Custom colors
Palette.Color customColor = new Palette.Color(128, 64, 255);
```

## Font Support

The library includes a PICO-8 style font that supports:

- Numbers: `0123456789`
- Lowercase: `abcdefghijklmnopqrstuvwxyz`
- Uppercase: `ABCDEFGHIJKLMNOPQRSTUVWXYZ`
- Special characters: `!'()+,-<=>?[]^_:;./{|}~$@%`

```java
// Check if character is supported
boolean supported = Font.isCharacterSupported('A');

// Get supported characters
Set<Character> chars = Font.getSupportedCharacters();
```

## Animation Example

```java
public void animatePixel(Pixoo pixoo) {
    for (int x = 0; x < 64; x++) {
        pixoo.clear(Palette.BLACK);
        pixoo.drawPixel(x, 32, Palette.RED);
        pixoo.push();
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            break;
        }
    }
}
```

## Error Handling

The library includes built-in error handling. Enable debug mode to see detailed error messages:

```java
Pixoo pixoo = new Pixoo("192.168.1.137", 64, true, true, false, null);
```

## Simulation Mode

For testing without a physical device, enable simulation mode:

```java
Pixoo pixoo = new Pixoo(null, 64, true, true, true, new SimulatorConfiguration(4));
```

## Requirements

- Java 11 or higher
- Network connection to Pixoo device
- Dependencies (automatically handled by Maven):
  - OkHttp for HTTP communication
  - Gson for JSON processing
  - ImgScalr for image processing

## Device Compatibility

Tested with:
- Pixoo 64
- Should work with Pixoo 16 and Pixoo 32 (specify size in constructor)

## License

This project follows the same license as the original Python library:
Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Troubleshooting

### Connection Issues
- Ensure your device is on the same network
- Check the IP address is correct
- Verify the device is powered on and connected to WiFi

### Performance
- Don't call `push()` more than once per second to avoid overwhelming the device
- Use the auto-refresh feature to prevent connection timeouts
- Consider using simulation mode for development and testing

### Memory
- Large images are automatically scaled down to fit the display
- The library manages its own pixel buffer efficiently

## Examples

See the `com.pixoo.examples` package for complete working examples including:
- Basic drawing operations
- Text rendering
- Animation
- Device control

For more examples and advanced usage, check the examples directory in the project repository.
