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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;

/**
 * AlcSlider
 * 
 * 
 */
public class AlcSlider extends JPanel {

    protected JSlider slider;

    public AlcSlider(String name, int minValue, int maxValue, int startValue) {

        // Top Left Bottom Right
        //this.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 4));
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, startValue);
        //alphaSlider.setMajorTickSpacing(75); // sets numbers for biggest tick marks
        slider.setMinorTickSpacing(25);  // smaller tick marks
        slider.setPaintTicks(true);     // display the ticks
        //slider.setPaintLabels(false);
        // TODO - customise this slider?  or set to number box? http://www.java2s.com/Code/Java/Swing-Components/ThumbSliderExample1.htm
        // or make a popupmenu with a slider inside?

        //alphaSlider.setUI(new BasicSliderUI(alphaSlider));
        slider.setOpaque(false);
        // This has to be set to avoid the ticks bg being default coloured
        slider.setBackground(new Color(225,225,225));
        //slider.setForeground(Color.black);
        slider.setPreferredSize(new Dimension(85, 28));
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(slider);

        JLabel label = new JLabel(name);
        label.setFont(AlcToolBar.toolBarFont);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        //label.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        this.add(label);


    }
}
