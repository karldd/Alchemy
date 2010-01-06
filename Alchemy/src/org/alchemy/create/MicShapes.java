/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
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
package org.alchemy.create;

import org.alchemy.core.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MicShapes.java
 * @author  Karl D.D. Willis
 */
public class MicShapes extends AlcModule implements AlcConstants {

    private AlcMicrophone micIn;
    private Point2D.Float lastPt;
    private float volume;
    private AlcToolBarSubSection subToolBarSection;
    private boolean shake = false;

    /** Creates a new instance of MicShapes */
    public MicShapes() {
    }

    @Override
    protected void setup() {
        // Create a new MicInput Object with a buffer of 10
        micIn = new AlcMicrophone(2);
        micIn.start();
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void deselect() {
        micIn.stop();
        micIn = null;
    }

    @Override
    protected void reselect() {
        micIn = new AlcMicrophone(2);
        micIn.start();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void cleared() {
        lastPt = null;
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Draw mode button
        AlcSubToggleButton drawModeButton = new AlcSubToggleButton("Draw Mode", AlcUtil.getUrlPath("drawmode.png", getClassLoader()));
        drawModeButton.setToolTipText("Change the draw mode between fatten and shake style");

        drawModeButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        shake = !shake;
                    }
                });
        subToolBarSection.add(drawModeButton);


        // Volume Slider
        int initialSliderValue = 50;
        final float levelOffset = 0.02F;
        volume = initialSliderValue * levelOffset;
        final AlcSubSlider volumeSlider = new AlcSubSlider("Volume", 0, 100, initialSliderValue);
        volumeSlider.setToolTipText("Adjust the microphone input volume");
        volumeSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!volumeSlider.getValueIsAdjusting()) {
                            int value = volumeSlider.getValue();
                            volume = value * levelOffset;
                        //System.out.println(volume);
                        }
                    }
                });
        subToolBarSection.add(volumeSlider);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        AlcShape shape = null;
        Point2D.Float p = canvas.getPenLocation();
        if (shake) {
            shape = new AlcShape(p);
        } else {
            shape = new AlcShape();
            shape.spineTo(p, getMicLevel());
        }
        canvas.createShapes.add(shape);
        lastPt = new Point2D.Float(p.x, p.y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {

            Point2D.Float p = canvas.getPenLocation();
            float micLevel = getMicLevel();
            
            // SHAKE MODE
            if (shake) {
                byte[] buffer = micIn.getBuffer();
                //int[] samples = micIn.getSamples();
                //Point2D.Float pt = rightAngle(p, lastPt,  micIn.getMicLevel() * 2);
                double thisLevel;
                if (buffer[0] == 0) {
                    thisLevel = micLevel;
                } else {
                    thisLevel = buffer[0] * micLevel;
                }

                Point2D.Float pt = AlcShape.rightAngle(p, lastPt, thisLevel * volume);
                currentShape.curveTo(pt);

            // FATTEN MODE
            } else {
                micLevel = micLevel * volume;
                // Set the min level to 1
                if (micLevel < 1) {
                    micLevel = 1;
                }
                currentShape.spineTo(p, micLevel);
            }
            canvas.redraw();
            lastPt = new Point2D.Float(p.x, p.y);
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {
            Point2D.Float p = canvas.getPenLocation();
            if (shake) {
                currentShape.lineTo(p);
            }
            canvas.redraw();
            canvas.commitShapes();
        }
    }

    private float getMicLevel() {
        return (float) micIn.getMicLevel() * 2;
    }
}
