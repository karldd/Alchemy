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
import javax.swing.JPanel;

public class AlcMainToolBar extends JPanel {

    private final AlcMain root;
    private int height = 60;

    /** Creates a new instance of AlcMainToolBar
     * @param root Reference to the root
     */
    public AlcMainToolBar(AlcMain root) {

        this.root = root;
        // Allow a transparent background
        this.setOpaque(false);
        //this.setName("Toolbar");
        //this.setBackground(AlcToolBar.toolBarBgColour);
        //this.setBorderPainted(false);
        //this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        this.setPreferredSize(new Dimension(root.getWindowSize().width, height));


    }

    // Override the paint component to draw the gradient bg
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int panelWidth = getWidth();
        GradientPaint gradientPaint = new GradientPaint(0, 0, AlcToolBar.toolBarBgStartColour, 0, this.height, AlcToolBar.toolBarBgEndColour, true);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setPaint(gradientPaint);
            g2.fillRect(0, 0, root.getWindowSize().width, this.height);
            g2.setPaint(AlcToolBar.toolBarLineColour);
            g2.drawLine(0, this.height - 1, root.getWindowSize().width, this.height - 1);
        }
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
