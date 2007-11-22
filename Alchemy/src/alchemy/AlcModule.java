package alchemy;



import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public abstract class AlcModule {
    
    public String moduleName, iconName, descriptionName;
    public int id, cursor;
    public boolean loaded = false;
    public boolean smooth, loop;
    public File pluginPath;
    
    public AlcModule(){
    }
    
    
    // STRUCTURE
    public void setup(){
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
    
    /* Processing crap...
     *
    // GLOBAL HARMONY
    public void setCursor(int c){
        root.cursor(c);
    }
    
    public int getCursor(){
        return cursor;
    }
    
    public void resetCursor(){
        root.cursor(cursor);
    }
    
    public void setSmooth(boolean b){
        if(b){
            root.smooth();
        } else {
            root.noSmooth();
        }
    }
    
    public boolean getSmooth(){
        return smooth;
    }
    
    public void resetSmooth(){
        if(smooth){
            root.smooth();
        } else {
            root.noSmooth();
        }
    }
    
    public void setLoop(boolean b){
        if(b){
            root.loop();
        } else {
            root.noLoop();
        }
    }
    
    public boolean getLoop(){
        return loop;
    }
    
    public void resetLoop(){
        if(loop){
            root.loop();
        } else {
            root.noLoop();
        }
    }
    
    
    // OBJECTS
    public void setUiVisible(boolean b){
        if(ui != null){
            ui.setVisible(b);
        }
    }
    
    public boolean hasUi(){
        if(ui == null){
            return false;
        } else{
            return true;
        }
    }
    */
    
    
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
