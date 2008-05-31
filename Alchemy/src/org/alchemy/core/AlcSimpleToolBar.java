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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * AlcSimpleToolBar.java
 */
public class AlcSimpleToolBar extends AlcAbstractToolBar implements AlcConstants {

    AlcSimpleToolBar() {
        // General Toolbar settings
        // Left align layout
        //this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(true);
        this.setBackground(toolBarBgColour);

        this.setName("Toolbar");


        final JLabel colourBox = new JLabel();
        colourBox.setOpaque(true);
        colourBox.setBackground(Alchemy.canvas.getColour());
        colourBox.setPreferredSize(new Dimension(150, 50));


        ImageIcon colourPickerIcon = AlcUtil.getImageIcon("simple-colour-picker.png");
        final int w = colourPickerIcon.getIconWidth();
        final int h = colourPickerIcon.getIconHeight();

        // Create a blank image then draw into it rather than casting the image
        final BufferedImage colourPickerBuffImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = colourPickerBuffImage.createGraphics();
        g2.drawImage(colourPickerIcon.getImage(), 0, 0, w, h, null);
        g2.dispose();

        //final BufferedImage colourPickerImage = (BufferedImage)colourPickerIcon.getImage();
        JLabel colourPicker = new JLabel(colourPickerIcon);

        final Cursor pickerCursor = AlcUtil.getCursor("cursor-picker.gif");
        colourPicker.setCursor(pickerCursor);
        //    colourPicker.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        colourPicker.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (x <= w && y <= h) {
                    Color c = new Color(colourPickerBuffImage.getRGB(x, y));
                    Alchemy.canvas.setColour(c);
                    colourBox.setBackground(c);
                //System.out.println(c + " " + e.getPoint());
                }
            }
        });
        this.add(colourPicker);
        this.add(colourBox);


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


