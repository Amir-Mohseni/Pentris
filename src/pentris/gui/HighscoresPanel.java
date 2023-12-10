package pentris.gui;

import java.awt.event.*;
import java.io.*;

import pentris.game.*;

import java.awt.Graphics2D;
import java.awt.Color;
import java.util.*;

public class HighscoresPanel extends GuiPanel {

    private int buttonWidth = 5*Game.WIDTH/11;
    private int buttonHeight = Game.HEIGHT/10;

    /**
     * Constructor.
     * Creates the highscores panel
     */
    public HighscoresPanel() {
        super();
        // Button for reseting the player highscores
        GuiButton resetPlayerButton = new GuiButton(Game.WIDTH/4-buttonWidth/2, Game.HEIGHT/15, buttonWidth, buttonHeight);
        resetPlayerButton.setText("RESET");
        resetPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game.resetHighscores(Game.PLAYER_HIGHSCORES);
            }
        });
        // Button for reseting the bot highscores
        GuiButton resetBotButton = new GuiButton(3*Game.WIDTH/4-buttonWidth/2, Game.HEIGHT/15, buttonWidth, buttonHeight);
        resetBotButton.setText("RESET");
        resetBotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game.resetHighscores(Game.BOT_HIGHSCORES);
            }
        });
        // Button for going back to the menu
        GuiButton menuButton = new GuiButton(Game.WIDTH/2-buttonWidth/2, 9*Game.HEIGHT/10-buttonHeight/2, buttonWidth, buttonHeight); 
        menuButton.setText("MENU");
        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Menu");
            }
        });

        addButton(resetPlayerButton);
        addButton(resetBotButton);
        addButton(menuButton);
    }

    /**
     * Overriden from GuiPanel.
     * Draws the highscore lists
     */
    @Override
    public void render(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0,Game.WIDTH,Game.HEIGHT);
        super.render(g2);
        g2.setColor(new Color(48, 74, 246));
        g2.drawString("PLAYER", 10, Game.HEIGHT/20);
        g2.drawString("BOT", Game.WIDTH/2+10, Game.HEIGHT/20);
        g2.drawString("S   Name", 10, Game.HEIGHT/4);
        g2.drawString("S   Name", Game.WIDTH/2+10, Game.HEIGHT/4);
        try {
            File f1 = new File(Game.HIGHSCORES_PATH+"player-highscores.txt");
            File f2 = new File(Game.HIGHSCORES_PATH+"bot-highscores.txt");
            if (f1.createNewFile()) {
                Game.resetHighscores(Game.PLAYER_HIGHSCORES);
            }
            if (f2.createNewFile()) {
                Game.resetHighscores(Game.BOT_HIGHSCORES);
            }
            BufferedReader br1 = new BufferedReader(new FileReader(f1));
            BufferedReader br2 = new BufferedReader(new FileReader(f2));
            g2.setColor(new Color(100, 100, 100));

            Map<String, Integer> userScores = new HashMap<>();
            while (br1.ready()) {
                String[] line = br1.readLine().split(" ");
                if (line.length > 1 && (userScores.get(line[1]) == null || userScores.get(line[1]) < Integer.parseInt(line[0]))){
                    userScores.put(line[1], Integer.valueOf(line[0]));
                }
            }
            int i=0;
            List<String> usernames = new ArrayList<>(userScores.keySet());
            Collections.reverse(usernames);
            for (String key : usernames) {
                g2.drawString(userScores.get(key)+"  "+key, 10, Game.HEIGHT/3+40*i);
                i++;
            }

            i = 0;
            while (br2.ready()) {
                String[] line = br2.readLine().split(" ");
                g2.drawString(line[0]+"  "+line[1], Game.WIDTH/2+10, Game.HEIGHT/3+40*i);
                i++;
            }
            br1.close();
            br2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
