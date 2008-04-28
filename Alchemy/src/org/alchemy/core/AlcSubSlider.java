/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2008 Karl D.D. Willis
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

/**
 * AlcSubSlider
 * 
 * 
 */
public class AlcSubSlider extends JPanel implements AlcConstants {

    private AlcSliderCustom slider;

    public AlcSubSlider(String name, int minValue, int maxValue, int startValue) {

        // TODO - Check this spacing on Win
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(1, 2, 0, 2));
        this.setOpaque(false);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        slider = new AlcSliderCustom(minValue, maxValue, startValue);
        this.add(slider);

        JLabel label = new JLabel(name);
        label.setFont(AlcToolBar.subToolBarFont);
        label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
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
