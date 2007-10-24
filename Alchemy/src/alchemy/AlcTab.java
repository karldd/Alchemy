package alchemy;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
//import seltar.unzipit.*;

import java.io.File;
import java.lang.reflect.Method;


public class AlcTab extends AlcObject{
    
    String text;
    int tx, ty, pad;
    
    public AlcTab(PApplet r, AlcUI ui, String n, int x, int y, String t, String file) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
        text = t;
        fileName = file;
        setup();
    }
    
    public AlcTab(PApplet r, AlcUI ui, String n, int x, int y, String t, String file, File path) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
        text = t;
        fileName = file;
        filePath = path;
        setup();
    }
    
    public void setup(){
        tabFont = root.loadFont("TheSans-Plain-12.vlw");
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
        tx = ox + width + pad;
        ty = oy + height/2 + 6;
        set(0);
    }
    
    public void draw(){
        
        // Bg
        root.noStroke();
        root.fill(25);
        root.beginShape();
        root.vertex(ox-pad, oy+height+pad);
        root.vertex(ox-pad, oy-pad);
        root.vertex(ox+width+pad, oy-pad);
        root.vertex(ox+width+pad, oy+height+pad);
        root.endShape(root.CLOSE);
        
        // Icon
        if(current != null){
            root.image(current, ox, oy);
        }
        
        // Label
        root.fill(0);
        root.textFont(tabFont, 12);
        root.text(text, tx, ty);
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



