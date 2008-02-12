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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Point;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * AlcPalette
 * @author Karl D.D. Willis
 */
public class AlcPalette extends JWindow {

    //private JPanel paletteContent;
    private JPanel container;
    private final AlcPaletteTitleBar titleBar;

    public AlcPalette(Frame owner) {
        super(owner);
        titleBar = new AlcPaletteTitleBar(this);
        this.setLayout(new BorderLayout());
        this.add("West", titleBar);
        this.setBackground(Color.WHITE);

        
        //JWindow pWindow = new JWindow(owner);
        container = new JPanel();
        //container = pWindow;
        this.getContentPane().add(container);
        this.setVisible(true);
    }

    protected void shiftPalette(int x, int y) {
        Point aPoint = this.getLocation();
        this.setLocation(aPoint.x + x, aPoint.y + y);
    }

//    public void setPaletteLocation(int x, int y) {
//        if (container == null) {
//            container = new JWindow();
//        }
//        container.setLocation(x, y);
//    }
//
//    public void setPaletteSize(int x, int y) {
//        if (container == null) {
//            container = new JWindow();
//        }
//        container.setSize(x, y);
//    }
}
