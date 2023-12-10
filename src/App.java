import pentris.game.Game;

import javax.swing.JFrame;
import java.awt.*;

public class App {
    public static void main(String[] args) throws Exception {

        Game game = new Game();

        JFrame frame = new JFrame("Pentris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.getContentPane().setBackground(Color.BLACK);
        game.start();
    }
}
