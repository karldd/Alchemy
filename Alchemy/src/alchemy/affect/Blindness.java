/**
 * Blindness.java
 *
 * Created on December 5, 2007, 1:52 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.affect;

import alchemy.*;
import alchemy.ui.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Blindness extends AlcModule implements AlcConstants{
    
    private boolean blindShapes = false;
    
    /** Creates a new instance of Blindness */
    public Blindness() {
    }
    
    public void setup(){
        root.canvas.setRedraw(false);
        
        // Add this modules toolbar to the main ui
        root.toolBar.addSubToolBar(createSubToolBar());
        
    }
    
    public void reselect(){
        root.canvas.setRedraw(false);
    }
    
    public void deselect(){
        // Turn drawing back on and show what is underneath
        root.canvas.setRedraw(true);
        root.canvas.redraw();
    }
    
    private void redrawOnce(){
        deselect();
        reselect();
    }
    
    public AlcSubToolBar createSubToolBar(){
        AlcSubToolBar subToolBar = new AlcSubToolBar(root, this, getName(), getIconUrl(), getDescription());
        
        // Buttons
        AlcSubButton redrawButton = new AlcSubButton(root.toolBar, "Redraw", getIconUrl());
        redrawButton.setToolTipText("Redraw the screen (b)");
        
        redrawButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                redrawOnce();
            }
        }
        );
        subToolBar.add(redrawButton);
        
        AlcSubToggleButton blindShapeButton = new AlcSubToggleButton(root.toolBar, "Blind Shapes", getIconUrl());
        blindShapeButton.setToolTipText("Redraw the screen after each shape");
        
        blindShapeButton.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleBlindShapes();
            }
        }
        );
        subToolBar.add(blindShapeButton);
        
        return subToolBar;
    }
    
    private void toggleBlindShapes(){
        if(blindShapes){
            blindShapes = false;
        } else {
            blindShapes = true;
        }
    }
    
    // MOUSE EVENTS
    
    public void mouseReleased(MouseEvent e) {
        if(blindShapes){
            redrawOnce();
        }
    }
    
    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        char keyChar = e.getKeyChar();
        
        //System.out.println(keyChar);
        switch(keyChar){
            case 'b':
                redrawOnce();
                break;
        }
        
        /*
        switch(keyCode){
            case BACKSPACE:
            case DELETE:
         
                //System.out.println("DELETE");
                //root.canvas.clear();
                break;
         
            case SPACE:
         
                //System.out.println("SPACE");
                break;
         
        }
         */
    }
    
}
