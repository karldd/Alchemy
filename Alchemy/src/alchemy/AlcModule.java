package alchemy;

import processing.core.PApplet;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.File;

abstract class AlcModule {
    
    PApplet root;
    String moduleName, iconName, descriptionName;
    int id;
    boolean loaded = false;
    File pluginPath;
    AlcUi ui;
    
    public AlcModule(){
    }
    
    
    // STRUCTURE
    public void setup(PApplet p){
    }
    
    public void draw(){
    }
    
    public void refocus(){
    }
    
    
    // MODULE DATA
    
    public String getName(){
        return moduleName;
    }
    
    public void setName(String m){
        moduleName = m;
    }
    
    public int getId(){
        return id;
    }
    
    public void setId(int i){
        id = i;
    }
    
    public boolean getLoaded(){
        return loaded;
    }
    
    public void setLoaded(boolean l){
        loaded = l;
    }
    
    public File getPluginPath(){
        return pluginPath;
    }
    
    public void setPluginPath(File p){
        pluginPath = p;
    }
    
    public String getIconName(){
        return iconName;
    }
    
    public void setIconName(String n){
        iconName = n;
    }
    
    public String getDescriptionName(){
        return descriptionName;
    }
    
    public void setDescriptionName(String n){
        descriptionName = n;
    }
    
    // OBJECTS
    public void setUiVisible(boolean b){
        if(ui != null){
            ui.setVisible(b);
        }
    }
    
    
    
    // MOUSE EVENTS
    // http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/MouseEvent.html
    public void mousePressed(MouseEvent e) {
        //int x = e.getX();
        //int y = e.getY();
        //Point p = e.getPoint();
    }
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mouseMoved(MouseEvent e) {
    }
    
    public void mouseDragged(MouseEvent e) {
    }
    
    public void mouseReleased(MouseEvent e) {
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
