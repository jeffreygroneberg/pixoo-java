package de.jeffreygroneberg.pixooj.demo;

import de.jeffreygroneberg.pixooj.objects.Pixoo; // Updated import
import de.jeffreygroneberg.pixooj.configurations.SimulatorConfiguration; // Added import
import java.io.IOException; // Required for sendAnimatedGif
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple test to demonstrate playing a local animated GIF.
 */
public class SimpleTest {
    public static void main(String[] args) {

        Properties prop = new Properties();
        String ipAddress = "";
        try (InputStream input = SimpleTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out
            ipAddress = prop.getProperty("ip.address");

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        // Create a Pixoo instance
        // Ensure this IP is correct and your Pixoo is on the same network
        // Set debug to true, simulated to false for device testing
        Pixoo pixoo = new Pixoo(ipAddress, 64, true, true, false, null); 

        // Path to your local GIF file in the assets folder
        String localGifPath = "assets/test_2.gif"; 

        try {
            System.out.println("Attempting to send local animation: " + localGifPath);
            // Send the animated GIF with a speed of 100ms per frame
            pixoo.sendAnimatedGif(localGifPath, 100);
            System.out.println("Local animation sent. Check your Pixoo device!");

        } catch (IOException e) {
            System.err.println("Error sending local animation: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
