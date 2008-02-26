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
package alchemy.affect;

import alchemy.*;
import alchemy.ui.*;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * MicExpand Alchemy Module
 * @author Karl D.D. Willis
 */
public class MicExpand extends AlcModule implements AlcMicInterface {

    private AlcMicInput micIn;
    private AlcShape currentShape;
    private int activeShape = -1;
    //private int centreX,  centreY;
    private Point currentPt;
    //private byte[] buffer;
    private int[] samples;
    private boolean running = false;
    // Timing
    private long delayGap = 10;
    private boolean firstRun = true;
    private long delayTime;
    private long mouseDelayGap = 500;
    private boolean mouseFirstRun = true;
    private long mouseDelayTime;
    //
    private boolean mouseDown = false;
    private AlcSubToolBarSection subToolBarSection;
    // UI settings
    private float waveVolume;
    private float levelVolume;
    private boolean wave = true;

    public MicExpand() {

    }

    protected void setup() {
        // Create the mic input object
        micIn = new AlcMicInput(this);
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void deselect() {
        stopExpand();
        micIn = null;
    }

    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
        micIn = new AlcMicInput(this);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Volume Slider
        int initialSliderValue = 50;
        AlcSubSlider volumeSlider = new AlcSubSlider("Volume", 0, 100, initialSliderValue);
        final float waveOffset = 0.0003F;
        final float levelOffset = 0.001F;
        waveVolume = initialSliderValue * waveOffset;
        levelVolume = initialSliderValue * levelOffset;
        volumeSlider.setToolTipText("Adjust the microphone input volume");
        volumeSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = source.getValue();
                            waveVolume = value * waveOffset;
                            levelVolume = value * levelOffset;
                        //System.out.println(volume);
                        }
                    }
                });
        subToolBarSection.add(volumeSlider);

        // Level/Wave button
        AlcSubToggleButton levelWaveButton = new AlcSubToggleButton("Level/Wave", AlcUtil.getUrlPath("levelwave.png", getClassLoader()));
        levelWaveButton.setToolTipText("Toggle between level and wave mode");

        levelWaveButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        wave = !wave;
                    }
                });
        subToolBarSection.add(levelWaveButton);
    }

    private void captureSound() {

        currentShape = (AlcShape) canvas.shapes.get(activeShape);

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
//                micIn.stopMicInput();
//            }
            running = true;
            //micIn = new AlcMicInput(this, totalPoints);
            // buffer equal to the number of points
            micIn.setBuffer(totalPoints);
            micIn.startMicInput();
        }
    }

    private void alterShape() {
        //System.out.println("Alter Shape Called");
        if (currentShape != null) {

            GeneralPath currentPath = currentShape.getPath();
            Rectangle rect = currentPath.getBounds();
            Dimension windowSize = root.getWindowSize();
            if (rect.contains(0, 0, windowSize.width, windowSize.height)) {
                if (activeShape >= 0) {
                    canvas.shapes.remove(activeShape);
                    activeShape = -1;
                    currentShape = null;
                    canvas.redraw();
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
                canvas.redraw();
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

//    private void mouseInside(Point p) {
//        int currentActiveShape = -1;
//        for (int i = 0; i < canvas.shapes.size(); i++) {
//            AlcShape thisShape = (AlcShape) canvas.shapes.get(i);
//            GeneralPath currentPath = thisShape.getPath();
//            if (currentPath.contains(p)) {
//                currentActiveShape = i;
//            }
//        }
//        // Inside a shape
//        if (currentActiveShape >= 0) {
//            currentPt = p;
//            // Filter out repeat calls
//            if (currentActiveShape != activeShape) {
//                activeShape = currentActiveShape;
//                captureSound();
//            }
//        // Outside a shape
//        } else {
//            stopExpand();
//        }
//    }

    //TODO - Mic Expand reaction is slow here?
    private void mouseInside(int[] activeShapes, Point cursorLocation) {
        System.out.println(cursorLocation);
        if (activeShapes == null) {
            stopExpand();
        } else {
            int lastShape = activeShapes[activeShapes.length - 1];
            currentPt = cursorLocation;
            if (lastShape != activeShape) {
                activeShape = lastShape;
                captureSound();
            }
        }
    }

    private void stopExpand() {
        activeShape = -1;
        if (running) {
            micIn.stopMicInput();
            running = false;
        }
    }

    protected void affectShapes(int[] activeShapes, Point cursorLocation) {
        if (!mouseDown) {

            // Dispatch checking for intersection at a slow rate
            if (mouseFirstRun) {
                mouseDelayTime = System.currentTimeMillis();
                mouseInside(activeShapes, cursorLocation);
                mouseFirstRun = false;
            } else {
                if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                    mouseDelayTime = System.currentTimeMillis();
                    mouseInside(activeShapes, cursorLocation);
                }
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

//    public void mouseMoved(MouseEvent e) {
//        if (!mouseDown) {
//
//            // Dispatch checking for intersection at a slow rate
//            if (mouseFirstRun) {
//                mouseDelayTime = System.currentTimeMillis();
//                mouseInside(e.getPoint());
//                mouseFirstRun = false;
//            } else {
//                if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
//                    mouseDelayTime = System.currentTimeMillis();
//                    mouseInside(e.getPoint());
//                }
//            }
//        }
//    }
    public void bufferFull() {
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
                // If there has been enough delay
                if (System.currentTimeMillis() - delayTime >= delayGap) {
                    delayTime = System.currentTimeMillis();
                    alterShape();
                }
            }
        }
    }
}
