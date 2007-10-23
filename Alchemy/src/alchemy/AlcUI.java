package alchemy;

import processing.core.PApplet;
import java.awt.event.MouseEvent;

import java.io.File;
import java.util.Vector;

public class AlcUI {
    
    public PApplet root;
    public Vector<Object> buttons, toggleButtons, sliders;
    
    public AlcUI(PApplet r) {
        root = r;
        root.registerDraw(this);
        root.registerMouseEvent(this);
        buttons = new Vector<Object>();
        toggleButtons = new Vector<Object>();
        sliders = new Vector<Object>();
    }
    
    public void draw(){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcButton)buttons.get(i)).draw();
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcToggleButton)toggleButtons.get(i)).draw();
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcSlider)sliders.get(i)).draw();
        }
    }
    
    public void addButton(String name, int x, int y, String icon){
        buttons.add(new AlcButton(root, this, name, x, y, icon));
    }
    
    public void addButton(String name, int x, int y, String icon, File filePath){
        buttons.add(new AlcButton(root, this, name, x, y, icon, filePath));
    }
    
    public void removeButton(String name){
        for(int i = 0; i < buttons.size(); i++) {
            if(((AlcButton) buttons.get(i)).name == name){
                buttons.remove(i);
                root.redraw();
                break;
            }
        }
    }
    
    public void addToggleButton(String name, int x, int y, Boolean on, String icon){
        toggleButtons.add(new AlcToggleButton(root, this, name, x, y, on, icon));
    }
    
    public void addToggleButton(String name, int x, int y, Boolean on, String icon, File filePath){
        toggleButtons.add(new AlcToggleButton(root, this, name, x, y, on, icon, filePath));
    }
    
    public void removeToogleButton(String name){
        for(int i = 0; i < toggleButtons.size(); i++) {
            if(((AlcToggleButton) toggleButtons.get(i)).name == name){
                toggleButtons.remove(i);
                root.redraw();
                break;
            }
        }
    }
    
    public boolean getToogleButtonState(String name){
        boolean state = false;
        boolean called = false;
        
        for(int i = 0; i < toggleButtons.size(); i++) {
            if(((AlcToggleButton) toggleButtons.get(i)).name == name){
                state = ((AlcToggleButton)toggleButtons.get(i)).getState();
                called = true;
                break;
            }
        }
        if(!called) root.println("No such button: " + name);
        return state;
    }
    
    public void addSlider(String name, int x, int y, String icon, int value){
        sliders.add(new AlcSlider(root, this, name, x, y, icon, value));
    }
    
    public int getSliderValue(String name){
        int value = 0;
        boolean called = false;
        for(int i = 0; i < sliders.size(); i++) {
            if(((AlcSlider) sliders.get(i)).name == name){
                called = true;
                value = ((AlcSlider) sliders.get(i)).value;
                break;
            }
        }
        if(!called) root.println("No such slider: " + name);
        return value;
    }
    
    public void removeSlider(String name){
        for(int i = 0; i < sliders.size(); i++) {
            if(((AlcSlider) sliders.get(i)).name == name){
                sliders.remove(i);
                root.redraw();
                break;
            }
        }
    }
    
    public void rollOverCheckObjects(int x, int y){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcButton)buttons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcToggleButton)toggleButtons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcSlider)sliders.get(i)).rollOverCheck(x, y);
        }
    }
    
    
    public void pressedCheckObjects(int x, int y){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcButton)buttons.get(i)).pressedCheck();
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcToggleButton)toggleButtons.get(i)).pressedCheck();
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcSlider)sliders.get(i)).pressedCheck(x, y);
        }
    }
    
    public void draggedCheckObjects(int x, int y){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcButton)buttons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcToggleButton)toggleButtons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcSlider)sliders.get(i)).draggedCheck(x, y);
        }
    }
    
    public void releasedCheckObjects(){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcButton)buttons.get(i)).releasedCheck();
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcToggleButton)toggleButtons.get(i)).releasedCheck();
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcSlider)sliders.get(i)).releasedCheck();
        }
    }
    
    public void mouseEvent(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        
        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                //root.println("Mouse Pressed - X:" + x + " Y: " + y);
                pressedCheckObjects(x, y);
                break;
            case MouseEvent.MOUSE_RELEASED:
                //root.println("Mouse Released - X:" + x + " Y: " + y);
                releasedCheckObjects();
                break;
            case MouseEvent.MOUSE_CLICKED:
                //root.println("Mouse Clicked - X:" + x + " Y: " + y);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                //root.println("Mouse Dragged - X:" + x + " Y: " + y);
                //rollOverCheckObjects(x, y);
                draggedCheckObjects(x, y);
                break;
            case MouseEvent.MOUSE_MOVED:
                //root.println("Mouse Moved - X:" + x + " Y: " + y);
                rollOverCheckObjects(x, y);
                break;
        }
    }
    
}
