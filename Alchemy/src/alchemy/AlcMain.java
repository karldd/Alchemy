package alchemy;

import processing.core.*;

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

public class AlcMain extends PApplet {
    
    public AlcMain() {
    }
    
    public static void main(String[] args) {
        PApplet.main( new String[]{"alchemy.AlcMain"} );
    }
    
    private PluginManager pluginManager;
    AlcModule[] modules;
    
    int numberOfPlugins;
    int currentModule;
    //PFont font;
    AlcUI ui, ui2;
    
    boolean saveOneFrame = false;
    boolean firstLoad = true;
    
    public void setup(){
        size(800, 600);
        background(255);
        
        registerMouseEvent(this);
        
        loadPlugins();
        
        if(numberOfPlugins > 0){
            addPlugins();
            currentModule = 1;
            loadTabs();
        }
        
        noLoop();
        
    }
    
    public void draw(){
        if(saveOneFrame) {
            beginRecord(PDF, "frame-####.pdf");
        }
        background(255);
        
        modules[currentModule].draw();
        
        if(saveOneFrame) {
            endRecord();
            saveOneFrame = false;
        }
    }
    
    public void loadTabs(){
        
        ui = new AlcUI(this);
        //ui2 = new AlcUI(this);
        int tabsWidth = 0;
        
        for(int i = 0; i < modules.length; i++) {
            boolean current = false;
            if(i == currentModule){
                current = true;
            }
            
            // Add Tabs
            ui.addTab(modules[i].getName(), tabsWidth + 5, 5, current, modules[i].getId(), modules[i].getName(), modules[i].getIconName(), modules[i].getPluginPath());
            
            tabsWidth += ui.getTabWidth(i);
            
        }
        
        setModule(currentModule);
        /*
        ui2.setVisible(true);
        ui2.addToggleButton("Test", 200, 210, true, "b.gif");
         
        //println(modules[1].getPluginPath().getPath());
        ui2.addSlider("ok", 100, 400, 50, "slider.gif");
         */
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
            modules = new AlcModule[numberOfPlugins];
            
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
                
                pluginManager.activatePlugin(descr.getId());
                
                ClassLoader classLoader = pluginManager.getPluginClassLoader(descr);
                Class pluginCls = classLoader.loadClass(ext.getParameter("class").valueAsString());
                
                modules[i] =  (AlcModule)pluginCls.newInstance();
                
                // GET THE FILE PATH & ICON NAME
                // Return the path of the XML file as a string
                String path = descr.getLocation().getPath();
                // Remove the XML file name after the "!" mark and make a URI
                URI pathUri = new URI(path.substring(0, path.lastIndexOf("!")));
                // Convert it into an abstract file name
                File pathFile = new File(pathUri);
                
                if(pathFile.exists()){
                    
                    modules[i].setPluginPath(pathFile);
                    println("Loaded " + pathFile.getPath());
                }
                
                // Set the icon name and the decription name from the XML
                String descriptionParam = ext.getParameter("description").valueAsString();
                String iconParam = ext.getParameter("icon").valueAsString();
                String nameParam = ext.getParameter("name").valueAsString();
                
                /*
                // Get the ID
                String name = descr.getId();
                name = name.substring(name.lastIndexOf(".")+1);
                println(name);
                 */
                
                modules[i].setName(nameParam);
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
        if(firstLoad){
            modules[i].setLoaded(true);
            modules[i].setup(this);
            firstLoad = false;
        } else{
            if(currentModule != i){
                if(modules[i].getLoaded()){
                    modules[i].refocus();
                } else{
                    modules[i].setLoaded(true);
                    modules[i].setup(this);
                }
                ui.changeTab(i);
                currentModule = i;
            }
        }
    }
    
    public void toogleTabs(int why){
        if(why < 20){
            if(!ui.getVisible()){
                ui.setVisible(true);
            }
        } else if(why > 100){
            if(ui.getVisible()){
                ui.setVisible(false);
            }
        }
    }
    
    public void keyPressed(){
        // Disable the default Processing quit key - ESC
        if(keyCode == ESC || key == ESC){
            key = 0;
            keyCode = 0;
        }
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
                toogleTabs(y);
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
        
        //println(e.getSource());
        
        // Determine what kind UI Object called the event
        String object = e.getSource().toString();
        int dot = object.lastIndexOf(".");
        int at = object.lastIndexOf("@");
        object = object.substring(dot+1, at);
        //println(object);
        
        // TAB
        if(object.equals("AlcUiTab")){
            //println("Tab");
            setModule(e.getID());
            
            // BUTTON
        } else if(object.equals("AlcUiButton")){
            //println("Button");
            
            // TOGGLE BUTTON
        } else if(object.equals("AlcUiToggleButton")){
            //println("Toggle Button");
            
            
            // SLIDER
        } else if(object.equals("AlcUiSlider")){
            //println("Slider");
        }
        
    }
    
    
}
