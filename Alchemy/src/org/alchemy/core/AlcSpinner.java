/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2009 Karl D.D. Willis
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
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * AlcSpinner
 * 
 */
class AlcSpinner extends JPanel implements AlcShortcutInterface, AlcConstants {

    protected AlcSpinnerCustom spinner;
    private JLabel label;
    private String toolTip;
    private int key1 = -1;

    AlcSpinner(String name, String toolTip, int value, int min, int max, int step) {

        this.toolTip = toolTip;
        // Top Left Bottom Right
        //this.setBorder(BorderFactory.createEmptyBorder(2, 8, 6, 4));
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        spinner = new AlcSpinnerCustom(value, min, max, step);
        spinner.setAlignmentX(Component.CENTER_ALIGNMENT);


        label = new JLabel(name);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(FONT_MEDIUM);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

//        System.out.println("Spinner: " + spinner.getPreferredSize());
//        System.out.println("Label: " + label.getPreferredSize());

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
        setToolTipText(toolTip);
    }

    @Override
    public void setToolTipText(String toolTip) {
        super.setToolTipText(toolTip);
        spinner.setToolTipText(toolTip);
        label.setToolTipText(toolTip);
    }

    public void refreshShortcut(int key, int modifier) {
        // Becuase there are two shortcuts for the spinner
        // Wait till the second call then add the tooltip
        if (key1 < 0) {
            key1 = key;
        } else {
            String doubleKey = "(" + AlcShortcuts.getShortcutString(key1, modifier) +
                    " " + AlcShortcuts.getShortcutString(key, modifier) + ")";
            this.setToolTipText(toolTip + " " + doubleKey);
            key1 = -1;
        }
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
