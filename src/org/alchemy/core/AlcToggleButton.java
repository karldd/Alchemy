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
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AlcToggleButton extends JToggleButton implements AlcShortcutInterface, AlcConstants {

    private String toolTip;
    private boolean preferenceButton = false;
    // Colors for the preferences button
    // Colors for the mac united toolbar
//    private Color centreTop = new Color(185, 185, 185);
//    private Color centreMiddle = new Color(139, 139, 139);
//    private Color centreBottom = new Color(149, 149, 149);
//    private Color innerMiddle = new Color(122, 122, 122);
//    private Color outerMiddle = new Color(85, 85, 85);
    private Color centreTop = new Color(233, 233, 233);
    private Color centreMiddle = new Color(187, 187, 187);
    private Color centreBottom = new Color(197, 197, 197);
    private Color innerMiddle = new Color(170, 170, 170);
    private Color outerMiddle = new Color(133, 133, 133);
    private Image backgroundImage;

    public AlcToggleButton() {
    }

    public AlcToggleButton(Action action) {
        this.setAction(action);
    }

    public AlcToggleButton(URL iconUrl) {
        setup(null, null, iconUrl, false);
    }

    public AlcToggleButton(String text, String toolTip, URL iconUrl) {
        setup(text, toolTip, iconUrl, false);
    }

    public AlcToggleButton(String text, String toolTip, URL iconUrl, boolean preferenceButton) {
        setup(text, toolTip, iconUrl, preferenceButton);
    }

    void setup(String text, String toolTip, URL iconUrl) {
        setup(text, toolTip, iconUrl, false);
    }

    void setup(String text, String toolTip, URL iconUrl, boolean preferenceButton) {

        this.preferenceButton = preferenceButton;

        if (toolTip != null) {
            this.toolTip = toolTip;
        }

        if (iconUrl != null) {
            // Set the main icon
            this.setIcon(AlcUtil.getImageIcon(iconUrl));
            // Set the rollover icon
            URL rolloverIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-over");
            this.setRolloverIcon(AlcUtil.getImageIcon(rolloverIconUrl));
            // Set the selected icon
            if (!preferenceButton) {
                URL selectedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-on");
                this.setSelectedIcon(AlcUtil.getImageIcon(selectedIconUrl));
            }
            // Set the rollover - selected icon
            URL rolloverSelectedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-on-over");
            this.setRolloverSelectedIcon(AlcUtil.getImageIcon(rolloverSelectedIconUrl));

            URL pressedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-down");
            this.setPressedIcon(AlcUtil.getImageIcon(pressedIconUrl));
        }

        this.setFont(FONT_MEDIUM);
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        if (text != null) {
            this.setText(text);
        }
        this.setToolTipText(toolTip);

        // Insets(int top, int left, int bottom, int right)
        this.setMargin(new Insets(0, 0, 0, 0));

        if (preferenceButton) {
            // Create some room for the background
            this.setBorder(new EmptyBorder(4, 8, 4, 8));
        } else {
            this.setBorderPainted(false);    // Draw the button shape
        }
        this.setContentAreaFilled(false);  // Draw the background behind the button
        this.setFocusPainted(false);       // Draw the highlight when focused

    }

    public void refreshShortcut(int key, int modifier) {
        this.setToolTipText(AlcShortcuts.getShortcutString(key, modifier, toolTip));
    }

    /** Create a background image to be used in the preferences pane */
    private Image createBackgroundImage() {
        int height = getHeight();
        int halfHeight = height / 2;
        // Leave some space for the left & right side lines
        int width = getWidth() - 3;

        BufferedImage image = new BufferedImage(width + 4, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Main
        g2.setPaint(new GradientPaint(0, 0, centreTop, 0, halfHeight, centreMiddle, true));
        g2.fillRect(2, 0, width, halfHeight);
        g2.setPaint(new GradientPaint(0, halfHeight, centreMiddle, 0, height, centreBottom, true));
        g2.fillRect(2, halfHeight, width, height);

        // Left Outer Line
        g2.setPaint(new GradientPaint(0, 0, centreTop, 0, halfHeight, outerMiddle, true));
        g2.fillRect(0, 0, 1, halfHeight);
        g2.setPaint(new GradientPaint(0, halfHeight, outerMiddle, 0, height, centreBottom, true));
        g2.fillRect(0, halfHeight, 1, height);
        // Left Inner Line
        g2.setPaint(new GradientPaint(0, 0, centreTop, 0, halfHeight, innerMiddle, true));
        g2.fillRect(1, 0, 1, halfHeight);
        g2.setPaint(new GradientPaint(0, halfHeight, innerMiddle, 0, height, centreBottom, true));
        g2.fillRect(1, halfHeight, 1, height);

        // Right Inner Line
        g2.setPaint(new GradientPaint(0, 0, centreTop, 0, halfHeight, innerMiddle, true));
        g2.fillRect(width, 0, 1, halfHeight);
        g2.setPaint(new GradientPaint(0, halfHeight, innerMiddle, 0, height, centreBottom, true));
        g2.fillRect(width, halfHeight, 1, height);
        // Right Outer Line
        g2.setPaint(new GradientPaint(0, 0, centreTop, 0, halfHeight, outerMiddle, true));
        g2.fillRect(width + 1, 0, 1, halfHeight);
        g2.setPaint(new GradientPaint(0, halfHeight, outerMiddle, 0, height, centreBottom, true));
        g2.fillRect(width + 1, halfHeight, 1, height);

        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (preferenceButton) {
            if (isSelected()) {
                if (backgroundImage == null) {
                    backgroundImage = createBackgroundImage();
                }
                g.drawImage(backgroundImage, 0, 0, null);
            }
        }
        super.paintComponent(g);
    }
}

