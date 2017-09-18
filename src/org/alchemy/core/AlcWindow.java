/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2010 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.alchemy.core;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

/**
 * AlcWindow
 * @author Karl D.D. Willis
 */
public class AlcWindow extends JFrame implements AlcConstants, WindowStateListener, WindowListener, ComponentListener, KeyListener, ClipboardOwner {

    //////////////////////////////////////////////////////////////
    // FULLSCREEN
    //////////////////////////////////////////////////////////////
    /** Toggle between windowed and fullscreen */
    private boolean fullscreen = false;
    /** For storing the old display size & location before entering fullscreen */
    private Rectangle oldBounds = null;
    /** Flag indicating if the window has been disposed or not */
    private boolean windowDisposed = false;
    /** Finish switching into transparent fullscreen */
    private boolean finishTransparentFullscreen = false;
    /** Device to use for finishing transparent fullscreen */
    private GraphicsDevice finishDevice = null;
    /** Bounds to use for finishing transparent fullscreen */
    private Rectangle finishBounds = null;

    //////////////////////////////////////////////////////////////
    // WINDOW
    //////////////////////////////////////////////////////////////    
    /** Preferred size of the window */
    private static Dimension windowSize = null;
    /** Minimum Size for the window */
    private static final Dimension minWindowSize = new Dimension(640, 400);
    /** Second monitor black out window */
//    private static JWindow[] screens;

    /** The Alchemy window <br>
     *  Handles things like fullscreen, setup of the canvas/toolbar, exiting etc...
     */
    AlcWindow() {

        // Set up our application to respond to the Mac OS X application menu
        super("OSXAdapter");
        registerForMacOSXEvents();

        // Exit Function
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exitAlchemy();
            }
        });

        this.addComponentListener(this);        // Add a component listener to detect window resizing
        this.addWindowStateListener(this);    // Add a window state listener to detect window maximising
        this.addWindowListener(this);
        this.addKeyListener(this);              // Key Listener
        this.setFocusable(true);                // Make the key listener focusable so we can get key events
        this.setTitle("Alchemy");               // Title of the frame - Dock name should also be set -Xdock:name="Alchemy"
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Let the exitAlchemy function take care of closing
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //This enables TAB to be used to switch between toolbar and swatchtoolbar
        this.setFocusTraversalKeysEnabled(false);
        
        
        // Find out how big the parent screen is
        GraphicsConfiguration grapConfig = this.getGraphicsConfiguration();
        Dimension currentWindowSize = grapConfig.getBounds().getSize();

        boolean windowSet = false;
        // If there is a saved window size then us it
        if (Alchemy.preferences.canvasSize != null) {
            Dimension savedWindowSize = new Dimension(Alchemy.preferences.canvasSize.width, Alchemy.preferences.canvasSize.height);

            // Make sure the window is not too big or too small
            if (savedWindowSize.width <= currentWindowSize.width && savedWindowSize.height <= currentWindowSize.height && savedWindowSize.width >= minWindowSize.width && savedWindowSize.height >= minWindowSize.width) {
                windowSize = savedWindowSize;
                windowSet = true;
            }
        }

        // If the window has still not been set then give it a standard size
        if (!windowSet) {
            if (currentWindowSize.width < 1000) {
                windowSize = new Dimension(800, 500);
            } else {
                windowSize = new Dimension(1024, 640);
            }
        }
    }

    /** Called once the interface is ready to be loaded into the window */
    void setupWindow() {

        if (Alchemy.OS == OS_MAC) {
            // Add normally if on MacOSX as the menu is listed above
            this.setJMenuBar(Alchemy.menuBar);

        } else {
            // Otherwise add it to the toolbar area
            //if (!Alchemy.preferences.simpleToolBar) {
            //Palette
            if (Alchemy.preferences.paletteAttached || Alchemy.preferences.simpleToolBar) {
//                this.setJMenuBar(Alchemy.menuBar);
            //Toolbar
            } else {
                Alchemy.toolBar.add("North", Alchemy.menuBar);
                Alchemy.toolBar.calculateTotalHeight();
            }
        //}
        }

        // LAYERED PANE
        JLayeredPane layeredPane = new JLayeredPane();
        // Add the UI on top of the canvas
        layeredPane.add(Alchemy.canvas, new Integer(1));
        layeredPane.add(Alchemy.toolBar, new Integer(2));

        // FRAME
        layeredPane.setPreferredSize(windowSize);
        // Set the layered pane as the main content pane
        this.setContentPane(layeredPane);

        // Finalize window layout
        this.pack();
        if (Alchemy.OS != OS_MAC) {
            setFrameIconImage();
        }

        // Load the old location if available
        // First check it is not off screen
        if (Alchemy.preferences.canvasLocation != null) {
            // Check if the canvas window is onscreen
            boolean onscreen = checkOnscreen(Alchemy.preferences.canvasLocation);
            if (onscreen) {
                this.setLocation(Alchemy.preferences.canvasLocation);
            } else {
                this.setLocationRelativeTo(null);
            }
        } else {
            this.setLocationRelativeTo(null);           // Center window on screen.

        }
        // Load the palette after the main window
        // as long as we are not in simple mode
        if (!Alchemy.preferences.simpleToolBar && Alchemy.preferences.paletteAttached) {
            setPalette(true);
        } else {
            this.requestFocus();
        }
    }

    //////////////////////////////////////////////////////////////
    // WINDOW
    //////////////////////////////////////////////////////////////
    /** Get the Window Size as a Dimension */
    Dimension getWindowSize() {
        return windowSize;
    }

    private void resizeWindow() {
        // Make sure the minimum size is observed
        this.setSize(Math.max(minWindowSize.width, this.getWidth()), Math.max(minWindowSize.height, this.getHeight()));
        // Get and set the new size of the window
        windowSize = this.getSize();
        // Resize the UI and Canvas
        Alchemy.toolBar.resizeToolBar(windowSize);
        Alchemy.canvas.resizeCanvas(windowSize);
    }

    /**
     * Method allows changing whether this window is displayed in fullscreen or
     * windowed mode. 
     * Based on code from http://gpsnippets.blogspot.com/2007/08/toggle-fullscreen-mode.html
     * 
     * @param fullscreen true = change to fullscreen, 
     *                   false = change to windowed
     */
    void setFullscreen(boolean fullscreen) {

        //GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        // The current monitor where the main window is located
        GraphicsConfiguration grapConfig = this.getGraphicsConfiguration();
        Rectangle bounds = grapConfig.getBounds();
        int currentDevice = 0;

        //are we actually changing modes.
        if (this.fullscreen != fullscreen) {

            //change modes.
            this.fullscreen = fullscreen;

            // NORMAL WINDOW
            if (!fullscreen) {

//                // Remove the other windows if present
//                if (screens != null) {
//                    //System.out.println("Remove Called");
//                    for (int i = 0; i < screens.length; i++) {
//                        screens[i].setVisible(false);
//                        screens[i].dispose();
//                    }
//                    screens = null;
//                }

                if (Alchemy.preferences.transparentFullscreen) {
                    Alchemy.canvas.setTransparentImage(null);
                    Alchemy.canvas.redraw();
                }

                //hide the frame so we can change it.
                this.setVisible(false);
                //remove the frame from being displayable.
                this.dispose();
                //put the borders back on the frame.
                this.setUndecorated(false);

                this.setFocusable(true);
                //needed to unset this window as the fullscreen window.
                //device.setFullScreenWindow(null); 

                this.setBounds(oldBounds);
                //setAlwaysOnTop(false);

                // Turn off the menubar if this is OSX and it is the primary monitor
                if (Alchemy.OS == OS_MAC && bounds.x == 0 && bounds.y == 0) {
                    AlcNative.setMenubarVisible(true);
                }

                this.setVisible(true);
                this.toFront();

            // FULLSCREEN
            } else {

                oldBounds = this.getBounds();

                try {
                    // If there are multiple monitors present
                    if (devices.length > 1) {
                        // As many screens as devices minus one for the primary monitor
//                        screens = new JWindow[devices.length - 1];
//                        int index = 0;
                        for (int i = 0; i < devices.length; i++) {
                            Rectangle screenBounds = devices[i].getDefaultConfiguration().getBounds();
                            // If not the current monitor make a new black window for each
                            if (!screenBounds.equals(bounds)) {
                                //System.out.println(i + " - " + screenBounds);
//                                screens[index] = new JWindow(this);
//                                screens[index].setBounds(screenBounds);
//                                // Set the window background to black
//                                JPanel blackBackground = new JPanel();
//                                blackBackground.setOpaque(true);
//                                blackBackground.setBackground(Color.BLACK);
//                                screens[index].setContentPane(blackBackground);
//                                screens[index].setFocusable(false);
//                                screens[index].setVisible(true);
//                                index++;
                            } else {
                                currentDevice = i;
                            }
                        }
                    }


                    // If on a mac and this is the primary monitor
                    // Make room for the mac menubar
//                    if (Alchemy.OS == MACOSX) {
//                        if (bounds.x == 0) {
//                            bounds.setLocation(bounds.x, 22);
//                        }
//                    }

                    //hide everything
                    this.setVisible(false);
                    //remove the frame from being displayable.
                    this.dispose();

                    // Turn off the menubar if on OSX and this is the primary monitor
                    if (Alchemy.OS == OS_MAC && bounds.x == 0 && bounds.y == 0) {
                        AlcNative.setMenubarVisible(false);
                    }

                    // If the current monitor and transparency is on
                    if (Alchemy.preferences.transparentFullscreen) {

                        // If the window has not been properly disposed
                        // Save the state and schedule it to be called
                        // when the window is officially closed
                        if(!windowDisposed){
                            finishDevice = devices[currentDevice];
                            finishBounds = bounds;
                            finishTransparentFullscreen = true;
                            return;
                        }
                           
                        captureTransparentScreen(devices[currentDevice], bounds);

                    }

                    //remove borders around the frame
                    this.setUndecorated(true);

                    this.setBounds(bounds);
                    //setAlwaysOnTop(true);
                    //device.setFullScreenWindow(this);   //make the window fullscreen.

                    this.setVisible(true);                   //show the frame

                    if (Alchemy.preferences.paletteAttached) {
                        Alchemy.palette.toFront();
                    }
                    this.toFront();

                } catch (Exception e) {
                    System.err.println("Error Entering Fullscreen");
                    e.printStackTrace();
                }
            }

            this.repaint();  //make sure that the screen is refreshed.

        }
    }

    /**
     * This method returns true is this frame is in fullscreen. False if in 
     * windowed mode.
     * 
     * @return true = fullscreen, false = windowed.
     */
    boolean isFullscreen() {
        return fullscreen;
    }

    /** Returns if the window is currently in transparent mode
     * 
     * @return  Transparent mode or not
     */
    boolean isTransparent() {
        return Alchemy.preferences.transparentFullscreen;
    }

    /** Set the transparent mode of the window for fullscreen
     * 
     * @param transparent   True to enter transparent mode for fullscreen, false to exit
     */
    void setTransparent(boolean transparent) {
        Alchemy.preferences.transparentFullscreen = transparent;
    }

    /** Finish switching into transparent fullscreen mode */
    private void finishTransparentFullscreen() {
        captureTransparentScreen(finishDevice, finishBounds);
        this.setUndecorated(true);
        this.setBounds(finishBounds);

        this.setVisible(true);
        if (Alchemy.preferences.paletteAttached) {
            Alchemy.palette.toFront();
        }
        this.toFront();
        this.repaint();
        finishTransparentFullscreen = false;
    }

    /** Capture the image for the transparent screen using the robot */
    private void captureTransparentScreen(GraphicsDevice device, Rectangle bounds) {
        try {
            Robot robot = null;
            if (device != null) {
                robot = new Robot(device);
            } else {
                robot = new Robot();
            }
            // Reset the location to zero
            Rectangle newBounds = new Rectangle(0, 0, bounds.width, bounds.height);
            Image screenCapture = null;
            try {
                screenCapture = robot.createScreenCapture(newBounds);
            } catch (IllegalArgumentException ex) {
                // If an error is thrown then use the old bounds
                screenCapture = robot.createScreenCapture(bounds);
                ex.printStackTrace();
            }
            Alchemy.canvas.setTransparentImage(screenCapture);
            Alchemy.canvas.updateCanvasImage(true);
            Alchemy.canvas.repaint();
        } catch (Exception e) {
            System.err.println("Error Entering Fullscreen");
            e.printStackTrace();
        }

    }

    /** Check if a point is on screen
     *  Used to make sure the window is not in dead space (from a detached monitor)
     * 
     * @param location  The location to check
     * @return          True if the point is onscreen otherwise false
     */
    private boolean checkOnscreen(Point location) {
        boolean onscreen = false;
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        for (int i = 0; i < devices.length; i++) {
            Rectangle screenBounds = devices[i].getDefaultConfiguration().getBounds();
            if (screenBounds.contains(location)) {
                onscreen = true;

            }
        }
        return onscreen;
    }

    /** This method sets the icon image of the frame according to the best imageIcon size requirements for the system's appearance settings. 
     *  This method should only be called after pack() or show() has been called for the Frame. 
     */
    private void setFrameIconImage() {
        java.awt.Insets insets = this.getInsets();
        int titleBarHeight = insets.top;
        if (titleBarHeight == 32) {
            //It's 'Windows Classic Style with Large Fonts', so use a 26 x 26 image  
            Image titleIcon26 = AlcUtil.getImage("alchemy-logo26.png");
            if (titleIcon26 != null) {
                this.setIconImage(titleIcon26);
            }
        } else {
            // Use the default 20 x 20 image - Looks fine on all other Windows Styles & Font Sizes 
            // (except 'Windows Classic Style with Extra Large Fonts' where image is slightly distorted. 
            // Have to live with that as cannot differentiate between 'Windows XP Style with Normal Fonts' appearance 
            // and 'Windows Classic Style with Extra Large Fonts' appearance as they both have the same insets values)  
            Image titleIcon20 = AlcUtil.getImage("alchemy-logo20.png");
            if (titleIcon20 != null) {
                this.setIconImage(titleIcon20);
            }
        }
    }

//
//    /** Reflection to call a Java 6_10 class that sets the window transparency */
//    private void setAlpha(float alpha) {
//        try {
//            //com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.0f);
//            Class awtutil = Class.forName("com.sun.awt.AWTUtilities");
//            Method setWindowOpaque = awtutil.getMethod("setWindowOpacity", Window.class, float.class);
//            setWindowOpaque.invoke(null, this,alpha);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//    
    //////////////////////////////////////////////////////////////
    // PALETTE
    //////////////////////////////////////////////////////////////
    /** Set the toolbar into a floating palette or on the main window */
    void setPalette(boolean seperate) {
        // PALETTE
        if (seperate) {
            // If this is not being called at startup
            if (!Alchemy.preferences.paletteAttached) {
                Alchemy.toolBar.detachToolBar();
            }

            // Make sure the palette will not be offscreen
            if (Alchemy.preferences.paletteLocation != null) {
                // Check that the palette is onscreen
                boolean onscreen = checkOnscreen(Alchemy.preferences.paletteLocation);
                if (onscreen) {
                    Alchemy.palette.setLocation(Alchemy.preferences.paletteLocation);
                } else {
                    Alchemy.palette.setLocation(100, 100);
                }
            } else {
                Alchemy.palette.setLocation(100, 100);
            }

            Alchemy.toolBar.addPaletteContent();
            //palette.pack();
            //palette.setVisible(true);

            Alchemy.preferences.paletteAttached = true;

            if (Alchemy.OS != OS_MAC) {
                this.setJMenuBar(Alchemy.menuBar);
                Alchemy.toolBar.calculateTotalHeight();
            }
            Alchemy.toolBar.toggleDetachButton(false);

            Alchemy.palette.setVisible(true);
            //palette.show();
            Alchemy.palette.toFront();
            Alchemy.palette.requestFocus();


        // TOOLBAR
        } else {
            if (Alchemy.palette != null) {

                Alchemy.preferences.paletteLocation = Alchemy.palette.getLocation();
                Alchemy.palette.setVisible(false);
                Alchemy.preferences.paletteAttached = false;
                Alchemy.canvas.setAutoToggleToolBar(true);
                Alchemy.toolBar.attachToolBar();
            }
        }
    }

    //////////////////////////////////////////////////////////////
    // EXIT HANDLERS
    //////////////////////////////////////////////////////////////
    /** Test if it is ok to exit Alchemy
     * 
     * @return Passed to the Mac quit handler - do or don't quit
     */
    boolean exitAlchemy() {
        // Ask to quit
        if (Alchemy.canvas.shapes.size() > 0) {

            boolean result = AlcUtil.showConfirmDialogFromBundle("exitDialogTitle", "exitDialogMessage", "quitDialogTitle", "quitDialogMessage");
            if (result) {
                exit();
                return true;
            } else {
                return false;
            }

        } else {
            exit();
            return true;
        }
    }

    /** Exit Alchemy, saving preferences and doing general clean up */
    private void exit() {

        // Don't need to save the window location when in simple mode

        // Save the window location if not in full screen mode
        if (!isFullscreen()) {
            Alchemy.preferences.canvasLocation = this.getLocation();

            // Get the size of the canvas without the titlebar and insets
            Rectangle visibleRect = Alchemy.canvas.getVisibleRect();

            if (Alchemy.preferences.simpleToolBar) {
                visibleRect.width += Alchemy.toolBar.toolBarWidth;
            }
            
            // Set the canvas size
            Alchemy.preferences.canvasSize = new Dimension(visibleRect.width, visibleRect.height);
        }

        if (!Alchemy.preferences.simpleToolBar && Alchemy.preferences.paletteAttached) {
            Alchemy.preferences.paletteLocation = Alchemy.palette.getLocation();
        }

        // Turn off recording if on
        if (Alchemy.session.isRecording()) {
            Alchemy.session.setRecording(false);
        }
        // Save changes to the preferences
        Alchemy.preferences.writeChanges();

        // TODO - Write this out as a file?

        this.dispose();
        System.exit(0);
    }

    //////////////////////////////////////////////////////////////
    // MAC SPECIFIC
    // For the moment this code is all grouped together here
    //////////////////////////////////////////////////////////////
    /**
     * Generic registration with the Mac OS X application menu
     * Checks the platform, then attempts to register with the Apple EAWT
     * See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
     */
    private void registerForMacOSXEvents() {
        if (Alchemy.OS == OS_MAC) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                //OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[]) null));
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exitAlchemy", (Class[]) null));
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("showAbout", (Class[]) null));
                OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("showPreferences", (Class[]) null));
            //OSXAdapter.setFileHandler(this, getClass().getDeclaredMethod("loadFile", new Class[]{String.class}));
            } catch (Exception e) {
                System.err.println("Error while loading the OSXAdapter:");
                e.printStackTrace();
            }
        }
    }

    /**
     * General info dialog; fed to the OSXAdapter as the method to call when 
     * "About OSXAdapter" is selected from the application menu
     */
    void showAbout() {
        new AlcAbout(Alchemy.window);
    }

    /** 
     * General preferences dialog; fed to the OSXAdapter as the method to call when
     * "Preferences..." is selected from the application menu
     */
    void showPreferences() {
        Alchemy.preferences.showWindow();
    }

    //////////////////////////////////////////////////////////////
    // KEY EVENTS
    //////////////////////////////////////////////////////////////
    public void keyPressed(KeyEvent event) {
        
        // This kills mouse zooming on any keypress
        Alchemy.canvas.stopZoomMousing();

        int keyCode = event.getKeyCode();

        // Turn off fullscreen mode with just the escape key if in fullscreen mode
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (isFullscreen()) {
                setFullscreen(false);
            }
        }

        if (Alchemy.plugins.currentCreate >= 0) {
            Alchemy.plugins.creates[Alchemy.plugins.currentCreate].keyPressed(event);
        }
        if (Alchemy.plugins.hasCurrentAffects()) {
            for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                if (Alchemy.plugins.currentAffects[i]) {
                    Alchemy.plugins.affects[i].keyPressed(event);
                }
            }
        }
    }

    public void keyTyped(KeyEvent event) {
        if (Alchemy.plugins.currentCreate >= 0) {
            Alchemy.plugins.creates[Alchemy.plugins.currentCreate].keyTyped(event);
        }
        if (Alchemy.plugins.hasCurrentAffects()) {
            for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                if (Alchemy.plugins.currentAffects[i]) {
                    Alchemy.plugins.affects[i].keyTyped(event);
                }
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        if (Alchemy.plugins.currentCreate >= 0) {
            Alchemy.plugins.creates[Alchemy.plugins.currentCreate].keyReleased(event);
        }
        if (Alchemy.plugins.hasCurrentAffects()) {
            for (int i = 0; i < Alchemy.plugins.currentAffects.length; i++) {
                if (Alchemy.plugins.currentAffects[i]) {
                    Alchemy.plugins.affects[i].keyReleased(event);
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
        resizeWindow();
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
        windowDisposed = true;
        if(finishTransparentFullscreen){
            finishTransparentFullscreen();
        }
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
        windowDisposed = false;
    }

    public void windowDeactivated(WindowEvent e) {
    }
    public void windowStateChanged(WindowEvent e) {
    }
}