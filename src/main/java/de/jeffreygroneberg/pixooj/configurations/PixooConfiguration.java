package de.jeffreygroneberg.pixooj.configurations;

/**
 * Configuration class for Pixoo device settings.
 */
public class PixooConfiguration {
    private String ipAddress;
    
    public PixooConfiguration() {
        this(null);
    }
    
    public PixooConfiguration(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
