package pentris.gui;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.MouseEvent;

public class GuiPanel {

    private ArrayList<GuiButton> buttons;

    /**
     * Constructor.
     * Creates a new Panel
     */
    public GuiPanel() {
        buttons = new ArrayList<GuiButton>();
    }

    /**
     * Used for potential needed reseting of a panel when the screen returns to it
     */
    public void reset() {}

    /**
     * Updates all the buttons in this panel
     */
    public void update() {
        for (GuiButton b : buttons) {
            b.update();
        }
    }

    /**
     * Renders all the buttons in this panel
     * @param g2 The {@code Graphics2D} object to draw to
     */
    public void render(Graphics2D g2) {
        for (GuiButton b : buttons) {
            b.render(g2);
        }
    }

    /**
     * Adds a new button to the panel
     * @param b The button to add
     */
    public void addButton(GuiButton b) {
        buttons.add(b);
    }

    /**
     * Removes a button from the current panel
     * @param b The button to remove
     */
    public void removeButton(GuiButton b) {
        if (buttons.indexOf(b) != -1) {
            buttons.remove(b);
        }
    }

    /**
     * Called when the mouse is pressed.
     * Calls the mousePressed for all buttons in the panel.
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        for (GuiButton b : buttons) {
            b.mousePressed(e);
        }
    }

    /**
     * Called when the mouse is released.
     * Calls the mouseReleased for all buttons in the panel.
     * @param e
     */
    public void mouseReleased(MouseEvent e) {
        for (GuiButton b : buttons) {
            b.mouseReleased(e);
        }
    }

    /**
     * Called when the mouse is dragged.
     * Calls the mouseDragged for all buttons in the panel.
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
        for (GuiButton b : buttons) {
            b.mouseDragged(e);
        }
    }

    /**
     * Called when the mouse is moved.
     * Calls the mouseMoved for all buttons in the panel.
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        for (GuiButton b : buttons) {
            b.mouseMoved(e);
        }
    }

}
