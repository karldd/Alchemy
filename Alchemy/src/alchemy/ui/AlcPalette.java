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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;

/**
 * AlcPalette
 * @author Karl D.D. Willis
 */
public class AlcPalette extends JWindow {

    //private JPanel paletteContent;
    private JPanel mainPalette, content;
    private final AlcPaletteTitleBar titleBar;

    public AlcPalette(Frame owner) {
        super(owner);

        this.setSize(800, 75);
        this.setLocation(100, 100);


        mainPalette = new JPanel();
        mainPalette.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        mainPalette.setLayout(new BorderLayout());

        titleBar = new AlcPaletteTitleBar(this);
        //this.setLayout(new BorderLayout());
        mainPalette.add("West", titleBar);



        //JWindow pWindow = new JWindow(owner);
        content = new JPanel();
        content.setBackground(Color.WHITE);
        //mainPalette.setSize(100, 400);
        content.add(new JLabel("hello"));
        mainPalette.add("Center", content);
        
        this.setContentPane(mainPalette);
        //this.pack();                               
        //container = pWindow;
        //this.add("East", container);
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
