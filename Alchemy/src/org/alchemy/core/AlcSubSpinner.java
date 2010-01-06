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

import java.awt.FlowLayout;
import javax.swing.*;

/**
 *
 * AlcSubSpinner.java
 */
public class AlcSubSpinner extends AlcAbstractSpinner {

    /** Sub tool bar 'spinner'
     * 
     * @param title     The title of the spinner
     * @param value     The start value
     * @param min       The minimum value
     * @param max       The maximum value
     * @param step      The step size to increment with
     */
    public AlcSubSpinner(String title, int min, int max, int value, int step) {

        this.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        this.setOpaque(false);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        spinner = new AlcSpinnerCustom(title, true, value, min, max, step);
        this.add(spinner);


        label = new JLabel(title);
        label.setFont(FONT_SMALL);
        label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        this.add(label);

    }
    
}
