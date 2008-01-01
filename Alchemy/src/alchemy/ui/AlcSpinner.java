/*
 *   Part of the Alchemy project - http://al.chemy.org
 * 
 *   Copyright (c) 2007 Karl D.D. Willis
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General
 *   Public License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA  02111-1307  USA
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
    AlcToolBar parent;

    public AlcSpinner(AlcToolBar parent, String name, SpinnerNumberModel numberModel) {

        this.parent = parent;
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(2, 4, 6, 4));
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

        JLabel label = new JLabel(name);
        label.setFont(AlcToolBar.toolBarFont);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        this.add(label);

    }
}
