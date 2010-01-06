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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * AlcPaletteTitleBar
 * @author Karl D.D. Willis
 */
class AlcPaletteTitleBar extends JPanel implements AlcConstants {
    //private final AlcPalette parent;
    private int originalX,  originalY;

    AlcPaletteTitleBar(final AlcPalette parent) {
        //this.parent = parent;
        this.setBackground(Color.LIGHT_GRAY);
        this.setPreferredSize(new Dimension(12, 88));

        JButton closeButton = new JButton(AlcUtil.getImageIcon("palette.png"));
        closeButton.setRolloverIcon(AlcUtil.getImageIcon("palette-over.png"));
        closeButton.setToolTipText("Close the palette and restore the toolbar to the main window");

        // Insets(int top, int left, int bottom, int right)
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setBorderPainted(false);    // Draw the button shape
        closeButton.setContentAreaFilled(false);  // Draw the background behind the button
        closeButton.setFocusPainted(false);       // Draw the highlight when focused

        closeButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Alchemy.window.setPalette(false);
                    }
                });



        this.add(closeButton);

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                originalX = e.getX();
                originalY = e.getY();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                parent.shiftPalette(e.getX() - originalX, e.getY() - originalY);
            }
        });
    }

    // Override the paint component to draw the gradient bg    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int panelWidth = getWidth();
        GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_UI_HIGHLIGHT, this.getWidth(), 0, new Color(208, 208, 208), true);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setPaint(gradientPaint);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.setPaint(COLOR_UI_LINE);
            int widthMinusOne = this.getWidth() - 1;
            //System.out.println(heightMinusOne);
            g2.drawLine(widthMinusOne, 0, widthMinusOne, this.getHeight());
        }
    }
}
