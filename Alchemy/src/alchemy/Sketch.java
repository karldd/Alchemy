package alchemy;

import processing.core.PApplet;

import org.java.plugin.Plugin;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class Sketch extends AlcModule {
    
    Vector<Object> lines;
    int currentLine;
    boolean firstPress = false;
    
    public Sketch(){
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
        
        lines = new Vector<Object>();
        lines.ensureCapacity(100);
        
        /*
        ui = new AlcUi(root);
        ui.setVisible(true);
        ui.addButton(this, "Test", 200, 210, "b.gif", pluginPath);
        */
         
        cursor = root.CROSS;
        smooth = true;
        setSmooth(smooth);
        
        loop = false;
        setLoop(loop);
    }
    
    public void draw(){
        root.noFill();
        root.stroke(0);
        resetSmooth();
        // Draw the lines
        for(int j = 0; j < lines.size(); j++) {
            ((AlcVertex)lines.get(j)).draw();
        }
        
    }
    
    public void refocus(){
        firstPress = false;
        root.redraw();
    }
    
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        lines.add(new AlcVertex(root, x, y));
        currentLine = lines.size() - 1;
        firstPress = true;
    }
    
    
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress){
            ((AlcVertex)lines.get(currentLine)).drag(x, y);
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress){
            ((AlcVertex)lines.get(currentLine)).release(x, y);
        }
    }
    
    public void buttonEvent(ActionEvent e) {
        root.println("Sketch " + e.getSource());
    }
    
}