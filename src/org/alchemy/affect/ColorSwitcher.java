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
 * ColorSwitcher.java
 */
public class ColorSwitcher extends AlcModule {

    private boolean switchColor = true;
    private boolean switchAlpha = true;
    private boolean constantColor = false;
    private AlcToolBarSubSection subToolBarSection;
    private Color baseColor = null;
    private float[] baseHSB = new float[3];
    private int baseRange = 50;    // Timing
    private long mouseDelayGap = 50;
    private long mouseDelayTime;

    public ColorSwitcher() {
    }

    @Override
    public void setup() {
        baseColor = canvas.getColor();
        Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), baseHSB);
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
        rangeSlider.setToolTipText("Set the range for the color to diverge from");
        rangeSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!rangeSlider.getValueIsAdjusting()) {
                            baseRange = rangeSlider.getValue();
                        }
                    }
                });


        //  Color
        final AlcSubToggleButton baseColorButton = new AlcSubToggleButton("Base Color", AlcUtil.getUrlPath("color.png", getClassLoader()));
        baseColorButton.setToolTipText("Set the base color to diverge from");

        // Action called when the user sets the color
        final ActionListener okAction = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                baseColor = colorSelector.getColor();
                //subToolBarSection.add(rangeSlider);
                //subToolBarSection.revalidate();
                switchColor = true;

                Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), baseHSB);
            }
        };

        // Action called when the user sets the color
        final ActionListener cancelAction = new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                baseColorButton.setSelected(false);
                switchColor = false;
            }
        };

        baseColorButton.setSelected(switchColor);
        subToolBarSection.add(baseColorButton);
        subToolBarSection.add(rangeSlider);


        // Shows the color chooser
        baseColorButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (!switchColor) {
                            colorSelector.show(okAction, cancelAction);
                        } else {
                            //subToolBarSection.remove(rangeSlider);
                            //subToolBarSection.revalidate();
                            switchColor = false;
                        }
                    }
                });

        // Constant switching
        AlcSubToggleButton constantButton = new AlcSubToggleButton("Constant", AlcUtil.getUrlPath("constant.png", getClassLoader()));
        constantButton.setToolTipText("Switch colors constantly");

        constantButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        constantColor = !constantColor;
                    }
                });
        constantButton.setSelected(constantColor);
        subToolBarSection.add(constantButton);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        mouseDelayTime = System.currentTimeMillis();
 
        Color newColor = getNewColor();

        if (newColor != null) {
            for (int i = 0; i < canvas.createShapes.size(); i++) {
                canvas.createShapes.get(i).setAlphaColor(canvas.getColor());
                canvas.createShapes.get(i).setAlpha(canvas.getAlpha());
            }
            for (int j = 0; j < canvas.affectShapes.size(); j++) {
                canvas.affectShapes.get(j).setAlphaColor(canvas.getColor());
                canvas.affectShapes.get(j).setAlpha(canvas.getAlpha());
            }
        }
        canvas.refreshColorButtonRelay();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (constantColor) {
            if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                mouseDelayTime = System.currentTimeMillis();
                
                Color newColor = getNewColor();

                if (newColor != null) {

//                    if (canvas.hasCreateShapes()) {
//                        canvas.getCurrentCreateShape().setAlphaColor(newColor);
//                    }
//                    if (canvas.getCurrentAffectShape() != null) {
//                        canvas.getCurrentAffectShape().setAlphaColor(newColor);
//                    }
                    
                    for (int i = 0; i < canvas.createShapes.size(); i++) {
                        canvas.createShapes.get(i).setAlphaColor(canvas.getColor());
                        canvas.createShapes.get(i).setAlpha(canvas.getAlpha());
                    }
                    for (int j = 0; j < canvas.affectShapes.size(); j++) {
                        canvas.affectShapes.get(j).setAlphaColor(canvas.getColor());
                        canvas.affectShapes.get(j).setAlpha(canvas.getAlpha());
                    }

                }

            }
        }
    }

    private Color getNewColor() {
        Color oldColor = canvas.getColor();
        Color newColor = null;

        if (switchAlpha && switchColor) {

            newColor = getRandomAlphaColor();
            canvas.setAlpha(newColor.getAlpha());
            canvas.setColor(newColor);

        } else if (switchColor) {

            newColor = getRandomColor();
            canvas.setColor(newColor);

        } else if (switchAlpha) {
            int alpha = (int) math.random(0, 255);
            canvas.setAlpha(alpha);
            newColor = new Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), alpha);
        }
        return newColor;
    }

    private Color getRandomColor() {
        Color randomColor = null;
        if (baseColor == null) {

            int r = (int) math.random(0, 255);
            int g = (int) math.random(0, 255);
            int b = (int) math.random(0, 255);
            randomColor = new Color(r, g, b);

        } else {

            // Work out the half range
            float halfRange = (baseRange / 100F) / 2F;

            float h = divergeNumber(baseHSB[0], halfRange);
            float s = divergeNumber(baseHSB[1], halfRange);
            float b = divergeNumber(baseHSB[2], halfRange);

            randomColor = Color.getHSBColor(h, s, b);
        //System.out.println(randomColor);

        }
        return randomColor;
    }

    private Color getRandomAlphaColor() {
        Color c = getRandomColor();
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
