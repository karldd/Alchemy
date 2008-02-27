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

import alchemy.AlcUtil;
import java.awt.Insets;
import java.net.URL;
import javax.swing.*;

public class AlcSubToggleButton extends JToggleButton {

    /**
     * Creates a new instance of AlcMainButton
     */
    public AlcSubToggleButton(String text, URL iconUrl) {

        if (iconUrl != null) {
            // Set the main icon
            this.setIcon(AlcUtil.createImageIcon(iconUrl));
            // Set the rollover icon
            URL rolloverIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-over");
            this.setRolloverIcon(AlcUtil.createImageIcon(rolloverIconUrl));
            // Set the selected icon
            URL selectedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-on");
            this.setSelectedIcon(AlcUtil.createImageIcon(selectedIconUrl));
            // Set the rollover - selected icon
            URL rolloverSelectedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-on-over");
            this.setRolloverSelectedIcon(AlcUtil.createImageIcon(rolloverSelectedIconUrl));
        }

        this.setFont(AlcToolBar.subToolBarFont);
        this.setText(text);
        // Insets(int top, int left, int bottom, int right)
        this.setMargin(new Insets(4, 0, 0, 0));
        this.setIconTextGap(3);
        this.setBorderPainted(false);    // Draw the button shape
        this.setContentAreaFilled(false);  // Draw the background behind the button
        this.setFocusPainted(false);       // Draw the highlight when focused
    }
}
