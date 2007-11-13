package alchemy;

import processing.core.PApplet;
import processing.core.PImage;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Image;

import org.java.plugin.Plugin;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ShapeImager extends AlcModule {
    
    PImage randomP = null;
    String searchWords[] = {};
    
    public ShapeImager() {
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
    }
    
    public void draw(){
        root.noFill();
        root.stroke(0);
        resetSmooth();

        if(randomP != null){
            root.image(randomP, 0, 0);
        }
    }
    
    public void randomImage(){
        String pageOffset = String.valueOf((int)root.random(1, 100));
        Image random = Flickr.getInstance().search("abstract", pageOffset);
        
        if(random != null){
            randomP = root.loadImageSync(random);
        }
        root.redraw();
    }
    
    public void clear(){
        
    }
    
    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        //root.println(keyCode);
        
        switch(keyCode){
            case 8: // Backspace
            case 127: // Delete
                // Is this cross platform?
                clear();
                break;
            case 32: // Load Image
                randomImage();
                root.redraw();
                break;
                
        }
    }
    
}
