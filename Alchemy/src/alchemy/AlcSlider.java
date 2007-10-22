package alchemy;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.lang.reflect.Method;


public class AlcSlider extends AlcObject{
    
    PImage current;
    PImage[] images = new PImage[4];
    String[] fileEnd = {"", "-over", "-down", "-bg"};
    boolean[] loaded = {false, false, false, false};
    
    boolean inside = false;
    boolean insideBg = false;
    boolean pressed = false;
    
    int sx, halfWidth, bgWidth, bgHeight, halfBgWidth, clickGap, leftLimit, rightLimit, value;
    float grain;
    
    public AlcSlider(PApplet r, AlcUI ui, String n, int x, int y, String fileName, int v) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        value = root.constrain(v, 0, 100);
        
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
                } else if(i == 3){
                    bgWidth = images[3].width;
                    bgHeight = images[3].height;
                }
            }
            
        }
        
        // Right Shift
        //sx = (bgWidth >> 1) - (width >> 1);
        
        halfWidth = width/2;
        halfBgWidth = bgWidth/2;
        leftLimit = ox;
        rightLimit = ox + (bgWidth - width);
        grain = 100.0F / (rightLimit - leftLimit);
        // Middle
        //sx = (halfBgWidth - halfWidth) + ox;
        float bigGrain = (rightLimit - leftLimit) / 100.F;
        sx = (int)(ox + bigGrain * value);
    }
    
    public void draw(){
        if(loaded[3]){
            root.image(images[3], ox, oy);
        }
        if(current != null){
            root.image(current, sx, oy);
        }
    }
    
    public void set(int a){
        if(loaded[a]) {
            current = images[a];
        }
        root.redraw();
    }
    
    public void setValue(){
        value = (int)((sx - leftLimit) * grain);
        root.redraw();
        //root.println(value);
    }
    
    public void rollOverCheck(int x, int y){
        if (x >= sx && x <= sx+width && y >= oy && y <= oy+height) {
            // CALLED ONCE WHEN ENTERING THE SLIDER BUTTON
            if(!inside){
                // OVER
                set(1);
                //root.println("Inside");
            }
            inside = true;
        } else {
            // CALLED ONCE WHEN EXITING THE SLIDER BUTTON
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
    
    public void pressedCheck(int x, int y){
        if (x >= ox && x <= ox+bgWidth && y >= oy && y <= oy+bgHeight) {
            if(inside){
                // DOWN
                set(2);
                //a.sendEvent(root);
                
                clickGap = x - sx;
                
            } else{
                if(x >= ox + (bgWidth-halfWidth)) {
                    sx = rightLimit;
                } else if(x <= ox+halfWidth){
                    sx = leftLimit;
                } else {
                    sx = x - halfWidth;
                }
                clickGap = halfWidth;
                setValue();
            }
            pressed = true;
        }
    }
    
    public void draggedCheck(int x, int y){
        // Check that the slider button has been clicked on
        if(pressed) {
            int sLeft = x - clickGap;
            //root.println(sx);
            if(sLeft <= rightLimit && sLeft >= leftLimit){
                // Dragging the slider button
                sx = sLeft;
            } else if(sLeft >= rightLimit) {
                sx = rightLimit;
            } else if(sLeft <= leftLimit){
                sx = leftLimit;
            }
            setValue();
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
