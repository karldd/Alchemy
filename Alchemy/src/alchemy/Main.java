package alchemy;

import processing.core.*;
//import seltar.unzipit.*;

import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.ArrayList;

import java.net.URI;
import java.net.URISyntaxException;

import org.java.plugin.ObjectFactory;
import org.java.plugin.PluginManager;
import org.java.plugin.PluginManager.PluginLocation;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.standard.StandardPluginLocation;
import org.java.plugin.PluginClassLoader;

public class Main extends PApplet {
    
    public Main() {
    }
    
    public static void main(String[] args) {
        PApplet.main( new String[]{"alchemy.Main"} );
    }
    
    private PluginManager pluginManager;
    Module[] modules;
    
    int numberOfPlugins;
    int currentModule;
    //PFont font;
    AlcUI ui;
    
    boolean saveOneFrame = false;
    //PImage test;
    
    public void setup(){
        size(800, 600);
        background(255);
        
        registerMouseEvent(this);
        
        loadPlugins();
        
        
        if(numberOfPlugins > 0){
            addPlugins();
            //
            loadTabs();
            //println(numberOfPlugins);
            //println(modules.length);
        }
        
        //UnZipIt zip = new UnZipIt("/Users/karldd/Alchemy/Code/svn/Alchemy/data/b.zip", this);
        //test = zip.loadImage("b.gif");
        
        //test = loadImage("file:/Users/karldd/Alchemy/Code/svn/Alchemy/plugins/alchemy.test-1.0.0.zip!/data/b.gif");
        
        
        //ui.addButton("mbutton", 100, 190, "b.gif");
        //ui.addToggleButton("lbutton", 250, 75, "b.gif", false);
        
    }
    
    public void draw(){
        if(saveOneFrame) {
            beginRecord(PDF, "frame-####.pdf");
        }
        background(255);
        
        //image(test, 300, 300);
        
        
        modules[currentModule].draw();
        //line(10, 10, 100, 100);
        
        
        if(saveOneFrame) {
            endRecord();
            saveOneFrame = false;
        }
    }
    
    public void loadTabs(){
        setModule(0);
        ui = new AlcUI(this);
        for(int i = 0; i < modules.length; i++) {
            //println(modules[i].getName());
            ui.addButton(modules[i].getName(), 10+120*i, 10, modules[i].getIconName(), modules[i].getPluginPath());
            
            // Name, Value, X, Y, Width, Height
            //controlP5.addButton(modules[i].getName(), i, 100*i, 160, 80, 20).setId(i);
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
            
            // Number of plugins minus one for the core plugin
            numberOfPlugins = plugins.length-1;
            modules = new Module[numberOfPlugins];
            
            for (int i = 0; i < plugins.length; i++) {
                locations[i] = StandardPluginLocation.create(plugins[i]);
                //println(plugins[i].getAbsolutePath());
            }
            
            // Registers plug-ins and their locations with this plug-in manager.
            pluginManager.publishPlugins(locations);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    private void addPlugins() {
        
        try {
            
            PluginDescriptor core = pluginManager.getRegistry().getPluginDescriptor("alchemy.core");
            
            ExtensionPoint point = pluginManager.getRegistry().getExtensionPoint(core.getId(), "Module");
            
            int i = 0;
            
            for (Iterator it = point.getConnectedExtensions().iterator(); it.hasNext();) {
                
                Extension ext = (Extension) it.next();
                
                PluginDescriptor descr = ext.getDeclaringPluginDescriptor();
                
                //println(descr.getId());
                //println(iconParam + " " + descrParam);
                pluginManager.activatePlugin(descr.getId());
                
                ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());
                
                modules[i] =  (Module)pluginCls.newInstance();
                
                // GET THE FILE PATH & ICON NAME
                // Return the path of the XML file as a string
                String path = ext.getDeclaringPluginDescriptor().getLocation().getPath();
                // Remove the XML file name after the "!" mark and make a URI
                URI pathUrl = new URI(path.substring(0, path.lastIndexOf("!")));
                // Convert it into an abstract file name
                File pathFile = new File(pathUrl);
                
                if(pathFile.exists()){
                    
                    modules[i].setPluginPath(pathFile);
                    println("Loaded " + pathFile.getPath());
                }
                
                // Set the icon name and the decription name from the XML
                String descriptionParam = ext.getParameter("description").valueAsString();
                String iconParam = ext.getParameter("icon").valueAsString();
                
                modules[i].setIconName(iconParam);
                modules[i].setDescriptionName(descriptionParam);
                modules[i].setId(i);
                i++;
                
                //textFont(font);
                //text(modules.getName(), 15, 60, -30);
                //println(modules.getName());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setModule(int i){
        if(modules[i].getLoaded()){
            modules[i].refocus();
        } else{
            modules[i].setLoaded(true);
            modules[i].setup(this);
        }
        currentModule = i;
    }
    
    public void mouseEvent(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                //println("Mouse Pressed - X:" + x + " Y: " + y);
                modules[currentModule].mousePressed(x, y);
                break;
            case MouseEvent.MOUSE_CLICKED:
                //println("Mouse Clicked - X:" + x + " Y: " + y);
                modules[currentModule].mouseClicked(x, y);
                break;
            case MouseEvent.MOUSE_MOVED:
                //println("Mouse Moved - X:" + x + " Y: " + y);
                modules[currentModule].mouseMoved(x, y);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                //println("Mouse Dragged - X:" + x + " Y: " + y);
                modules[currentModule].mouseDragged(x, y);
                
                break;
            case MouseEvent.MOUSE_RELEASED:
                //println("Mouse Released - X:" + x + " Y: " + y);
                modules[currentModule].mouseReleased(x, y);
                break;
                
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        
        for(int i = 0; i < modules.length; i++) {
            if(e.getActionCommand() == modules[i].getName()) {
                if(i != currentModule){
                    setModule(i);
                    //println(e.getActionCommand());
                    break;
                }
            }
        }
    }
    
    
}
