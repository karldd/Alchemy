package alchemy;

import org.java.plugin.Plugin;

import processing.core.PApplet;

public class Test2 extends AlcModule {
    
    public Test2(){
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
    }
        
    public void draw(){
        
    }
    
    public void mousePressed() {
    }
    
    public void mouseClicked() {
    }
    
    public void mouseMoved() {
    }
    
    public void mouseDragged() {
    }
    
    public void mouseReleased() {
    }
}