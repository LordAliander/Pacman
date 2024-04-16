package com.zetcode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Config {
    private static final Logger logger = Logger.getLogger(Config.class.getName());
    private static final Properties properties;

    static {
        properties = new Properties();
        // Der folgende Code ist dazu da, auf eventuelle Fehler direkt zu melden
        try {
            properties.load(new FileInputStream("src/com/zetcode/config.properties")); // Adjust the path accordingly
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading config.properties", e);
        }
    }

    // Example method to get a variable
    public static int getFeldGroese() {
        return Integer.parseInt(properties.getProperty("feldGroese"));
    }
    public static int getFeldAnzahl() {
        return Integer.parseInt(properties.getProperty("feldAnzahl"));
    }

}