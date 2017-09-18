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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * AlcSubComboBox.java
 */
public class AlcSubComboBox extends JPanel implements AlcConstants {

    private JComboBox comboBox;

    public AlcSubComboBox(String name) {

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        this.setOpaque(false);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        comboBox = new JComboBox();
        comboBox.setFont(FONT_SMALL);
        if (Alchemy.OS == OS_MAC) {
            comboBox.setPreferredSize(new Dimension(85, 23));
        } else {
            comboBox.setPreferredSize(new Dimension(80, 16));
        }
        //comboBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.add(comboBox);

        if (name != null) {
            JLabel label = new JLabel(name);
            label.setFont(FONT_SMALL);
            label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
            //label.setPreferredSize(new Dimension(label.getPreferredSize().width, 26));
            this.add(label);
        }

    }

    public void addItem(Object object) {
        comboBox.addItem(object);
    }

    public void removeAllItems() {
        comboBox.removeAllItems();
    }

    public void addActionListener(ActionListener l) {
        comboBox.addActionListener(l);
    }

    public void setSelectedIndex(int index) {
        comboBox.setSelectedIndex(index);
    }

    public int getSelectedIndex() {
        return comboBox.getSelectedIndex();
    }
}
