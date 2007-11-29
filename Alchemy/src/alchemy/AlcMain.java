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
    
    
    private AlcPlugin plugins;
    public ArrayList<AlcModule> creates;
    public ArrayList<AlcModule> affects;
    
    AlcModule currentCreate;
    ArrayList<AlcModule> currentAffects;
    
    // Module name strings to pass to the toolBar
    //private String[] createNames, affectNames;
    
    /** Preferred size of the window */
    private Dimension windowSize = new Dimension(800, 600);
    private Color bgColour = Color.WHITE;
    
    /** User Interface Tool Bar */
    public AlcToolBar toolBar;
    /** Canvas to draw on to */
    public AlcCanvas canvas;
    
    public AlcMain() {
        
        // LOAD PLUGINS
        plugins = new AlcPlugin();
        System.out.println("Number of Plugins: "+plugins.getNumberOfPlugins());
        
        // Add each type of plugin
        if(plugins.getNumberOfPlugins() > 0){
            creates = plugins.addPlugins("Create");
            affects = plugins.addPlugins("Affect");
        }
        
        // LOAD INTERFACE AND CANVAS
        loadInterface();
        
    }
    
    public static void main(String[] args) {
        AlcMain window = new AlcMain();
        window.setVisible(true);
    }
    
    private void loadInterface(){
        
        /*
        if(plugins.getNumberOfPlugins() > 0){
            // Load the names of the plugins for the ComboBoxes
            createNames = new String[creates.size()];
            for (int i = 0; i < creates.size(); i++) {
                createNames[i] = creates.get(i).getName();
            }
            affectNames = new String[affects.size()];
            for (int i = 0; i < affects.size(); i++) {
                affectNames[i] = affects.get(i).getName();
            }
        }
         */
        
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
    
    
    // SETTER FUNCTIONS
    
    /** Set the current create function */
    public void setCurrentCreate(int i) {
        currentCreate = creates.get(i);
        
        // Call that module
        if(!currentCreate.getLoaded()){
            currentCreate.setup(this);
        } else {
            currentCreate.refocus();
        }
    }
    
    public void removeAffect(int i){
        currentAffects.remove(i);
    }
    
    public void addAffect(int i){
        currentAffects.add(affects.get(i));
    }
    
    
    // KEY EVENTS
    public void keyPressed(KeyEvent e) {
        if(currentCreate!= null){
            currentCreate.keyPressed(e);
        }
        /*
        if(currentAffects.size() > 0){
            for (int i = 0; i < currentAffects.size(); i++) {
                currentAffects.get(i).keyPressed(e);
            }
        }
         */
    }
    
    public void keyTyped(KeyEvent e)   {
        if(currentCreate!= null){
            currentCreate.keyTyped(e);
        }
        /*
        if(currentAffects.size() > 0){
            for (int i = 0; i < currentAffects.size(); i++) {
                currentAffects.get(i).keyTyped(e);
            }
        }
         */
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
        if(currentCreate!= null){
            currentCreate.keyReleased(e);
        }
        
        
        /*
        if(currentAffects.size() > 0){
            for (int i = 0; i < currentAffects.size(); i++) {
                currentAffects.get(i).keyReleased(e);
            }
        }
         */
    }
    
    public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    
    public void componentResized(ComponentEvent e) {
        
        // Get and set the new size of the window
        windowSize = e.getComponent().getSize();
        // Resize the UI and Canvas
        toolBar.resizeUi(windowSize);
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
