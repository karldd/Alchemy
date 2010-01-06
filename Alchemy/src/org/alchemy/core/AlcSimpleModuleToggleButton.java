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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;

/**
 *
 * AlcSimpleModuleToggleButton.java
 */
class AlcSimpleModuleToggleButton extends JToggleButton {

    AlcSimpleModuleToggleButton() {
    }

    AlcSimpleModuleToggleButton(Action action) {
        this.setAction(action);
    }

    AlcSimpleModuleToggleButton(URL iconUrl) {
        setup(iconUrl, null);
    }

    void setup(URL iconUrl) {
        setup(iconUrl, null);
    }

    void setup(URL iconUrl, String toolTip) {
        if (iconUrl != null) {
            ImageIcon icon = AlcUtil.getImageIcon(iconUrl);
            this.setIcon(createIcon(icon, AlcToolBar.COLOR_UI_BG));
            this.setRolloverIcon(createIcon(icon, AlcToolBar.COLOR_UI_END));
            this.setSelectedIcon(createIcon(icon, Color.GRAY));
        }

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        //this.setMargin(new Insets(5, 5, 5, 5));
        //this.setBorderPainted(false);    // Draw the button shape
        this.setContentAreaFilled(false);  // Draw the background behind the button
        this.setFocusPainted(false);       // Draw the highlight when focused

    }

    private ImageIcon createIcon(ImageIcon icon, Color bgColor) {
        int padding = 12;
        int halfPad = padding / 2;
        int width = icon.getIconWidth() + padding;
        int height = icon.getIconHeight() + padding;

        BufferedImage iconBg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = iconBg.createGraphics();
        // Background
        g2.setColor(bgColor);
        g2.fillRect(0, 0, width, height);
        // Outline
        g2.setColor(AlcToolBar.COLOR_UI_LINE);
        g2.drawRect(0, 0, width - 1, height - 1);
        // Icon
        g2.drawImage(icon.getImage(), halfPad, halfPad, null);
        g2.dispose();

        return new ImageIcon(iconBg);
    }
}
