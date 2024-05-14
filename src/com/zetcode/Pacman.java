package com.zetcode;

import java.awt.Image;

public class Pacman {
    // Für jede Richtung des Pacmans werden 4 Bilder aufgrund der Animationen erfordert
    Image[] rechts = new Image[4];
    Image[] links = new Image[4];
    Image[] oben = new Image[4];
    Image[] unten = new Image[4];

    public int leben;
}
