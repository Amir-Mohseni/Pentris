package pentris.gui;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.*;

public class GuiButton {

    private String text;
    private State currentState;
    private Rectangle box;
    private ArrayList<ActionListener> actionListeners;
    private Color releasedColor, pressedColor, hoverColor;

    private enum State {
        PRESSED,
        HOVER,
        RELEASED
    }

    /**
     * Constructor.
     * Creates a new button instance
     * @param x The x position of the top left corner of the button
     * @param y The y position of the top left corner of the button
     * @param width The width of the button
     * @param height The height of the button
     */
    public GuiButton(int x, int y, int width, int height) {
        text = "";
        box = new Rectangle(x, y, width, height);
        currentState = State.RELEASED;
        actionListeners = new ArrayList<ActionListener>();
        pressedColor = new Color(206, 18, 18);
        releasedColor = new Color(208, 28, 141);
        hoverColor = new Color (255,0, 119);
    }

    /**
     * Updates the button (does nothing)
     */
    public void update() {}

    /**
     * Renders the button based on the current state of the button
     * @param g2 The {@code Graphics2D} object to draw to
     */
    public void render(Graphics2D g2) {
        if (currentState == State.RELEASED) {
            g2.setColor(releasedColor);
            g2.fill(box);
        } else if (currentState == State.PRESSED) {
            g2.setColor(pressedColor);
            g2.fill(box);
        } else if (currentState == State.HOVER) {
            g2.setColor(hoverColor);
            g2.fill(box);
        }
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("TimesRoman", Font.BOLD, 25));
        g2.drawString(text, box.x + box.width/10, box.y+box.height/2+12);
    }

    /**
     * Adds an action listener to this button
     * @param listener The action listener to add
     */
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Sets the text displayed by this button
     * @param text The text the button will display
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Called when the mouse was pressed.
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        if (box.contains(e.getPoint())) {
            currentState = State.PRESSED;
        }
    }

    /**
     * Called when the mouse was released.
     * If the mouse was released on the button, it performs actions for all action listeners
     * @param e
     */
    public void mouseReleased(MouseEvent e) {
        if (box.contains(e.getPoint())) {
            for (int i=0; i<actionListeners.size(); i++) {
                actionListeners.get(i).actionPerformed(null);
            }
        }
        currentState = State.RELEASED;
    }

    /**
     * Called when the mouse was moved
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        if (box.contains(e.getPoint())) {
            currentState = State.HOVER;
        } else {
            currentState = State.RELEASED;
        }
    }

    /**
     * Called when the mouse was dragged
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
        if (box.contains(e.getPoint())) {
            currentState = State.PRESSED;
        } else {
            currentState = State.RELEASED;
        }
    }
}