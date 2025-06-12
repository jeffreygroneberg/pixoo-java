package de.jeffreygroneberg.pixooj.configurations;

/**
 * Configuration class for simulator settings.
 */
public class SimulatorConfiguration {
    private int scale;
    
    public SimulatorConfiguration() {
        this(4);
    }
    
    public SimulatorConfiguration(int scale) {
        this.scale = scale;
    }
    
    public int getScale() {
        return scale;
    }
    
    public void setScale(int scale) {
        this.scale = scale;
    }
}
