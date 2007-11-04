package alchemy;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;

import java.io.File;
import java.lang.reflect.Method;


public class AlcUiTab extends AlcUiObject{
    
    PFont tabFont;
    String text;
    int tx, ty, pad, fontSize, fullWidth, fullHeight, ix, iy, c1, c2, c3, c4;
    float textWidth;
    boolean on, drawToolBarBg;
    
    public AlcUiTab(PApplet r, AlcUi ui, String n, int x, int y, boolean o, int i, String txt, String file) {
        root = r;
        parent = ui;
        id = i;
        name = n;
        on = o;
        ox = x;
        oy = y;
        a = new AlcUiAction(this, id, name, "tabEvent");
        text = txt;
        fileName = file;
        setup();
    }
    
    public AlcUiTab(PApplet r, AlcUi ui, String n, int x, int y, boolean o, int i, String txt, String file, File path) {
        root = r;
        parent = ui;
        name = n;
        id = i;
        on = o;
        ox = x;
        oy = y;
        a = new AlcUiAction(this, id, name, "tabEvent");
        text = txt;
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
        root.textFont(tabFont);
        textWidth = root.textWidth(name);
        tx = ox + pad*2 + width;
        ty = oy + pad*2 + height/2;
        ix = ox + pad;
        iy = oy + pad;
        fullWidth = ox + width + (int)textWidth + pad*3;
        fullHeight = oy + height + pad*2;
        c1 = oy+2;
        c2 = ox+2;
        c3 = fullWidth-2;
        c4 = oy+2;
        set(0);
    }
    
    public void draw(){
        // Draw Bg for the toolbar if the module has some ui elements
        if(drawToolBarBg){
            root.noStroke();
            root.fill(245);
            root.rect(0, 39, root.width, 44);
        }
        
        // Tab
        if(on){
            root.fill(245);
        } else if(inside){
            root.fill(235);
        } else{
            root.fill(225);
        }
        root.noStroke();
        root.beginShape();
        root.vertex(ox, fullHeight);
        root.vertex(ox, c1);
        root.vertex(c2, oy);
        root.vertex(c3, oy);
        root.vertex(fullWidth, c4);
        root.vertex(fullWidth, fullHeight);
        root.endShape(root.CLOSE);
        
        // Icon
        if(current != null){
            root.image(current, ix, iy);
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
    
    public int getWidth(){
        return fullWidth;
    }
    
    public void setToolBarBg(boolean hasUi){
        drawToolBarBg = hasUi;
    }
    
    public void rollOverCheck(int x, int y){
        if (x >= ox && x <= fullWidth && y >= oy && y <= fullHeight) {
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



