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

import java.awt.Component;
import javax.swing.*;

/**
 * AlcSpinner
 * 
 */
public class AlcSpinner extends AlcAbstractSpinner {


    public AlcSpinner(String title, int min, int max, int value, int step) {

        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        spinner = new AlcSpinnerCustom(title, false, value, min, max, step);
        spinner.setAlignmentX(Component.CENTER_ALIGNMENT);


        label = new JLabel(title);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(FONT_MEDIUM);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        // Box layout CENTER_ALIGHNMENT is not working as expected when the bottom label is bigger than the top
        // To work around this add padding outselves based on the size of each component
        if (label.getPreferredSize().width > spinner.getPreferredSize().width) {
            int padLeft = (label.getPreferredSize().width - spinner.getPreferredSize().width) / 2;
            JPanel padPanel = new JPanel();
            padPanel.setOpaque(false);
            padPanel.setLayout(new BoxLayout(padPanel, BoxLayout.LINE_AXIS));
            // Top Left Bottom Right
            padPanel.setBorder(BorderFactory.createEmptyBorder(0, padLeft, 0, 0));
            padPanel.add(spinner);

            this.add(padPanel);
        } else {
            this.add(spinner);
        }
        this.add(label);
    }
}
