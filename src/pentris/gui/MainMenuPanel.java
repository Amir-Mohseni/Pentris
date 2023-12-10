package pentris.gui;

import pentris.game.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

public class MainMenuPanel extends GuiPanel {

    private int buttonWidth = 5*Game.WIDTH/11;
    private int buttonHeight = Game.HEIGHT/10;
    private int buttonSpacing = Game.HEIGHT/20;

    /**
     * Constructor.
     * Creates the main menu panel
     */
    public MainMenuPanel() {
        super();
        GuiButton playerButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, Game.HEIGHT/3, buttonWidth, buttonHeight);
        playerButton.setText("PLAYER");
        GuiButton botButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, Game.HEIGHT/3+buttonHeight+buttonSpacing, buttonWidth, buttonHeight);
        botButton.setText("BOT");
        GuiButton highscoresButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, Game.HEIGHT/3+buttonHeight*2+buttonSpacing*2, buttonWidth, buttonHeight);
        highscoresButton.setText("SCORES");
        GuiButton quitButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, Game.HEIGHT/3+buttonHeight*3+buttonSpacing*3, buttonWidth, buttonHeight);
        quitButton.setText("QUIT");
        // Button for running the game with the player
        playerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Player");
            }
        });
        // Button for going to the bot menu
        botButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Bot Menu");
            }
        });
        // Button for exiting the game
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        // Button for going to the highscores menu
        highscoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Highscores");
            }
        });
        addButton(playerButton);
        addButton(botButton);
        addButton(quitButton);
        addButton(highscoresButton);
    }

    /**
     * Overriden from GuiPanel.
     * Draws the main menu
     */
    @Override
    public void render(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,Game.WIDTH,Game.HEIGHT);

        g2.setColor(new Color(48, 74, 246));
        Rectangle2D rectangle = new Rectangle2D.Double();
        rectangle.setFrame(50,60,300,80);
        g2.setStroke(new BasicStroke(5));
        g2.draw(rectangle);

        Rectangle2D rectangle2 = new Rectangle2D.Double();
        rectangle2.setFrame(140,Game.HEIGHT/6+55,115,40);
        g2.setStroke(new BasicStroke(3));
        g2.draw(rectangle2);

        g2.setColor(new Color(42, 232, 12));
        Rectangle2D rectangle3 = new Rectangle2D.Double();
        rectangle3.setFrame(40,50,320,100);
        g2.setStroke(new BasicStroke(5));
        g2.draw(rectangle3);

        Rectangle2D rectangle4 = new Rectangle2D.Double();
        rectangle4.setFrame(135,Game.HEIGHT/6+50,125,50);
        g2.setStroke(new BasicStroke(3));
        g2.draw(rectangle4);

        super.render(g2);
        g2.setColor(new Color(42, 232, 12));
        g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g2.drawString("The game", 152, Game.HEIGHT/6+80);

        super.render(g2);
        g2.setColor(new Color(42, 232, 12));
        g2.setFont(new Font("TimesRoman", Font.BOLD, 60));
        g2.drawString("PENTRIS", 70, Game.HEIGHT/6);
    }

}