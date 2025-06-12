package de.jeffreygroneberg.pixooj;

import de.jeffreygroneberg.pixooj.objects.Pixoo;
import de.jeffreygroneberg.pixooj.enums.Channel;
import de.jeffreygroneberg.pixooj.enums.TextScrollDirection;
import de.jeffreygroneberg.pixooj.constants.Palette;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PixooTest {

    private Pixoo pixoo;
    private final boolean runDeviceTests = true;

    @BeforeEach
    void setUp() {
        Properties prop = new Properties();
        String deviceIpAddress = null; // Initialize to null
        try (InputStream input = PixooTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("WARNING: config.properties not found in classpath. Tests requiring device IP may fail or be skipped.");
            } else {
                prop.load(input);
                deviceIpAddress = prop.getProperty("ip.address");
                if (deviceIpAddress == null || deviceIpAddress.trim().isEmpty()) {
                    System.err.println("WARNING: ip.address not found in config.properties. Tests requiring device IP may fail or be skipped.");
                    deviceIpAddress = null; // Ensure it's null if empty
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("WARNING: IOException while reading config.properties. Tests requiring device IP may fail or be skipped.");
        }

        // If deviceIpAddress is still null, tests requiring it should handle this (e.g. by being skipped or asserting specific behavior)
        // For now, we proceed to initialize Pixoo. If deviceIpAddress is null, Pixoo constructor might throw an error or handle it.
        // Alternatively, conditionally initialize or skip tests based on deviceIpAddress availability.
        if (deviceIpAddress != null) {
            pixoo = new Pixoo(deviceIpAddress, 64, runDeviceTests, true, !runDeviceTests, null);
            System.out.println("Pixoo instance created for: " + pixoo.getUrlString() + ", Simulated: " + pixoo.isSimulated());
        } else {
            // Optionally, create a simulated Pixoo instance if IP is not available but tests need a Pixoo object
            // pixoo = new Pixoo("127.0.0.1", 64, false, true, true, new SimulatorConfiguration()); // Example fallback
            System.out.println("Pixoo instance could not be created with a device IP. Tests requiring a device will likely fail or be skipped.");
            // It's better to let tests that require `pixoo` to be non-null and configured to fail if it's not, 
            // or use Assume.assumeNotNull(pixoo) in those tests.
        }
    }

    @Test
    void testPixooCreation() {
        assertNotNull(pixoo, "Pixoo object should not be null.");
        assertEquals(!runDeviceTests, pixoo.isSimulated(), "Pixoo simulated state should match inverted runDeviceTests flag.");
        System.out.println("testPixooCreation: PASSED - Pixoo object created. Simulated: " + pixoo.isSimulated());
    }

    @Test
    void testFillAndPush() {
        pixoo.fill(Palette.BLUE);
        pixoo.push();
        System.out.println("testFillAndPush: Executed fill with BLUE and push. Verify on device if not simulated.");
    }

    @Test
    void testClearAndPush() {
        pixoo.clear(Palette.BLACK); // Pixoo.clear expects a color
        pixoo.push();
        System.out.println("testClearAndPush: Executed clear (to BLACK) and push. Verify on device if not simulated.");
    }
    
    @Test
    void testFillRgbAndPush() {
        pixoo.fillRgb(255, 0, 0); // Fill with red
        pixoo.push();
        System.out.println("testFillRgbAndPush: Executed fillRgb with RED and push. Verify on device if not simulated.");
    }

    @Test
    void testSetPixelAndPush() {
        pixoo.drawPixel(0, 0, Palette.GREEN); // Changed from setPixel and java.awt.Color
        pixoo.drawPixel(10,10, new Palette.Color(255,0,0)); // Changed from setPixel and direct RGB
        pixoo.push();
        System.out.println("testSetPixelAndPush: Executed drawPixel (0,0) GREEN, (10,10) RED and push. Verify on device if not simulated.");
    }
    
    @Test
    void testDrawTextAndPush() {
        // Pixoo.drawText uses custom font rendering, not a font ID for the device's internal text engine.
        // The Font.FONT_PICO_8 is a map for character glyphs.
        pixoo.drawText("Hi", 0, 0, Palette.YELLOW); // Removed Font.FONT_PICO_8, Pixoo.drawText handles it internally
        pixoo.push();
        System.out.println("testDrawTextAndPush: Executed drawText 'Hi' at (0,0) YELLOW and push. Verify on device if not simulated.");
    }

    // Device control tests
    @Test
    void testSetBrightness() {
        pixoo.setBrightness(50); 
        System.out.println("testSetBrightness: Executed setBrightness to 50. Verify on device if not simulated.");
    }

    @Test
    void testSetChannel() {
        pixoo.setChannel(Channel.FACES.getValue()); // Pixoo.setChannel expects an int
        System.out.println("testSetChannel: Executed setChannel to FACES. Verify on device if not simulated.");
    }
    
    @Test
    @Disabled("Need to verify if this command has immediate visible effect or requires further interaction")
    void testSetClock() {
        pixoo.setClock(1); 
        System.out.println("testSetClock: Executed setClock to ID 1. Verify on device if not simulated.");
    }

    @Test
    @Disabled("Disabling to avoid rapid screen changes during general testing. EqPosition enum not available yet.")
    void testSetVisualizer() {
        // pixoo.setVisualizer(EqPosition.FULL_SCREEN_PYRAMID_FULL_COLOR.getValue()); // EqPosition not available
        pixoo.setVisualizer(0); // Example: set to first visualizer type
        System.out.println("testSetVisualizer: Executed setVisualizer. Verify on device if not simulated.");
    }
    
    @Test
    void testSendText() {
        // Pixoo.sendText expects an integer font ID (e.g., 0-7 or specific values based on device firmware)
        // Font.FONT_PICO_8 is for custom character drawing, not this command.
        pixoo.sendText("Scroll", 0, 0, Palette.CYAN, 1, 2, 64, 100, TextScrollDirection.LEFT); // Using font ID 2 as an example
        System.out.println("testSendText: Executed sendText 'Scroll'. Verify on device if not simulated.");
    }

    @Test
    @Disabled("Requires a GIF file; ensure 'assets/test.gif' exists or provide a valid path")
    void testSendAnimatedGif() {
        String gifPath = "assets/test.gif"; 
        try {
            pixoo.sendAnimatedGif(gifPath, 100);
            System.out.println("testSendAnimatedGif: Executed sendAnimatedGif with " + gifPath + ". Verify on device.");
        } catch (IOException e) {
            fail("IOException during sendAnimatedGif: " + e.getMessage());
        }
    }
    
    @Test
    @Disabled("This test interacts with network resources and might be slow or fail due to network issues.")
    void testPlayNetGif() {
        String netGifUrl = "https://media.giphy.com/media/3o7TKSxR2702vY71f2/giphy.gif";
        pixoo.playNetGif(netGifUrl);
        System.out.println("testPlayNetGif: Executed playNetGif with " + netGifUrl + ". Verify on device.");
    }

     @Test
    void testSetScreenPowerState() {
        pixoo.setScreen(true); // Changed from setScreenPowerState
        System.out.println("testSetScreenPowerState: Screen ON command sent.");
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        pixoo.setScreen(false); // Changed from setScreenPowerState
        System.out.println("testSetScreenPowerState: Screen OFF command sent.");
    }

    @Test
    @Disabled("resetHttpGifId is a private method (resetCounter) in Pixoo.java and not directly testable as a public API.")
    void testResetHttpGifId() {
        // pixoo.resetHttpGifId(); // Method not available publicly
        System.out.println("testResetHttpGifId: Test disabled as method is not public.");
    }
    
    @Test
    void testGetAllSettings() {
        // Ensure pixoo is initialized before using it, especially if setUp might not initialize it due to missing config
        if (pixoo == null) {
            System.out.println("testGetAllSettings: SKIPPED - Pixoo instance is null (likely due to missing configuration).");
            // Optionally, use JUnit's Assumptions.assumeNotNull(pixoo); to formally skip the test
            return; 
        }
        // Changed from getAllSettings to getAllDeviceConfigurations
        Object settings = pixoo.getAllDeviceConfigurations(); 
        assertNotNull(settings, "Settings object should not be null.");
        // settings is guaranteed to be non-null here due to assertNotNull above.
        System.out.println("testGetAllSettings: Received settings: " + settings.toString());
    }
}
