package alchemy;
import processing.core.PApplet;

public interface Module {
    
    String category();
    
    void setIndex(int i);
    
    int getIndex();
    
    void setup(PApplet p);
    
    void mousePressed();
    
    void mouseDragged();
    
    void mouseReleased();
}
