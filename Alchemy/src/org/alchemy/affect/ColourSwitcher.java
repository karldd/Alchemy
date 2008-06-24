/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2008 Karl D.D. Willis
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
package org.alchemy.affect;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import org.alchemy.core.*;

/**
 *
 * ColourSwitcher.java
 */
public class ColourSwitcher extends AlcModule {

    private boolean switchColour = false;
    private boolean switchAlpha = true;
    private AlcToolBarSubSection subToolBarSection;

    public ColourSwitcher() {
    }

    @Override
    public void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Switch Colour
        AlcSubToggleButton colourButton = new AlcSubToggleButton("Colour", AlcUtil.getUrlPath("vertical.png", getClassLoader()));
        colourButton.setToolTipText("Change the colour");

        colourButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        switchColour = !switchColour;
                    }
                });
        colourButton.setSelected(switchColour);
        subToolBarSection.add(colourButton);

        // Switch Alpha
        AlcSubToggleButton alphaButton = new AlcSubToggleButton("Transparency", AlcUtil.getUrlPath("horizontal.png", getClassLoader()));
        alphaButton.setToolTipText("Change the transparency");

        alphaButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        switchAlpha = !switchAlpha;
                    }
                });
        alphaButton.setSelected(switchAlpha);
        subToolBarSection.add(alphaButton);

        //Alchemy.colourChooser.getColor()
        // Base Colour
        AlcSubToggleButton baseColourButton = new AlcSubToggleButton("Base Colour", AlcUtil.getUrlPath("horizontal.png", getClassLoader()));
        baseColourButton.setToolTipText("Set the base colour to randomise from");

        // Action called when the user sets the colour
        final ActionListener colorAction = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                canvas.setColour(colourChooser.getColor());
                System.out.println(colourChooser.getColor());
                //Alchemy.canvas.setColour(Alchemy);
                //refreshColourButton();
            }
        };

        // Shows the colour chooser
        baseColourButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        colourChooser.show(colorAction, null);
                    }
                });
        //baseColourButton.setSelected(false);
        subToolBarSection.add(baseColourButton);

    }

    @Override
    public void mousePressed(MouseEvent e) {

        Color oldColour = canvas.getColour();
        Color newColour = null;

        if (switchAlpha && switchColour) {

            newColour = getRandomAlphaColour();
            canvas.setAlpha(newColour.getAlpha());
            canvas.setColour(newColour);

        } else if (switchColour) {

            newColour = getRandomColour();
            canvas.setColour(newColour);

        } else if (switchAlpha) {
            int alpha = (int) math.random(0, 255);
            canvas.setAlpha(alpha);
            newColour = new Color(oldColour.getRed(), oldColour.getGreen(), oldColour.getBlue(), alpha);
        }

        if (newColour != null) {
            for (int i = 0; i < canvas.createShapes.size(); i++) {
                canvas.createShapes.get(i).setAlphaColour(newColour);
            }
            for (int j = 0; j < canvas.affectShapes.size(); j++) {
                canvas.affectShapes.get(j).setAlphaColour(newColour);
            }
        }
    }

    private Color getRandomColour() {
        int r = (int) math.random(0, 255);
        int g = (int) math.random(0, 255);
        int b = (int) math.random(0, 255);
        return new Color(r, g, b);
    }

    private Color getRandomAlphaColour() {
        int r = (int) math.random(0, 255);
        int g = (int) math.random(0, 255);
        int b = (int) math.random(0, 255);
        int a = (int) math.random(0, 255);
        return new Color(r, g, b, a);
    }
}
