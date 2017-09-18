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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

/**
 *
 * AlcAbstractSpinner.java
 */
public abstract class AlcAbstractSpinner extends JPanel implements AlcShortcutInterface, AlcConstants {

    AlcSpinnerCustom spinner;
    JLabel label;
    String toolTip;
    int key1 = -1;

    public void refreshShortcut(int key, int modifier) {
        // Becuase there are two shortcuts for the spinner
        // Wait till the second call then add the tooltip
        if (key1 < 0) {
            key1 = key;
        } else {
            String doubleKey = "(" + AlcShortcuts.getShortcutString(key1, modifier) +
                    " " + AlcShortcuts.getShortcutString(key, modifier) + ")";
            String fullTip = toolTip + " " + doubleKey;
            spinner.setToolTipText(fullTip);
            label.setToolTipText(fullTip);
            key1 = -1;
        }
    }

    /** Set the tooltip of the spinner
     * @param toolTip   The tooltip string
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * Returns this slider's isAdjusting value which is true if the
     * thumb is being dragged.
     *
     * @return The slider's isAdjusting value.
     */
    public boolean getValueIsAdjusting() {
        return spinner.mouseDown;
    }

    /**
     * Returns the current value of the slider.
     *
     * @return The value of the slider stored in the model.
     */
    public int getValue() {
        return spinner.value;
    }

    /** Sets the current value of the slider.
     * 
     * @param value The new value
     */
    public void setValue(int value) {
        spinner.setValue(value);
    }

    /** Returns the next number in the sequence
     * 
     * @return  value + step or max if the sum exceeds maximum.
     */
    public int getNextValue() {
        return spinner.getNextValue();
    }

    /** Set the spinner to the next value in the sequence */
    public void setNextValue() {
        spinner.setNextValue();
    }

    /** Returns the previous number in the sequence
     * 
     * @return  value - step or min if the sum exceeds minimum.
     */
    public int getPreviousValue() {
        return spinner.getPreviousValue();
    }

    /** Set the spinner to the previous value in the sequence */
    public void setPreviousValue() {
        spinner.setPreviousValue();
    }

    /** Returns the maximum number in the sequence.
     * 
     * @return  The value of the max property
     */
    public int getMaximum() {
        return spinner.max;
    }

    /** Returns the minimum number in the sequence.
     * 
     * @return  The value of the min property
     */
    public int getMinimum() {
        return spinner.min;
    }

    /**
     * Registers a listener to this slider. The listener will be
     * informed of new ChangeEvents.
     *
     * @param listener The listener to register.
     */
    public void addChangeListener(ChangeListener listener) {
        spinner.addChangeListener(listener);
    }

    /**
     * Removes a listener from this slider.
     *
     * @param listener The listener to remove.
     */
    public void removeChangeListener(ChangeListener listener) {
        spinner.removeChangeListener(listener);
    }
}
