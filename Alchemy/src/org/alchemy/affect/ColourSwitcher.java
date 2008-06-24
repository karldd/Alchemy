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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 *
 * ColourSwitcher.java
 */
public class ColourSwitcher extends AlcModule {

    private boolean switchColour = true;
    private boolean switchAlpha = true;
    private AlcToolBarSubSection subToolBarSection;
    private Color baseColour = null;
    private float[] baseHSB = new float[3];
    private int baseRange = 50;

    public ColourSwitcher() {
    }

    @Override
    public void setup() {
        baseColour = canvas.getColour();
        Color.RGBtoHSB(baseColour.getRed(), baseColour.getGreen(), baseColour.getBlue(), baseHSB);
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Switch Alpha
        AlcSubToggleButton alphaButton = new AlcSubToggleButton("Transparency", AlcUtil.getUrlPath("transparency.png", getClassLoader()));
        alphaButton.setToolTipText("Turn transparency changing on");

        alphaButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        switchAlpha = !switchAlpha;
                    }
                });
        alphaButton.setSelected(switchAlpha);
        subToolBarSection.add(alphaButton);

        // Hue Slider
        final AlcSubSlider rangeSlider = new AlcSubSlider("Range", 0, 100, baseRange);
        rangeSlider.setToolTipText("Set the range for the colour to diverge from");
        rangeSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!rangeSlider.getValueIsAdjusting()) {
                            baseRange = rangeSlider.getValue();
                        }
                    }
                });


        //  Colour
        final AlcSubToggleButton baseColourButton = new AlcSubToggleButton("Colour", AlcUtil.getUrlPath("colour.png", getClassLoader()));
        baseColourButton.setToolTipText("Set the base colour to diverge from");

        // Action called when the user sets the colour
        final ActionListener okAction = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                baseColour = colourChooser.getColor();
                subToolBarSection.add(rangeSlider);
                subToolBarSection.revalidate();
                switchColour = true;

                Color.RGBtoHSB(baseColour.getRed(), baseColour.getGreen(), baseColour.getBlue(), baseHSB);
            }
        };

        // Action called when the user sets the colour
        final ActionListener cancelAction = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                baseColourButton.setSelected(false);
                switchColour = false;
            }
        };

        baseColourButton.setSelected(switchColour);
        subToolBarSection.add(baseColourButton);
        subToolBarSection.add(rangeSlider);


        // Shows the colour chooser
        baseColourButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (!switchColour) {
                            colourChooser.show(okAction, cancelAction);
                        } else {
                            subToolBarSection.remove(rangeSlider);
                            subToolBarSection.revalidate();
                            switchColour = false;
                        }

                    }
                });

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
        Color randomColour = null;
        if (baseColour == null) {

            int r = (int) math.random(0, 255);
            int g = (int) math.random(0, 255);
            int b = (int) math.random(0, 255);
            randomColour = new Color(r, g, b);

        } else {

            // Work out the half range
            float halfRange = (baseRange / 100F) / 2F;

            float h = divergeNumber(baseHSB[0], halfRange);
            float s = divergeNumber(baseHSB[1], halfRange);
            float b = divergeNumber(baseHSB[2], halfRange);

            randomColour = Color.getHSBColor(h, s, b);
        //System.out.println(randomColour);

        }
        return randomColour;
    }

    private Color getRandomAlphaColour() {
        Color c = getRandomColour();
        int a = (int) math.random(0, 255);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    /** Diverge a number within a certain range */
    private float divergeNumber(float num, float range) {
        float min = num - range;
        float max = num + range;
        // Pad out the range in the other direction if out of bounds
        if (min < 0F) {
            max += min * -1F;
            min = 0F;
        } else if (max > 1F) {
            min -= (max - 1F);
            max = 1F;
        }

        float random = math.random(min, max);
        //System.out.println(num + " > " + random + " : " + min + " " + max);
        //System.out.println(num + " > " + random);
        return random;
    }
}
