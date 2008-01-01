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
 * AlcSlider
 * 
 * 
 */
public class AlcSlider extends JPanel {

    protected JSlider slider;
    AlcToolBar parent;

    public AlcSlider(AlcToolBar parent, String name, int minValue, int maxValue, int startValue) {

        this.parent = parent;

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 4));
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, startValue);
        //alphaSlider.setMajorTickSpacing(75); // sets numbers for biggest tick marks
        //alphaSlider.setMinorTickSpacing(1);  // smaller tick marks
        slider.setPaintTicks(true);     // display the ticks

        // TODO - customise this slider?  or set to number box? http://www.java2s.com/Code/Java/Swing-Components/ThumbSliderExample1.htm
        // or make a popupmenu with a slider inside?

        //alphaSlider.setUI(new BasicSliderUI(alphaSlider));
        slider.setOpaque(false);
        //alphaSlider.setBackground(Color.black);
        //alphaSlider.setForeground(Color.black);
        slider.setPreferredSize(new Dimension(100, 28));
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(slider);

        JLabel label = new JLabel(name);
        label.setFont(AlcToolBar.toolBarFont);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        //label.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        this.add(label);


    }
}
