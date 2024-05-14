package com.zetcode;
/*
 * Hier werden alle Module importiert, die für das Programm nötig sind.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Board extends JPanel implements ActionListener {
    /*
     * Anfangs werden alle wichigen Variablen deklariert, bzw. initialisiert
     */
    private final Font smallFont = new Font("Helvetica", Font.BOLD, 14); // Schriftart
    private final int feldGroese = Config.getFeldGroese();
    private final int feldAnzahl = Config.getFeldAnzahl();
    // Berechnung der Bildschirmgröße -> daraus Folgt die größe des Spielfeldes
    private final int bildschirmGroese = feldAnzahl * feldGroese;
    private Timer timer;
    private final Dimension d =  new Dimension(624, 690); // Größe des Bildschirms
    private final Color wandFarbe = new Color(5, 100, 5); // Farbe der Wände
    private final Color punktFarbe = new Color(250, 100, 0); // Farbe der Punkte

    // Erstellung einer Instanz der Pacman Klasse
    Pacman pacman = new Pacman();
    // Variablen für die Korrekte Abfolge von Animationen
    private final int animationsDauer = 2;
    private int pacAnimCount = animationsDauer;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    // Wichtige "Pacman Variablen"
    private int pacman_x, pacman_y; // Position von Pacman
    private int pacmand_x, pacmand_y; // Bewegungsrichtung von Pacman
    private int req_dx, req_dy; // Wohin Pacman als nächstes drehen soll
    private int view_dx, view_dy; // Änderung des Bildes, in die selbe Richtung

    // Erstellung einer Instanz der Drop Klasse und Hilfsvariablen
    private Drop[] dropArray;
    private boolean gezeichnet = false;
    private int dropZahl;

    Random random = new Random(); // Für die Generierung zufölliger Zahlen

    /*
     * In dieser Array befinden sich die Informationen über das Spielfeld
     * Hier sind keine Informationen über die Position der Geister, des Pacmans oder der Drops enthalten
     * !!Es handelt sich um eine eindimensionale array, unglaublich!!
     * Fun-fact: Alle diese Zahlen wurden per Hand eingegeben :(
    */

    private final short[] levelData = {
            19, 26, 26, 26, 18, 18, 26, 26, 18, 18, 18, 22,  2,  2, 19, 18, 18, 18, 26, 26, 18, 18, 26, 26, 26, 22,
            21,  0,  0,  0, 17, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 16, 16, 20,  0,  0, 17, 20,  0,  0,  0, 21,
            21,  0,  0,  0, 17, 20,  0,  0, 17, 16, 16, 52,  0,  0, 49, 16, 16, 20,  0,  0, 17, 20,  0,  0,  0, 21,
            17, 18, 26, 26, 24, 24, 26, 26, 24, 24, 16, 20,  0,  0, 17, 16, 24, 24, 26, 26, 24, 24, 26, 26, 18, 20,
            17, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 16, 18, 18, 16, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 20,
            17, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 20,
            17, 20,  0,  0, 51, 18, 18, 18, 26, 26, 16, 16, 16, 16, 16, 16, 26, 26, 18, 18, 18, 54,  0,  0, 17, 20,
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
            21,  0, 57, 24, 24, 24, 16, 20,  0, 17, 16, 20,  0,  0, 17, 16, 20,  0, 17, 16, 24, 24, 24, 60,  0, 21,
            21,  0,  0,  0,  0,  0, 17, 16, 18, 16, 16, 24, 26, 26, 24, 16, 16, 18, 16, 20,  0,  0,  0,  0,  0, 21,
            25, 26, 26, 26, 26, 26, 24, 24, 24, 24, 28,  0,  0,  0,  0, 25, 24, 24, 24, 24, 26, 26, 26, 26, 26, 28
    };
    /*
     * Am Anfang des Spiels ist screenData <=> levelData, jeoch wird sie im Laufe des Spiels verändert.
     * Sobald alle Punkte gefressen wurden wird screenData mit levelData überschrieben. Somit ist levelData die
     * Blaupause der ganzen Map.
     */
    private short[] screenData;

    private Geist[] geisterArray; // Alle Geister sind in dieser Array gespeichert
    private final int maxGeisterAnzahl = 12;
    private int geisterAnzahl = 6;
    // Definierung aller zugellasenen Geistergeschwindigkeiten
    private final int[] zugelasseneGeschwindigkeiten = {2, 3, 4, 6, 8};
    // bezieht sich auf die Anzahl unterschiedlicher Geschwindigkeiten => Länge der obigen Array
    private int currentSpeed;
    // Diese Variable wird im Laufe des Spiels immer größer gemacht, sodass Geister erst im Laufe des Spiels
    // an die schnelleren Geschwindigkeiten kommen können
    private final int maximaleGeschwindigkeit = zugelasseneGeschwindigkeiten.length;
    private int[] dx, dy; // Diese beiden Arrays werden die "legalen" Bewegungsrichtungen der Geister enthalten
    private boolean essbar = false; //
    // Hilfsvariablen für die korrekte Funktionsweise von der Animaiton, falls die Geister essbar sind
    private int animationPos = 0;
    private int animationDelay = 5;
    private int essbarTimeout = 0;

    // Spielspezifische Variablen
    private int score; // Punkteanzahl
    private boolean imSpiel = false;
    private boolean tot = false;


    public Board() {
        /*
         * Diese "Methode" wird ausgeführt, sobald eine neue Instanz der Board Klasse erstellt wird
         * Hier werden alle Resourcen geladen, damit das Spiel laufen kann,
         * es beginnt jedoch erst, falls 's' gedrückt wird
         */
        initVariables();
        loadImages();
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter()); // Erstellung der "Tastaturüberwachung"
        setFocusable(true);
        setBackground(Color.black); // Hintergrundfarbe
    }

    private void initVariables() {
        // Alle Arrays bekommen hier ihre Länge zugewiesen
        screenData = new short[feldAnzahl * feldAnzahl];
        geisterArray = new Geist[maxGeisterAnzahl];
        dropArray = new Drop[4];
        dx = new int[4];
        dy = new int[4];
        // Zudem wird der Timer erstellt und gestartet
        timer = new Timer(40, this);
        timer.start();
        // (Meinen Berechnungen nach sollte das Spiel ca. 25FPS haben)
    }

    @Override
    public void addNotify() {
        super.addNotify();
        initGame();
    }

    private void animation() {
        /*
         * Diese Methode übernimmt die Abfolge der Animationen. Es gibt 4 zustände, deshalb bracht es diese Methode.
         * Sie sorgt dafür, dass die Variable pacmanAnimPos, in gleichen Zeitabständen von 0 auf 3 und wieder zurück geht.
         * 0 -> 1 -> 2 -> 3 -> 2 -> 1 -> 0 und so weiter
         */
        pacAnimCount--;
        if (pacAnimCount <= 0) {
            pacAnimCount = animationsDauer;
            pacmanAnimPos += pacAnimDir;

            int animationsAnzahl = 4;
            if (pacmanAnimPos == (animationsAnzahl -1) || pacmanAnimPos == 0) {
                pacAnimDir = -pacAnimDir;
            }
        }
    }
    // Immer das Gegenteil
    private void geisterAnimation() {
        // Da die "essbar Animation nur aus zwei Bildern besteht gibt es nur zwei Zustände
        // Diese Methode gibt lediglich immer das Gegenteil des Wertes "zurück"
        if (animationPos == 0)
            animationPos = 1;
        else animationPos = 0;
    }

    private void playGame(Graphics2D g2d) {
        if (tot) { // Wenn Pacman tot ist endet das Spiel
            death();
        } else { // Ansonsten wird er bewegt und gezeichnet
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d); // Die gester werden einzeln bewegt und gezeichnet
            checkMaze(); // Die ganze "Map" wird neu gezeichnet bzw. geupdated
        }
    }

    private void showIntroScreen(Graphics2D g2d) {
        /*
         * In dieser Methode wird der Anfangsbildschirm gezeichnet.
         * Beim Anfangsbilschirm handelt es sich nur um zwei Rechtecke und den kleinen Infotext, der erklärt
         * wie man das Spiel beginnen kann.
         */
        g2d.setColor(new Color(100, 32, 48));
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
        /*
         * Methode zum updaten der Punkteanzahl und der verbleibenden Leben.
         * Hier wird einfach unten rechts die Punkteanzahl und unten links die verbl. Leben eingeblendet.
         */
        int i;
        String s;
        g.setFont(smallFont);
        g.setColor(new Color(96, 128, 255));
        s = "Score: " + score;
        g.drawString(s, bildschirmGroese / 2 + 230, bildschirmGroese + 20);
        // Für jedes Leben, das noch übrig ist wird ein Pacman gezeichnet
        for (i = 0; i < pacman.leben; i++) {
            g.drawImage(pacman.links[2], i * 28 + 8, bildschirmGroese + 1, this);
        }
    }

    private void checkMaze() {
        // Überprüfung des Spielfeldes
        short i = 0;
        boolean finished = true;

        // Hier wird mit der Wahrscheinlichkeit 1 : 1000 ein Drop gespawnt und das nur wenn es noch keinen gibt.
        if (0 == random.nextInt(1000) && ((screenData[195] & 64) == 0)) {
            screenData[195] += 64;
            gezeichnet = false;
        }
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
        // Ein Leben wird abgezogen, wenn Pacman in Kontakt mit einem Gesit gerät
        pacman.leben--;
        if (pacman.leben == 0) { // Falls er keine Leben hat wird das Spiel gestoppt.
            imSpiel = false;
        }
        continueLevel(); // Ansonsten geht es weiter
    }

    private void moveGhosts(Graphics2D g2d) {

        short i;
        int pos; // Position der Geister
        int count;

        for (i = 0; i < geisterAnzahl; i++) { // Für jeden Geist
            // Dieses Statement überprüft ob sich der Geist genau in der Mitte eines Feldes befindet
            if (geisterArray[i].x % feldGroese == 0 && geisterArray[i].y % feldGroese == 0) {
                // Umrechnung eines Positionsvektors in eine Eindimensionale Position => sehr interessant
                pos = geisterArray[i].x / feldGroese + feldAnzahl * (geisterArray[i].y / feldGroese);
                count = 0;// Dies wird die Anzahl an mögichen Bewegungsrichtungen sein

                /*
                 * All diese IF-statements speichern die Möglichen Wege, die der Geist nehmen kann.
                 */
                if ((screenData[pos] & 1) == 0 && geisterArray[i].dx != 1) { // für links
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && geisterArray[i].dy != 1) { // für oben
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && geisterArray[i].dx != -1) { // für rechts
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && geisterArray[i].dy != -1) { // für unten
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }
                // Hier wird anhand der zugelassenen Bewegungsrichtungen eine zufällige Richtung gewählt
                count = (int) (Math.random() * count);
                if (count > 3) {
                    count = 3;
                }
                geisterArray[i].dx = dx[count];
                geisterArray[i].dy = dy[count];
            }
            //Portal für die Geister
            if (geisterArray[i].y > 300 && geisterArray[i].y < 320) {
                if (geisterArray[i].x > 620)
                    geisterArray[i].x = 4;
                else if (geisterArray[i].x < 4)
                    geisterArray[i].x = 620;
            }
            // Änderung der Position der Geister s = s0 + (Richtung * Geschwindigkeit)
            geisterArray[i].x = geisterArray[i].x + (geisterArray[i].dx * geisterArray[i].geschwindigkeit);
            geisterArray[i].y = geisterArray[i].y + (geisterArray[i].dy * geisterArray[i].geschwindigkeit);
            // Hier wird der Geist auf der Map gezeichnet, ein bisschen verschoben aus Grafikausgabegründen
            drawGhost(g2d, geisterArray[i].x + 1, geisterArray[i].y + 1, i, geisterArray[i].dx, geisterArray[i].dy);

            // Hier wird die Kollision zwischen Pacman und einem Geist registriert
            if (pacman_x > (geisterArray[i].x - 12) && pacman_x < (geisterArray[i].x + 12)
                    && pacman_y > (geisterArray[i].y - 12) && pacman_y < (geisterArray[i].y + 12)
                    && imSpiel) {
                if (essbar) { // Haben sie kollidiert und die Geister sind essbar werden sie gegessen
                    issGeist(i);
                } else // Ansonsten wird dem Pacman ein Leben abgezogen, bzw. das Spiel beendet
                    tot = true;
            }
        }
    }

    private void issGeist(int i) {
        /*
         * Wird ein Geist gefressen, so wird ein neuer erstellt und dem Spieler 100 Punkte überwiesen
         */
        erstelleNeuenGeist(i);
        score += 100;
    }

    private void drawGhost(Graphics2D g2d, int x, int y, int index, int dir_x, int dir_y) {
        /*
         * Grafikausgabe der Geister. Jeder Geist wird einzeln gezeichnet und seine Richtung bestimmt.
         */
        if (! essbar) {
            if (dir_x == -1)
                g2d.drawImage(geisterArray[index].links, x, y, this);
            if (dir_x == 1)
                g2d.drawImage(geisterArray[index].rechts, x, y, this);
            if (dir_y == -1)
                g2d.drawImage(geisterArray[index].oben, x, y, this);
            if (dir_y == 1)
                g2d.drawImage(geisterArray[index].unten, x, y, this);
        } else { // Animation im Falle, dass sie essbar sind
            if (animationPos == 0)
                g2d.drawImage(geisterArray[index].essbar1, x, y, this);
            else g2d.drawImage(geisterArray[index].essbar2, x, y, this);
        }
    }

    private void movePacman() {
        /*
         * Diese Methode übernimmt die korrekte Bewegung von Pacman.
         */
        int pos;
        short ch;

        // Wenn Pacman zurück, aus der Richtung von der er gekommen ist zurück will -> muss nicht geprüft werden
        if (req_dx == -pacmand_x && req_dy == -pacmand_y) {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        } else if (pacman_x % feldGroese == 0 && pacman_y % feldGroese == 0) { // neue Richtungen müssen geprüft werden
            pos = pacman_x / feldGroese + feldAnzahl * (pacman_y / feldGroese); // Umrechnung seiner Position
            ch = screenData[pos]; // Informaiton zum Feld auf dem er sich befindet

            if ((ch & 16) != 0) { // Pacman befinet sich auf einem Punkt
                screenData[pos] = (short) (ch & 15); // ist das gleiche wie -= 16
                score++;
            }
            if ((ch & 32) != 0) { // Pacman frisst einen großen Punkt um Geister zu fressen
                essbar = true;
            }
            if ((ch & 64) != 0) { // Pacman frisst einen Drop
                screenData[195] -= 64;
                score += 150;
            }

            // Ermittlung ob sich Pacman ein eine Richtung bewegen will
            if (req_dx != 0 || req_dy != 0) {
                // Hier wird ermittelt ob Pacman in diese Richtung drehen darf, oder ob sich dort eine Wand befindet
                // Betrifft Bewegungen, bei denen Pacman indirekt auf eine Wand zulaufen will
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

            // Befindet sich dort eine Wand wird seine Bewegung auf Null gesetzt
            // Betrifft nur Bewegungen, bei denen Pacman direkt auf eine Wand zuläuft
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
        // Portal
        if (pacman_x > 630)
            pacman_x = 0; // Ein negativer Wert wäre schöner -> geht aber leider nicht
        else if (pacman_x < -5)
            pacman_x = 630;
        // Veränderung seiner Position s = s0 + Richung * Geschwindigkeit
        int pacmanGeschwindigkeit = 6;
        pacman_x = pacman_x + pacmanGeschwindigkeit * pacmand_x;
        pacman_y = pacman_y + pacmanGeschwindigkeit * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {
        /*
         * Eine sehr vereinfachte und kompakte Weise den Pacman grafisch auszugeben.
         */
        if (view_dx == -1) {
            g2d.drawImage(pacman.links[pacmanAnimPos], pacman_x+1, pacman_y+1, this);
        } else if (view_dx == 1) {
            g2d.drawImage(pacman.rechts[pacmanAnimPos], pacman_x+1, pacman_y+1, this);
        } else if (view_dy == -1) {
            g2d.drawImage(pacman.oben[pacmanAnimPos], pacman_x+1, pacman_y+1, this);
        } else {
            g2d.drawImage(pacman.unten[pacmanAnimPos], pacman_x+1, pacman_y+1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {
        /*
         * Hier wird Eintrag für Eintrag der Array durchgeganen un die Map gezeichnet.
         */
        short i = 0;
        int x, y;

        for (y = 0; y < bildschirmGroese; y += feldGroese) {
            for (x = 0; x < bildschirmGroese; x += feldGroese) {

                g2d.setColor(wandFarbe);
                g2d.setStroke(new BasicStroke(2));
                /*
                 * Um die Wände zu zeichnen, werden zwei Punkte der drawLine Methode übergeben. Sie verbindet
                 * sie dann graphisch mit der kürzesten Distanz, also eine Strecke. Sie müssen natürlich um
                 * + oder - die feldGroese verschoben sein.
                 */
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
                /*
                 * Auch für den Punkt und den custom power-up müssen offsets gemacht werden, sodass sie zentriert sind.
                 */
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

                if ((screenData[i] & 64) != 0 && !gezeichnet) {
                     // Ein zufälliger Drop wird genommen, nur wenn er noch nicht gezeichnet wurde
                    dropZahl = random.nextInt(4);
                    g2d.drawImage(dropArray[dropZahl].bild, x, y, this);
                    gezeichnet = true;
                }
                i++;
            }
        }
    }

    private void initGame() {
        // Variablen werden für den Spielanfang "vorbereitet"
        pacman.leben = 3;
        score = 0;
        initLevel();
        geisterAnzahl = 6;
        currentSpeed = 3;
    }

    private void initLevel() {
        /*
         * In dieser Methode werden die Daten aus levelData mit den Daten innerhalb screenData überschrieben.
         * !!!
         */
        int i;
        for (i = 0; i < feldAnzahl * feldAnzahl; i++) {
            screenData[i] = levelData[i];
        }
        continueLevel();
    }

    private void erstelleNeuenGeist(int i) {
        /*
         * Falls ein Geist gefressen worden ist scheint es, dass ein neuer erstellt wird, das stimmt jedoch nicht.
         * Seine Position wird lediglich zurückgestellt und er bekommt eine neue zufällige Geschwindigkeit.
         * Dies löst das Problem der Gesiteranzahl, die zu groß werden könnte und so hat man auch keine leeren Indizes.
         */
        int dx = 1;
        int random;
        // Dies sind die Spawn Koordinaten
        geisterArray[i].y = 13 * feldGroese;
        geisterArray[i].x = 13 * feldGroese;
        // Gibt die Anfangsrichtung an (rechts)
        geisterArray[i].dy = 0;
        geisterArray[i].dx = dx;
        // Hier wird jedem Geist eine eigene Geschwindigkeit zugeordnet
        random = (int) (Math.random() * (currentSpeed + 1));

        if (random > currentSpeed) {
            random = currentSpeed;
        }

        geisterArray[i].geschwindigkeit = zugelasseneGeschwindigkeiten[random];
    }

    private void continueLevel() {
        // In diesem for-loop werden die Geister gespawnt
        // Zudem bekommen sie eine Geschwindigkeit
        for (short i = 0; i < geisterAnzahl; i++) {
            erstelleNeuenGeist(i);
        }
        // Spawn Koordidaten von Pacman
        pacman_x = 13 * feldGroese;
        pacman_y = 19 * feldGroese;
        // Anfangsgeschwindigkeit von Pacman
        pacmand_x = 0;
        pacmand_y = 0;
        // Die Variablen, die die Bewegungsrichtung von Pacman ändern werden auf Null gesetzt
        req_dx = 0;
        req_dy = 0;
        view_dx = 0;
        view_dy = 0;
        tot = false; // Pacman ist am Anfang des Spiels nicht tot :)
    }

    private void loadImages() {
        /*
         * 32 Bilder werden hier ins Spiel geladen, im Laufe des Spiels werden sie alle verwendet werden.
         * Diese Import-Logik ist sehr kompakt, man hätte auch können alles blind ausschreiben, so ist es jedoch
         * viel einfacher neue Dinge hinzuzufügen.
         */
        // Laden der Bilder für die Geister
        for (int i = 0; i < geisterArray.length; ++i) {
            geisterArray[i] = new Geist();
            String[] farben = {"rot", "pink", "blau", "orange"};
            int randomIndex = random.nextInt(farben.length);
            String randomElement = farben[randomIndex];
            // Hier muss eine zufällige Farbe ausgewählt, werden, damit jeder Geist eine andere Farbe hat
            geisterArray[i].oben = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_oben.png").getImage();
            geisterArray[i].unten = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_unten.png").getImage();
            geisterArray[i].links = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_links.png").getImage();
            geisterArray[i].rechts = new ImageIcon("src/resources/images/meineKunst/geist/"+randomElement+"/geist_rechts.png").getImage();
            geisterArray[i].essbar1 = new ImageIcon("src/resources/images/meineKunst/geist/essbar/geist1.png").getImage();
            geisterArray[i].essbar2 = new ImageIcon("src/resources/images/meineKunst/geist/essbar/geist2.png").getImage();
        }
        // Laden der Bilder für die Drops
        for (int i = 0; i < dropArray.length; ++i) {
            dropArray[i] = new Drop();
            dropArray[i].bild = new ImageIcon("src/resources/images/meineKunst/power_ups/"+(i+1)+".png").getImage();
        }

        // Laden der Bilder für Pacman
        String[] richtungen = {"oben", "unten", "links", "rechts"};
        // Es werden jeweils 4 Bilder für oben, 4 für unten und so weiter hintereinander geladen.
        for (String x : richtungen) {
            for (int i = 0; i <= 3; ++i){
                switch(x) {
                    case "oben":
                        pacman.oben[i] = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_oben"+i+".png").getImage();
                        break;
                    case "unten":
                        pacman.unten[i] = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_unten"+i+".png").getImage();
                        break;
                    case "links":
                        pacman.links[i] = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_links"+i+".png").getImage();
                        break;
                    case "rechts":
                        pacman.rechts[i] = new ImageIcon("src/resources/images/meineKunst/pacman/pacman_rechts"+i+".png").getImage();
                        break;
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        /*
         * Die genutzten Module erforden das Überschreiben dieser Modulinternen Methode. Sie wird automatisch
         * aufgerufen und geht die Programmausführung geht dann gleich in die doDrawing() Methode weiter.
         */
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        /*
         * Der größte Teil der Grafikausgabe wird hier Bewältigt.
         */
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d); // Spielfeld wird ausgegeben
        drawScore(g2d); // Punkteanzahl und Leben werden ausgegeben
        animation(); // Pacman Animaiton wird ausgegeben

        // Diese If-Statements kümmern sich um die Korrekte Abfolge der Geister Animationen
        if(essbar && animationDelay >= 4) { // Jede vier Ticks wird animiert
            geisterAnimation(); // wechselt animation von blau auf weiß oder umgekehrt
            animationDelay = 0;
            essbarTimeout++;
        } else { // Es braucht ein Delay, sonst flackert die Farbe viel zu schnell
            animationDelay ++;
        }
        if (essbarTimeout > 23) { // Sobald die Gester 23 Mal die Farbe gewechselt haben ist fertig
            essbar = false;
            essbarTimeout = 0;
        }
        // Zusammenfassend ist dies ein interessanter Weg um die Animaitionen der Geister zeitabhängig zu machen
        // ohne auf den Timer zuzugreifen. Vielleicht ist es Resourcenaufwendig, aber das ist ja kein Problem.

        if (imSpiel) { // Außer am Anfang wird immer playGame ausgeführt.
            playGame(g2d);
        } else { // Nur am Anfang
            showIntroScreen(g2d);
        }

        // Dies sind Dinge, die vom Modul angefordert werden
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Diese Funktion muss man neu definieren, wenn man den Action listener nutzt => vom Modul erfordert
        repaint();
    }

    class TAdapter extends KeyAdapter {
        /*
         * Diese Funktion wird immer aufgerufen, wenn eine Taste gedrückt wird.
         * Die Variable key beinhaltet Informationen über welche Taste gerade gedrückt worden ist.
         * Je nach dem welche Taste gedrückt wurde, passieren unterschiedliche Sachen.
         */
        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (imSpiel) {
                switch (key) {
                    case 65: // A
                    case KeyEvent.VK_LEFT:
                        req_dx = -1;
                        req_dy = 0;
                        break;
                    case 87: // W
                    case KeyEvent.VK_UP:
                        req_dx = 0;
                        req_dy = -1;
                        break;
                    case 68: // D
                    case KeyEvent.VK_RIGHT:
                        req_dx = 1;
                        req_dy = 0;
                        break;
                    case 83: // S
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
            } else { // Beginn des Spiels
                if (key == 's' || key == 'S') {
                    imSpiel = true;
                    initGame();
                }
            }
        }
    }
}