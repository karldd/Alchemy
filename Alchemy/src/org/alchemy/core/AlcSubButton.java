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

/** Custom button to fit the subtoolbar */
public class AlcSubButton extends JButton implements AlcConstants {

    public AlcSubButton(String text) {
        setup(text);
    }

    public AlcSubButton(String text, URL iconUrl) {

        if (iconUrl != null) {
            // Set the main icon
            this.setIcon(AlcUtil.getImageIcon(iconUrl));
            // Set the rollover icon
            URL rolloverIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-over");
            this.setRolloverIcon(AlcUtil.getImageIcon(rolloverIconUrl));

            URL pressedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-down");
            this.setPressedIcon(AlcUtil.getImageIcon(pressedIconUrl));
        }
        setup(text);
    }

    private void setup(String text) {
        this.setFont(FONT_SMALL);
        this.setText(text);
        // Insets(int top, int left, int bottom, int right)
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setIconTextGap(2);
        this.setBorderPainted(false);    // Draw the button shape
        this.setContentAreaFilled(false);  // Draw the background behind the button
    //this.setPreferredSize(new Dimension(this.getPreferredSize().width, 26));
    //this.setFocusPainted(false);       // Draw the highlight when focused
    }
}
