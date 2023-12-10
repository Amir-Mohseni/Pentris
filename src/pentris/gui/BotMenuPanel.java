package pentris.gui;

import pentris.game.*;

import java.awt.event.*;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;

public class BotMenuPanel extends GuiPanel {

    private int buttonWidth = 5*Game.WIDTH/11;
    private int buttonHeight = Game.HEIGHT/10;
    private int buttonSpacing = Game.HEIGHT/15;

    /**
     * Constructor.
     * Creates the bot menu panel
     */
    public BotMenuPanel() {
        super();
        GuiButton regularButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, Game.HEIGHT/3, buttonWidth, buttonHeight);
        regularButton.setText("GREEDY");
        GuiButton bestBatchButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, Game.HEIGHT/3+buttonHeight+buttonSpacing, buttonWidth, buttonHeight);
        bestBatchButton.setText("GENETICS");
        GuiButton menuButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, Game.HEIGHT/3+buttonHeight*2+buttonSpacing*2, buttonWidth, buttonHeight);
        menuButton.setText("MENU");
        // Button for running a regular bot game
        regularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Bot");
                Username.getInstance().setBotName("Regular");
            }
        });
        // Button for running the best batch with the bot
        bestBatchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Best Batch");
                Username.getInstance().setBotName("Best Batch");
            }
        });
        // Button for going back to the menu
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Menu");
            }
        });

        addButton(regularButton);
        addButton(bestBatchButton);
        addButton(menuButton);
    }

    /**
     * Overriden from GuiPanel.
     * Draws the bot menu
     */
    @Override
    public void render(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,Game.WIDTH,Game.HEIGHT);
        super.render(g2);
        g2.setColor(new Color(42, 232, 12));
        g2.setFont(new Font("TimesRoman", Font.BOLD, 60));
        g2.drawString("BOT", 135, Game.HEIGHT/6);
    }
    
}