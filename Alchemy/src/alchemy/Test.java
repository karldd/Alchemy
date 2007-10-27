package alchemy;

import processing.core.PApplet;

import org.java.plugin.Plugin;
import java.util.Vector;

public class Test extends AlcModule {
    
    Vector<Object> lines;
    int currentLine;
    boolean firstPress = false;
    
    public Test(){
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
        
        lines = new Vector<Object>();
        lines.ensureCapacity(100);
        
        root.noFill();
        root.stroke(0);
        root.smooth();
        //root.cursor(root.CROSS);
        root.noLoop();
    }
    
    public void draw(){
        root.noFill();
        root.stroke(0);
        // Draw the lines
        for(int j = 0; j < lines.size(); j++) {
            ((AlcVertex)lines.get(j)).draw();
        }
        
    }
    
    public void refocus(){
        firstPress = false;
        root.redraw();
    }
    
    public void mousePressed(int x, int y) {
        
        lines.add(new AlcVertex(root, x, y));
        currentLine = lines.size() - 1;
        firstPress = true;
    }
    
    public void mouseClicked(int x, int y) {
    }
    
    public void mouseMoved(int x, int y) {
    }
    
    public void mouseDragged(int x, int y) {
        if(firstPress){
            ((AlcVertex)lines.get(currentLine)).drag(x, y);
        }
    }
    
    public void mouseReleased(int x, int y) {
        if(firstPress){
            ((AlcVertex)lines.get(currentLine)).release(x, y);
        }
    }
    
}