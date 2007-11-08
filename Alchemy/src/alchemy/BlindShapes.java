package alchemy;

import processing.core.PApplet;

import org.java.plugin.Plugin;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class BlindShapes extends AlcModule {
    
    // GENERAL
    boolean firstPress = false;
    boolean cleared = false;
    
    // LINE
    Vector<Object> shapes;
    int currentLine;
    
    public BlindShapes(){
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
        
        shapes = new Vector<Object>();
        shapes.ensureCapacity(100);
        
        /*
        ui = new AlcUi(root);
        ui.setVisible(true);
        ui.addButton(this, "Increase Stroke", 10, 50, "stokeUp.png", pluginPath);
        */
         
        cursor = root.CROSS;
        smooth = true;
        setSmooth(smooth);
        
        loop = false;
        setLoop(loop);
    }
    
    public void draw(){
        root.fill(0, 0, 0, 25);
        root.noStroke();
        resetSmooth();
        // Draw the shapes
        for(int i = 0; i < shapes.size(); i++) {
            ((AlcSketchPath)shapes.get(i)).draw();
        }
        
    }
    
    public void refocus(){
        firstPress = false;
        root.redraw();
    }
    
    public int mirror(int x){
        // Divide by 2
        int halfWidth = root.width >> 1;
        return halfWidth - (x - halfWidth);
    }
    
    public void clear(){
        shapes.removeAllElements();
        if(root.mousePressed) cleared = true;
        root.redraw();
    }
    
    // MOUSE EVENTS
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        shapes.add(new AlcSketchPath(root, x, y));
        currentLine = shapes.size() - 1;
        
        firstPress = true;
    }
    
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress && !cleared){
            ((AlcSketchPath)shapes.get(currentLine)).drag(x, y, false);
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress && !cleared){
            ((AlcSketchPath)shapes.get(currentLine)).release(x, y, true);
        }
        cleared = false;
    }
    
    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch(keyCode){
            case 8: // Backspace
            case 127: // Delete
                // Is this cross platform?
                clear();
                break;
        }
    }
    
    public void buttonEvent(ActionEvent e) {
        //root.println("Sym Skribble" + e.getSource());
    }
    
}