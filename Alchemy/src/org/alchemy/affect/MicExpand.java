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
package org.alchemy.affect;

import org.alchemy.core.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MicExpand Alchemy Module
 * @author Karl D.D. Willis
 */
public class MicExpand extends AlcModule implements AlcMicInterface {

    private AlcMicrophone micIn;
    private AlcShape currentShape;
    private int activeShape = -1;
    //private int centreX,  centreY;
    private Point currentPt;
    //private byte[] buffer;
    private int[] samples;
    private boolean running = false;
    // Timing
    // Have decent gap here to allow the shapes to be drawn before the next call
    private long delayGap = 50;
    private boolean firstRun = true;
    private long delayTime;
//    private long mouseDelayGap = 500;
//    private boolean mouseFirstRun = true;
//    private long mouseDelayTime;
    //
    private boolean mouseDown = false;
    private AlcToolBarSubSection subToolBarSection;
    // UI settings
    private float waveVolume;
    private float levelVolume;
    private boolean wave = true;

    public MicExpand() {

    }

    @Override
    protected void setup() {
        // Create the mic input object
        micIn = new AlcMicrophone(this);
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void deselect() {
        stopExpand();
        micIn = null;
    }

    @Override
    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
        micIn = new AlcMicrophone(this);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Volume Slider
        int initialSliderValue = 50;
        final AlcSubSlider volumeSlider = new AlcSubSlider("Volume", 0, 100, initialSliderValue);
        final float waveOffset = 0.0003F;
        final float levelOffset = 0.001F;
        waveVolume = initialSliderValue * waveOffset;
        levelVolume = initialSliderValue * levelOffset;
        volumeSlider.setToolTipText("Adjust the microphone input volume");
        volumeSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!volumeSlider.getValueIsAdjusting()) {
                            int value = volumeSlider.getValue();
                            waveVolume = value * waveOffset;
                            levelVolume = value * levelOffset;
                        //System.out.println(volume);
                        }
                    }
                });
        subToolBarSection.add(volumeSlider);

        // Level/Wave button
        AlcSubToggleButton levelWaveButton = new AlcSubToggleButton("Mode", AlcUtil.getUrlPath("levelwave.png", getClassLoader()));
        levelWaveButton.setToolTipText("Toggle between wave and level mode");

        levelWaveButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        wave = !wave;
                    }
                });
        subToolBarSection.add(levelWaveButton);
    }

    private void captureSound() {
        currentShape = canvas.shapes.get(activeShape);
        if (currentShape != null) {
            // Calculate the centre of the shape
//            Rectangle size = currentShape.getShape().getBounds();
//            centreX = size.width / 2 + size.x;
//            centreY = size.height / 2 + size.y;

            // Default value
            //int totalPoints = 100;

            //System.out.println("Centres: " + centreX + " " + centreY);
            //if (wave) {
            int totalPoints = currentShape.getTotalPoints();
            //int totalPoints = 100000;
            //TotalPoints has to be an even number
            if (totalPoints % 2 != 0) {
                totalPoints += 1;
            // For some reason it likes having a few more points???
            } else {
                totalPoints += 2;
            }

            totalPoints *= 2;
            //}

//            if(running){
//                micIn.stop();
//            }
            running = true;
            //micIn = new AlcMicrophone(this, totalPoints);
            // buffer equal to the number of points
            micIn.setBuffer(totalPoints);
            micIn.start();
        }
    }

    private void alterShape() {
        //System.out.println("Alter Shape Called");
        if (currentShape != null) {

            GeneralPath currentPath = currentShape.getPath();
            Rectangle rect = currentPath.getBounds();
            Dimension windowSize = canvas.getSize();
            // If the shape is out of the window, remove it
            if (rect.contains(0, 0, windowSize.width, windowSize.height)) {
                if (activeShape >= 0) {
                    canvas.shapes.remove(activeShape);
                    activeShape = -1;
                    currentShape = null;
                    canvas.redraw(true);

                }
                stopExpand();
            //System.out.println("CONTAINED");

            } else {

                GeneralPath expandedPath = null;
                if (wave) {
                    expandedPath = expand(currentShape.getPath());
                } else {
                    double adjustedLevel = 0.9 + (micIn.getMicLevel() * levelVolume);
                    //System.out.println(adjustedLevel);

                    expandedPath = (GeneralPath) currentPath.createTransformedShape(getScaleTransform(adjustedLevel, rect));
                }
                currentShape.setPath(expandedPath);
                //canvas.setCurrentCreateShape(currentShape);
                canvas.redraw(true);
            }
        }
    }

    /* Gets a scale transform based on the sound level */
    private AffineTransform getScaleTransform(double level, Rectangle rect) {
        AffineTransform scaleTransform = new AffineTransform();

        double offsetX = rect.x + (rect.width / 2);
        double offsetY = rect.y + (rect.height / 2);

        scaleTransform.translate(offsetX, offsetY);
        scaleTransform.scale(level, level);
        scaleTransform.translate(-offsetX, -offsetY);

        return scaleTransform;
    }

    private GeneralPath expand(GeneralPath shape) {
        //if (wave) {
        //buffer = micIn.getBuffer();
        samples = micIn.getSamples();
        //} else {
        //  dist = (float) micIn.getMicLevel();
        //}
        //float distance = buffer[(int) root.math.random(0, buffer.length)];
        //float distance = (float) micIn.getMicLevel();
        //System.out.println(distance);
        //System.out.println("Buffer Length : " + buffer.length);

        GeneralPath newShape = new GeneralPath();
        PathIterator path = shape.getPathIterator(null);
        float[] pathPts = new float[6];
        int pathType;
        int pathCount = 0;

        while (!path.isDone()) {

            float dist = samples[pathCount];
            //System.out.println(dist);

            pathType = path.currentSegment(pathPts);
            switch (pathType) {
                case PathIterator.SEG_MOVETO:
                    float[] expandMove = expandPoint(pathPts, dist);
                    newShape.moveTo(expandMove[0], expandMove[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    float[] expandLine = expandPoint(pathPts, dist);
                    newShape.lineTo(expandLine[0], expandLine[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    float[] expandQuad = expandPoint(pathPts, dist);
                    newShape.quadTo(expandQuad[0], expandQuad[1], expandQuad[2], expandQuad[3]);
                    //newShape.quadTo(pathPts[0], pathPts[1], pathPts[2], pathPts[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    // Curves tend to go crazy when processed so leave em out
                    newShape.curveTo(pathPts[0], pathPts[1], pathPts[2], pathPts[3], pathPts[4], pathPts[5]);
                    //float[] expandCubic = expandPoint(pathPts, buffer[pathCount]);
                    //newShape.curveTo(expandCubic[0], expandCubic[1], expandCubic[2], expandCubic[3], expandCubic[4], expandCubic[5]);

                    break;
                case PathIterator.SEG_CLOSE:
                    newShape.closePath();
                    break;
            }
            pathCount++;
            path.next();
        }
        return newShape;
    }

    private float[] expandPoint(float[] pts, float distance) {
        float[] expandedPts = new float[pts.length];
        float adjustedDistance = distance * waveVolume;

        //System.out.println(distance);
        //distance = distance/10;
        // Points come in multiples of 2, either 2, 4 or 6
        for (int i = 0; i < pts.length / 2; i += 2) {

            float p1 = pts[i];
            float p2 = pts[i + 1];

            // Calculate the angle in radians between the centre and the point
            //double angle = Math.atan2(centreX - pts[i], centreY - pts[i + 1]);
            //double angle = Math.atan2(centreY - p2, centreX - p1);
            double angle = Math.atan2(currentPt.y - p2, currentPt.x - p1);
            // Convert the polar coordinates to cartesian
            double offsetX = adjustedDistance * Math.cos(angle);
            double offsetY = adjustedDistance * Math.sin(angle);

            //System.out.println("Offsets: "+ offsetX + " " + offsetY);

            expandedPts[i] = (float) (p1 + offsetX);
            expandedPts[i + 1] = (float) (p2 + offsetY);
        }
        return expandedPts;
    }

    private void stopExpand() {
        activeShape = -1;
        if (running) {
            micIn.stop();
            running = false;
        //canvas.commitShapes();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!mouseDown) {
            int firstShape = -1;
            // Loop through from the newest shape and find the first one the mouse is over
            for (int i = canvas.shapes.size() - 1; i >= 0; i--) {
                AlcShape thisShape = canvas.shapes.get(i);
                if (thisShape.getPath().contains(e.getPoint())) {
                    firstShape = i;
                    currentPt = e.getPoint();
                    break;
                }
            }
            if (firstShape >= 0) {
                if (firstShape != activeShape) {
                    activeShape = firstShape;
                    captureSound();
                }
            } else {
                stopExpand();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

    public void microphoneEvent() {
        // If the spacebar has just been pressed
        if (firstRun) {
            delayTime = System.currentTimeMillis();
            alterShape();
            firstRun = false;
        } else {
            if (!running) {
                stopExpand();
            // If the spacebar is down
            } else {
                if (!mouseDown) {
                    // If there has been enough delay
                    if (System.currentTimeMillis() - delayTime >= delayGap) {
                        delayTime = System.currentTimeMillis();
                        alterShape();
                    }
                } else {
                    stopExpand();
                }
            }
        }
    }
}
