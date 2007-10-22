package alchemy;

import alchemy.Module;
import org.java.plugin.Plugin;

import processing.core.PApplet;

import java.util.Vector;

public class Test extends Plugin implements Module {
    
    int id;
    PApplet parent;
    Vector lines;
    
    public String category() {
        return "TEST ONE";
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
        
        lines = new Vector();
        
        parent.noFill();
        parent.stroke(0);
        parent.smooth();
        parent.cursor(parent.CROSS);
        parent.noLoop();
    }
    
    public void mousePressed() {
        //lines.add(new Line(this));
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