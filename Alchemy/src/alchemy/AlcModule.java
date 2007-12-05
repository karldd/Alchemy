package alchemy;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

public abstract class AlcModule {
    
    /** Access to the root */
    public AlcMain root;
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
    
    
    public AlcModule(){
    }
    
    // STRUCTURE
    /** Called by the plugin manager once the module is found */
    public void setRoot(AlcMain root){
        this.root = root;
    }
    
    /* Called to load the module when first run */
    public void setup(){
    }
    
    public void draw(){
    }
    
    /** Called when the module is reselected */
    public void reselect(){
    }
    
    /** Called when the module is deselected */
    public void deselect(){
    }
    
    /**
     *  Affect - Initialise an AlcShape.
     *  Used to initialise shapes, typically from when the mouse is first pressed down to draw a line.
     */
    public void initialiseShape(AlcShape shape){
    }
    
    /**
     *  Affect - Process an AlcShape.
     *  Used to process whole shapes, typically those generated from create modules.
     *  Get the shape, processShape it in some way and return and replace the original.
     */
    public AlcShape processShape(AlcShape shape){
        return shape;
    }
    
    /**
     * Affect - Increment an AlcShape.
     *  Used to increment a shape, typically for drawn lines etc...
     *  Typically store the temp shape in a canvas buffer until it gets added on mouse up.
     */
    public void incrementShape(AlcShape shape){
    }
    
    
    
    // MODULE DATA
    public String getName(){
        return moduleName;
    }
    
    public void setName(String m){
        moduleName = m;
    }
    
    public int getIndex(){
        return index;
    }
    
    public void setIndex(int i){
        index = i;
    }
    
    public int getModuleType(){
        return moduleType;
    }
    
    public void setModuleType(int moduleType){
        this.moduleType = moduleType;
    }
    
    
    public boolean getLoaded(){
        return loaded;
    }
    
    public void setLoaded(boolean l){
        loaded = l;
    }
    
    public String getIconName(){
        return iconName;
    }
    
    public void setIconName(String n){
        iconName = n;
    }
    
    /** Get the Icon URL from within the modules .zip file */
    public URL getIconUrl(){
        return iconUrl;
    }
    
    /** Set the Icon URL within the modules .zip file */
    public void setIconUrl(URL url){
        iconUrl = url;
    }
    
    public String getDescription(){
        return description;
    }
    
    public void setDescription(String n){
        description = n;
    }
    
    
    
    // MOUSE EVENTS
    // http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/MouseEvent.html
    public void mousePressed(MouseEvent e) {
        //int x = e.getX();
        //int y = e.getY();
        //Point p = e.getPoint();
    }
    
    
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
    
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
