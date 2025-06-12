package de.jeffreygroneberg.pixooj.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.jeffreygroneberg.pixooj.constants.Font; // Fixed import
import de.jeffreygroneberg.pixooj.constants.Palette; // Fixed import
import de.jeffreygroneberg.pixooj.enums.ImageResampleMode; // Fixed import
import de.jeffreygroneberg.pixooj.enums.TextScrollDirection; // Fixed import
import de.jeffreygroneberg.pixooj.utilities.MathUtils; // Fixed import
import de.jeffreygroneberg.pixooj.configurations.SimulatorConfiguration; // Fixed import

import java.awt.image.BufferedImage;
import java.awt.Graphics2D; // Added import
import java.awt.Color;      // Added import
import java.awt.RenderingHints; // Added import
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/**
 * Main Pixoo class for communicating with Divoom Pixoo devices.
 */
public class Pixoo {
    private static final int DEFAULT_SIZE = 64;
    private static final int REFRESH_COUNTER_LIMIT = 32;
    private static final boolean HIGHER_QUALITY_SCALING = true; // Control flag for scaling quality
    
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
    
    /**
     * Creates a new Pixoo instance with default settings.
     */
    public Pixoo() {
        this(null);
    }
    
    /**
     * Creates a new Pixoo instance with the specified IP address.
     * @param ipAddress The IP address of the Pixoo device
     */
    public Pixoo(String ipAddress) {
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
    public Pixoo(String ipAddress, int size, boolean debug, boolean refreshConnectionAutomatically, 
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
        
        // Initialize HTTP client with HTTP/1.1 (Pixoo devices don't support HTTP/2)
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
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
        buffer = new int[pixelCount * 3];
        int[] rgb = MathUtils.clampColor(color.toArray());
        for (int i = 0; i < pixelCount; i++) {
            int index = i * 3;
            buffer[index] = rgb[0];
            buffer[index + 1] = rgb[1];
            buffer[index + 2] = rgb[2];
        }
    }

    public void fillRgb(int r, int g, int b) {
        fill(new Palette.Color(r, g, b));
    }

    public void drawPixel(int x, int y, Palette.Color color) {
        if (x < 0 || x >= size || y < 0 || y >= size) return;
        int[] rgb = MathUtils.clampColor(color.toArray());
        int index = (y * size + x) * 3;
        
        buffer[index] = rgb[0];
        buffer[index + 1] = rgb[1];
        buffer[index + 2] = rgb[2];
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
        // Return the specified Pixoo device IP
        return "91.89.197.67:9001";
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
                // Ensure proper unsigned byte conversion (0-255 range)
                byteBuffer[i] = (byte) (buffer[i] & 0xFF);
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
        if (debug) {
            System.out.println("[DEBUG] Sending request to: " + url);
            System.out.println("[DEBUG] Request JSON: " + requestJson.toString());
        }
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (debug) {
            System.out.println("[DEBUG] Response status: " + response.statusCode());
            System.out.println("[DEBUG] Response body: " + response.body());
        }
        
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected code " + response.statusCode() + " - Response: " + response.body());
        }
        
        return response.body();
    }

    private void handleError(JsonNode error) {
        if (debug) {
            System.err.println("[x] Error on request " + counter);
            System.err.println(error);
        }
    }

    // Device information methods
    public JsonNode getAllDeviceConfigurations() {
        if (simulated) return null;
        
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
            
            return data;
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error getting device time: " + e.getMessage());
            }
            return null;
        }
    }
    
    // GIF and media methods
    public void playLocalGif(String filePath) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/PlayTFGif");
            request.put("FileType", 0);
            request.put("FileName", filePath);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error playing local GIF: " + e.getMessage());
            }
        }
    }
    
    public void playNetGif(String gifFileUrl) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/PlayTFGif");
            request.put("FileType", 2);
            request.put("FileName", gifFileUrl);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error playing network GIF: " + e.getMessage());
            }
        }
    }
    
    public void playLocalGifDirectory(String directoryPath) {
        if (simulated) return;
        
        try {
            File directory = new File(directoryPath);
            if (!directory.exists() || !directory.isDirectory()) {
                if (debug) {
                    System.err.println("[x] Directory does not exist: " + directoryPath);
                }
                return;
            }
            
            File[] gifFiles = directory.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".gif"));
                
            if (gifFiles != null) {
                for (File gifFile : gifFiles) {
                    playLocalGif(gifFile.getAbsolutePath());
                    // Small delay between GIFs to prevent overwhelming the device
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error playing GIF directory: " + e.getMessage());
            }
        }
    }

    /**
     * Sends a local animated GIF file to the Pixoo device for playback.
     * This method reads the GIF frame by frame, processes each frame,
     * and sends it to the device.
     *
     * @param localGifPath The path to the local GIF file on the computer.
     * @param speed The display speed for each frame in milliseconds.
     * @throws IOException If there's an error reading the file or communicating with the device.
     */
    public void sendAnimatedGif(String localGifPath, int speed) throws IOException {
        if (simulated) {
            if (debug) System.out.println("[.] Simulated mode: sendAnimatedGif called for " + localGifPath);
            return;
        }

        File gifFile = new File(localGifPath);
        if (!gifFile.exists()) {
            if (debug) System.err.println("[x] GIF file not found: " + localGifPath);
            throw new IOException("GIF file not found: " + localGifPath);
        }

        resetCounter();

        ImageReader reader = null;
        BufferedImage cumulativeFrame = null;
        Graphics2D cumulativeG2d = null;

        try (FileInputStream inputStream = new FileInputStream(gifFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) {
                throw new IOException("No GIF ImageReaders found in ImageIO.");
            }
            reader = readers.next();
            reader.setInput(ImageIO.createImageInputStream(inputStream));

            int numFrames = reader.getNumImages(true);
            if (debug) System.out.println("[.] Processing GIF: " + localGifPath + " with " + numFrames + " frames.");

            cumulativeFrame = new BufferedImage(this.size, this.size, BufferedImage.TYPE_INT_RGB);
            cumulativeG2d = cumulativeFrame.createGraphics();
            cumulativeG2d.setColor(Color.BLACK);
            cumulativeG2d.fillRect(0, 0, this.size, this.size);

            int animationPicId = 1;
            if (debug && numFrames > 0) {
                System.out.println("[DEBUG] sendAnimatedGif - Target device size (this.size): " + this.size + "x" + this.size);
            }

            for (int i = 0; i < numFrames; i++) {
                BufferedImage rawFrame = reader.read(i);

                if (debug && i == 0) {
                    System.out.println("[DEBUG] sendAnimatedGif - Frame 0: rawFrame dimensions: " + rawFrame.getWidth() + "x" + rawFrame.getHeight());
                }

                BufferedImage frameToDrawOnCumulative;
                if (rawFrame.getWidth() != this.size || rawFrame.getHeight() != this.size) {
                    // Use the new scaleImage helper method
                    frameToDrawOnCumulative = scaleImage(rawFrame, this.size, this.size, HIGHER_QUALITY_SCALING);
                    if (debug && i == 0) {
                         System.out.printf("[DEBUG] sendAnimatedGif - Frame 0: Scaled frameToDrawOnCumulative dimensions: %dx%d using %s quality%n", 
                               frameToDrawOnCumulative.getWidth(), frameToDrawOnCumulative.getHeight(), HIGHER_QUALITY_SCALING ? "higher" : "standard");
                    }
                } else {
                    frameToDrawOnCumulative = rawFrame;
                }

                if (debug && i == 0) {
                    System.out.println("[DEBUG] sendAnimatedGif - Frame 0: frameToDrawOnCumulative (final) dimensions: " + frameToDrawOnCumulative.getWidth() + "x" + frameToDrawOnCumulative.getHeight());
                }

                cumulativeG2d.drawImage(frameToDrawOnCumulative, 0, 0, null);

                if (debug && i == 0) {
                    System.out.println("[DEBUG] sendAnimatedGif - Frame 0: cumulativeFrame (being sent) dimensions: " + cumulativeFrame.getWidth() + "x" + cumulativeFrame.getHeight());
                }

                byte[] rgbData = bufferedImageToRgbBytes(cumulativeFrame);
                String base64Data = Base64.getEncoder().encodeToString(rgbData);

                ObjectNode requestJson = objectMapper.createObjectNode();
                requestJson.put("Command", "Draw/SendHttpGif");
                requestJson.put("PicID", animationPicId); 
                requestJson.put("PicNum", numFrames);     
                requestJson.put("PicOffset", i);          
                requestJson.put("PicWidth", this.size);   
                requestJson.put("PicSpeed", speed);       
                requestJson.put("PicData", base64Data);
                
                try {
                    String response = makeRequest(requestJson);
                    JsonNode responseData = objectMapper.readTree(response);
                    if (responseData.get("error_code").asInt() != 0) {
                        handleError(responseData);
                        System.err.println("[x] Error sending GIF frame " + i + " for " + localGifPath + ". Aborting GIF send.");
                        break; 
                    }
                    if (debug) System.out.println("[.] Sent GIF frame " + i + "/" + (numFrames - 1) + " for " + localGifPath);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); 
                    System.err.println("[x] Interrupted while sending GIF frame " + i + " for " + localGifPath + ". Aborting GIF send.");
                    throw new IOException("Sending GIF frame was interrupted", e);
                } 
            }
            if (debug) System.out.println("[.] Finished sending all frames for " + localGifPath);

        } finally {
            if (cumulativeG2d != null) {
                cumulativeG2d.dispose(); // Dispose graphics context for cumulativeFrame
            }
            if (reader != null) {
                reader.dispose();
            }
        }
    }

    /**
     * Converts a BufferedImage to a raw RGB byte array.
     * @param image The BufferedImage to convert.
     * @return A byte array containing RGB pixel data.
     */
    private byte[] bufferedImageToRgbBytes(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        // Each pixel is 3 bytes (R, G, B)
        byte[] rgbBytes = new byte[width * height * 3];
        int k = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y); // ARGB format
                rgbBytes[k++] = (byte) ((pixel >> 16) & 0xFF); // Red
                rgbBytes[k++] = (byte) ((pixel >> 8) & 0xFF);  // Green
                rgbBytes[k++] = (byte) (pixel & 0xFF);         // Blue
            }
        }
        return rgbBytes;
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
    public void sendTextAtLocationRgb(String text, int x, int y, int r, int g, int b) {
        sendText(text, x, y, new Palette.Color(r, g, b), 1, 2, 64, 1, TextScrollDirection.LEFT);
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

    // Additional drawing methods
    public void drawFilledRectangleFromTopLeftToBottomRightRgb(int topLeftX, int topLeftY, 
                                                               int bottomRightX, int bottomRightY, 
                                                               int r, int g, int b) {
        drawFilledRectangle(topLeftX, topLeftY, bottomRightX, bottomRightY, new Palette.Color(r, g, b));
    }

    public void drawText(String text, int x, int y, Palette.Color color) {
        drawText(text, new MathUtils.Point(x, y), color);
    }

    public void drawText(String text, MathUtils.Point xy, Palette.Color color) {
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            drawCharacter(character, new MathUtils.Point(i * 4 + xy.x, xy.y), color);
        }
    }

    public void drawCharacter(char character, int x, int y, Palette.Color color) {
        drawCharacter(character, new MathUtils.Point(x, y), color);
    }

    public void drawCharacter(char character, MathUtils.Point xy, Palette.Color color) {
        int[] matrix = Font.retrieveGlyph(character);
        if (matrix != null) {
            for (int index = 0; index < matrix.length; index++) {
                if (matrix[index] == 1) {
                    int localX = index % 3;
                    int localY = index / 3;
                    drawPixel((int)xy.x + localX, (int)xy.y + localY, color);
                }
            }
        }
    }

    public void drawImage(String imagePath, int x, int y, ImageResampleMode resampleMode) throws IOException {
        drawImage(imagePath, new MathUtils.Point(x, y), resampleMode, false);
    }

    public void drawImage(String imagePath, MathUtils.Point xy, ImageResampleMode resampleMode, boolean padResample) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        drawImage(image, xy, resampleMode, padResample);
    }

    public void drawImage(BufferedImage image, MathUtils.Point xy, ImageResampleMode resampleMode, boolean padResample) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        BufferedImage imageToDraw = image;

        // Scale image if needed
        if (originalWidth > size || originalHeight > size || originalWidth < size || originalHeight < size ) { // Also scale up if smaller
            if (padResample) {
                // Create a new image with black background and draw the original image onto it
                BufferedImage paddedImage = new BufferedImage(size, size, imageToDraw.getType());
                Graphics2D g2d = paddedImage.createGraphics();
                g2d.setColor(java.awt.Color.BLACK);
                g2d.fillRect(0, 0, size, size);

                // Calculate position to center the image
                int x_centered = (size - originalWidth) / 2;
                int y_centered = (size - originalHeight) / 2;

                g2d.drawImage(imageToDraw, x_centered, y_centered, null);
                g2d.dispose();
                imageToDraw = paddedImage;
                
                if (debug) {
                    System.out.printf("[.] Padded image to fit on screen: (%d, %d) -> (%d, %d)%n",
                            originalWidth, originalHeight, imageToDraw.getWidth(), imageToDraw.getHeight());
                }
            } else {
                 // Determine scaling quality based on resampleMode
                boolean useHighQualityScaling = (resampleMode != ImageResampleMode.PIXEL_ART) && HIGHER_QUALITY_SCALING;
                imageToDraw = scaleImage(imageToDraw, size, size, useHighQualityScaling);
                if (debug) {
                    System.out.printf("[.] Resized image to fit on screen: (%d, %d) -> (%d, %d) using %s quality%n",
                            originalWidth, originalHeight, imageToDraw.getWidth(), imageToDraw.getHeight(), useHighQualityScaling ? "higher" : "standard/pixel_art");
                }
            }
        }

        // Draw pixels
        for (int imgY = 0; imgY < imageToDraw.getHeight(); imgY++) {
            for (int imgX = 0; imgX < imageToDraw.getWidth(); imgX++) {
                int placedX = imgX + (int)xy.x;
                int placedY = imgY + (int)xy.y;

                if (placedX < 0 || placedX >= size || placedY < 0 || placedY >= size) {
                    continue;
                }

                int rgb = imageToDraw.getRGB(imgX, imgY);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                drawPixel(placedX, placedY, new Palette.Color(r, g, b));
            }
        }
    }

    /**
     * Scales an image to the target dimensions using Graphics2D for better quality.
     *
     * @param originalImage The image to scale.
     * @param targetWidth The target width.
     * @param targetHeight The target height.
     * @param highQuality If true, uses bicubic interpolation for scaling. Otherwise, uses bilinear.
     * @return The scaled image.
     */
    private BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight, boolean highQuality) {
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D g2d = scaledImage.createGraphics();

        if (highQuality) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            // Default or less intensive, e.g., bilinear
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return scaledImage;
    }

    public void setScreenOff() {
        setScreen(false);
    }
    
    public void setScreenOn() {
        setScreen(true);
    }

    public void setScreen(boolean on) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Channel/OnOffScreen");
            request.put("OnOff", on ? 1 : 0);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting screen: " + e.getMessage());
            }
        }
    }

    // RGB variant methods
    public void drawTextAtLocationRgb(String text, int x, int y, int r, int g, int b) {
        drawText(text, x, y, new Palette.Color(r, g, b));
    }

    public void drawCharacterAtLocationRgb(char character, int x, int y, int r, int g, int b) {
        drawCharacter(character, x, y, new Palette.Color(r, g, b));
    }

    public void drawPixelAtLocationRgb(int x, int y, int r, int g, int b) {
        drawPixel(x, y, new Palette.Color(r, g, b));
    }

    public void drawLineFromStartToStopRgb(int startX, int startY, int endX, int endY, int r, int g, int b) {
        drawLine(startX, startY, endX, endY, new Palette.Color(r, g, b));
    }

    public void clearRgb(int r, int g, int b) {
        fillRgb(r, g, b);
    }

    public void drawPixelAtIndex(int index, Palette.Color color) {
        if (index < 0 || index >= pixelCount) {
            if (debug) {
                System.out.printf("[!] Invalid index given: %d (maximum index is %d)%n", index, pixelCount - 1);
            }
            return;
        }
        
        int[] rgb = MathUtils.clampColor(color.toArray());
        index *= 3;
        
        buffer[index] = rgb[0];
        buffer[index + 1] = rgb[1];
        buffer[index + 2] = rgb[2];
    }

    public void drawPixelAtIndexRgb(int index, int r, int g, int b) {
        drawPixelAtIndex(index, new Palette.Color(r, g, b));
    }

    // Getters
    public int getSize() { return size; }
    public int getPixelCount() { return pixelCount; }
    public boolean isSimulated() { return simulated; }
    public boolean isDebug() { return debug; }

    public String getUrlString() { return url; } // Added getter for URL

    // Clean up resources
    public void close() {
        // JDK HttpClient doesn't require explicit closing
        // Resources are automatically managed
    }
    
    // Additional device control methods from original implementation
    public void setHighLightMode(boolean on) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/SetHighLightMode");
            request.put("Mode", on);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting highlight mode: " + e.getMessage());
            }
        }
    }
    
    public void setMirrorMode(boolean on) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/SetMirrorMode");
            request.put("Mode", on);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting mirror mode: " + e.getMessage());
            }
        }
    }
    
    public void setNoiseStatus(boolean on) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/SetNoiseStatus");
            request.put("NoiseStatus", on);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting noise status: " + e.getMessage());
            }
        }
    }
    
    public void setScoreBoard(int blueScore, int redScore) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Tools/SetScoreBoard");
            request.put("BlueScore", blueScore);
            request.put("RedScore", redScore);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting scoreboard: " + e.getMessage());
            }
        }
    }
    
    public void setVisualizer(int equalizerPosition) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Channel/SetEqPosition");
            request.put("EqPosition", equalizerPosition);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting visualizer: " + e.getMessage());
            }
        }
    }
    
    public void setWhiteBalance(int r, int g, int b) {
        if (simulated) return;
        
        try {
            r = MathUtils.clamp(r, 0, 100);
            g = MathUtils.clamp(g, 0, 100);
            b = MathUtils.clamp(b, 0, 100);
            
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/SetWhiteBalance");
            request.put("RValue", r);
            request.put("GValue", g);
            request.put("BValue", b);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error setting white balance: " + e.getMessage());
            }
        }
    }
    
    public void soundBuzzer(int activeCycleTime, int inactiveCycleTime, int totalTime) {
        if (simulated) return;
        
        try {
            ObjectNode request = objectMapper.createObjectNode();
            request.put("Command", "Device/PlayBuzzer");
            request.put("ActiveTimeInCycle", activeCycleTime);
            request.put("OffTimeInCycle", inactiveCycleTime);
            request.put("PlayTotalTime", totalTime);
            
            String response = makeRequest(request);
            JsonNode data = objectMapper.readTree(response);
            
            if (data.get("error_code").asInt() != 0) {
                handleError(data);
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("[x] Error playing buzzer: " + e.getMessage());
            }
        }
    }
}
