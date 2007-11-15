package alchemy.ui;

import alchemy.*;
import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.lang.reflect.Method;


public class AlcUiButton extends AlcUiObject{
    
    public AlcUiButton(PApplet r, AlcUi ui, AlcModule c, String n, int x, int y, String file) {
        root = r;
        parent = ui;
        caller = c;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        a = new AlcUiAction(this, id, name, "buttonEvent");
        fileName = file;
        setup();
    }
    
    public AlcUiButton(PApplet r, AlcUi ui,  AlcModule c, String n, int x, int y, String file, File path) {
        root = r;
        parent = ui;
        caller = c;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        a = new AlcUiAction(this, id, name, "buttonEvent");
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
            a.sendEvent(caller);
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



