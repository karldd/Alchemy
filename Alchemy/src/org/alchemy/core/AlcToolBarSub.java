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
import javax.swing.*;

class AlcToolBarSub extends JPanel implements AlcConstants {

    //private static final int rowHeight = 26;
    private int height = 25;
    //private int numberOfRows = 1;
    /** Creates a new instance of AlcSubToolBar */
    AlcToolBarSub() {

        // Allow Transparency
        this.setOpaque(false);
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.setPreferredSize(new Dimension(Alchemy.window.getWindowSize().width, height));
    }

//    void setRows(int numberOfRows) {
//        this.numberOfRows = numberOfRows;
//        height = numberOfRows * rowHeight;
//        this.setPreferredSize(new Dimension(Alchemy.window.getWindowSize().width, height));
//    }
//    int getLayoutHeight() {
//        Dimension layoutSize = this.getLayout().preferredLayoutSize(this);
//        return layoutSize.height;
//    }

    int getContentWidth() {
        return this.getLayout().preferredLayoutSize(this).width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void revalidate() {
        super.revalidate();
    //System.out.println("Revalidate called");
    }

    // Override the paint component to draw the gradient bg
    @Override
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
            g2.setPaint(COLOR_UI_HIGHLIGHT);
            g2.drawLine(0, 0, targetWidth, 0);
            if (!Alchemy.preferences.paletteAttached) {
                g2.setPaint(COLOR_UI_LINE);
                g2.drawLine(0, heightMinusOne, targetWidth, heightMinusOne);
            }
        }
    }
}
