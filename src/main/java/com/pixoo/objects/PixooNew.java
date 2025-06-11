package com.pixoo.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pixoo.constants.Font;
import com.pixoo.constants.Palette;
import com.pixoo.enums.ImageResampleMode;
import com.pixoo.enums.TextScrollDirection;
import com.pixoo.utilities.MathUtils;
import com.pixoo.configurations.SimulatorConfiguration;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Main Pixoo class for communicating with Divoom Pixoo devices.
 */
public class PixooNew {
    private static final int DEFAULT_SIZE = 64;
    private static final int REFRESH_COUNTER_LIMIT = 32;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String url;
    private final boolean debug;
    private final boolean refreshConnectionAutomatically;
    private final boolean simulated;
    private final int size;
    private final int pixelCount;
    
    private int[] buffer;
    private int buffersSent = 0;
    private int counter = 0;
    private SimulatorConfiguration simulationConfig;
    
    /**
     * Creates a new Pixoo instance with default settings.
     */
    public PixooNew() {
        this(null);
    }
    
    /**
     * Creates a new Pixoo instance with the specified IP address.
     * @param ipAddress The IP address of the Pixoo device
     */
    public PixooNew(String ipAddress) {
        this(ipAddress, DEFAULT_SIZE, false, true, false, new SimulatorConfiguration());
    }
    
    /**
     * Creates a new Pixoo instance with full configuration.
     * @param ipAddress The IP address of the Pixoo device (null for auto-discovery)
     * @param size The screen size in pixels (16, 32, or 64)
     * @param debug Enable debug logging
     * @param refreshConnectionAutomatically Enable automatic connection refresh
     * @param simulated Enable simulation mode
     * @param simulationConfig Configuration for simulation mode
     */
    public PixooNew(String ipAddress, int size, boolean debug, boolean refreshConnectionAutomatically, 
                 boolean simulated, SimulatorConfiguration simulationConfig) {
        
        // Validate size
        if (size != 16 && size != 32 && size != 64) {
            throw new IllegalArgumentException(
                "Invalid screen size in pixels given. Valid options are 16, 32, and 64");
        }
        
        this.size = size;
        this.pixelCount = size * size;
        this.debug = debug;
        this.refreshConnectionAutomatically = refreshConnectionAutomatically;
        this.simulated = simulated;
        this.simulationConfig = simulationConfig;
        
        // Initialize HTTP client
        this.httpClient = HttpClient.newHttpClient();
        
        this.objectMapper = new ObjectMapper();
        
        // Determine IP address
        if (ipAddress == null && !simulated) {
            this.url = "http://" + findLocalDeviceIp() + "/post";
        } else if (!simulated) {
            this.url = "http://" + ipAddress + "/post";
        } else {
            this.url = null; // Not needed for simulation
        }
        
        // Initialize buffer
        fill(Palette.BLACK);
        
        if (!simulated) {
            if (!validateConnection()) {
                System.err.println("[x] No connection could be made. Verify all settings");
                return;
            }
            
            // Load counter
            loadCounter();
            
            // Reset if needed
            if (refreshConnectionAutomatically && counter > REFRESH_COUNTER_LIMIT) {
                resetCounter();
            }
        } else {
            counter = 1;
        }
    }

    // Drawing methods
    public void clear(Palette.Color color) {
        fill(color);
    }

    public void fill(Palette.Color color) {
        Arrays.fill(buffer, color.toInt());
    }

    public void fillRgb(int r, int g, int b) {
        fill(new Palette.Color(r, g, b));
    }

    public void drawPixel(int x, int y, Palette.Color color) {
        if (x < 0 || x >= size || y < 0 || y >= size) return;
        buffer[y * size + x] = color.toInt();
    }

    public void drawLine(int x1, int y1, int x2, int y2, Palette.Color color) {
        // Bresenham's line algorithm
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1, y = y1;
        while (true) {
            drawPixel(x, y, color);
            if (x == x2 && y == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    public void drawFilledRectangle(int x, int y, int width, int height, Palette.Color color) {
        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                drawPixel(x + dx, y + dy, color);
            }
        }
    }

    public void push() {
        sendBuffer();
    }

    // Private helper methods
    private String findLocalDeviceIp() {
        // Simple implementation - in real usage, this would discover devices on the network
        return "192.168.1.100"; // Placeholder
    }

    private boolean validateConnection() {
        if (simulated) return true;
        
        try {
            // Test connection with a simple request
            JsonNode data = getAllDeviceConfigurations();
            return data != null;
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Connection validation failed: " + e.getMessage());
            }
            return false;
        }
    }

    private void loadCounter() {
        if (simulated) {
            counter = 1;
            return;
        }
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Draw/GetHttpGifId");
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            } else {
                counter = data.get("PicId").asInt();
                if (debug) {
                    System.out.println("[.] Counter loaded and stored: " + counter);
                }
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error loading counter: " + e.getMessage());
            }
        }
    }

    private void sendBuffer() {
        counter++;
        
        if (refreshConnectionAutomatically && counter >= REFRESH_COUNTER_LIMIT) {
            resetCounter();
            counter = 1;
        }
        
        if (debug) {
            System.out.printf("[.] Counter set to %d%n", counter);
        }
        
        if (simulated) {
            // TODO: Implement simulator display
            buffersSent++;
            return;
        }
        
        try {
            // Encode buffer to base64
            byte[] byteBuffer = new byte[buffer.length];
            for (int i = 0; i < buffer.length; i++) {
                byteBuffer[i] = (byte) buffer[i];
            }
            String encodedBuffer = Base64.getEncoder().encodeToString(byteBuffer);
            
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Draw/SendHttpGif");
            request.put("PicNum", 1);
            request.put("PicWidth", size);
            request.put("PicOffset", 0);
            request.put("PicID", counter);
            request.put("PicSpeed", 1000);
            request.put("PicData", encodedBuffer);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            } else {
                buffersSent++;
                if (debug) {
                    System.out.printf("[.] Pushed %d buffers%n", buffersSent);
                }
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error sending buffer: " + e.getMessage());
            }
        }
    }

    private void resetCounter() {
        if (debug) {
            System.out.println("[.] Resetting counter remotely");
        }
        
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Draw/ResetHttpGifId");
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error resetting counter: " + e.getMessage());
            }
        }
    }

    private String makeRequest(ObjectNode requestJson) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected code " + response.statusCode());
        }
        
        return response.body();
    }

    private void handleError(JsonNode error) {
        if (debug) {
            System.err.println("[x] Error on request " + counter);
            System.err.println(error);
        }
    }

    private JsonNode getAllDeviceConfigurations() {
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Channel/GetAllConf");
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
                return null;
            }
            
            return data;
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error getting device configurations: " + e.getMessage());
            }
            return null;
        }
    }

    // Device control methods
    
    /**
     * Reboots the device.
     */
    public void reboot() {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/SysReboot");
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error rebooting device: " + e.getMessage());
            }
        }
    }

    /**
     * Sends scrolling text to the device.
     * @param text The text to display
     * @param x X coordinate
     * @param y Y coordinate
     * @param color The text color
     * @param identifier Text identifier (0-19)
     * @param font Font size
     * @param width Text width
     * @param movementSpeed Scroll speed
     * @param direction Scroll direction
     */
    public void sendText(String text, int x, int y, Palette.Color color, int identifier, 
                        int font, int width, int movementSpeed, TextScrollDirection direction) {
        if (simulated) return;
        
        try {
            identifier = MathUtils.clamp(identifier, 0, 19);
            
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Draw/SendHttpText");
            request.put("TextId", identifier);
            request.put("x", x);
            request.put("y", y);
            request.put("dir", direction.getValue());
            request.put("font", font);
            request.put("TextWidth", width);
            request.put("speed", movementSpeed);
            request.put("TextString", text);
            request.put("color", color.toHex());
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error sending text: " + e.getMessage());
            }
        }
    }

    // Convenience methods for text
    public void sendTextAtLocationRgb(String text, int x, int y, int r, int g, int b, 
                                     int identifier, int font, int width, int movementSpeed, 
                                     TextScrollDirection direction) {
        sendText(text, x, y, new Palette.Color(r, g, b), identifier, font, width, movementSpeed, direction);
    }

    /**
     * Sets the brightness of the display.
     * @param brightness Brightness level (0-100)
     */
    public void setBrightness(int brightness) {
        if (simulated) return;
        
        try {
            brightness = MathUtils.clamp(brightness, 0, 100);
            
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Channel/SetBrightness");
            request.put("Brightness", brightness);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting brightness: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the current channel.
     * @param channel The channel to switch to
     */
    public void setChannel(int channel) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Channel/SetIndex");
            request.put("SelectIndex", channel);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting channel: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the clock/face.
     * @param clockId The clock ID to display
     */
    public void setClock(int clockId) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Channel/SetClockSelectId");
            request.put("ClockId", clockId);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting clock: " + e.getMessage());
            }
        }
    }

    // Additional device control methods would follow the same pattern...
    // For brevity, I'm including just a few more key methods

    /**
     * Gets the device time.
     * @return JsonNode containing device time information, or null if error
     */
    public JsonNode getDeviceTime() {
        if (simulated) return null;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/GetDeviceTime");
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
                return null;
            }
            
            if (debug) {
                System.out.println(data);
            }
            
            return data;
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error getting device time: " + e.getMessage());
            }
            return null;
        }
    }

    // Getters
    public int getSize() { return size; }
    public int getPixelCount() { return pixelCount; }
    public boolean isSimulated() { return simulated; }
    public boolean isDebug() { return debug; }

    // Clean up resources
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
