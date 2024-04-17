package com.zetcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Board extends JPanel implements ActionListener {

    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14);
    private final Color punktFarbe = new Color(250, 100, 0);
    private final int feldGroese = Config.getFeldGroese(); // sollte dynamisch gestaltet werden
    private final int feldAnzahl = Config.getFeldAnzahl(); // sollte auch dynamisch gestaltet werden
    private final int bildschirmGroese = feldAnzahl * feldGroese; // wird demzufolge auch dynamisch sein
    private final int animationsDauer = 2;

    private final int maxGeisterAnzahl = 12;

    // In dieser Array befinden sich die Informationen über das Spielfeld
    // Hier sind keine Informationen über die Lage von den Geistern oder Pacman enthalten
    // !!Es handelt sich um eine eindimensionale array, unglaublich!!
    private final short[] levelData = {
            19, 26, 26, 26, 18, 18, 26, 26, 18, 18, 18, 22,  2,  2, 19, 18, 18, 18, 26, 26, 18, 18, 26, 26, 26, 22,
            21,  0,  0,  0, 17, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 20,  0,  0,  0, 21,
            21,  0,  0,  0, 17, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 20,  0,  0,  0, 21,
            17, 18, 26, 26, 24, 24, 26, 26, 24, 24, 16, 20,  0,  0, 17, 16, 24, 24, 26, 26, 24, 24, 26, 26, 18, 20,
            17, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 16, 18, 18, 16, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 20,
            17, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 20,
            17, 20,  0,  0, 19, 18, 18, 18, 26, 26, 16, 16, 16, 16, 16, 16, 26, 26, 18, 18, 18, 22,  0,  0, 17, 20,
            17, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 24, 24, 24, 24, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 20,
            17, 20,  0,  0, 17, 16, 16, 24, 18, 18, 20,  0,  0,  0,  0, 17, 18, 18, 24, 16, 16, 20,  0,  0, 17, 20,
            25, 24, 26, 26, 24, 16, 20,  0, 17, 16, 20,  0,  0,  0,  0, 17, 16, 20,  0, 17, 16, 24, 26, 26, 24, 28,
             0,  0,  0,  0,  0, 17, 20,  0, 17, 16, 16, 18, 18, 18, 18, 16, 16, 20,  0, 17, 20,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 17, 20,  0, 17, 16, 24, 24, 16, 16, 24, 24, 16, 20,  0, 17, 20,  0,  0,  0,  0,  0,
             2,  2,  2,  2,  2, 16, 20,  0, 17, 20,  3,  2,  0,  0,  2,  6, 17, 20,  0, 17, 16,  2,  2,  2,  2,  2,
             8,  8,  8,  8,  8, 16, 20,  0, 17, 20,  9,  8,  8,  8,  8, 12, 17, 20,  0, 17, 16,  8,  8,  8,  8,  8,
             0,  0,  0,  0,  0, 17, 20,  0, 17, 16, 26, 26, 26, 26, 26, 26, 16, 20,  0, 17, 20,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 17, 20,  0, 17, 20,  0,  0,  0,  0,  0,  0, 17, 20,  0, 17, 20,  0,  0,  0,  0,  0,
            19, 18, 18, 26, 18, 16, 20,  0, 17, 20,  0,  0,  0,  0,  0,  0, 17, 20,  0, 17, 16,  18, 26, 18, 18, 22,
            17, 16, 28,  0, 17, 16, 16, 18, 16, 16, 18, 22,  0,  0, 19, 18, 16, 16, 18, 16, 16, 20,  0, 25, 16, 20,
            17, 20,  0,  0, 17, 24, 24, 24, 24, 16, 16, 20,  0,  0, 17, 16, 16, 24, 24, 24, 24, 20,  0,  0, 17, 20,
            17, 24, 18, 18, 20,  0,  0,  0,  0, 17, 24, 24, 26, 26, 24, 24, 20,  0,  0,  0,  0, 17, 18, 18, 24, 20,
            21,  0, 17, 16, 20,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 17, 16, 20,  0, 21,
            21,  0, 17, 16, 20,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 17, 16, 20,  0, 21,
            21,  0, 17, 16, 16, 18, 18, 22,  0, 17, 18, 22,  0,  0, 19, 18, 20,  0, 19, 18, 18, 16, 16, 20,  0, 21,
            21,  0, 25, 24, 24, 24, 16, 20,  0, 17, 16, 20,  0,  0, 17, 16, 20,  0, 17, 16, 24, 24, 24, 28,  0, 21,
            21,  0,  0,  0,  0,  0, 17, 16, 18, 16, 16, 24, 26, 26, 24, 16, 16, 18, 16, 20,  0,  0,  0,  0,  0, 21,
            25, 26, 26, 26, 26, 26, 24, 24, 24, 24, 28,  0,  0,  0,  0, 25, 24, 24, 24, 24, 26, 26, 26, 26, 26, 28
    };

    // Hier sind alle möglichen Geistergeschwindigkeiten enthalten
    private final int[] zugelasseneGeschwindigkeiten = {2, 3, 4, 6, 8};
    private final int maximaleGeschwindigkeit = zugelasseneGeschwindigkeiten.length;
    private Dimension d;
    private Color feldFarbe;
    private boolean imSpiel = false;
    private boolean tot = false;
    private int pacAnimCount = animationsDauer;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int geisterAnzahl = 6;
    private int leben, score;
    private int[] dx, dy;
    private Geist[] geisterArray; // Hier sind alle Geister enthalten
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
        d = new Dimension(624, 690);
        // In jedem Index dieser Array ist Geisterspezifische Information enthalten
        // Dies könnte und sollte man mit einer Klasse austauschen
        geisterArray = new Geist[maxGeisterAnzahl];
        for (int i = 0; i < geisterArray.length; ++i) {
            geisterArray[i] = new Geist();
            String[] farben = {"rot", "pink", "blau", "orange"};
            Random random = new Random();
            int randomIndex = random.nextInt(farben.length);
            String randomElement = farben[randomIndex];
            // Hier muss eine zufällige Farbe ausgewählt, werden, damit jeder Geist eine andere Farbe hat
            geisterArray[i].oben = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_oben.png").getImage();
            geisterArray[i].unten = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_unten.png").getImage();
            geisterArray[i].links = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_links.png").getImage();
            geisterArray[i].rechts = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_rechts.png").getImage();
        }

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

            int animationsAnzahl = 4;
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
        g.drawString(s, bildschirmGroese / 2 + 230, bildschirmGroese + 20);
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
            // Here we have to pass also the directional information
            drawGhost(g2d, geisterArray[i].x + 1, geisterArray[i].y + 1, i, geisterArray[i].dx, geisterArray[i].dy);

            // Hier wird die Kollision zwischen Pacman und einem Geist registriert
            if (pacman_x > (geisterArray[i].x - 12) && pacman_x < (geisterArray[i].x + 12)
                    && pacman_y > (geisterArray[i].y - 12) && pacman_y < (geisterArray[i].y + 12)
                    && imSpiel) {

                tot = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y, int index, int dir_x, int dir_y) {
        if (dir_x == -1)
            g2d.drawImage(geisterArray[index].links, x, y, this);
        if (dir_x == 1)
            g2d.drawImage(geisterArray[index].rechts, x, y, this);
        if (dir_y == -1)
            g2d.drawImage(geisterArray[index].oben, x, y, this);
        if (dir_y == 1)
            g2d.drawImage(geisterArray[index].unten, x, y, this);

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

        // Hier wird nachgesehen, ob sich Pacmans position genau mit den Feldern, auf denen er gehen kann überlappt
        if (pacman_x % feldGroese == 0 && pacman_y % feldGroese == 0) {
            pos = pacman_x / feldGroese + feldAnzahl * (pacman_y / feldGroese);
            ch = screenData[pos];

            // Hier wird überprüft ob Pacman dabei ist einen Punkt zu essen, ist dies der Fall
            // dann wird der Punkt gelöscht und Pacman bekommt einen Punkt
            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            // Hier wird geschaut ob der Spieler will, dass sich Pacman in eine gewisse Richtung bewegt
            if (req_dx != 0 || req_dy != 0) {
                // Hier wird ermittelt ob Pacman in diese Richtung drehen darf, oder ob sich dort eine Wand befindet
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    // Falls der "Zug legal ist" wird er ausgeführt
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }

            // Hier wird geschaut ob Pacman dabei ist in eine Wand zu "laufen"
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                // Falls dies der Fall ist, wird seine Bewegung auf Null gesetzt -> er bleibt stehen
                // und läuft nicht in die Wand
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        // Hier wird die Position verändert
        int pacmanGeschwindigkeit = 6;
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

                // zeichnet eine linke Wand
                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + feldGroese - 1);
                }
                // zeichnet eine obere Wand
                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + feldGroese - 1, y);
                }
                // zeichnet eine rechte Wand
                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + feldGroese - 1, y, x + feldGroese - 1,
                            y + feldGroese - 1);
                }
                // zeichnet eine untere Wand
                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + feldGroese - 1, x + feldGroese - 1,
                            y + feldGroese - 1);
                }

                // zeichnet einen Punkt
                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(punktFarbe);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }

                // zeichnet einen custom power up
                if ((screenData[i] & 32) != 0) {
                    g2d.setColor(punktFarbe);
                    g2d.fillOval(x+7, y+7, 8, 8);
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
            geisterArray[i].y = 13 * feldGroese;
            geisterArray[i].x = 13 * feldGroese;
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
        pacman_x = 13 * feldGroese;
        pacman_y = 19 * feldGroese;
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
        pacman1up = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_oben0.png").getImage();
        pacman2up = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_oben1.png").getImage();
        pacman3up = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_oben2.png").getImage();
        pacman4up = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_oben3.png").getImage();

        pacman1down = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_unten0.png").getImage();
        pacman2down = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_unten1.png").getImage();
        pacman3down = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_unten2.png").getImage();
        pacman4down = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_unten3.png").getImage();

        pacman1left = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_links0.png").getImage();
        pacman2left = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_links1.png").getImage();
        pacman3left = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_links2.png").getImage();
        pacman4left = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_links3.png").getImage();

        pacman1right = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_rechts0.png").getImage();
        pacman2right = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_rechts1.png").getImage();
        pacman3right = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_rechts2.png").getImage();
        pacman4right = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_rechts3.png").getImage();

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

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Diese Funktion muss man neu definieren, wenn man den Action listener nutzt
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
