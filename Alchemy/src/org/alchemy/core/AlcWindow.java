/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2008 Karl D.D. Willis
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
class AlcWindow extends JFrame implements AlcConstants, ComponentListener, KeyListener, ClipboardOwner {

    //////////////////////////////////////////////////////////////
    // FULLSCREEN
    //////////////////////////////////////////////////////////////
    /** Toggle between windowed and fullscreen */
    private boolean fullscreen = false;
    /** For storing the old display size & location before entering fullscreen */
    private Rectangle oldBounds = null;
    //////////////////////////////////////////////////////////////
    // WINDOW
    //////////////////////////////////////////////////////////////    
    /** Preferred size of the window */
    private static Dimension windowSize = null;
    /** Minimum Size for the window */
    private static final Dimension minWindowSize = new Dimension(640, 400);
    /** Second monitor black out window */
    private static JWindow[] screens;

    public AlcWindow() {

        super("OSXAdapter");

        // Set up our application to respond to the Mac OS X application menu
        registerForMacOSXEvents();


        // Exit Function
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                exitAlchemy();
            }
        });

        this.addComponentListener(this);        // Add a component listener to detect window resizing
        //this.addWindowStateListener(this);    // Add a window state listener to detect window maximising

        this.addKeyListener(this);              // Key Listener

        this.setFocusable(true);                // Make the key listener focusable so we can get key events

        this.setTitle("Alchemy");               // Title of the frame - Dock name should also be set -Xdock:name="Alchemy"

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Let the exitAlchemy function take care of closing
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Image titleBarIcon = AlcUtil.getImage("titlebar.png");
        if (titleBarIcon != null) {
            this.setIconImage(titleBarIcon);
        }


        // Find out how big the parent screen is
        GraphicsConfiguration grapConfig = this.getGraphicsConfiguration();
        Dimension currentWindowSize = grapConfig.getBounds().getSize();

        boolean windowSet = false;
        // If there is a saved window size then us it
        if (Alchemy.preferences.canvasSize != null) {
            Dimension savedWindowSize = Alchemy.preferences.canvasSize;

            // Make sure the window is not too big
            if (savedWindowSize.width <= currentWindowSize.width && savedWindowSize.height <= currentWindowSize.height) {
                windowSize = savedWindowSize;
                windowSet = true;
            }
        }

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

        if (Alchemy.PLATFORM == MACOSX) {
            // Add normally if on MacOSX as the menu is listed above
            this.setJMenuBar(Alchemy.menuBar);

        } else {
            // Otherwise add it to the toolbar area
            //if (!Alchemy.preferences.simpleToolBar) {
            //Palette
            if (Alchemy.preferences.paletteAttached || Alchemy.preferences.simpleToolBar) {
                this.setJMenuBar(Alchemy.menuBar);
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
        //this.setSize(windowSize);
        layeredPane.setPreferredSize(windowSize);          // Set the window size

        this.setContentPane(layeredPane);           // Set the layered pane as the main content pane

        this.pack();                                // Finalize window layout

        // Load the old location if available
        // First check it is not off screen
        if (Alchemy.preferences.canvasLocation != null) {
            boolean onscreen = false;
            GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            for (int i = 0; i < devices.length; i++) {
                Rectangle screenBounds = devices[i].getDefaultConfiguration().getBounds();
                if (screenBounds.contains(Alchemy.preferences.canvasLocation)) {
                    onscreen = true;
                //System.out.println("CONTAINED within: " + screenBounds);
                }
            }
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
    public Dimension getWindowSize() {
        return windowSize;
    }

    private void resizeWindow(ComponentEvent e) {
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
    public void setFullscreen(boolean fullscreen) {

        //GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        // The current monitor where the main window is located
        GraphicsConfiguration grapConfig = this.getGraphicsConfiguration();
        Rectangle bounds = grapConfig.getBounds();

        if (this.fullscreen != fullscreen) {        //are we actually changing modes.

            this.fullscreen = fullscreen;           //change modes.

            // NORMAL WINDOW
            if (!fullscreen) {

                // Remove the other windows if present
                if (screens != null) {
                    //System.out.println("Remove Called");
                    for (int i = 0; i < screens.length; i++) {
                        screens[i].setVisible(false);
                        screens[i].dispose();
                    }
                    screens = null;
                }

                this.setVisible(false);                //hide the frame so we can change it.

                this.dispose();                          //remove the frame from being displayable.

                this.setUndecorated(false);              //put the borders back on the frame.

                this.setFocusable(true);
                //device.setFullScreenWindow(null);   //needed to unset this window as the fullscreen window.

                this.setBounds(oldBounds);

                //setAlwaysOnTop(false);

                // Turn on the menubar
                if (Alchemy.PLATFORM == MACOSX) {
                    new fullscreen.NativeOSX().setVisible(true);
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
                        screens = new JWindow[devices.length - 1];
                        int index = 0;
                        for (int i = 0; i < devices.length; i++) {
                            Rectangle screenBounds = devices[i].getDefaultConfiguration().getBounds();
                            //System.out.println(i + " - " + screenBounds);
                            // If not the current monitor
                            // make a new full size window for each
                            if (!screenBounds.equals(bounds)) {
                                screens[index] = new JWindow(this);
                                screens[index].setBounds(screenBounds);
                                // Set the window background to black
                                JPanel blackBackground = new JPanel();
                                blackBackground.setOpaque(true);
                                blackBackground.setBackground(Color.BLACK);
                                screens[index].setContentPane(blackBackground);
                                screens[index].setFocusable(false);
                                screens[index].setVisible(true);
                                index++;
                            }
                        }
                    }

                    // If on a mac and this is the primary monitor
                    // Make room for the mac menubar
//                    if (Alchemy.PLATFORM == MACOSX) {
//                        if (bounds.x == 0) {
//                            bounds.setLocation(bounds.x, 22);
//                        }
//                    }

                    this.setVisible(false);                  //hide everything

                    this.dispose();                          //remove the frame from being displayable.

                    this.setUndecorated(true);               //remove borders around the frame

                    this.setBounds(bounds);
                    //setAlwaysOnTop(true);
                    //device.setFullScreenWindow(this);   //make the window fullscreen.

                    // Turn off the menubar
                    if (Alchemy.PLATFORM == MACOSX) {
                        new fullscreen.NativeOSX().setVisible(false);
                    }

                    this.setVisible(true);                   //show the frame

                    if (Alchemy.preferences.paletteAttached) {
                        Alchemy.palette.toFront();
                    }
                    this.toFront();

                } catch (Exception e) {
                    System.err.println(e);
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
    public boolean isFullscreen() {
        return fullscreen;
    }

    //////////////////////////////////////////////////////////////
    // PALETTE
    //////////////////////////////////////////////////////////////
    /** Set the toolbar into a floating palette or on the main window */
    public void setPalette(boolean seperate) {
        // PALETTE
        if (seperate) {
            // If this is not being called at startup
            if (!Alchemy.preferences.paletteAttached) {
                Alchemy.toolBar.detachToolBar();
            }

            // Make sure the palette will not be offscreen
            if (Alchemy.preferences.paletteLocation != null) {
                boolean onscreen = false;
                GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
                for (int i = 0; i < devices.length; i++) {
                    Rectangle screenBounds = devices[i].getDefaultConfiguration().getBounds();
                    if (screenBounds.contains(Alchemy.preferences.paletteLocation)) {
                        onscreen = true;
                    //System.out.println("CONTAINED within: " + screenBounds);
                    }
                }
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

            if (Alchemy.PLATFORM != MACOSX) {
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
                //palette.dispose();
                //palette = null;
                Alchemy.toolBar.attachToolBar();
                Alchemy.preferences.paletteAttached = false;

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
    public boolean exitAlchemy() {
        // Ask to quit
        if (Alchemy.canvas.shapes.size() > 0) {

            boolean result = AlcUtil.showConfirmDialog("exitDialogTitle", "exitDialogMessage", "quitDialogTitle", "quitDialogMessage", Alchemy.bundle);
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
    public void registerForMacOSXEvents() {
        if (Alchemy.PLATFORM == MACOSX) {
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
    public void showAbout() {
        new AlcAbout(Alchemy.window);
    }

    /** 
     * General preferences dialog; fed to the OSXAdapter as the method to call when
     * "Preferences..." is selected from the application menu
     */
    public void showPreferences() {
        Alchemy.preferences.showWindow();
    }

    /** 
     * General load file handler; fed to the OSXAdapter as the method to call when a file is dragged into the dock icon
     */
//    public void loadFile(String path) {
//        try {
//            currentImage = ImageIO.read(new File(path));
//            imageLabel.setIcon(new ImageIcon(currentImage));
//            imageLabel.setBackground((Color) colors[colorComboBox.getSelectedIndex()]);
//            imageLabel.setText("");
//        } catch (IOException ioe) {
//            System.out.println("Could not load image " + path);
//        }
//        repaint();
//    }
    //////////////////////////////////////////////////////////////
    // KEY EVENTS
    //////////////////////////////////////////////////////////////
    public void keyPressed(KeyEvent event) {

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
        resizeWindow(e);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
