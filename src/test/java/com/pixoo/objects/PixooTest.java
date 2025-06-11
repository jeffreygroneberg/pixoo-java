package com.pixoo.objects;

import com.pixoo.constants.Font;
import com.pixoo.constants.Palette;
import com.pixoo.enums.TextScrollDirection;
import com.pixoo.enums.Channel;
import com.pixoo.enums.ImageResampleMode;
import com.pixoo.utilities.MathUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic tests for the Pixoo Java library.
 */
public class PixooTest {
    
    private Pixoo pixoo;
    
    @BeforeEach
    public void setUp() {
        // Create a simulated Pixoo for testing
        pixoo = new Pixoo(null, 64, false, true, true, null);
    }
    
    @Test
    public void testPixooCreation() {
        assertNotNull(pixoo);
        assertEquals(64, pixoo.getSize());
        assertEquals(4096, pixoo.getPixelCount());
        assertTrue(pixoo.isSimulated());
    }
    
    @Test
    public void testInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Pixoo(null, 128, false, true, true, null);
        });
    }
    
    @Test
    public void testBasicDrawing() {
        // These should not throw exceptions in simulation mode
        assertDoesNotThrow(() -> {
            pixoo.clear(Palette.BLACK);
            pixoo.drawPixel(10, 10, Palette.RED);
            pixoo.drawLine(0, 0, 20, 20, Palette.WHITE);
            pixoo.drawFilledRectangle(5, 5, 15, 15, Palette.BLUE);
            pixoo.push();
        });
    }
    
    @Test
    public void testTextDrawing() {
        assertDoesNotThrow(() -> {
            pixoo.clear(Palette.BLACK);
            pixoo.drawText("Hello", 0, 0, Palette.GREEN);
            pixoo.drawCharacter('A', 0, 10, Palette.RED);
            pixoo.push();
        });
    }
    
    @Test
    public void testColorCreation() {
        Palette.Color color = new Palette.Color(128, 64, 255);
        assertEquals(128, color.getRed());
        assertEquals(64, color.getGreen());
        assertEquals(255, color.getBlue());
        assertEquals("#8040FF", color.toHex());
    }
    
    @Test
    public void testColorClamping() {
        Palette.Color color = new Palette.Color(300, -50, 128);
        assertEquals(255, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(128, color.getBlue());
    }
    
    @Test
    public void testFontSupport() {
        assertTrue(Font.isCharacterSupported('A'));
        assertTrue(Font.isCharacterSupported('z'));
        assertTrue(Font.isCharacterSupported('5'));
        assertTrue(Font.isCharacterSupported('!'));
        assertFalse(Font.isCharacterSupported('ñ'));
        
        assertNotNull(Font.retrieveGlyph('A'));
        assertNull(Font.retrieveGlyph('ñ'));
    }
    
    @Test
    public void testMathUtils() {
        assertEquals(255, MathUtils.clamp(300, 0, 255));
        assertEquals(0, MathUtils.clamp(-50, 0, 255));
        assertEquals(100, MathUtils.clamp(100, 0, 255));
        
        MathUtils.Point p1 = new MathUtils.Point(0, 0);
        MathUtils.Point p2 = new MathUtils.Point(10, 10);
        assertEquals(10, MathUtils.minimumAmountOfSteps(p1, p2));
        
        MathUtils.Point lerped = MathUtils.lerpLocation(p1, p2, 0.5);
        assertEquals(5.0, lerped.x, 0.001);
        assertEquals(5.0, lerped.y, 0.001);
    }
    
    @Test
    public void testEnums() {
        assertEquals(0, TextScrollDirection.LEFT.getValue());
        assertEquals(1, TextScrollDirection.RIGHT.getValue());
        
        assertEquals(0, Channel.FACES.getValue());
        assertEquals(1, Channel.CLOUD.getValue());
        assertEquals(2, Channel.VISUALIZER.getValue());
        assertEquals(3, Channel.CUSTOM.getValue());
    }
    
    @Test
    public void testDeviceControlInSimulation() {
        // These should not throw exceptions in simulation mode
        assertDoesNotThrow(() -> {
            pixoo.setBrightness(50);
            pixoo.setChannel(1);
            pixoo.setClock(0);
            pixoo.setScreen(true);
            pixoo.setScreenOff();
            pixoo.setScreenOn();
        });
    }
    
    @Test
    public void testBoundsChecking() {
        // Drawing outside bounds should not crash
        assertDoesNotThrow(() -> {
            pixoo.drawPixel(-1, -1, Palette.RED);
            pixoo.drawPixel(100, 100, Palette.RED);
            pixoo.drawPixelAtIndex(-1, Palette.RED);
            pixoo.drawPixelAtIndex(10000, Palette.RED);
        });
    }
    
    @Test
    public void testRgbMethods() {
        assertDoesNotThrow(() -> {
            pixoo.clearRgb(255, 0, 0);
            pixoo.drawPixelAtLocationRgb(10, 10, 0, 255, 0);
            pixoo.drawLineFromStartToStopRgb(0, 0, 10, 10, 0, 0, 255);
            pixoo.drawFilledRectangleFromTopLeftToBottomRightRgb(5, 5, 15, 15, 255, 255, 0);
            pixoo.drawTextAtLocationRgb("Test", 0, 20, 255, 255, 255);
            pixoo.drawCharacterAtLocationRgb('B', 20, 20, 128, 128, 128);
        });
    }
}
