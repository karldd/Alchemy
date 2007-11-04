package alchemy;

import processing.core.PApplet;

import org.java.plugin.Plugin;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Symmskribbl extends AlcModule {
    
    Vector<Object> lines, mirrorLines;
    int currentLine, currentMirrorLine;
    boolean firstPress = false;
    boolean cleared = false;
    
    public Symmskribbl(){
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
        
        lines = new Vector<Object>();
        lines.ensureCapacity(100);
        
        mirrorLines = new Vector<Object>();
        mirrorLines.ensureCapacity(100);
        
        ui = new AlcUi(root);
        ui.setVisible(true);
        ui.addButton(this, "Increase Stroke", 10, 50, "stokeUp.png", pluginPath);
        
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
        for(int i = 0; i < lines.size(); i++) {
            ((AlcVertex)lines.get(i)).draw();
        }
        for(int j = 0; j < mirrorLines.size(); j++) {
            ((AlcVertex)mirrorLines.get(j)).draw();
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
        lines.removeAllElements();
        mirrorLines.removeAllElements();
        if(root.mousePressed) cleared = true;
        root.redraw();
    }
    
    // MOUSE EVENTS
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        lines.add(new AlcVertex(root, x, y));
        currentLine = lines.size() - 1;
        
        mirrorLines.add(new AlcVertex(root, mirror(x), y));
        currentMirrorLine = mirrorLines.size() - 1;
        
        firstPress = true;
    }
    
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress && !cleared){
            ((AlcVertex)lines.get(currentLine)).drag(x, y);
            ((AlcVertex)mirrorLines.get(currentMirrorLine)).drag(mirror(x), y);
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress && !cleared){
            ((AlcVertex)lines.get(currentLine)).release(x, y);
            ((AlcVertex)mirrorLines.get(currentMirrorLine)).drag(mirror(x), y);
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