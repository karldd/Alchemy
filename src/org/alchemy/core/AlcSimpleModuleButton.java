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

import java.awt.Insets;
import java.net.URL;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.border.*;

/**
 *
 * AlcSimpleModuleButton.java
 */
class AlcSimpleModuleButton extends JButton implements AlcConstants{

    AlcSimpleModuleButton() {
    }

    AlcSimpleModuleButton(Action action) {
        this.setAction(action);
    }

    AlcSimpleModuleButton(URL iconUrl) {
        setup(iconUrl);
    }

    void setup(URL iconUrl) {
        if (iconUrl != null) {
            // Set the main icon
            this.setIcon(AlcUtil.getImageIcon(iconUrl));
//            // Set the rollover icon
//            URL rolloverIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-over");
//            this.setRolloverIcon(AlcUtil.getImageIcon(rolloverIconUrl));
//
//            URL pressedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-down");
//            this.setPressedIcon(AlcUtil.getImageIcon(pressedIconUrl));
        }

        this.setMargin(new Insets(5, 5, 5, 5));
        //this.setBorderPainted(false);    // Draw the button shape
        //this.setContentAreaFilled(false);  // Draw the background behind the button
        //this.setFocusPainted(false);       // Draw the highlight when focused

        CompoundBorder doubleBorder = new CompoundBorder(
                new MatteBorder(1, 1, 1, 1, COLOR_UI_LINE),
                new EmptyBorder(5, 5, 5, 5));

        CompoundBorder tripleBorder = new CompoundBorder(
                new EmptyBorder(10, 10, 0, 0),
                doubleBorder);


        //this.setBorder(tripleBorder);

    }
}
