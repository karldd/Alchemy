package alchemy;

import processing.core.*;
import controlP5.*;

import java.util.Vector;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.ArrayList;

import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;

public class Main extends PApplet {
    
    public Main() {
    }
    
    public static void main(String[] args) {
        PApplet.main( new String[]{"alchemy.Main"} );
    }
    
    private PluginManager pluginManager;
    Module[] modules;
    ControlP5 controlP5;
    
    int numberOfPlugins;
    int currentModule;
    //PFont font;
    
    public void setup(){
        size(800, 600);
        background(255);
        controlP5 = new ControlP5(this);
        
        //font = loadFont("PRO5L___.ttf");
        loadPlugins();
        
        
        if(numberOfPlugins > 0){
            addPlugins();
            //
            loadTabs();
        }
        
    }
    
    public void draw(){
         line(10, 10, 100, 100);
    }
    
    public void mousePressed() {
        modules[currentModule].mousePressed();
    }
    
    public void mouseDragged() {
        modules[currentModule].mouseDragged();
    }
    
    public void mouseReleased() {
        modules[currentModule].mouseReleased();
    }
    
    public void loadTabs(){
        for(int i = 0; i < modules.length; i++) {
            if(i == 0){
                controlP5.tab("default").activateEvent(true);
                controlP5.tab("default").setId(i);
                controlP5.tab("default").setLabel(modules[i].category());
                //controlP5.tab("default").setMoveable(true);
                //controlP5.tab("default").setPosition(100, 100);
                //controlP5.tab("default").getTab().setPosition(100, 100);
                //println(controlP5.tab("default").getTab());
                currentModule = 0;
                
            } else {
                controlP5.tab(modules[i].category()).activateEvent(true);
                controlP5.tab(modules[i].category()).setId(i);
                controlP5.tab(modules[i].category()).setPosition(100, 100);
            }
            
            // Name, Value, X, Y, Width, Height
            //controlP5.addButton(modules[i].category(), i, 100*i, 160, 80, 20).setId(i);
        }
    }
    
    
    void controlEvent(ControlEvent theEvent) {
        /*
        if(theEvent.isController()) {
         
            int id = theEvent.controller().id();
            switch(id){
                case 0:
                    rect((int)random(width),(int)random(height),5,5);
                    println("Button 0");
                    break;
                case 1:
                    println("Button 1");
                    break;
            }
         
        } else
         */
        // TAB EVENTS
        if (theEvent.isTab()) {
            
            int id = theEvent.tab().id();
            println(id);
            modules[id].setup(this);
            currentModule = id;
            
            // theEvent.tab().name();
            /*switch(id){
                case 0:
                    rect((int)random(width),(int)random(height),5,5);
                    println("Tab 0");
                    break;
                case 1:
                    println("Tab 1");
                    break;
            }
             */
        }
        
        
    }
    
    private void loadPlugins() {
        
        pluginManager = ObjectFactory.newInstance().createManager();
        
        File pluginsDir = new File("plugins");
        
        File[] plugins = pluginsDir.listFiles(new FilenameFilter() {
            
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".zip");
            }
            
        });
        
        try {
            
            PluginLocation[] locations = new PluginLocation[plugins.length];
            numberOfPlugins = plugins.length;
            modules = new Module[numberOfPlugins];
            
            for (int i = 0; i < plugins.length; i++) {
                locations[i] = StandardPluginLocation.create(plugins[i]);
            }
            
            // Registers plug-ins and their locations with this plug-in manager.
            pluginManager.publishPlugins(locations);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    private void addPlugins() {
        
        try {
            
            //Returns collection of descriptors of all plug-ins that was successfully populated by this registry.
            Iterator it = pluginManager.getRegistry().getPluginDescriptors().iterator();
            int i = 0;
            
            while (it.hasNext()) {
                
                // Main interface to get access to all meta-information for particular plug-in, described in plug-in manifest file.
                PluginDescriptor p = (PluginDescriptor) it.next();
                //println(p.getId());
                
                modules[i] = (Module) pluginManager.getPlugin(p.getId());
                modules[i].setIndex(i);
                i++;
                
                //textFont(font);
                //text(modules.category(), 15, 60, -30);
                //println(modules.category());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
}
