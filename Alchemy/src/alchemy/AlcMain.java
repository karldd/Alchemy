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
//import com.apple.cocoa.application.NSMenu;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class AlcMain extends JFrame implements AlcConstants, ComponentListener, KeyListener {

    /**
     * Current PLATFORM in use, one of WINDOWS, MACOSX, LINUX or OTHER.
     */
    static public int PLATFORM;
    /** Modifier Key to show for tool tips */
    static public String MODIFIER_KEY;

    static {
        if (PLATFORM_NAME.indexOf("Mac") != -1) {
            PLATFORM = MACOSX;
            // Mac command key symbol
            MODIFIER_KEY = "\u2318";

        } else if (PLATFORM_NAME.indexOf("Windows") != -1) {
            PLATFORM = WINDOWS;
            MODIFIER_KEY = "Ctrl";

        } else if (PLATFORM_NAME.equals("Linux")) {  // true for the ibm vm
            PLATFORM = LINUX;
            MODIFIER_KEY = "Ctrl";

        } else {
            PLATFORM = OTHER;
            MODIFIER_KEY = "Modifier";
        }
    }
    //
    //////////////////////////////////////////////////////////////
    // ALCHEMY CLASSES AND MODULES
    //////////////////////////////////////////////////////////////
    /** Class to take care of plugin loading */
    private AlcPlugin plugins;
    /** Class of utility math functions */
    public AlcMath math = new AlcMath();
    /** Canvas to draw on to */
    public AlcCanvas canvas;
    /** User Interface Tool Bar */
    public AlcToolBar toolBar;
    /** Preferences class */
    public AlcPreferences prefs;
    /** Session class - controls automatic saving of the canvas */
    public AlcSession session;
    /** Lists of the installed modules */
    public AlcModule[] creates;
    public AlcModule[] affects;
    //
    //////////////////////////////////////////////////////////////
    // ALCHEMY STATUS
    //////////////////////////////////////////////////////////////
    /** The currently selected create module - set to -1 initially when nothing is selected */
    public int currentCreate = -1;
    /** The currently selected affect modules */
    public boolean[] currentAffects;
    /** The number of affect modules currently selected */
    private int numberOfCurrentAffects = 0;
    /** Preferred size of the window */
    private Dimension windowSize = new Dimension(800, 600);
    //
    //////////////////////////////////////////////////////////////
    // FULLSCREEN
    //////////////////////////////////////////////////////////////
    /** Toggle between windowed and fullscreen */
    protected boolean fullscreen = false;
    /** For storing the old display size before entering fullscreen */
    private Dimension oldWindowSize = null;
    /** For storing the old display location before entering fullscreen */
    private Point oldLocation = null;
    /** Toggle the state of the osx menu bar on a mac */
    private boolean macMenuBarVisible = true;

    public AlcMain() {

        // TODO - Sort out the build.xml - copy correctly etc...

        // LOAD PREFERENCES
        prefs = new AlcPreferences();

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

        // INITIALISE THE MODULES
        initialiseModules();


        // Exit Function
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exitAlchemy();
            }
        });

    }

    public static void main(String[] args) {
        if (PLATFORM == MACOSX) {
            //////////////////////////////////////////////////////////////
            // MAC ONLY PROPERTIES 
            // Called before the interface is built
            // This may needsto go somewhere else
            //////////////////////////////////////////////////////////////
            // Mac Java 1.3
            //System.setProperty("com.apple.macos.useScreenMenuBar", "true");
            //System.setProperty("com.apple.mrj.application.growbox.intrudes", "true");
            //System.setProperty("com.apple.hwaccel", "true"); // only needed for 1.3.1 on OS X 10.2
            //System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Yes Test");

            // Mac Java 1.4
            //System.setProperty("apple.laf.useScreenMenuBar", "true");
            //System.setProperty("apple.awt.showGrowBox", "true");

        }

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlcMain window = new AlcMain();
        window.setVisible(true);
    }

    private void exitAlchemy() {
        System.out.println("Closing");
        // Turn off recording if on
        if (session.isRecording()) {
            session.setRecording(false);
        }
        // Save changes to the preferences
        prefs.writeChanges();
        dispose();
        System.exit(0); //calling the method is a must
    }

    private void loadInterface() {

        // The canvas to draw on
        canvas = new AlcCanvas(this);
        // LOAD SESSION
        session = new AlcSession(this);

        // User Interface toolbar
        toolBar = new AlcToolBar(this);


        // LAYERED PANE
        JLayeredPane layeredPane = new JLayeredPane();
        // Add the UI on top of the canvas
        layeredPane.add(canvas, new Integer(1));
        // LOAD SESSION
        session = new AlcSession(this);
        layeredPane.add(toolBar, new Integer(2));

        // FRAME
        this.setContentPane(layeredPane);           // Set the layered pane as the main content pane
        this.setPreferredSize(windowSize);          // Set the window size
        this.addComponentListener(this);            // Add a component listener to detect window resizing
        //this.addWindowStateListener(this);          // Add a window state listener to detect window maximising
        this.addKeyListener(this);                  // Key Listener
        this.setFocusable(true);                    // Make the key listener focusable so we can get key events
        this.requestFocus();                        // Get focus for the key listener
        //this.setTitle("al.chemy");                // Title of the frame - Dock name should also be set -Xdock:name="Alchemy"
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();                                // Finalize window layout
        this.setLocationRelativeTo(null);           // Center window on screen.

    }

    private void initialiseModules() {
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

    // GLOBAL GETTER INFO
    /** Get the Window Size as a Dimension */
    public Dimension getWindowSize() {
        return windowSize;
    }

    /** Get the PLATFORM */
    public int getPlatform() {
        return PLATFORM;
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
     * Based on code from http://gpsnippets.blogspot.com/2007/08/toggle-fullscreen-mode.html
     * 
     * @param fullscreen true = change to fullscreen, 
     *                   false = change to windowed
     */
    public void setFullscreen(boolean fullscreen) {
        if (FULLSCREEN_SUPPORTED) {
            if (this.fullscreen != fullscreen) {        //are we actually changing modes.

                this.fullscreen = fullscreen;           //change modes.

                //change to windowed mode.
                if (!fullscreen) {

                    //System.out.println(System.getProperty("user.name"));

                    //setVisible(false);                //hide the frame so we can change it.
                    dispose();                          //remove the frame from being displayable.
                    setUndecorated(false);              //put the borders back on the frame.
                    //DEVICE.setFullScreenWindow(null);   //needed to unset this window as the fullscreen window.
                    setSize(oldWindowSize);             //make sure the size of the window is correct.
                    setLocation(oldLocation);           //reset location of the window
                    setAlwaysOnTop(false);

                    macMenuBarVisible = true;
                    setVisible(true);

                //change to fullscreen.
                } else {

                    oldWindowSize = windowSize;          //save the old window size and location
                    oldLocation = getLocation();

                    try {
                        setVisible(false);                  //hide everything
                        dispose();                          //remove the frame from being displayable.

                        setUndecorated(true);               //remove borders around the frame
                        setSize(DISPLAY_MODE.getWidth(), DISPLAY_MODE.getHeight());   // set the size to maximum
                        setLocation(0, 0);
                        setAlwaysOnTop(true);
                        //DEVICE.setFullScreenWindow(this);   //make the window fullscreen.
                        macMenuBarVisible = false;
                        setVisible(true);                   //show the frame

                    } catch (Exception e) {
                        System.err.println(e);
                    }
                }

                repaint();  //make sure that the screen is refreshed.
            }
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
     * Override the JFrame setVisible
     * This is a very hacky way of turning off the mac menubar. 
     * Unfortunately using setFullScreenWindow() on a mac causes very buggy behaviour 
     * ie. PopupMenus do not appear at all
     * 
     * @param visible true for visible, otherwise false
     */
    /*
    @Override
    public void setVisible(final boolean visible) {
        // Only 
        if (PLATFORM == MACOSX) {
            // Turn on
            if (macMenuBarVisible) {
                //call it when already not visible and it crashes, so check first
                if (!NSMenu.menuBarVisible()) {
                    NSMenu.setMenuBarVisible(true);
                }
            // Turn off
            } else {
                if (NSMenu.menuBarVisible()) {
                    NSMenu.setMenuBarVisible(false);
                }
            }
        }
        super.setVisible(visible);
    }
    */
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

        // Turn off fullscreen mode with the escape key if in fullscreen mode
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (isFullscreen()) {
                setFullscreen(false);
            }
        }

        // GLOBAL KEYS - when the Modifier is down
        if (e.getModifiers() == MENU_SHORTCUT) {

            switch (keyCode) {
                // Clear the Canvas
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    canvas.clear();
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
}
