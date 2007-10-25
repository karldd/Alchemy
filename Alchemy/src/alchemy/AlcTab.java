package alchemy;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
//import seltar.unzipit.*;

import java.io.File;
import java.lang.reflect.Method;


public class AlcTab extends AlcObject{
    
    String text;
    int tx, ty, pad, fontSize, textWidth, fullWidth, fullHeight;
    boolean on;
    
    public AlcTab(PApplet r, AlcUI ui, String n, int x, int y, boolean o, String t, String file) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        on = o;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
        text = t;
        fileName = file;
        setup();
    }
    
    public AlcTab(PApplet r, AlcUI ui, String n, int x, int y, boolean o, String t, String file, File path) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        on = o;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
        text = t;
        fileName = file;
        filePath = path;
        setup();
    }
    
    public void setup(){
        images = new PImage[3];
        fileEnd = new String[3];
        loaded = new boolean[3];
        fileEnd[0] = "";
        fileEnd[1] = "-over";
        fileEnd[2] = "-down";
        inside = false;
        pressed = false;
        loadImages();
        pad = 5;
        fontSize = 12;
        root.hint(root.ENABLE_NATIVE_FONTS);
        tabFont = root.createFont("Helvetica", fontSize, true);
        tx = ox + pad*2 + width;
        ty = oy + pad*2 + height/2;
        textWidth = name.length() * (int)(fontSize/1.5);
        fullWidth = ox + width + textWidth;
        fullHeight = oy + height + pad*2;
        set(0);
    }
    
    public void draw(){
        
        // Tab
        if(on){
            root.fill(245);
        } else {
            root.fill(235);
        }
        root.noStroke();
        root.beginShape();
        root.vertex(ox, fullHeight);
        root.vertex(ox, oy);
        root.vertex(fullWidth, oy);
        root.vertex(fullWidth, fullHeight);
        root.endShape(root.CLOSE);
        
        // Icon
        if(current != null){
            root.image(current, ox+pad, oy+pad);
        }
        
        // Label
        root.fill(0);
        root.textFont(tabFont);
        root.text(text, tx, ty);
    }
    
    public boolean getState(){
        return on;
    }
    
    public void setState(boolean o){
        on = o;
    }
    
    public void rollOverCheck(int x, int y){
        if (x >= ox && x <= ox+width && y >= oy && y <= oy+height) {
            // CALLED ONCE WHEN ENTERING THE BUTTON
            if(!inside){
                // OVER
                set(1);
                //root.println("Inside");
            }
            inside = true;
        } else {
            // CALLED ONCE WHEN EXITING THE BUTTON
            if(inside){
                // CHECK IF AN OVER BUTTON EXISTS
                if(current != images[0]){
                    // UP
                    set(0);
                    //root.println("Outside");
                }
                
            }
            inside = false;
        }
    }
    
    public void pressedCheck(){
        if(inside){
            // DOWN
            set(2);
            a.sendEvent(root);
            pressed = true;
        }
    }
    
    public void releasedCheck(){
        if(pressed) {
            if(inside){
                if(loaded[1]){
                    // OVER
                    set(1);
                } else {
                    // UP
                    set(0);
                }
            }
            pressed = false;
        }
    }
    
}



