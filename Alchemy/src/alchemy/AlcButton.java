package alchemy;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.lang.reflect.Method;


public class AlcButton extends AlcObject{
    
    PImage current;
    PImage[] images = new PImage[3];
    String[] fileEnd = {"", "-over", "-down"};
    boolean[] loaded = {false, false, false};
    
    boolean inside = false;
    boolean pressed = false;
    
    
    public AlcButton(PApplet r, AlcUI ui, String n, int x, int y, String fileName) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
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
                    set(0);
                }
            }
            
        }
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



