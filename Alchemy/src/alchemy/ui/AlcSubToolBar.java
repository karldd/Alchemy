/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007 Karl D.D. Willis
 * 
 *  Alchemy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Alchemy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package alchemy.ui;

import alchemy.*;
import java.awt.*;
import javax.swing.*;

public class AlcSubToolBar extends JPanel {

    private final AlcMain root;
    //private AlcToolBar parent;
    private static final int height = 26;

    /** Creates a new instance of AlcSubToolBar */
    public AlcSubToolBar(AlcMain root) {

        this.root = root;
        //this.parent = root.toolBar;
        // Allow Transparency
        this.setOpaque(false);
        //this.setBorderPainted(false);
        //this.setFloatable(false); 
        this.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        //this.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.setPreferredSize(new Dimension(root.getWindowSize().width, height));

    //this.add(new AlcLabel(parent, title, null, description));

    // this.setAlignmentY(Component.TOP_ALIGNMENT);

    //this.setBackground(Color.BLACK);
    //this.setLocation(0, parent.getToolBarHeight());
    //this.setBounds(500, root.getWindowSize().width, 600, 25);

    //JButton ok = new JButton("OK");
    //this.add(ok);
    }

    // Override the paint component to draw the gradient bg
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int panelWidth = getWidth();

        GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(215, 215, 215, 235), 0, height, new Color(207, 207, 207, 235), true);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int targetWidth = getRootPane().getSize().width;
            int heightMinusOne = height - 1;
            g2.setPaint(gradientPaint);
            g2.fillRect(0, 0, targetWidth, height);
            g2.setPaint(AlcToolBar.toolBarHighlightColour);
            g2.drawLine(0, 0, targetWidth, 0);
            if (!root.prefs.getPaletteAttached()) {
                g2.setPaint(AlcToolBar.toolBarLineColour);
                g2.drawLine(0, heightMinusOne, targetWidth, heightMinusOne);
            }
        }
    }

    /** Return the height of the sub toolbar */
    public int getHeight() {
        return height;
    }
}
