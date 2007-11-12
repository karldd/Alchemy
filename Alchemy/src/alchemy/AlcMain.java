package alchemy;

import processing.core.*;
import processing.pdf.*;

import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import java.awt.Toolkit;
import javax.swing.*;

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
    
    // platform IDs
    static final int WINDOWS = 1;
    static final int MACOSX  = 3;
    static final int LINUX   = 4;
    static final int OTHER   = 0;
    
    public static void main(String[] args) {
        PApplet.main( new String[]{"alchemy.AlcMain"} );
    }
    
    /**
     * Full name of the Java version (i.e. 1.5.0_11).
     */
    public static final String javaVersionName = System.getProperty("java.version");
    
    /**
     * Version of Java that's in use, whether 1.1 or 1.3 or whatever,
     * stored as a float.
     */
    public static final float javaVersion = new Float(javaVersionName.substring(0, 3)).floatValue();
    
    /**
     * Current platform in use.
     * <P>
     * Equivalent to System.getProperty("os.name"), just used internally.
     */
    static public String platformName =
            System.getProperty("os.name");
    
    /**
     * Current platform in use, one of the
     * PConstants WINDOWS, MACOSX, LINUX or OTHER.
     */
    static public int platform;
    
    static {
        if (platformName.indexOf("Mac") != -1) {
            platform = MACOSX;
            
        } else if (platformName.indexOf("Windows") != -1) {
            platform = WINDOWS;
            
        } else if (platformName.equals("Linux")) {  // true for the ibm vm
            platform = LINUX;
            
        } else {
            platform = OTHER;
        }
    }
    
    /**
     * Modifier flags for the shortcut key used to trigger menus.
     * (Cmd on Mac OS X, Ctrl on Linux and Windows)
     */
    static public final int MENU_SHORTCUT = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    
    
    
    
    private PluginManager pluginManager;
    AlcModule[] modules;
    int numberOfPlugins;
    int currentModule;
    
    AlcUi ui;
    
    boolean saveOneFrame = false;
    String pdfURL;
    
    boolean firstLoad = true;
    boolean inToolBar = false;
    
    public void setup(){
        size(640, 480);
        //size(screen.width, screen.height);
        
        /* RESIZEABLE FRAME - BUGGY
        frame.setResizable(true);
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if(e.getSource()==frame) {
                    println("Redraw");
                    redraw();
                }
            }
        }
        );
         */
        
        //frame.setTitle("Alchemy");
        
        // set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        registerMouseEvent(this);
        registerKeyEvent(this);
        
        loadPlugins();
        
        background(255);
        
        if(numberOfPlugins > 0){
            addPlugins();
            // Set the start module
            currentModule = 0;
            loadTabs();
        }
        
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        System.out.println("Menu shortcut key mask = " + tk.getMenuShortcutKeyMask());
        
        noLoop();
        
    }
    
    public void draw(){
        if(saveOneFrame) {
            //beginRecord(PDF, "Alchemy-####.pdf");
            beginRecord(PDF, pdfURL);
        }
        
        background(255);
        //rect(width-40, height-40, 40, 40);
        
        modules[currentModule].draw();
        
        if(saveOneFrame) {
            endRecord();
            saveOneFrame = false;
        }
    }
    
    public void loadTabs(){
        
        ui = new AlcUi(this);
        int tabsWidth = 0;
        
        for(int i = 0; i < modules.length; i++) {
            boolean current = false;
            if(i == currentModule){
                current = true;
            }
            
            // Add Tabs
            ui.addTab(modules[i].getName(), tabsWidth + 5, 5, current, modules[i].getId(), modules[i].getName(), modules[i].getIconName(), modules[i].getPluginPath());
            
            tabsWidth = ui.getTabWidth(i);
            
        }
        
        setModule(currentModule);
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
            modules[i].resetCursor();
            firstLoad = false;
        } else{
            if(currentModule != i){
                if(modules[i].getLoaded()){
                    modules[i].refocus();
                    modules[i].resetSmooth();
                    modules[i].resetLoop();
                } else{
                    modules[i].setLoaded(true);
                    modules[i].setup(this);
                }
                ui.changeTab(i, modules[i].hasUi());
                
                // Toggle visibility of Module UI
                for(int j = 0; j < modules.length; j++) {
                    if (j == i){
                        modules[j].setUiVisible(true);
                    }else {
                        modules[j].setUiVisible(false);
                    }
                }
                currentModule = i;
            }
        }
    }
    
    public void toggleToolbar(int why){
        if(why < 5){
            if(!ui.getVisible()){
                cursor(ARROW);
                ui.setVisible(true);
                modules[currentModule].setUiVisible(true);
                inToolBar = true;
            }
        } else if(why > 85){
            if(ui.getVisible()){
                modules[currentModule].resetCursor();
                modules[currentModule].setUiVisible(false);
                ui.setVisible(false);
                inToolBar = false;
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
        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                if(!inToolBar){
                    modules[currentModule].mousePressed(event);
                }
                break;
            case MouseEvent.MOUSE_CLICKED:
                if(!inToolBar){
                    modules[currentModule].mouseClicked(event);
                }
                break;
            case MouseEvent.MOUSE_MOVED:
                int y = event.getY();
                toggleToolbar(y);
                if(!inToolBar){
                    modules[currentModule].mouseMoved(event);
                }
                break;
            case MouseEvent.MOUSE_DRAGGED:
                if(!inToolBar){
                    modules[currentModule].mouseDragged(event);
                }
                //resizeWindow();
                break;
            case MouseEvent.MOUSE_RELEASED:
                if(!inToolBar){
                    modules[currentModule].mouseReleased(event);
                }
                //resizing = false;
                break;
        }
    }
    
    public void keyEvent(KeyEvent event) {
        
        int keyCode = event.getKeyCode();
        //String keyText = event.getKeyText(keyCode);
        
        switch(event.getID()){
            case KeyEvent.KEY_PRESSED:
                modules[currentModule].keyPressed(event);
                //println(keyCode);
                break;
            case KeyEvent.KEY_RELEASED:
                modules[currentModule].keyReleased(event);
                //println(event.getModifiersExText(keyCode));
                
                // Modifier + E = Save a PDF file
                if(event.getModifiers() == MENU_SHORTCUT && keyCode == 69){
                    openFileDialog();
                    //saveOneFrame = true;
                    //println("PDF Export");
                    redraw();
                }
                
                break;
            case KeyEvent.KEY_TYPED:
                modules[currentModule].keyTyped(event);
                break;
        }
    }
    
    public void tabEvent(ActionEvent e){
        setModule(e.getID());
    }
    
    public void openFileDialog(){
        // create a file chooser
        final JFileChooser fc = new JFileChooser();
        
        // in response to a button click:
        int returnVal = fc.showSaveDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            pdfURL = file.getPath();
            
            saveOneFrame = true;
            redraw();
            println(pdfURL);
            
        } else {
            println("Open command cancelled by user.");
        }
    }
    
}
