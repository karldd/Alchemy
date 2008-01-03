package alchemy;

import alchemy.ui.AlcToolBar;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

public abstract class AlcModule {

    /** Access to the root */
    protected AlcMain root;
    /** Access to the canvas */
    protected AlcCanvas canvas;
    /** Access to the toolBar */
    protected AlcToolBar toolBar;
    /** ClassLoaded used to load other resources from the plugin */
    private ClassLoader classLoader;
    /** The name of this module */
    private String moduleName;
    /** The type of module  - either CREATE (0) or AFFECT (1) */
    private int moduleType;
    /** The description of this module as documented in the modules plugin.xml file */
    private String description;
    /** The name of the icon used for this modules button as documented in the modules plugin.xml file */
    private String iconName;
    /** The full icon url for this modules button */
    private URL iconUrl;
    /** The index of this module in the "root.creates" or "root.affects" array */
    private int index;
    /** The loaded state of this module - set to true when the module is first selected */
    private boolean loaded = false;

    public AlcModule() {
    }

    // STRUCTURE
    /** Called by the plugin manager once the module is found
     *  Sets global references to the root, canvas, and toolbar */
    final void setGlobals(AlcMain root, AlcCanvas canvas, AlcToolBar toolBar) {
        this.root = root;
        this.canvas = canvas;
        this.toolBar = toolBar;
    }

    /* Called to load the module when first run */
    protected void setup() {
    }

    /** Called when the module is reselected */
    protected void reselect() {
    }

    /** Called when the module is deselected */
    protected void deselect() {
    }

    /** Called after the canvas is cleared */
    protected void cleared() {
    }

    /**
     *  Affect - Process an AlcShape.
     *  Used to process whole shapes, typically those generated from create modules.
     *  Get the shape, processShape it in some way and return and replace the original.
     */
    /*
    public AlcShape processShape(AlcShape shape){
    return shape;
    }
     * /
    /**
     * Affect - Increment an AlcShape.
     *  Used to increment a shape, typically for drawn lines etc...
     *  Typically store the temp shape in a canvas buffer until it gets added on mouse up.
     */
    protected void incrementShape(AlcShape shape) {
    }

    // MODULE DATA
    public String getName() {
        return moduleName;
    }

    protected void setName(String m) {
        moduleName = m;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {
        index = i;
    }

    public int getModuleType() {
        return moduleType;
    }

    protected void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public boolean getLoaded() {
        return loaded;
    }

    protected void setLoaded(boolean l) {
        loaded = l;
    }

    public String getIconName() {
        return iconName;
    }

    protected void setIconName(String n) {
        iconName = n;
    }

    /** Get the Icon URL from within the modules .zip file */
    public URL getIconUrl() {
        return iconUrl;
    }

    /** Set the Icon URL within the modules .zip file */
    protected void setIconUrl(URL url) {
        iconUrl = url;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String n) {
        description = n;
    }

    // MOUSE EVENTS
    // http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/MouseEvent.html
    public void mousePressed(MouseEvent e) {
    //int x = e.getX();
    //int y = e.getY();
    //Point p = e.getPoint();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    // KEY EVENTS
    // http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/KeyEvent.html
    public void keyPressed(KeyEvent e) {
    //char keyChar = e.getKeyChar();
    //int keyCode = e.getKeyCode();
    //String keyText = e.getKeyText(keyCode);
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
