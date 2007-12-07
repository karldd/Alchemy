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
     * Current platform in use, one of the
     * PConstants WINDOWS, MACOSX, LINUX or OTHER.
     */
    static public int platform;
    /** Modifier Key to show for tool tips */
    static public String MODIFIER_KEY;

    static {
        if (platformName.indexOf("Mac") != -1) {
            platform = MACOSX;
            // Mac command key symbol
            MODIFIER_KEY = "\u2318";

        } else if (platformName.indexOf("Windows") != -1) {
            platform = WINDOWS;
            MODIFIER_KEY = "Ctrl";

        } else if (platformName.equals("Linux")) {  // true for the ibm vm
            platform = LINUX;
            MODIFIER_KEY = "Ctrl";

        } else {
            platform = OTHER;
            MODIFIER_KEY = "Modifier";
        }
    }
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
    /** User Interface Tool Bar */
    public AlcToolBar toolBar;
    /** Canvas to draw on to */
    public AlcCanvas canvas;
    /** Toggle between windowed and fullscreen */
    protected boolean fullscreen = false;
    /** For storing the old display size before entering fullscreen */
    private Dimension oldWindowSize = null;
    /** For storing the old display location before entering fullscreen */
    private Point oldLocation = null;

    public AlcMain() {

        // TODO - Sort out the build.xml - copy correctly etc...

        // LOAD PLUGINS
        plugins = new AlcPlugin();
        System.out.println("Number of Plugins: " + getNumberOfPlugins());

        // Initialise the on/off array for current affects
        currentAffects = new boolean[plugins.getNumberOfAffectModules()];

        // Add each type of plugin
        if (plugins.getNumberOfPlugins() > 0) {
            String[] createsOrder = {"Shapes", "Inverse Shapes", "Type Shapes"};
            String[] affectsOrder = {"Symmetry", "Blindness", "Microphone"};
            // Extension Point Name, Array Size, Module Type
            creates = plugins.addPlugins("Create", getNumberOfCreateModules(), CREATE, createsOrder);
            affects = plugins.addPlugins("Affect", getNumberOfAffectModules(), AFFECT, affectsOrder);
        }

        // LOAD INTERFACE AND CANVAS
        loadInterface();

        // Set the global access to root, canvas, and toolBar for each module
        for (int i = 0; i < creates.length; i++) {
            creates[i].setGlobals(this, canvas, toolBar);
        }
        for (int i = 0; i < affects.length; i++) {
            affects[i].setGlobals(this, canvas, toolBar);
        }

        // Set the default create module
        currentCreate = 0;
        creates[currentCreate].setup();

    }

    public static void main(String[] args) {
        AlcMain window = new AlcMain();
        window.setVisible(true);
    }

    private void loadInterface() {

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
        //this.addWindowStateListener(this);          // Add a window state listener to detect window maximising
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
    public Dimension getWindowSize() {
        return windowSize;
    }

    /** Get the platform */
    public int getPlatform() {
        return platform;
    }

    /** Get the number of plugins */
    public int getNumberOfPlugins() {
        return plugins.getNumberOfPlugins();
    }

    /** Get the number of create modules */
    public int getNumberOfCreateModules() {
        return plugins.getNumberOfCreateModules();
    }

    /** Get the number of affect modules */
    public int getNumberOfAffectModules() {
        return plugins.getNumberOfAffectModules();
    }

    /** Return true if there are affect modules currently loaded */
    public boolean hasCurrentAffects() {
        if (numberOfCurrentAffects > 0) {
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
        if (creates[i].getLoaded()) {
            creates[i].reselect();
        } else {
            creates[i].setLoaded(true);
            creates[i].setup();
        }

    }

    /** Add an affect to the current affect array to be processed
     *  The affect is added at its index value so it can easily be removed later
     */
    public void addAffect(int i) {
        numberOfCurrentAffects++;
        currentAffects[i] = true;

        // Call that module
        if (affects[i].getLoaded()) {
            affects[i].reselect();
        } else {
            affects[i].setLoaded(true);
            affects[i].setup();

        }

    }

    /** Remove an affect from the current affect array
     *  The affect is removed at its index value
     */
    public void removeAffect(int i) {
        numberOfCurrentAffects--;
        currentAffects[i] = false;
        affects[i].deselect();
    }

    //////////////////////////////////////////////////////////////
    // WINDOW CONTROLS
    //////////////////////////////////////////////////////////////
    private void resizeWindow(ComponentEvent e) {
        // Get and set the new size of the window
        windowSize = e.getComponent().getSize();
        // Resize the UI and Canvas
        toolBar.resizeToolBar(windowSize);
        canvas.resizeCanvas(windowSize);
    }

    /**
     * Method allows changing whether this window is displayed in fullscreen or
     * windowed mode. 
     * 
     * @param fullscreen true = change to fullscreen, 
     *                   false = change to windowed
     */
    public void setFullscreen(boolean fullscreen) {
        if (this.fullscreen != fullscreen) { //are we actually changing modes.
            //change modes.
            this.fullscreen = fullscreen;

            // toggle fullscreen mode
            if (!fullscreen) { //change to windowed mode.

                //set the display mode back to the what it was when
                //the program was launched.
                //device.setDisplayMode(oldDispMode);

                //hide the frame so we can change it.
                //setVisible(false);

                //remove the frame from being displayable.
                dispose();

                //put the borders back on the frame.
                setUndecorated(false);

                //needed to unset this window as the fullscreen window.
                device.setFullScreenWindow(null);

                //make sure the size of the window is correct.
                setSize(oldWindowSize);

                //recenter window
                //setLocationRelativeTo(null);
                setLocation(oldLocation);

                //reset the display mode to what it was before 
                //we changed it.
                setVisible(true);

            } else { //change to fullscreen.
                // Save the old window size and location
                oldWindowSize = windowSize;
                oldLocation = getLocation();

                //hide everything
                //setVisible(false);

                //remove the frame from being displayable.
                dispose();

                //remove borders around the frame
                setUndecorated(true);

                //make the window fullscreen.
                device.setFullScreenWindow(this);

                //attempt to change the screen resolution.
                //device.setDisplayMode(displayMode);

                //show the frame
                setVisible(true);

            } // end if

            //make sure that the screen is refreshed.
            repaint();
        }
    }

    /**
     * This method returns true is this frame is in fullscreen. False if in 
     * windowed mode.
     * 
     * @return true = fullscreen, false = windowed.
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Used as a single function to save off information before exiting
     * and to keep all cleanup code in the same place.
     */
    public void onExit() {

        //immediately hide the window (no falling apart windows)
        setVisible(false);

        //cleanup and destroy the window threads
        dispose();

        //exit the application without an error
        System.exit(0);
    }

    //////////////////////////////////////////////////////////////
    // KEY EVENTS
    //////////////////////////////////////////////////////////////
    public void keyPressed(KeyEvent e) {
        // Pass the key event on to the current modules
        if (currentCreate >= 0) {
            creates[currentCreate].keyPressed(e);
        }

        // Pass the key event to the current affects
        if (hasCurrentAffects()) {
            for (int i = 0; i < currentAffects.length; i++) {
                if (currentAffects[i]) {
                    affects[i].keyPressed(e);
                }
            }
        }

    }

    public void keyTyped(KeyEvent e) {
        // Pass the key event on to the current modules
        if (currentCreate >= 0) {
            creates[currentCreate].keyTyped(e);
        }

        // Pass the key event to the current affects
        if (hasCurrentAffects()) {
            for (int i = 0; i < currentAffects.length; i++) {
                if (currentAffects[i]) {
                    affects[i].keyTyped(e);
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {

        int keyCode = e.getKeyCode();

        // Toggle Fullscreen
        if (keyCode == KeyEvent.VK_ESCAPE) {
            setFullscreen(!isFullscreen());
        }

        // GLOBAL KEYS - when the Modifier is down
        if (e.getModifiers() == MENU_SHORTCUT) {

            switch (keyCode) {
                // Clear the Canvas
                case BACKSPACE:
                case DELETE:
                    canvas.clear();
                    break;
            }

            int keyChar = e.getKeyChar();

            switch (keyChar) {
                // Save Pdf
                case 's':
                    canvas.startPdf();
                    break;
                case '.':
                    canvas.savePdfFrame();
                    break;
                case ',':
                    canvas.endPdf();
                    break;
            }


        }

        // Pass the key event on to the current modules
        if (currentCreate >= 0) {
            creates[currentCreate].keyReleased(e);
        }


        // Pass the key event to the current affects
        if (hasCurrentAffects()) {
            for (int i = 0; i < currentAffects.length; i++) {
                if (currentAffects[i]) {
                    affects[i].keyReleased(e);
                }
            }
        }

    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        resizeWindow(e);
    }
    /*
    public void windowStateChanged(WindowEvent e) {
    System.out.println("STATE CHANGED");
    //resizeWindow(e);
    }
     */
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
