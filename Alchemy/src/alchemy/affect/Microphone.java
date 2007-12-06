/**
 * Microphone.java
 *
 * Created on December 6, 2007, 5:39 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.affect;

import alchemy.*;
import java.awt.event.KeyEvent;

public class Microphone extends AlcModule{
    
    AlcMicInput micIn;
    AlcShape tempShape;
    
    /** Creates a new instance of Microphone */
    public Microphone() {
    }
    
    public void setup(){
        micIn = new AlcMicInput(10);
    }
    
    
    /*
    private void drawSound(byte buffer[]){
     
        for (int i = 0; i < buffer.length; i++) {
            if(i == 0){
                tempShape = new AlcShape( new Point(i*10, buffer[i]+500) );
            } else {
                tempShape.drag( new Point(i*10, buffer[i]+500) );
            }
        }
     
        canvas.addTempShape(tempShape);
        canvas.redraw();
    }
     */
    
    
    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        char keyChar = e.getKeyChar();
        
        //System.out.println(keyChar);
        switch(keyChar){
            case '[':
                System.out.println("[");
                
                micIn.startMicInput();
                break;
                
            case ']':
                
                System.out.println("]");
                
                micIn.stopMicInput();
                break;
            case 'p':
                System.out.println(micIn.getMicLevel());
                break;
        }
    }
    
}
