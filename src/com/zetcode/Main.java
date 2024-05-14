package com.zetcode;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main extends JFrame {

    public Main() {
        initUI();
    }

    private void initUI() {
        // HIer wird alles "wichtige" Definiert und die Board Klasse dem JFrame Modul übergeben
        add(new Board()); // Sobald das passiert wird alles in der Klasse Board ausgeführt
        // In den folgenden Zeilen werden lediglich einige Dinge getan, die die Grafikausgabe ermöglichen
        setTitle("Pacman");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(624, 690);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        /*
         * Das ist der "Entry-Point" des Programms, also der Start.
         */
        EventQueue.invokeLater(() -> {
            // Eine Instanz dieser Klasse wird erstellt.
            var ex = new Main();
            ex.setVisible(true);
        });
    }
}
