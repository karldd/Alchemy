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

import alchemy.AlcMain;
import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;

/**
 * AlcPalette
 * @author Karl D.D. Willis
 */
public class AlcPalette extends JWindow {

    //private JPanel paletteContent;
    private JPanel mainPalette,  content;
    private AlcPaletteTitleBar titleBar;
    private AlcMain root;

    public AlcPalette(Frame owner) {
        super(owner);
        setup(owner, null);
    }

    public AlcPalette(Frame owner, Component comp) {
        super(owner);
        setup(owner, comp);
    }

    private void setup(Frame owner, Component comp) {

        //if (owner instanceof AlcMain) {
        root = (AlcMain) owner;
        //}

        this.setPreferredSize(new Dimension(root.getWindowSize().width, 88));
        // TODO - Remember the location of the palette
        this.setLocation(100, 100);
        this.setBackground(Color.WHITE);
        //this.setLocationRelativeTo(null); 


        mainPalette = new JPanel();
        mainPalette.setBorder(new LineBorder(AlcToolBar.toolBarLineColour, 1));
        mainPalette.setLayout(new BorderLayout());
        mainPalette.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        titleBar = new AlcPaletteTitleBar(this, root);
        //titleBar.add(new JLabel("ho"));
        //this.setLayout(new BorderLayout());
        mainPalette.add("West", titleBar);




        //JWindow pWindow = new JWindow(owner);
        content = new JPanel();
        content.setBackground(Color.WHITE);
        if (comp != null) {
            addContent(comp);
        }


        this.setContentPane(mainPalette);
        //this.pack();                               
        //container = pWindow;
        //this.add("East", container);
        this.pack();
        this.setVisible(true);
    }

    protected void shiftPalette(int x, int y) {
        Point aPoint = this.getLocation();
        this.setLocation(aPoint.x + x, aPoint.y + y);
    }

    /** Add a component to the main content area */
    public void addContent(Component comp) {
        mainPalette.add("Center", comp);
        mainPalette.revalidate();
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
