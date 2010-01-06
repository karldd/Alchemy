/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
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
package org.alchemy.core;

import java.awt.*;
import javax.swing.JPanel;

class AlcToolBarMain extends JPanel implements AlcConstants{

    private int height = 60;

    /** Creates a new instance of AlcMainToolBar
     * @param root Reference to the root
     */
    AlcToolBarMain() {

        // Allow a transparent background
        this.setOpaque(false);
        //this.setName("Toolbar");
        //this.setBackground(AlcToolBar.toolBarBgColor);
        //this.setBorderPainted(false);
        //this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        //this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(Alchemy.window.getWindowSize().width, height));


    }

    // Override the paint component to draw the gradient bg    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int panelWidth = getWidth();
        GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_UI_START, 0, this.height, COLOR_UI_END, true);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int targetWidth = getRootPane().getSize().width;
            g2.setPaint(gradientPaint);
            g2.fillRect(0, 0, targetWidth, this.height);
            g2.setPaint(COLOR_UI_LINE);
            int heightMinusOne = this.height - 1;
            g2.drawLine(0, heightMinusOne, targetWidth, heightMinusOne);
        }
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}
