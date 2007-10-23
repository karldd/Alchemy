package alchemy;

import processing.core.PApplet;
import processing.core.PImage;
//import seltar.unzipit.*;

import java.io.File;
import java.lang.reflect.Method;


public class AlcButton extends AlcObject{
    
    public AlcButton(PApplet r, AlcUI ui, String n, int x, int y, String file) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
        fileName = file;
        setup();
    }
    
    public AlcButton(PApplet r, AlcUI ui, String n, int x, int y, String file, File path) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
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
        set(0);
    }
    
    public void draw(){
        // UP state Button
        if(current != null){
            root.image(current, ox, oy);
        }
    }
    
    public void set(int a){
        if(loaded[a]) {
            current = images[a];
        }
        root.redraw();
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



