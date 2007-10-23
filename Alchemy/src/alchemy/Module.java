package alchemy;

import processing.core.PApplet;

import java.awt.event.MouseEvent;
import java.io.File;

abstract class Module {
    
    PApplet root;
    String moduleName, iconName, descriptionName;
    int id;
    boolean loaded = false;
    File pluginPath;
    
    public Module(){
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
    
    public void setId(int i){
        id = i;
    }
    
    public int getId(){
        return id;
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
    
    
    
    // MOUSE EVENTS
    public void mousePressed(int x, int y) {
    }
    
    public void mouseClicked(int x, int y) {
    }
    
    public void mouseMoved(int x, int y) {
    }
    
    public void mouseDragged(int x, int y) {
    }
    
    public void mouseReleased(int x, int y) {
    }
    
    
}
