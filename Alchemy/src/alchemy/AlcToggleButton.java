package alchemy;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.lang.reflect.Method;


public class AlcToggleButton extends AlcObject{
    
    PImage current;
    PImage[] images = new PImage[6];
    String[] fileEnd = {"", "-over", "-down", "-on", "-onover", "-ondown"};
    boolean[] loaded = {false, false, false, false, false, false};
    
    boolean inside = false;
    boolean pressed = false;
    boolean on;
        
    public AlcToggleButton(PApplet r, AlcUI ui, String n, int x, int y, String fileName, Boolean o) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        on = o;
        ox = x;
        oy = y;
        
        
        a = new AlcAction(this, name);
        
        // LOOP TO LOAD IMAGES FROM ARRAY
        for(int i = 0; i < images.length; i++) {
            // Apped the button state to the filename
            String fn = editName(fileName, fileEnd[i]);
            // File Object
            File f = new File(root.dataPath(fn));
            if(f.exists()){
                images[i] = root.loadImage(fn);
                loaded[i] = true;
                if(i == 0){
                    width = images[0].width;
                    height = images[0].height;
                }
            }
            
        }
        // Set the default button state
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
    
    // FUNCTION TO EDIT THE NAME OF THE ORIGINAL BUTTON FILE
    public String editName(String f, String e){
        int dot = f.lastIndexOf(".");
        return f.substring(0, dot) + e + f.substring(dot);
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



