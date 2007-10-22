package alchemy;

import processing.core.PApplet;

abstract class AlcObject {
    
    int ox, oy, width, height, id;
    
    PApplet root;
    AlcUI parent;
    String name;
    String actionCommand;
    AlcAction a;
    
    
    public AlcObject() {
    }
    
    // FUNCTION TO EDIT THE NAME OF THE ORIGINAL BUTTON FILE
    public String editName(String f, String e){
        int dot = f.lastIndexOf(".");
        return f.substring(0, dot) + e + f.substring(dot);
    }
}
