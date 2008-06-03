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

import java.awt.Component;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * AlcSlider
 * 
 * 
 */
class AlcSlider extends JPanel implements AlcShortcutInterface {

    protected AlcSliderCustom slider;
    private JLabel label;
    private String toolTip;

    AlcSlider(String name, String toolTip, int minValue, int maxValue, int startValue) {



        // Top Left Bottom Right
        //this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        slider = new AlcSliderCustom(80, 25, minValue, maxValue, startValue);
        this.add(slider);

        if (name != null) {
            label = new JLabel(name);
            label.setFont(AlcToolBar.toolBarFont);
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
