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

import java.awt.Insets;
import java.net.URL;
import javax.swing.*;

public class AlcButton extends JButton implements AlcShortcutInterface, AlcConstants {

    private String toolTip;

    public AlcButton() {
    }

    public AlcButton(Action action) {
        this.setAction(action);
    }

    public AlcButton(URL iconUrl) {
        setup(null, null, iconUrl);
    }

    public AlcButton(String text, String toolTip) {
        setup(text, toolTip, null);
    }

    public AlcButton(String text, String toolTip, URL iconUrl) {
        setup(text, toolTip, iconUrl);
    }

    void setup(String text, String toolTip, URL iconUrl) {

        if (toolTip != null) {
            this.toolTip = toolTip;
        }

        if (iconUrl != null) {
            // Set the main icon
            this.setIcon(AlcUtil.getImageIcon(iconUrl));
            // Set the rollover icon
            URL rolloverIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-over");
            this.setRolloverIcon(AlcUtil.getImageIcon(rolloverIconUrl));

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
        this.setBorderPainted(false);    // Draw the button shape
        this.setContentAreaFilled(false);  // Draw the background behind the button
        this.setFocusPainted(false);       // Draw the highlight when focused
    }

    public void refreshShortcut(int key, int modifier) {
        this.setToolTipText(AlcShortcuts.getShortcutString(key, modifier, toolTip));
    }
}
