package alchemy;

import processing.core.PApplet;
import processing.core.PImage;
//import seltar.unzipit.*;

import java.io.File;

abstract class AlcObject {
    
    int ox, oy, width, height, id;
    
    PApplet root;
    AlcUI parent;
    AlcAction a;
    String name, fileName, actionCommand;
    File filePath;
    UnZipIt zip;
    
    PImage current;
    PImage[] images;
    String[] fileEnd;
    boolean[] loaded;
    
    boolean pressed, inside;
    
    
    public AlcObject() {
    }
    
    // LOAD IMAGES
    public void loadImages(){
        // If a filePath isnt specified then look for the image in the root data folder
        if(filePath == null) {
            for(int i = 0; i < images.length; i++) {
                // Append the button state to the filename
                String fn = editName(fileName, fileEnd[i]);
                // Path to the file
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
        } else{
            // Open the zip file
            zip = new UnZipIt(filePath.getPath(), root);
            for(int i = 0; i < images.length; i++) {
                // Append the button state to the filename
                String fn = editName(fileName, fileEnd[i]);
                
                if(zip.fileExists("data/"+fn)) {
                    images[i] = zip.loadImage("data/"+fn);
                    loaded[i] = true;
                    if(i == 0){
                        width = images[0].width;
                        height = images[0].height;
                    }
                } else{
                    loaded[i] = false;
                }
                
            }
            
        }
    }
    
// FUNCTION TO EDIT THE NAME OF THE ORIGINAL BUTTON FILE
    public String editName(String f, String e){
        int dot = f.lastIndexOf(".");
        return f.substring(0, dot) + e + f.substring(dot);
    }
}
