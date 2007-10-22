package alchemy;

import processing.core.PApplet;

import java.awt.event.MouseEvent;

abstract class Module {
    
    PApplet root;
    String moduleName;
    int id;
    
    public Module(){
    }
    
    public String getName(){
        return moduleName;
    }
    
    public void setId(int i){
        id = i;
    }
    
    int getId(){
        return id;
    }
    
    public void setup(PApplet p){
    }
    
    public void draw(){
    }
    
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
