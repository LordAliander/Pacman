package com.zetcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import com.zetcode.Geist;

public class Board extends JPanel implements ActionListener {

    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);
    private final Color punktFarbe = new Color(250, 100, 0);
    private final int feldGroese = Config.getFeldGroese(); // sollte dynamisch gestaltet werden
    private final int feldAnzahl = Config.getFeldAnzahl(); // sollte auch dynamisch gestaltet werden
    private final int bildschirmGroese = feldAnzahl * feldGroese; // wird demzufolge auch dynamisch sein
    private final int animationsDauer = 2;

    private final int animationsAnzahl = 4;
    private final int maxGeisterAnzahl = 12;
    private final int pacmanGeschwindigkeit = 6;
    // In dieser Array befinden sich die Informationen über das Spielfeld
    // Hier sind keine Informationen über die Lage von den Geistern oder Pacman enthalten
    private final short[] levelData = {
            19, 26, 26, 26, 18, 18, 18, 18, 22, 0, 0, 0, 19, 18, 22,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 18, 18, 18, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
            25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
            1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
            1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
            1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
            9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };

    // Hier sind alle möglichen Geistergeschwindigkeiten enthalten
    private final int[] zugelasseneGeschwindigkeiten = {1, 2, 3, 4, 6, 8};
    private final int maximaleGeschwindigkeit = zugelasseneGeschwindigkeiten.length;
    private Dimension d;
    private Image ii;
    private Color feldFarbe;
    private boolean imSpiel = false;
    private boolean tot = false;
    private int pacAnimCount = animationsDauer;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int geisterAnzahl = 6;
    private int leben, score;
    private int[] dx, dy;
    private Geist[] geisterArray;
    private Image geist;
    private Image pacman1up, pacman2up, pacman3up, pacman4up;
    private Image pacman1right, pacman2right, pacman3right, pacman4right;
    private Image pacman1down, pacman2down, pacman3down, pacman4down;
    private Image pacman1left, pacman2left, pacman3left, pacman4left;


    private int pacman_x, pacman_y; // Position von Pacman
    private int pacmand_x, pacmand_y; // Bewegungsrichtung von Pacman
    private int req_dx, req_dy; // Wohin Pacman als nächstes drehen soll
    private int view_dx, view_dy; // Änderung des Bildes, in die selbe Richtung
    private int currentSpeed; // bezieht sich auf die Anzahl unterschiedlicher Geschwindigkeiten
    private short[] screenData; // Hier wird die Information über das Spielfeld gespeichert
    private Timer timer;

    // Das ist der Entry-Point des Programms
    // Hier wird alles geladen, damit das Spiel laufen kann, es beginnt jedoch erst, falls 's' gedrückt wird
    public Board() {
        loadImages();
        initVariables();
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter()); // Hier wird der "Sensor" für die Tastatureingabe aktiviert
        setFocusable(true);
        setBackground(Color.black); // Hintergrundfarbe
    }

    private void initVariables() {
        // Hier werden alle wichtigen Variabeln in den Speicher geladen
        screenData = new short[feldAnzahl * feldAnzahl];
        feldFarbe = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        // In jedem Index dieser Array ist Geisterspezifische Information enthalten
        // Dies könnte und sollte man mit einer Klasse austauschen
        geisterArray = new Geist[maxGeisterAnzahl];
        for (int i = 0; i < geisterArray.length; ++i)
            geisterArray[i] = new Geist();

        dx = new int[4];
        dy = new int[4];
        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }

    private void animation() {
        pacAnimCount--;
        if (pacAnimCount <= 0) {
            pacAnimCount = animationsDauer;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (animationsAnzahl -1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }

    private void playGame(Graphics2D g2d) {
        if (tot) {
            death();
        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {
        // Das hier ist der Infobildschirm, hier beginnt das Spiel
        g2d.setColor(new Color(0, 32, 48));
        g2d.fillRect(50, bildschirmGroese / 2 - 30, bildschirmGroese - 100, 50);
        g2d.setColor(Color.white);
        g2d.drawRect(50, bildschirmGroese / 2 - 30, bildschirmGroese - 100, 50);

        String s = "Press s to start.";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = this.getFontMetrics(small);

        g2d.setColor(Color.white);
        g2d.setFont(small);
        g2d.drawString(s, (bildschirmGroese - metr.stringWidth(s)) / 2, bildschirmGroese / 2);
    }

    private void drawScore(Graphics2D g) {
        // Hier wird die Punkteanzal immer wieder geupdated
        int i;
        String s;
        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, bildschirmGroese / 2 + 96, bildschirmGroese + 16);
        // Für jedes Leben, das noch übrig ist wird ein Pacman gezeichnet
        for (i = 0; i < leben; i++) {
            g.drawImage(pacman3left, i * 28 + 8, bildschirmGroese + 1, this);
        }
    }

    private void checkMaze() {
        short i = 0;
        boolean finished = true;
        // Hier wird nachgeschaut ob alle Punkte gegessen wurden
        while (i < feldAnzahl * feldAnzahl && finished) {
            if ((screenData[i] & 48) != 0) {
                finished = false;
            }
            i++;
        }
        // Wurden alle Punkte gegessen, dann
        if (finished) {
            // wird die Punktzahl um 50 erhöht
            score += 50;
            // Wird ein neuer Geist generiert
            if (geisterAnzahl < maxGeisterAnzahl) {
                geisterAnzahl++;
            }
            // Die Geschwindigkeit der Geister wird potentiell erhöht
            // Nur potentiell, da es immer noch eine zufällige Zahl ist
            if (currentSpeed < maximaleGeschwindigkeit) {
                currentSpeed++;
            }
            // Die Runde wird mit einem neuem Level gestartet
            initLevel();
        }
    }

    private void death() {

        leben--;

        if (leben == 0) {
            imSpiel = false;
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {

        short i;
        int pos;
        int count;

        for (i = 0; i < geisterAnzahl; i++) {
            if (geisterArray[i].x % feldGroese == 0 && geisterArray[i].y % feldGroese == 0) {

                pos = geisterArray[i].x / feldGroese + feldAnzahl * (geisterArray[i].y / feldGroese);

                count = 0;

                /*
                 * This can be compressed into something more concise.
                 */
                if ((screenData[pos] & 1) == 0 && geisterArray[i].dx != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && geisterArray[i].dy != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && geisterArray[i].dx != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && geisterArray[i].dy != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        geisterArray[i].dx = 0;
                        geisterArray[i].dy = 0;
                    } else {
                        geisterArray[i].dx = -geisterArray[i].dx;
                        geisterArray[i].dy = -geisterArray[i].dy;
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    geisterArray[i].dx = dx[count];
                    geisterArray[i].dy = dy[count];
                }

            }

            geisterArray[i].x = geisterArray[i].x + (geisterArray[i].dx * geisterArray[i].geschwindigkeit);
            geisterArray[i].y = geisterArray[i].y + (geisterArray[i].dy * geisterArray[i].geschwindigkeit);
            drawGhost(g2d, geisterArray[i].x + 1, geisterArray[i].y + 1);

            if (pacman_x > (geisterArray[i].x - 12) && pacman_x < (geisterArray[i].x + 12)
                    && pacman_y > (geisterArray[i].y - 12) && pacman_y < (geisterArray[i].y + 12)
                    && imSpiel) {

                tot = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {

        g2d.drawImage(geist, x, y, this);
    }

    // Diese Funktion muss ich mir noch gut anschauen
    private void movePacman() {
        int pos;
        short ch;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % feldGroese == 0 && pacman_y % feldGroese == 0) {
            pos = pacman_x / feldGroese + feldAnzahl * (pacman_y / feldGroese);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + pacmanGeschwindigkeit * pacmand_x;
        pacman_y = pacman_y + pacmanGeschwindigkeit * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {

        if (view_dx == -1) {
            drawPacnanLeft(g2d);
        } else if (view_dx == 1) {
            drawPacmanRight(g2d);
        } else if (view_dy == -1) {
            drawPacmanUp(g2d);
        } else {
            drawPacmanDown(g2d);
        }
    }

    private void drawPacmanUp(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1up, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanDown(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1down, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacnanLeft(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1left, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawPacmanRight(Graphics2D g2d) {

        switch (pacmanAnimPos) {
            case 1:
                g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1right, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < bildschirmGroese; y += feldGroese) {
            for (x = 0; x < bildschirmGroese; x += feldGroese) {

                g2d.setColor(feldFarbe);
                g2d.setStroke(new BasicStroke(2));

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + feldGroese - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + feldGroese - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + feldGroese - 1, y, x + feldGroese - 1,
                            y + feldGroese - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + feldGroese - 1, x + feldGroese - 1,
                            y + feldGroese - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(punktFarbe);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                i++;
            }
        }
    }

    private void initGame() {
        // Die Variabeln werden hier verändert
        leben = 3;
        score = 0;
        initLevel();
        geisterAnzahl = 6;
        currentSpeed = 3;
    }

    private void initLevel() {
        // Hier werden die Daten vom Level auf den Bildschirm kopiert
        int i;
        for (i = 0; i < feldAnzahl * feldAnzahl; i++) {
            // Hier kann das das default-Feld nochmals auf den Bildschirm kopiert werden
            // !! Dies ist sehr intelligent
            screenData[i] = levelData[i];
        }
        continueLevel();
    }

    private void continueLevel() {

        short i;
        int dx = 1;
        int random;
        // In diesem for-loop werden die Geister gespawnt
        // Zudem bekommen sie eine Geschwindigkeit
        for (i = 0; i < geisterAnzahl; i++) {
            // Dies sind die Spawn Koordinaten
            geisterArray[i].y = 4 * feldGroese;
            geisterArray[i].x = 2 * feldGroese;
            // Wozu sind diese beiden Variablen
            geisterArray[i].dy = 0;
            geisterArray[i].dx = dx;
            dx = -dx;
            // Hier wird jedem Geist eine eigene Geschwindigkeit zugeordnet
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            geisterArray[i].geschwindigkeit = zugelasseneGeschwindigkeiten[random];
        }
        // Spawn Koordidaten von Pacman
        pacman_x = 7 * feldGroese;
        pacman_y = 11 * feldGroese;
        // Anfangsgeschwindigkeit von Pacman
        pacmand_x = 0;
        pacmand_y = 0;
        // Die Variablen, die die Geschwindigkeit von Pacman ändern werden auf Null gesetzt
        req_dx = 0;
        req_dy = 0;
        // Wozun sind diese Variablen
        view_dx = -1;
        view_dy = 0;
        tot = false;
    }

    private void loadImages() {
        // Hier werden alle benötigten Bilder in das Spiel geladen
        geist = new ImageIcon("src/resources/images/ghost.png").getImage();

        pacman1up = new ImageIcon("src/resources/images/meineKunst/pacman_oben0.png").getImage();
        pacman2up = new ImageIcon("src/resources/images/meineKunst/pacman_oben1.png").getImage();
        pacman3up = new ImageIcon("src/resources/images/meineKunst/pacman_oben2.png").getImage();
        pacman4up = new ImageIcon("src/resources/images/meineKunst/pacman_oben3.png").getImage();

        pacman1down = new ImageIcon("src/resources/images/meineKunst/pacman_unten0.png").getImage();
        pacman2down = new ImageIcon("src/resources/images/meineKunst/pacman_unten1.png").getImage();
        pacman3down = new ImageIcon("src/resources/images/meineKunst/pacman_unten2.png").getImage();
        pacman4down = new ImageIcon("src/resources/images/meineKunst/pacman_unten3.png").getImage();

        pacman1left = new ImageIcon("src/resources/images/meineKunst/pacman_links0.png").getImage();
        pacman2left = new ImageIcon("src/resources/images/meineKunst/pacman_links1.png").getImage();
        pacman3left = new ImageIcon("src/resources/images/meineKunst/pacman_links2.png").getImage();
        pacman4left = new ImageIcon("src/resources/images/meineKunst/pacman_links3.png").getImage();

        pacman1right = new ImageIcon("src/resources/images/meineKunst/pacman_rechts0.png").getImage();
        pacman2right = new ImageIcon("src/resources/images/meineKunst/pacman_rechts1.png").getImage();
        pacman3right = new ImageIcon("src/resources/images/meineKunst/pacman_rechts2.png").getImage();
        pacman4right = new ImageIcon("src/resources/images/meineKunst/pacman_rechts3.png").getImage();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);
        animation();

        if (imSpiel) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        g2d.drawImage(ii, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        repaint();
    }

    class TAdapter extends KeyAdapter {
        /*
         * Diese Funktion wird immer aufgerufen, wenn eine Taste gedrückt wird.
         * Die Variable key beinhaltet Informationen über welche Taste gerade gedrückt worden ist.
         * Je nach dem welche Taste gedrückt wurde wird, passieren unterschiedliche Sachen.
         */

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (imSpiel) {
                switch (key) {
                    case 65:
                    case KeyEvent.VK_LEFT:
                        req_dx = -1;
                        req_dy = 0;
                        break;
                    case 87:
                    case KeyEvent.VK_UP:
                        req_dx = 0;
                        req_dy = -1;
                        break;
                    case 68:
                    case KeyEvent.VK_RIGHT:
                        req_dx = 1;
                        req_dy = 0;
                        break;
                    case 83:
                    case KeyEvent.VK_DOWN:
                        req_dx = 0;
                        req_dy = 1;
                        break;
                    // Das Spiel wird beendet
                    case KeyEvent.VK_ESCAPE:
                        if (timer.isRunning()) {
                            imSpiel = false;
                        }
                        break;
                    // Das Spiel wird gestoppt
                    case 10: // Enter-Taste
                    case 80: // p-Taste
                        if (timer.isRunning()) {
                            timer.stop();
                        } else {
                            timer.start();
                        }
                        break;
                }
            } else {
                if (key == 's' || key == 'S') {
                    imSpiel = true;
                    initGame();
                }
            }
        }
    }

}
