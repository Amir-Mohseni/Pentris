package pentris.gui;

import pentris.game.*;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.Color;

public class GeneticsPanel extends GuiPanel {

    private Main main;

    private int buttonWidth = 5*Game.WIDTH/11;
    private int buttonHeight = Game.HEIGHT/10;
    private int buttonSpacing = Game.HEIGHT/15;

    private double gameOverOverlayHeight;

    /**
     * Constructor.
     * Creates the best batch panel
     */
    public GeneticsPanel() {
        super();
        reset();
        // Button for going back to the menu
        GuiButton menuButton = new GuiButton(3*Game.WIDTH/4-buttonWidth/2, 2*Game.HEIGHT/3, buttonWidth, buttonHeight);
        menuButton.setText("MENU");
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Menu");
            }
        });
        // Button for reseting the current game
        GuiButton resetButton = new GuiButton(3*Game.WIDTH/4-buttonWidth/2, 2*Game.HEIGHT/3+buttonHeight+buttonSpacing, buttonWidth, buttonHeight);
        resetButton.setText("RESET");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        addButton(menuButton);
        addButton(resetButton);
    }

    /**
     * Overriden from GuiPanel.
     * Runs the game loop and game over animation
     */
    @Override
    public void update() {
        main.step();
        if (main.isOver()) {
            gameOverOverlayHeight += Main.cellSize/2.0;
            if (gameOverOverlayHeight >= Main.cellSize*18) {
                gameOverOverlayHeight = Main.cellSize*18;
            }
        }
    }

    /**
     * Overriden from GuiPanel.
     * Draws this panel and the current game
     */
    @Override
    public void render(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,Game.WIDTH,Game.HEIGHT);
        super.render(g2);
        main.render(g2);
        if (main.isOver()) {
            g2.setColor(new Color(200, 200, 200));
            g2.fillRect(Main.boardX, Main.boardY+Main.cellSize*18 - (int) gameOverOverlayHeight, Main.cellSize*5, (int) gameOverOverlayHeight);
        }
    }

    /**
     * Overriden from GuiPanel.
     * Resets this panel and its current game
     */
    @Override 
    public void reset() {
        main = new Main();
        main.enableBot();
        main.enableBestBatch();
        gameOverOverlayHeight = 0;
    }
}
