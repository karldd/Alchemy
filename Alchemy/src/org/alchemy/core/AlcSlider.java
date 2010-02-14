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
import javax.swing.event.ChangeListener;

/**
 * AlcSlider
 * 
 * 
 */
public class AlcSlider extends JPanel implements AlcShortcutInterface, AlcConstants{

    protected AlcSliderCustom slider;
    private JLabel label;
    private String toolTip;

    public AlcSlider(String name, String toolTip, int minValue, int maxValue, int startValue) {

        // Top Left Bottom Right
        //this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        slider = new AlcSliderCustom(name, 80, 25, minValue, maxValue, startValue);
        this.add(slider);

        if (name != null) {
            label = new JLabel(name);
            label.setFont(FONT_MEDIUM);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
            this.add(label);
        }


        if (toolTip != null) {
            this.toolTip = toolTip;
            setToolTipText(toolTip);
        }

    }

    @Override
    public void setToolTipText(String toolTip) {
        slider.setToolTipText(toolTip);
        label.setToolTipText(toolTip);
    }

    public void refreshShortcut(int key, int modifier) {
        this.setToolTipText(AlcShortcuts.getShortcutString(key, modifier, toolTip));
    }

    /**
     * Returns this slider's isAdjusting value which is true if the
     * thumb is being dragged.
     *
     * @return The slider's isAdjusting value.
     */
    public boolean getValueIsAdjusting() {
        return slider.mouseDown;
    }

    /**
     * Returns the current value of the slider.
     *
     * @return The value of the slider stored in the model.
     */
    public int getValue() {
        return slider.trueValue;
    }

    /** Sets the current value of the slider.
     * 
     * @param value The new value
     */
    public void setValue(int value) {
        slider.setValue(value);
    }

    /**
     * Registers a listener to this slider. The listener will be
     * informed of new ChangeEvents.
     *
     * @param listener The listener to register.
     */
    public void addChangeListener(ChangeListener listener) {
        slider.addChangeListener(listener);
    }

    /**
     * Removes a listener from this slider.
     *
     * @param listener The listener to remove.
     */
    public void removeChangeListener(ChangeListener listener) {
        slider.removeChangeListener(listener);
    }
}
