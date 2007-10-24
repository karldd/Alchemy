package alchemy;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.lang.reflect.Method;


public class AlcSlider extends AlcObject{
    
    boolean insideBg;
    int sx, halfWidth, bgWidth, bgHeight, halfBgWidth, clickGap, leftLimit, rightLimit, value;
    float grain;
    
    public AlcSlider(PApplet r, AlcUI ui, String n, int x, int y, int v, String file) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        value = root.constrain(v, 0, 100);
        a = new AlcAction(this, name);
        fileName = file;
        setup();
    }
    
    public AlcSlider(PApplet r, AlcUI ui, String n, int x, int y, int v, String file, File path) {
        root = r;
        parent = ui;
        id = parent.buttons.size();
        name = n;
        ox = x;
        oy = y;
        value = root.constrain(v, 0, 100);
        a = new AlcAction(this, name);
        fileName = file;
        filePath = path;
        setup();
    }
    
    public void setup(){
        images = new PImage[4];
        fileEnd = new String[4];
        loaded = new boolean[4];
        fileEnd[0] = "";
        fileEnd[1] = "-over";
        fileEnd[2] = "-down";
        fileEnd[3] = "-bg";
        inside = false;
        pressed = false;
        insideBg = false;
        loadImages();
        
        if(loaded[3]){
            bgWidth = images[3].width;
            bgHeight = images[3].height;
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
        set(0);
    }
    
    public void draw(){
        // Draw the Slider Background
        if(loaded[3]){
            root.image(images[3], ox, oy);
        }
        // Draw the Slider
        if(current != null){
            root.image(current, sx, oy);
        }
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
