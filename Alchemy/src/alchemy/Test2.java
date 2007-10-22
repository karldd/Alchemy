package alchemy;

import alchemy.Module;
import org.java.plugin.Plugin;

import processing.core.PApplet;

public class Test2 extends Plugin implements Module {
    
    int id;
    PApplet parent;
    
    public String category() {
        return "TEST TWO";
    }
    
    public void setIndex(int i){
        id = i;
    }
    
    public int getIndex(){
        return id;
    }
    
    public void setup(PApplet p){
        parent = p;
        parent.println("Module " + id + " Loaded");
    }
    
    public void mousePressed() {
    }
    
    public void mouseDragged() {
    }
    
    public void mouseReleased() {
    }
    
    
    
    
    protected void doStart() throws Exception {
    }
    
    protected void doStop() throws Exception {
    }
    
}