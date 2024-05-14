package com.zetcode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Config {
    /*
     * Diese Klasse ist total überflüssig, wir hätten können die beiden Variablen einfach in der Board Klasse
     * definieren, jedoch fanden wir diese Herangehensweise, auch wenn sie ziemlich limitiert ist interessant
     * auszuprobieren.
     * Hier wird einfach eine Schnittstelle zwischen den Klassen und der config.properites Datei erstellt.
     */
    private static final Logger logger = Logger.getLogger(Config.class.getName());
    private static final Properties properties;
    // Hier wird geprüft ob die config.properties Datei existiert und ob die Klasse auf die Datei Zugriff hat
    static {
        properties = new Properties();
        // Der folgende Code ist dazu da, auf eventuelle Fehler direkt zu melden
        try {
            properties.load(new FileInputStream("src/com/zetcode/config.properties")); // Adjust the path accordingly
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading config.properties", e);
        }
    }

    // Dies sind Benutzerdefinierte Methoden um die jeweiligen Werte, die angefragt werden zurückzugeben
    // Total überflüssig aber cool
    public static int getFeldGroese() {
        return Integer.parseInt(properties.getProperty("feldGroese"));
    }
    public static int getFeldAnzahl() {
        return Integer.parseInt(properties.getProperty("feldAnzahl"));
    }

}