/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007 Karl D.D. Willis
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
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JMenuItem;

/**
 * AlcColourPicker
 * @author Karl D.D. Willis
 */
class AlcColourPicker extends JMenuItem implements AlcConstants {

    private BufferedImage colourArray;

    AlcColourPicker() {

        this.setPreferredSize(new Dimension(100, 115));

        this.setOpaque(true);
        this.setBackground(Color.WHITE);

        // Create a colour array
        int w = 100;
        int h = 200;
        int pixels[] = new int[w * h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float hue = x / 100F;
                float saturation = Math.min(y / 100F, 1F);
                float brightness = Math.min((h - y) / 100F, 1F);
                Color c = Color.getHSBColor(hue, saturation, brightness);
                pixels[y * w + x] = c.getRGB();
            }
        }
        // Make the colour array into an image
        BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        temp.setRGB(0, 0, w, h, pixels, 0, w);

        // Use a graphics object to scale the array and draw the black and white sections in
        colourArray = new BufferedImage(w, 115, BufferedImage.TYPE_INT_ARGB);

        Graphics g = colourArray.createGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 25, 15);

        g.setColor(Color.WHITE);
        g.fillRect(50, 0, 50, 15);

        // 50% Grey
        g.setColor(new Color(0.5F, 0.5F, 0.5F));
        g.fillRect(25, 0, 25, 15);

        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(75, 0, 24, 14);

        // Draw the edges of the base colours
        g.drawLine(50, 14, 100, 14);
        g.drawLine(50, 0, 100, 0);

        // Draw some dots
        g.fillRect(82, 10, 2, 2);
        g.fillRect(86, 10, 2, 2);
        g.fillRect(90, 10, 2, 2);

        g.drawImage(temp, 0, 15, 100, 100, null);
        g.dispose();

        // CURSOR
        // Cursor size differs depending on the platform
        // Add padding based on the best cursor size
        final Cursor pickerCursor;
        Image smallPicker = AlcUtil.getImage("cursor-picker.gif", Alchemy.window);
        Dimension smallPickerSize = new Dimension(smallPicker.getWidth(null), smallPicker.getHeight(null));
        Dimension cursorSize = TOOLKIT.getBestCursorSize(smallPickerSize.width, smallPickerSize.height);

        if (cursorSize.equals(smallPickerSize)) {
            pickerCursor = TOOLKIT.createCustomCursor(
                    smallPicker,
                    new Point(smallPickerSize.width / 2, smallPickerSize.height / 2),
                    "Picker");
        } else {
            int leftGap = (cursorSize.width - smallPickerSize.width) / 2;
            int topGap = (cursorSize.height - smallPickerSize.height) / 2;

            BufferedImage bigPicker = new BufferedImage(cursorSize.width, cursorSize.height, BufferedImage.TYPE_INT_ARGB);
            g = bigPicker.createGraphics();
            g.drawImage(smallPicker, leftGap, topGap, null);
            g.dispose();

            pickerCursor = TOOLKIT.createCustomCursor(
                    bigPicker,
                    new Point(cursorSize.width / 2, cursorSize.height / 2),
                    "Picker");
        }

        this.setCursor(pickerCursor);

        this.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent e) {
                // OSX does not seem to obey the set cursor so set the other cursors
                if (Alchemy.PLATFORM == MACOSX) {
                    Alchemy.canvas.setCursor(pickerCursor);
                    Alchemy.toolBar.setCursor(pickerCursor);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (Alchemy.PLATFORM == MACOSX) {
                    Alchemy.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    Alchemy.toolBar.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
            });

    }

    Color getColor(int x, int y) {
        return new Color(colourArray.getRGB(x, y));
    }

    protected void paintComponent(Graphics g) {
        //super.paintComponent(g);
//        g.setColor(Color.WHITE);
//        g.fillRect(0, 0, 100, 115);
        g.drawImage(colourArray, 0, 0, null);
    }
}
