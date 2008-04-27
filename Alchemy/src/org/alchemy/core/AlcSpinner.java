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
import java.awt.Dimension;
import javax.swing.*;

/**
 * AlcSpinner
 * 
 * 
 */
class AlcSpinner extends JPanel implements AlcShortcutInterface {

    protected JSpinner spinner;
    private JLabel label;
    private String toolTip;
    private int key1 = -1;

    AlcSpinner(String name, SpinnerNumberModel numberModel, String toolTip) {

        this.toolTip = toolTip;
        // Top Left Bottom Right
        //this.setBorder(BorderFactory.createEmptyBorder(2, 8, 6, 4));
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        spinner = new JSpinner(numberModel);
        spinner.setPreferredSize(new Dimension(50, 25));
        spinner.setOpaque(false);
        spinner.setBackground(AlcToolBar.toolBarBgColour);
        // Top Left Bottom Right
        spinner.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        spinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(spinner);

        label = new JLabel(name);
        label.setFont(AlcToolBar.toolBarFont);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        this.add(label);

        setToolTipText(toolTip);

    }

    @Override
    public void setToolTipText(String toolTip) {
        super.setToolTipText(toolTip);
        spinner.setToolTipText(toolTip);
        // Hack here for Swing bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4680204
        ((JSpinner.NumberEditor) spinner.getEditor()).getTextField().setToolTipText(toolTip);
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
}
