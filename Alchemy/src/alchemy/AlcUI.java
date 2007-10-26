package alchemy;

import processing.core.PApplet;
import java.awt.event.MouseEvent;

import java.io.File;
import java.util.Vector;

public class AlcUI {
    
    PApplet root;
    Vector<Object> buttons, toggleButtons, sliders, tabs;
    boolean visible = false;
    
    public AlcUI(PApplet r) {
        root = r;
        root.registerDraw(this);
        root.registerMouseEvent(this);
        buttons = new Vector<Object>();
        toggleButtons = new Vector<Object>();
        sliders = new Vector<Object>();
        tabs = new Vector<Object>();
    }
    
    public void draw(){
        if(visible){
            for(int i = 0; i < buttons.size(); i++) {
                ((AlcUiButton)buttons.get(i)).draw();
            }
            for(int i = 0; i < toggleButtons.size(); i++) {
                ((AlcUiToggleButton)toggleButtons.get(i)).draw();
            }
            for(int i = 0; i < sliders.size(); i++) {
                ((AlcUiSlider)sliders.get(i)).draw();
            }
            
            if(tabs.size() > 0){
                tabBg();
            }
            for(int i = 0; i < tabs.size(); i++) {
                ((AlcUiTab)tabs.get(i)).draw();
            }
        }
    }
    
    public void addButton(String name, int x, int y, String icon){
        buttons.add(new AlcUiButton(root, this, name, x, y, icon));
    }
    
    public void addButton(String name, int x, int y, String icon, File filePath){
        buttons.add(new AlcUiButton(root, this, name, x, y, icon, filePath));
    }
    
    public void removeButton(String name){
        for(int i = 0; i < buttons.size(); i++) {
            if(((AlcUiButton) buttons.get(i)).name == name){
                buttons.remove(i);
                root.redraw();
                break;
            }
        }
    }
    
    public void addToggleButton(String name, int x, int y, Boolean on, String icon){
        toggleButtons.add(new AlcUiToggleButton(root, this, name, x, y, on, icon));
    }
    
    public void addToggleButton(String name, int x, int y, Boolean on, String icon, File filePath){
        toggleButtons.add(new AlcUiToggleButton(root, this, name, x, y, on, icon, filePath));
    }
    
    public void removeToogleButton(String name){
        for(int i = 0; i < toggleButtons.size(); i++) {
            if(((AlcUiToggleButton) toggleButtons.get(i)).name == name){
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
            if(((AlcUiToggleButton) toggleButtons.get(i)).name == name){
                state = ((AlcUiToggleButton)toggleButtons.get(i)).getState();
                called = true;
                break;
            }
        }
        if(!called) root.println("No such button: " + name);
        return state;
    }
    
    public void addSlider(String name, int x, int y, int value, String icon){
        sliders.add(new AlcUiSlider(root, this, name, x, y, value, icon));
    }
    
    public void addSlider(String name, int x, int y, int value, String icon, File filePath){
        sliders.add(new AlcUiSlider(root, this, name, x, y, value, icon, filePath));
    }
    
    public int getSliderValue(String name){
        int value = 0;
        boolean called = false;
        for(int i = 0; i < sliders.size(); i++) {
            if(((AlcUiSlider) sliders.get(i)).name == name){
                called = true;
                value = ((AlcUiSlider) sliders.get(i)).value;
                break;
            }
        }
        if(!called) root.println("No such slider: " + name);
        return value;
    }
    
    public void removeSlider(String name){
        for(int i = 0; i < sliders.size(); i++) {
            if(((AlcUiSlider) sliders.get(i)).name == name){
                sliders.remove(i);
                root.redraw();
                break;
            }
        }
    }
    
    public void addTab(String name, int x, int y, boolean on, int id, String text, String icon){
        tabs.add(new AlcUiTab(root, this, name, x, y, on, id, text, icon));
    }
    
    public void addTab(String name, int x, int y, boolean on, int id, String text, String icon, File filePath){
        tabs.add(new AlcUiTab(root, this, name, x, y, on, id, text, icon, filePath));
    }
    
    public void changeTab(int t){
        for(int i = 0; i < tabs.size(); i++) {
            if(i == t){
                ((AlcUiTab)tabs.get(i)).setState(true);
            } else {
                ((AlcUiTab)tabs.get(i)).setState(false);
            }
        }
        root.redraw();
    }
    
    public int getTabWidth(int i){
        return ((AlcUiTab)tabs.get(i)).getWidth();
    }
    
    public void tabBg(){
        // Tab Bg
        root.noStroke();
        root.fill(215);
        root.rect(0, 0, root.width, 39);
        root.fill(245);
        root.rect(0, 39, root.width, 30);
    }
    
    
    public void rollOverCheckObjects(int x, int y){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcUiButton)buttons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcUiToggleButton)toggleButtons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcUiSlider)sliders.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < tabs.size(); i++) {
            ((AlcUiTab)tabs.get(i)).rollOverCheck(x, y);
        }
    }
    
    public void pressedCheckObjects(int x, int y){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcUiButton)buttons.get(i)).pressedCheck();
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcUiToggleButton)toggleButtons.get(i)).pressedCheck();
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcUiSlider)sliders.get(i)).pressedCheck(x, y);
        }
        for(int i = 0; i < tabs.size(); i++) {
            ((AlcUiTab)tabs.get(i)).pressedCheck();
        }
    }
    
    public void draggedCheckObjects(int x, int y){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcUiButton)buttons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcUiToggleButton)toggleButtons.get(i)).rollOverCheck(x, y);
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcUiSlider)sliders.get(i)).draggedCheck(x, y);
        }
        for(int i = 0; i < tabs.size(); i++) {
            ((AlcUiTab)tabs.get(i)).rollOverCheck(x, y);
        }
    }
    
    public void releasedCheckObjects(){
        for(int i = 0; i < buttons.size(); i++) {
            ((AlcUiButton)buttons.get(i)).releasedCheck();
        }
        for(int i = 0; i < toggleButtons.size(); i++) {
            ((AlcUiToggleButton)toggleButtons.get(i)).releasedCheck();
        }
        for(int i = 0; i < sliders.size(); i++) {
            ((AlcUiSlider)sliders.get(i)).releasedCheck();
        }
        for(int i = 0; i < tabs.size(); i++) {
            ((AlcUiTab)tabs.get(i)).releasedCheck();
        }
    }
    
    public void mouseEvent(MouseEvent event) {
        if(visible){
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
    
    public boolean getVisible(){
        return visible;
    }
    
    public void setVisible(boolean v){
        visible = v;
        root.redraw();
    }
    
}
