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
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 * AlcColourPicker
 * @author Karl D.D. Willis
 */
class AlcColourPicker extends JMenuItem implements MouseListener, AlcConstants {

    private BufferedImage colourArray;
    private AlcPopupButton parent;
    private JFrame[] screens;

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
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        // As many screens as devices
        screens = new JFrame[devices.length];

        for (int i = 0; i < devices.length; i++) {
            try {

                GraphicsConfiguration gc = devices[i].getDefaultConfiguration();
                Rectangle screenBounds = gc.getBounds();
                Robot robot = robot = new Robot(devices[i]);

                Rectangle newBounds = new Rectangle(0, 0, screenBounds.width, screenBounds.height);
                final BufferedImage screenCapture = robot.createScreenCapture(newBounds);

                screens[i] = new JFrame(gc);
                screens[i].setUndecorated(true);
                // Get rid of the window shadow on Mac
                if (Alchemy.PLATFORM == MACOSX) {
                    screens[i].getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
                }
                screens[i].setBounds(screenBounds);
                screens[i].setCursor(CURSOR_EYEDROPPER);
                JPanel imagePanel = new JPanel() {

                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.drawImage(screenCapture, 0, 0, null);
                    }
                };

                screens[i].setContentPane(imagePanel);
                final JFrame screen = screens[i];
                screens[i].addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        Color colour = new Color(screenCapture.getRGB(e.getX(), e.getY()));
                        System.out.println(colour);
                        Alchemy.canvas.setColour(colour);
                        Alchemy.toolBar.refreshColourButton();
                        stopEyeDropper();
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        // So that the cursor is constantly displayed
                        // Bring the window to the front
                        screen.toFront();
                    }
                });
                screens[i].setVisible(true);

            } catch (Exception ex) {
                System.err.println("Error Picking a screen colour");
                ex.printStackTrace();
            }
        }
        // Primary monitor
        screens[0].toFront();
    }

    /** Dispose of the screens created for the eye dropper */
    private void stopEyeDropper() {
        for (int i = 0; i < screens.length; i++) {
            screens[i].setVisible(false);
            screens[i].dispose();
        }
        screens = null;
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
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(colourArray, 0, 0, null);
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

            startEyeDropper();

        // Launch Colour Selector
        } else if (e.getX() >= 80 && e.getY() <= 20) {

            startColourSelector();

        } else {

            Alchemy.canvas.setColour(this.getColor(e.getX(), e.getY()));
            parent.hidePopup();
            if (Alchemy.PLATFORM == MACOSX) {
                Alchemy.canvas.restoreCursor();
                //Alchemy.canvas.setCursor(CURSOR_CROSS);
                setCursor(CURSOR_ARROW);
            }
            Alchemy.toolBar.refreshColourButton();
        }
    }

    public void mouseEntered(MouseEvent e) {
        // OSX does not seem to obey the set cursor so set the other cursors
        if (Alchemy.PLATFORM == MACOSX) {
            Alchemy.canvas.setTempCursor(CURSOR_CIRCLE);
            Alchemy.toolBar.setCursor(CURSOR_CIRCLE);
        }
    }

    public void mouseExited(MouseEvent e) {
        if (Alchemy.PLATFORM == MACOSX) {
            Alchemy.canvas.restoreCursor();
            //Alchemy.canvas.setCursor(CURSOR_CROSS);
            Alchemy.toolBar.setCursor(CURSOR_ARROW);
        }
    }
}
