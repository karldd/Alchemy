package alchemy;



import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

public abstract class AlcModule {
    
    public AlcMain root;
    
    private String moduleName, iconName, descriptionName;
    private int id, cursor;
    private boolean loaded = false;
    private boolean smooth, loop;
    private File pluginPath;
    private URL iconUrl;
    
    public AlcModule(){
    }
    
    
    // STRUCTURE
    /** Called on first load */
    public void setup(AlcMain root){
    }
    
    public void draw(){
    }
    
    /** Called when the module is re-selected */
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
    
    /** Get the Icon URL from within the modules .zip file */
    public URL getIconUrl(){
        return iconUrl;
    }
    
    /** Set the Icon URL within the modules .zip file */
    public void setIconUrl(URL url){
        iconUrl = url;
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
