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

import java.awt.FlowLayout;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/** Custom slider to fit the subtoolbar */
public class AlcSubSlider extends JPanel implements AlcConstants {

    private AlcSliderCustom slider;

    /** Sub tool bar slider
     * 
     * @param title    The title of the slider
     * @param min      The minimum value of the slider
     * @param max      The maximum value of the slider
     * @param value    The start value of the slider
     */
    public AlcSubSlider(String title, int min, int max, int value) {

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        this.setOpaque(false);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        slider = new AlcSliderCustom(title, 75, 15, min, max, value);
        //.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        this.add(slider);
               

        JLabel label = new JLabel(title);
        label.setFont(FONT_SMALL);
        label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        //label.setPreferredSize(new Dimension(label.getPreferredSize().width, 26));
        this.add(label);
    }

    /**
     * This method returns this slider's isAdjusting trueValue which is true if the
     * thumb is being dragged.
     *
     * @return The slider's isAdjusting trueValue.
     */
    public boolean getValueIsAdjusting() {
        return slider.mouseDown;
    }

    /**
     * This method returns the current trueValue of the slider.
     *
     * @return The trueValue of the slider stored in the model.
     */
    public int getValue() {
        return slider.trueValue;
    }
    
    /**
     * Set the value of the slider.
     * 
     * @param val The value.
     */
    public void setValue(int val) {
    	slider.setValue(val);
    }

    /**
     * This method registers a listener to this slider. The listener will be
     * informed of new ChangeEvents.
     *
     * @param listener The listener to register.
     */
    public void addChangeListener(ChangeListener listener) {
        slider.addChangeListener(listener);
    }

    /**
     * This method removes a listener from this slider.
     *
     * @param listener The listener to remove.
     */
    public void removeChangeListener(ChangeListener listener) {
        slider.removeChangeListener(listener);
    }
}
