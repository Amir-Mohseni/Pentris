package pentris.game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import pentris.gui.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Game extends JPanel implements Runnable, MouseListener, MouseMotionListener {
    
    public static final int wantedFps = 60;
    public static final int WIDTH = 400, HEIGHT = 720;
    private boolean running;
    private GuiScreen screen;
    private Thread game;
    private final BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private Input input;
    private static Game runningGame;

    /**
     * Constructor.
     * Creates a game isntance.
     */
    public Game() {
        runningGame = this;
        running = false;
        screen = GuiScreen.getInstance();
        input = new Input();
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(input);
        addMouseListener(this);
        addMouseMotionListener(this);
        screen.addPanel("Menu", new MainMenuPanel());
        screen.addPanel("Player", new PlayerPanel());
        screen.addPanel("Bot", new BotPanel());
        screen.addPanel("Best Batch", new GeneticsPanel());
        screen.addPanel("Bot Menu", new BotMenuPanel());
        screen.addPanel("Highscores", new HighscoresPanel());
        screen.setCurrentPanel("Menu");
    }

    /**
     * Gets the current active game instance
     * @return The active game instance
     */
    public static Game getInstance() {
        return runningGame;
    }

    public static final int
    PLAYER_HIGHSCORES = 0,
    BOT_HIGHSCORES = 1;

    public static final String HIGHSCORES_PATH = "src/pentris/Highscores/";

    /**
     * Updates the highscore list by adding a new highscore to it
     * @param type The type of highscore to add to, either {@code Game.PLAYER_HIGHSCORES} or {@code Game.BOT_HIGHSCORES}
     * @param value The new value to add to the highscore list
     */
    public static void updateHighscores(int type, int value) {
        try {
            String saveName = "NAME NOT FOUND";
            String path = HIGHSCORES_PATH;
            if (type == PLAYER_HIGHSCORES) {
                path += "player-highscores.txt";
                saveName = Username.getInstance().getUsername();
            } else if (type == BOT_HIGHSCORES) {
                path += "bot-highscores.txt";
                saveName = Username.getInstance().getBotName();
            }
            File f = new File(path);
            if (f.createNewFile()) {
                // For java to recognize that a new file was created
            }
            BufferedReader br = new BufferedReader(new FileReader(f));
            ArrayList<String> lines = new ArrayList<String>();
            while (br.ready()) {
                lines.add(br.readLine());
            }
            br.close();

            // Add new highscore
            lines.add(value+" "+saveName);

            String data = "";
            for (String line : lines){
                data += line + "\n";
            }

            FileWriter fw = new FileWriter(f);
            fw.append(data);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }

    /**
     * Resets the highscore list of a given type
     * @param type The type of highscore to reset, either {@code Game.PLAYER_HIGHSCORES} or {@code Game.BOT_HIGHSCORES}
     */
    public static void resetHighscores(int type) {
        try {
            String path = HIGHSCORES_PATH;
            if (type == PLAYER_HIGHSCORES) {
                path += "player-highscores.txt";
            } else if (type == BOT_HIGHSCORES) {
                path += "bot-highscores.txt";
            }
            File f = new File(path);
            FileWriter fw = new FileWriter(f);
            String empty = "";
            for (int i=0; i<10; i++) {
                empty += ". .";
                if (i != 9) {
                    empty += "\n";
                }
            }
            fw.append(empty);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current best highscore in the Game for a given type
     * @param type The type of highscore to get the best from, either {@code Game.PLAYER_HIGHSCORES} or {@code Game.BOT_HIGHSCORES}
     * @return The best highscore in that type
     */
    public static int getCurrenctHighscore(int type) {
        try {
            String path = HIGHSCORES_PATH;
            if (type == PLAYER_HIGHSCORES) {
                path += "player-highscores.txt";
            } else if (type == BOT_HIGHSCORES) {
                path += "bot-highscores.txt";
            }
            File f = new File(path);
            if (f.createNewFile()) {
                // To notify java
            }
            BufferedReader br = new BufferedReader(new FileReader(f));
            String best = br.readLine();
            if (best != null){
                String[] line = best.split(" ");
                if (line[0].equals(".")) {
                    br.close();
                    return 0;
                } else {
                    br.close();
                    return Integer.parseInt(line[0]);
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Overriden method.
     * Runs the main game loop.
     */
    @Override
    public void run() {

        Username.getInstance().setUsername(JOptionPane.showInputDialog(new JFrame(),"Enter your name"));

        double updateNanos = 1000000000.0 / wantedFps;
        double then = System.nanoTime();
        double unprocessed = 0;

        while (running) {
            double now = System.nanoTime();
            unprocessed += (now-then) / updateNanos;
            then = now;
            boolean shouldRender = false;

            while (unprocessed >= 1) {
                update();
                unprocessed--;
                shouldRender = true;
            }

            if (shouldRender) {
                render();
                shouldRender = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Starts the main game loop
     */
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        game = new Thread(this, "game");
        game.start();
    }

    /**
     * Stops the main game loop and exits the program
     */
    public synchronized void stop() {
        if (!running) {
            return;
        }
        running = false;
        System.exit(0);
    }

    /**
     * Called at fps speed, does all ui and game logic
     */
    public void update() {
        screen.update();
        input.removeFromPressed();
    }

    /**
     * Called at fps speed, renders the entire game
     */
    public void render() {
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        screen.render(g2);
        g2.dispose();

        g2 = (Graphics2D) getGraphics();
        g2.drawImage(image, null, 0, 0);
        g2.dispose();
    }

    /**
     * Overriden method.
     * Used when a mouse is dragged on the screen.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        screen.mouseDragged(e);
    }

    /**
     * Overriden method.
     * Used when a mouse is moved on the screen.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        screen.mouseMoved(e);
    }

    /**
     * Overriden method.
     * Used when a mouse is pressed on the screen.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        screen.mousePressed(e);
    }

    /**
     * Overriden method.
     * Used when a mouse is released on the screen.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        screen.mouseReleased(e);
    }

    /**
     * Checks if a key was pressed.
     * @param key The key to check. see {@code Input}
     * @return {@code true} if it was pressed and {@code false} otherwise
     */
    public boolean checkKeyPressed(int key) {
        return input.isPressed(key);
    }

    /**
     * Checks if a key is being held down.
     * @param key The key to check. see {@code Input}
     * @return {@code true} if it is being helf down and {@code false} otherwise
     */
    public boolean checkKeyDown(int key) {
        return input.isDown(key);
    }

    //
    // Unused
    //
    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}
    
}
