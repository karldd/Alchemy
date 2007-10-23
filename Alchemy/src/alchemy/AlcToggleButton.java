package alchemy;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.lang.reflect.Method;


public class AlcToggleButton extends AlcObject{
    
    boolean on;
    
    public AlcToggleButton(PApplet r, AlcUI ui, String n, int x, int y, Boolean o, String file) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        on = o;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
        fileName = file;
        setup();
    }
    
    public AlcToggleButton(PApplet r, AlcUI ui, String n, int x, int y, Boolean o, String file, File path) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        on = o;
        ox = x;
        oy = y;
        a = new AlcAction(this, name);
        fileName = file;
        filePath = path;
        setup();
    }
    
    public void setup(){
        images = new PImage[6];
        fileEnd = new String[6];
        loaded = new boolean[6];
        fileEnd[0] = "";
        fileEnd[1] = "-over";
        fileEnd[2] = "-down";
        fileEnd[3] = "-on";
        fileEnd[4] = "-onover";
        fileEnd[5] = "-ondown";
        inside = false;
        pressed = false;
        loadImages();
        if(on){
            set(3);
        } else {
            set(0);
        }
    }
    
    public void draw(){
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
    
    public boolean getState(){
        return on;
    }
    
    public void rollOverCheck(int x, int y){
        if (x >= ox && x <= ox+width && y >= oy && y <= oy+height) {
            // CALLED ONCE WHEN ENTERING THE BUTTON
            if(!inside){
                // CHECK IF AN OVER BUTTON EXISTS
                if(on){
                    // ONOVER
                    set(4);
                } else{
                    // OVER
                    set(1);
                }
                //root.println("Inside");
            }
            inside = true;
        } else {
            // CALLED ONCE WHEN EXITING THE BUTTON
            if(inside){
                if(on){
                    // ON
                    set(3);
                } else{
                    // UP
                    set(0);
                }
                //root.println("Outside");
            }
            inside = false;
        }
    }
    
    public void pressedCheck(){
        if(inside){
            
            if(on){
                on = false;
                // DOWN
                set(2);
            } else{
                on = true;
                // ONDOWN
                set(5);
            }
            a.sendEvent(root);
            pressed = true;
        }
    }
    
    public void releasedCheck(){
        if(pressed) {
            if(inside){
                if(on){
                    // ONOVER
                    set(4);
                } else{
                    // OVER
                    set(1);
                }
            }
            pressed = false;
        }
    }
    
}



