/**
 * AlcPlugin.java
 *
 * Created on November 22, 2007, 6:38 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy;

import alchemy.ui.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AlcMain extends JFrame implements AlcConstants, ComponentListener, KeyListener {
    
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
    
    /** Class to take care of plugin loading */
    private AlcPlugin plugins;
    /** Class of utility math functions */
    public AlcMath math = new AlcMath();
    
    /** Lists of the installed modules */
    public AlcModule[] creates;
    public AlcModule[] affects;
    
    /** The currently selected create module - set to -1 initially when nothing is selected */
    public int currentCreate = -1;
    /** The currently selected affect modules */
    boolean[] currentAffects;
    /** The number of affect modules currently selected */
    private int numberOfCurrentAffects = 0;
    
    
    /** Preferred size of the window */
    private Dimension windowSize = new Dimension(800, 600);
    private Color bgColour = Color.WHITE;
    
    /** User Interface Tool Bar */
    public AlcToolBar toolBar;
    /** Canvas to draw on to */
    public AlcCanvas canvas;
    
    public AlcMain() {
        
        // LOAD PLUGINS
        plugins = new AlcPlugin(this);
        System.out.println("Number of Plugins: " + getNumberOfPlugins());
        
        // Initialise the on/off array for current affects
        currentAffects = new boolean[plugins.getNumberOfAffectModules()];
        
        // Add each type of plugin
        if(plugins.getNumberOfPlugins() > 0){
            // Extension Point Name, Array Size, Module Type
            creates = plugins.addPlugins("Create", getNumberOfCreateModules(), CREATE);
            affects = plugins.addPlugins("Affect", getNumberOfAffectModules(), AFFECT);
        }
        
        // LOAD INTERFACE AND CANVAS
        loadInterface();
        
    }
    
    public static void main(String[] args) {
        AlcMain window = new AlcMain();
        window.setVisible(true);
    }
    
    private void loadInterface(){
        
        // User Interface toolbar
        toolBar = new AlcToolBar(this);
        
        // The canvas to draw on
        canvas = new AlcCanvas(this);
        
        
        // LAYERED PANE
        JLayeredPane layeredPane = new JLayeredPane();
        // Add the UI on top of the canvas
        layeredPane.add(canvas, new Integer(1));
        layeredPane.add(toolBar, new Integer(2));
        
        // FRAME
        this.setContentPane(layeredPane);           // Set the layered pane as the main content pane
        this.setPreferredSize(windowSize);          // Set the window size
        this.addComponentListener(this);            // Add a component listener to detect window resizing
        this.addKeyListener(this);                  // Key Listener
        this.setFocusable(true);                    // Make the key listener focusable so we can get key events
        this.requestFocus();                        // Get focus for the key listener
        //this.setTitle("al.chemy");                // Title of the frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();                                // Finalize window layout
        this.setLocationRelativeTo(null);           // Center window on screen.
        
        
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    // GLOBAL GETTER INFO
    
    /** Get the Window Size as a Dimension */
    public Dimension getWindowSize(){
        return windowSize;
    }
    
    /** Get the platform */
    public int getPlatform(){
        return platform;
    }
    
    /** Get the Background Colour */
    public Color getBgColour(){
        return bgColour;
    }
    
    /** Get the number of plugins */
    public int getNumberOfPlugins(){
        return plugins.getNumberOfPlugins();
    }
    
    /** Get the number of create modules */
    public int getNumberOfCreateModules(){
        return plugins.getNumberOfCreateModules();
    }
    
    /** Get the number of affect modules */
    public int getNumberOfAffectModules(){
        return plugins.getNumberOfAffectModules();
    }
    
    
    /** Return true if there are affect modules currently loaded */
    public boolean hasCurrentAffects(){
        if(numberOfCurrentAffects > 0){
            return true;
        } else {
            return false;
        }
    }
    
    
    // SETTER FUNCTIONS
    
    /** Set the current create function */
    public void setCurrentCreate(int i) {
        
        currentCreate = i;
        
        // Call that module
        // Check to see if it has been loaded, if not load it and run setup()
        if(creates[i].getLoaded()){
            creates[i].reselect();
        } else {
            creates[i].setLoaded(true);
            creates[i].setup();
        }
        
    }
    
    /** Add an affect to the current affect array to be processed
     *  The affect is added at its index value so it can easily be removed later
     */
    public void addAffect(int i){
        numberOfCurrentAffects++;
        currentAffects[i] = true;
        
        // Call that module
        if(affects[i].getLoaded()){
            affects[i].reselect();
        } else {
            affects[i].setLoaded(true);
            affects[i].setup();
            
        }
        
    }
    
    /** Remove an affect from the current affect array
     *  The affect is removed at its index value
     */
    public void removeAffect(int i){
        numberOfCurrentAffects--;
        currentAffects[i] = false;
        affects[i].deselect();
    }
    
    
    // KEY EVENTS
    
    public void keyPressed(KeyEvent e) {
        // Pass the key event on to the current modules
        if(currentCreate >= 0){
            creates[currentCreate].keyPressed(e);
        }
        
        // Pass the key event to the current affects
        if(hasCurrentAffects()){
            for (int i = 0; i < currentAffects.length; i++) {
                if(currentAffects[i])
                    affects[i].keyPressed(e);
            }
        }
        
    }
    
    public void keyTyped(KeyEvent e)   {
        // Pass the key event on to the current modules
        if(currentCreate >= 0){
            creates[currentCreate].keyTyped(e);
        }
        
        // Pass the key event to the current affects
        if(hasCurrentAffects()){
            for (int i = 0; i < currentAffects.length; i++) {
                if(currentAffects[i])
                    affects[i].keyTyped(e);
            }
        }
    }
    
    public void keyReleased(KeyEvent e){
        
        int keyCode = e.getKeyCode();
        
        // GLOBAL KEYS - when the Modifier is down
        if(e.getModifiers() == MENU_SHORTCUT){
            
            switch(keyCode){
                // Clear the Canvas
                case BACKSPACE:
                case DELETE:
                    canvas.clear();
                    break;
            }
            
        }
        
        // Pass the key event on to the current modules
        if(currentCreate >= 0){
            creates[currentCreate].keyReleased(e);
        }
        
        
        // Pass the key event to the current affects
        if(hasCurrentAffects()){
            for (int i = 0; i < currentAffects.length; i++) {
                if(currentAffects[i])
                    affects[i].keyReleased(e);
            }
        }
        
    }
    
    public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    
    public void componentResized(ComponentEvent e) {
        
        // Get and set the new size of the window
        windowSize = e.getComponent().getSize();
        // Resize the UI and Canvas
        toolBar.resizeToolBar(windowSize);
        canvas.resizeCanvas(windowSize);
        
    }
    /*
    public void openFileDialog(){
        // create a file chooser
        final JFileChooser fc = new JFileChooser();
     
        // in response to a button click:
        int returnVal = fc.showSaveDialog(this);
     
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
     
            pdfURL = file.getPath();
     
            saveOneFrame = true;
            //redraw();
            //println(pdfURL);
     
        } else {
            //println("Open command cancelled by user.");
        }
    }
     */
    
}
