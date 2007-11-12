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
    
    Image random = null;
    PImage randomP = null;
    
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
        root.println(randomP);
        if(randomP != null){
            root.println("NOT NULL");
            root.image(randomP, 0, 0);
        }
    }
    
    public void randomImage(){
        Flickr f = null;
        try{
            f = new Flickr();
        } catch (ParserConfigurationException e) {
            System.err.println("Caught ParserConfigurationException: " + e.getMessage());
        }
        //root.println(f);
        random = f.search("test");
        
        PImage randomP = null;
        if(random != null){
            root.println("random is not null... loading PIMAGE");
            randomP = root.loadImageSync(random);
        }
        
        if(random != null){
            root.println("Random == null");
        } else {
            root.println("Random != null");
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
