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
import javax.swing.*;

/**
 *
 * AlcSimpleToolBar.java
 */
public class AlcSimpleToolBar extends AlcAbstractToolBar implements AlcConstants {

    AlcSimpleToolBar() {
        // TOOLBAR
        // Left align layout
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(true);
        this.setBackground(toolBarBgColour);
        this.setName("Toolbar");

        // COLOUR BOX
        // Rectangle colourBoxRect = new Rectangle(0, 100, 150, 50);
        final Rect colourBox = new Rect(150, 50, Alchemy.canvas.getColour());
//        colourBox.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.red),
//                colourBox.getBorder()));
        //colourBox.setBounds(colourBoxRect);

//        colourBox.setOpaque(true);
//        colourBox.setBackground(Alchemy.canvas.getColour());


        // COLOUR PICKER
        // Get the icon for the label
        ImageIcon colourPickerIcon = AlcUtil.getImageIcon("simple-colour-picker.png");
        // Create a rectangle for easy reference 
        final Rectangle colourPickerRect = new Rectangle(0, 0, colourPickerIcon.getIconWidth(), colourPickerIcon.getIconHeight());

        // Create a blank image then draw into it rather than casting the image
        final BufferedImage colourPickerBuffImage = new BufferedImage(colourPickerRect.width, colourPickerRect.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = colourPickerBuffImage.createGraphics();
        g2.drawImage(colourPickerIcon.getImage(), colourPickerRect.x, colourPickerRect.y, colourPickerRect.width, colourPickerRect.height, null);
        g2.dispose();

        //final BufferedImage colourPickerImage = (BufferedImage)colourPickerIcon.getImage();
        JLabel colourPicker = new JLabel(colourPickerIcon);

        colourPicker.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        final Cursor pickerCursor = AlcUtil.getCursor("cursor-picker.gif");
        colourPicker.setCursor(pickerCursor);
        //    colourPicker.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        colourPicker.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                if (colourPickerRect.contains(p)) {
                    Color c = new Color(colourPickerBuffImage.getRGB(p.x, p.y));
                    Alchemy.canvas.setColour(c);
                    colourBox.update(c);
                //System.out.println(c + " " + e.getPoint());
                }
            }
        });

        colourPicker.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();
                if (colourPickerRect.contains(p)) {
                    Color c = new Color(colourPickerBuffImage.getRGB(p.x, p.y));
                    colourBox.update(c);
                }
            }
        });
        this.add(colourPicker);
        this.add(colourBox);

        AlcToggleButton styleButton = new AlcToggleButton(AlcUtil.getUrlPath("simple-style.png"));
        this.add(styleButton);

        this.add(Box.createVerticalGlue());
//        colourPicker.setBounds(colourPickerRect);
//        colourBox.setBounds(colourBoxrRect);




        //this.setPreferredSize(new Dimension(100, 100));

        this.setVisible(true);

    }

    @Override
    void resizeToolBar( Dimension windowSize) {
        this.setBounds(0, 0, 151, windowSize.height);
        this.windowSize = windowSize;
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw a line on the right edge of the toolbar
        g.setColor(toolBarLineColour);
        g.drawLine(150, 0, 150, windowSize.height);
    }
}

class Rect extends JPanel {

    int width, height;
    Color colour;

    Rect(int width, int height, Color colour) {
        this.width = width;
        this.height = height;
        this.colour = colour;
        this.setPreferredSize(new Dimension(width, height));
//        this.setMinimumSize(new Dimension(width, height));
//        this.setMaximumSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(colour);
        g.fillRect(0, 0, width, height);
    }

    void update(Color colour) {
        this.colour = colour;
        this.repaint();
    }
}


