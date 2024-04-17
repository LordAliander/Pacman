package com.zetcode;

import java.awt.Image;

public class Pacman {

    Image[] rechts = new Image[4];
    Image[] links = new Image[4];
    Image[] oben = new Image[4];
    Image[] unten = new Image[4];

    public int leben;
    public int x, y;
    public int dx, dy;
}
