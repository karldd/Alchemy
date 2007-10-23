package alchemy;

import processing.core.PApplet;

import org.java.plugin.Plugin;
import java.util.Vector;

public class Test extends Module {
    
    Vector<Object> lines;
    int currentLine;
    boolean firstPress = false;
    
    public Test(){
    }
    
    public String getName() {
        moduleName = "TEST ONE";
        return moduleName;
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
        
        // Draw the lines
        for(int j = 0; j < lines.size(); j++) {
            ((Vertex)lines.get(j)).draw();
        }
        
    }
    
    public void refocus(){
        firstPress = false;
        root.redraw();
    }
    
    public void mousePressed(int x, int y) {
        
        lines.add(new Vertex(root, x, y));
        currentLine = lines.size() - 1;
        firstPress = true;
    }
    
    public void mouseClicked(int x, int y) {
    }
    
    public void mouseMoved(int x, int y) {
    }
    
    public void mouseDragged(int x, int y) {
        if(firstPress){
            ((Vertex)lines.get(currentLine)).drag(x, y);
        }
    }
    
    public void mouseReleased(int x, int y) {
        if(firstPress){
            ((Vertex)lines.get(currentLine)).release(x, y);
        }
    }
    
}