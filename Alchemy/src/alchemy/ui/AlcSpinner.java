/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007 Karl D.D. Willis
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
package alchemy.ui;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;

/**
 * AlcSpinner
 * 
 * 
 */
public class AlcSpinner extends JPanel {

    protected JSpinner spinner;
    private JLabel label;

    public AlcSpinner(String name, SpinnerNumberModel numberModel) {

        // Top Left Bottom Right
        //this.setBorder(BorderFactory.createEmptyBorder(2, 8, 6, 4));
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        spinner = new JSpinner(numberModel);
        spinner.setPreferredSize(new Dimension(40, 24));
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

    }

    public void setToolTipText(String toolTip) {
        spinner.setToolTipText(toolTip);
        label.setToolTipText(toolTip);
    }
}
