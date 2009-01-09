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
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * AlcColourPicker
 * @author Karl D.D. Willis
 */
class AlcColourPicker extends JMenuItem implements MouseListener, AlcConstants {

    private BufferedImage colourArray;
    private AlcPopupButton parent;
    private BufferedImage[] screenShots;
    private GraphicsDevice[] devices;
    private int currentDevice = 0;
    private JDialog eyeDropperWindow;
    private javax.swing.Timer eyeDropperTimer;

    AlcColourPicker(AlcPopupButton parent) {

        this.parent = parent;
        this.setPreferredSize(new Dimension(100, 115));

        this.setOpaque(true);
        this.setBackground(Color.WHITE);

        Image colourPicker = AlcUtil.getImage("colour-picker.png");

        // Draw the colourPicker png into a buffered image so we can access the pixels
        colourArray = new BufferedImage(100, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = colourArray.createGraphics();
        g2.drawImage(colourPicker, 0, 0, 100, 120, null);
        g2.dispose();

        this.setCursor(CURSOR_CIRCLE);
        this.addMouseListener(this);
    }

    Color getColor(int x, int y) {
        return new Color(colourArray.getRGB(x, y));
    }

    /** Captures the screen and displays that image fullscreen
     *  When the user clicks on the image, the colour from that point  is loaded
     */
    private void startEyeDropper() {

        Alchemy.canvas.setAutoToggleToolBar(false);

        // Create a screenshot of each monitor
        devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        screenShots = new BufferedImage[devices.length];

        for (int i = 0; i < devices.length; i++) {
            try {

                GraphicsConfiguration gc = devices[i].getDefaultConfiguration();
                Rectangle screenBounds = gc.getBounds();
                Robot robot = new Robot(devices[i]);

                Rectangle newBounds = new Rectangle(0, 0, screenBounds.width, screenBounds.height);
                screenShots[i] = robot.createScreenCapture(newBounds);

            } catch (Exception ex) {
                System.err.println("Error creating a screenshot for the eye dropper");
                ex.printStackTrace();
            }
        }

        // Create a small window that follows the mouse and captures mouse events
        eyeDropperWindow = new JDialog(Alchemy.window, false);
        eyeDropperWindow.setUndecorated(true);
        eyeDropperWindow.setBounds(new Rectangle(12, 12));
        eyeDropperWindow.setCursor(CURSOR_EYEDROPPER);
        //Alchemy.canvas.setTempCursor(CURSOR_EYEDROPPER);
        //eyeDropperWindow.setAlwaysOnTop(true);

        // Get rid of the window shadow on Mac
        if (Alchemy.OS == OS_MAC) {
            eyeDropperWindow.getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
        }
        eyeDropperWindow.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                stopEyeDropper();
            }
        });
        eyeDropperTimer = new javax.swing.Timer(5, new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                updateEyeDropper();
            }
        });

        JPanel imagePanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {

                Point pos = this.getLocationOnScreen();
                Point offset = new Point(-pos.x, -pos.y);
                Rectangle bounds = devices[currentDevice].getDefaultConfiguration().getBounds();
                Point origin = bounds.getLocation();
                // Primary monitor
                if (origin.x == 0 && origin.y == 0) {
                    g.drawImage(screenShots[currentDevice], offset.x, offset.y, null);
                // Non-primary monitor
                } else {
                    g.drawImage(screenShots[currentDevice], offset.x + origin.x, offset.y + origin.y, null);
                }
            }
        };

        eyeDropperWindow.setContentPane(imagePanel);
        if (Alchemy.OS != OS_MAC) {
            eyeDropperWindow.setFocusableWindowState(false);
        }
        //eyeDropperWindow.toFront();
        eyeDropperTimer.start();
        eyeDropperWindow.setVisible(true);

    }

    /** Update the picker window */
    private void updateEyeDropper() {
        if (eyeDropperWindow != null) {
            PointerInfo info = MouseInfo.getPointerInfo();
            GraphicsDevice device = info.getDevice();
            Point mouseLoc = info.getLocation();
            eyeDropperWindow.setLocation(mouseLoc.x - 6, mouseLoc.y - 6);
            // Figure out which device to select the correct screenshot
            for (int i = 0; i < devices.length; i++) {
                if (devices[i].equals(device)) {
                    currentDevice = i;
                }
            }
            eyeDropperWindow.repaint();
        }

    }

    /** Dispose of the screens created for the eye dropper */
    private void stopEyeDropper() {
        if (eyeDropperWindow != null) {
            try {

                PointerInfo info = MouseInfo.getPointerInfo();
                Point mouseLoc = info.getLocation();

                Rectangle bounds = devices[currentDevice].getDefaultConfiguration().getBounds();
                Point origin = bounds.getLocation();
                Color c;
                // Primary monitor
                if (origin.x == 0 && origin.y == 0) {
                    // Offset to pick out the bottom left corner
                    c = new Color(screenShots[currentDevice].getRGB(mouseLoc.x - 6, mouseLoc.y + 6));
                // Non-primary monitor
                } else {
                    c = new Color(screenShots[currentDevice].getRGB(mouseLoc.x - origin.x - 6, mouseLoc.y - origin.y + 6));
                }

                Alchemy.canvas.setColour(c);
                Alchemy.toolBar.refreshColourButton();

                if (eyeDropperTimer.isRunning()) {
                    eyeDropperTimer.stop();
                }
                eyeDropperWindow.setVisible(false);
                eyeDropperWindow.dispose();
                screenShots = null;
                devices = null;


                //if (Alchemy.OS == MACOSX) {
                Alchemy.canvas.restoreCursor();
                //setCursor(CURSOR_ARROW);
                //}

                Alchemy.canvas.setAutoToggleToolBar(true);

            } catch (Exception ex) {
                System.err.println("Error selecting colour from the eye dropper");
                ex.printStackTrace();
            }
        }
    }

    /** Start and show the colour selector */
    private void startColourSelector() {
        // Action to change the colour
        ActionListener colorAction = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                Alchemy.canvas.setColour(Alchemy.colourSelector.getColour());
                Alchemy.toolBar.refreshColourButton();
            }
        };

        Alchemy.colourSelector.show(colorAction, null, Alchemy.canvas.getColour());
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.setOpaque(true);
        Rectangle size = this.getBounds();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.width, size.height);
        g.drawImage(colourArray, 0, 0, null);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // Launch Colour Picker
        if (x >= 60 && x < 80 && y <= 20) {

            if (!Alchemy.preferences.paletteAttached) {
                Alchemy.toolBar.setToolBarVisible(false);
            }

            // Allow some time for the dozy screen grabbing robot to create a shot
            // WITHOUT the toolbar and colour picker onscreen
            javax.swing.Timer initialDelay = new javax.swing.Timer(50, new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    startEyeDropper();
                }
            });

            initialDelay.setRepeats(false);
            initialDelay.start();


        // Launch Colour Selector
        } else if (e.getX() >= 80 && e.getY() <= 20) {

            startColourSelector();

        } else {

            Alchemy.canvas.setColour(this.getColor(e.getX(), e.getY()));
            parent.hidePopup();
            if (Alchemy.OS == OS_MAC) {
                Alchemy.canvas.restoreCursor();
                //Alchemy.canvas.setCursor(CURSOR_CROSS);
                setCursor(CURSOR_ARROW);
            }
            Alchemy.toolBar.refreshColourButton();
        }
    }

    public void mouseEntered(MouseEvent e) {
        // OSX does not seem to obey the set cursor so set the other cursors
        if (Alchemy.OS == OS_MAC) {
            Alchemy.canvas.setTempCursor(CURSOR_CIRCLE);
            Alchemy.toolBar.setCursor(CURSOR_CIRCLE);
        }
    }

    public void mouseExited(MouseEvent e) {
        if (Alchemy.OS == OS_MAC) {
            Alchemy.canvas.restoreCursor();
            //Alchemy.canvas.setCursor(CURSOR_CROSS);
            Alchemy.toolBar.setCursor(CURSOR_ARROW);
        }
    }
}
