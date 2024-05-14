package com.zetcode;

import java.awt.Image;

public class Geist {
    Image oben, unten, links, rechts; // Hier werden die Bilder für die 4 Richtungen gespeichert
    Image essbar1, essbar2; // Die beiden Bilder für die essbar Animation
    public int x, y; // Position des Geists
    public int dx, dy; // Bewegungsrichtung des Geists
    public int geschwindigkeit; // seine Geschwindigkeit
}
