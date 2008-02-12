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
package alchemy.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;

/**
 * AlcPaletteTitleBar
 * @author Karl D.D. Willis
 */
public class AlcPaletteTitleBar extends JPanel {

    //private final AlcPalette parent;
    private int originalX,  originalY;

    public AlcPaletteTitleBar(final AlcPalette parent) {
        //this.parent = parent;
        this.setBackground(Color.DARK_GRAY);
        this.setSize(new Dimension(14, 100));
        this.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                originalX = e.getX();
                originalY = e.getY();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {

            public void mouseDragged(MouseEvent e) {
                parent.shiftPalette(e.getX() - originalX, e.getY() - originalY);
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);
        System.out.println(this.getSize() + " " + this.getLocation());
    }
}
