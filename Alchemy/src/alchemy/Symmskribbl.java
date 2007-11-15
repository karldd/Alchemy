package alchemy;

import processing.core.PApplet;
import processing.core.PGraphics;

import org.java.plugin.Plugin;
import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Symmskribbl extends AlcModule {
    
    // GENERAL
    boolean firstPress = false;
    boolean cleared = false;
    
    // LINE
    ArrayList<Object> lines, mirrorLines;
    int currentLine, currentMirrorLine;
    
    PGraphics canvas;
    
    public Symmskribbl(){
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
        
        canvas = root.createGraphics(root.width, root.height, root.JAVA2D);
        canvas.beginDraw();
        canvas.background(255);
        canvas.smooth();
        canvas.endDraw();
        
        lines = new ArrayList<Object>();
        lines.ensureCapacity(100);
        
        mirrorLines = new ArrayList<Object>();
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
        // Draw the buffer
        //root.println("Draw");
        root.image(canvas, 0, 0);
    }
    
    public void drawPDF(){
        root.noFill();
        root.stroke(0);
        resetSmooth();
        // Draw the lines
        for(int i = 0; i < lines.size(); i++) {
            ((AlcSketchPath)lines.get(i)).drawPDF();
        }
        for(int j = 0; j < mirrorLines.size(); j++) {
            ((AlcSketchPath)mirrorLines.get(j)).drawPDF();
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
        lines.clear();
        mirrorLines.clear();
        if(root.mousePressed) cleared = true;
        root.redraw();
    }
    
    // MOUSE EVENTS
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        lines.add(new AlcSketchPath(root, canvas, x, y));
        currentLine = lines.size() - 1;
        
        mirrorLines.add(new AlcSketchPath(root, canvas, mirror(x), y));
        currentMirrorLine = mirrorLines.size() - 1;
        
        firstPress = true;
    }
    
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        /*
        if(firstPress && !cleared){
            ((AlcSketchPath)lines.get(currentLine)).drag(x, y);
            ((AlcSketchPath)mirrorLines.get(currentMirrorLine)).drag(mirror(x), y);
        }
         */
        
        canvas.beginDraw();
        canvas.smooth();
        canvas.line(x, y, root.pmouseX, root.pmouseY);
        canvas.endDraw();
        root.redraw();
        
    }
    
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        /*
        if(firstPress && !cleared){
            ((AlcSketchPath)lines.get(currentLine)).release(x, y);
            ((AlcSketchPath)mirrorLines.get(currentMirrorLine)).drag(mirror(x), y);
        }
        cleared = false;
         */
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