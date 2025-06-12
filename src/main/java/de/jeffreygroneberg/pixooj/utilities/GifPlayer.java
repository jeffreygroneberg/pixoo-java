package de.jeffreygroneberg.pixooj.utilities;

import de.jeffreygroneberg.pixooj.objects.Pixoo;
import de.jeffreygroneberg.pixooj.enums.ImageResampleMode;
import de.jeffreygroneberg.pixooj.constants.Palette;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for playing GIF animations on Pixoo devices by extracting
 * frames and displaying them sequentially.
 */
public class GifPlayer {
    
    private static class GifFrame {
        public BufferedImage image;
        public int delayMs;
        
        public GifFrame(BufferedImage image, int delayMs) {
            this.image = image;
            this.delayMs = delayMs;
        }
    }
    
    /**
     * Plays a GIF file on the Pixoo device by extracting frames and displaying them.
     * 
     * @param pixoo The Pixoo device instance
     * @param gifFile The GIF file to play
     * @param loops Number of times to loop the animation (0 for infinite)
     * @throws IOException If there's an error reading the GIF file
     */
    public static void playGif(Pixoo pixoo, File gifFile, int loops) throws IOException {
        System.out.println("Loading GIF frames from: " + gifFile.getName());
        
        List<GifFrame> frames = extractGifFrames(gifFile);
        System.out.println("Extracted " + frames.size() + " frames");
        
        if (frames.isEmpty()) {
            System.err.println("No frames found in GIF");
            return;
        }
        
        // Play the animation
        int loopCount = 0;
        while (loops == 0 || loopCount < loops) {
            for (int i = 0; i < frames.size(); i++) {
                GifFrame frame = frames.get(i);
                
                // Clear and draw the frame
                pixoo.clear(Palette.BLACK);
                pixoo.drawImage(frame.image, new MathUtils.Point(0, 0), 
                               ImageResampleMode.PIXEL_ART, false);
                pixoo.push();
                
                // Wait for the frame delay
                try {
                    Thread.sleep(Math.max(frame.delayMs, 50)); // Minimum 50ms delay
                } catch (InterruptedException e) {
                    System.out.println("Animation interrupted");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            
            if (loops > 0) {
                loopCount++;
                System.out.println("Completed loop " + loopCount + "/" + loops);
            }
        }
        
        System.out.println("GIF animation completed");
    }
    
    /**
     * Plays a GIF file once.
     */
    public static void playGif(Pixoo pixoo, File gifFile) throws IOException {
        playGif(pixoo, gifFile, 1);
    }
    
    /**
     * Plays a GIF file from a path.
     */
    public static void playGif(Pixoo pixoo, String gifPath, int loops) throws IOException {
        playGif(pixoo, new File(gifPath), loops);
    }
    
    /**
     * Plays a GIF file from a path once.
     */
    public static void playGif(Pixoo pixoo, String gifPath) throws IOException {
        playGif(pixoo, new File(gifPath), 1);
    }
    
    /**
     * Extracts frames and timing information from a GIF file.
     */
    private static List<GifFrame> extractGifFrames(File gifFile) throws IOException {
        List<GifFrame> frames = new ArrayList<>();
        
        try (ImageInputStream input = ImageIO.createImageInputStream(gifFile)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            
            if (!readers.hasNext()) {
                throw new IOException("No GIF readers found");
            }
            
            ImageReader reader = readers.next();
            reader.setInput(input);
            
            int frameCount = reader.getNumImages(true);
            
            for (int i = 0; i < frameCount; i++) {
                BufferedImage frame = reader.read(i);
                int delay = getFrameDelay(reader, i);
                frames.add(new GifFrame(frame, delay));
            }
            
            reader.dispose();
        }
        
        return frames;
    }
    
    /**
     * Extracts the delay time for a specific frame from GIF metadata.
     */
    private static int getFrameDelay(ImageReader reader, int frameIndex) {
        try {
            IIOMetadata metadata = reader.getImageMetadata(frameIndex);
            String metaFormatName = metadata.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
            IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
            
            if (graphicsControlExtensionNode != null) {
                String delayTime = graphicsControlExtensionNode.getAttribute("delayTime");
                if (delayTime != null && !delayTime.isEmpty()) {
                    // GIF delay is in hundredths of a second, convert to milliseconds
                    return Integer.parseInt(delayTime) * 10;
                }
            }
        } catch (Exception e) {
            // If we can't read the delay, use a default
            System.out.println("Could not read frame delay, using default: " + e.getMessage());
        }
        
        // Default delay of 100ms if we can't read it
        return 100;
    }
    
    /**
     * Helper method to find a node in the metadata tree.
     */
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        return null;
    }
}
