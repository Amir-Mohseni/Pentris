package pentris.gui;

import java.util.HashMap;

import java.awt.event.MouseEvent;

import java.awt.Graphics2D;

public class GuiScreen {

    private static GuiScreen screen;
    private HashMap<String, GuiPanel> panels;
    private String currentPanel;

    /**
     * Constructor.
     * Creates a Screen instance
     */
    public GuiScreen() {
        panels = new HashMap<String, GuiPanel>();
    }

    /**
     * Updates the current active panel of the screen
     */
    public void update() {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).update();
        }
    }

    /**
     * Renders the current active panel of the screen
     * @param g2 The {@code Graphics2D} object to draw to
     */
    public void render(Graphics2D g2) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).render(g2);
        }
    }

    /**
     * Adds a new panel to the game
     * @param panelName The name used for this panel
     * @param panel The panel to add
     */
    public void addPanel(String panelName, GuiPanel panel) {
        panels.put(panelName, panel);
    }

    /**
     * Gets the current active screen instance
     * @return The active screen instance
     */
    public static GuiScreen getInstance() {
        if (screen == null) {
            screen = new GuiScreen();
        }
        return screen;
    }

    /**
     * Sets a new active panel
     * @param panelName The name of the panel to be active
     */
    public void setCurrentPanel(String panelName) {
        currentPanel = panelName;
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).reset();
        }
    }

    /**
     * Executed when the mouse is pressed.
     * Calls mousePressed for the current active panel.
     * @param e 
     */
    public void mousePressed(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mousePressed(e);
        }
    }

    /**
     * Executed when the mouse is released
     * Calls mouseReleased for the current active panel.
     * @param e 
     */
    public void mouseReleased(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mouseReleased(e);
        }
    }

    /**
     * Executed when the mouse is dragged
     * Calls mouseDragged for the current active panel.
     * @param e 
     */
    public void mouseDragged(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mouseDragged(e);
        }
    }

    /**
     * Executed when the mouse is moved
     * Calls mouseMoved for the current active panel.
     * @param e 
     */
    public void mouseMoved(MouseEvent e) {
        if (panels.get(currentPanel) != null) {
            panels.get(currentPanel).mouseMoved(e);
        }
    }
}