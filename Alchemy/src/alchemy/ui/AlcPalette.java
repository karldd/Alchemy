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
    private JPanel mainPalette;
    private Component content;
    private AlcPaletteTitleBar titleBar;
    private AlcMain root;
    private int maxSize = 1280;
    private int minSize = 600;
    private int paletteHeight = 88;

    public AlcPalette(Frame owner) {
        super(owner);

        if (owner instanceof AlcMain) {
            root = (AlcMain) owner;
        }

        setGoodSize();
        this.setBackground(Color.WHITE);
        //this.setLocationRelativeTo(null); 
        this.setFocusable(true);

        mainPalette = new JPanel();
        mainPalette.setBackground(AlcToolBar.toolBarBgColour);
        mainPalette.setBorder(new LineBorder(AlcToolBar.toolBarLineColour, 1));
        mainPalette.setLayout(new BorderLayout());
        mainPalette.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        titleBar = new AlcPaletteTitleBar(this, root);
        //titleBar.add(new JLabel("ho"));
        //this.setLayout(new BorderLayout());
        mainPalette.add("West", titleBar);

        this.setContentPane(mainPalette);
//        this.pack();
//        this.setVisible(true);
    }

    protected void shiftPalette(int x, int y) {
        Point aPoint = this.getLocation();
        this.setLocation(aPoint.x + x, aPoint.y + y);
    }

    /** Add a component to the main content area */
    public void addContent(Component comp) {
        setGoodSize();
        if (content != null) {
            mainPalette.remove(content);
        }
        this.content = comp;
        mainPalette.add("Center", comp);
        mainPalette.revalidate();
    }

    private void setGoodSize() {
        if (root.getWindowSize().width < minSize) {
            this.setSize(new Dimension(minSize, paletteHeight));
        } else if (root.getWindowSize().width > maxSize) {
            this.setSize(new Dimension(maxSize, paletteHeight));
        } else {
            this.setSize(new Dimension(root.getWindowSize().width, paletteHeight));
        }
    }
}
