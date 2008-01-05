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
package alchemy.affect;

import alchemy.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * MicExpand
 * 
 * 
 */
public class MicExpand extends AlcModule implements AlcMicInterface {

    private AlcMicInput micIn;
    private AlcShape currentShape;
    private int centreX,  centreY;
    private byte[] buffer;
    private boolean spaceDown = false;
    // Timing
    private boolean firstRun = true;
    private long delayGap = 40;
    private long delayTime;

    public MicExpand() {

    }

    @Override
    public void setup() {
    }

    @Override
    public void deselect() {
        micIn.stopMicInput();
    //micIn = null;
    }

    @Override
    public void reselect() {
        setup();
    }

    private void captureSound() {
        if (canvas.createShapes.size() == 0) {
            if (canvas.shapes.size() > 0) {
                // Clone the shape then remove the original
                currentShape = (AlcShape) canvas.getCurrentShape().clone();
                canvas.removeCurrentShape();
            }
        } else {
            currentShape = canvas.getCurrentCreateShape();
        }


        if (currentShape != null) {

            // Calculate the centre of the shape
            Rectangle size = currentShape.getShape().getBounds();
            centreX = size.width / 2 + size.x;
            centreY = size.height / 2 + size.y;

            //System.out.println("Centres: " + centreX + " " + centreY);

            int totalPoints = currentShape.getTotalPoints();
            // TotalPoints has to be an even number
            if (totalPoints % 2 != 0) {
                totalPoints += 1;
                System.out.println("One Added to totalPoints");
            } else {
                totalPoints += 2;
            }
            // Create a new MicInput Object with a buffer equal to the number of points

            micIn = new AlcMicInput(this, totalPoints);
            micIn.startMicInput();
        }
    }

    private void alterShape() {
        //System.out.println("Alter Shape Called");
        if (currentShape != null) {
            System.out.println(currentShape);
            GeneralPath expandedPath = expand(currentShape.getShape());
            //AlcShape tempShape = new AlcShape(expandedShape, currentShape.getColour(), currentShape.getAlpha(), currentShape.getStyle(), currentShape.getLineWidth());
            //tempShape.setShape(expandedShape);
            //currentShape.setShape(expandedShape);
            currentShape.setShape(expandedPath);
            canvas.setCurrentCreateShape(currentShape);
            canvas.redraw();
        }
    }

    private GeneralPath expand(GeneralPath shape) {
        buffer = micIn.getBuffer();
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
            pathType = path.currentSegment(pathPts);
            switch (pathType) {
                case PathIterator.SEG_MOVETO:
                    float[] expandMove = expandPoint(pathPts, buffer[pathCount]);
                    newShape.moveTo(expandMove[0], expandMove[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    float[] expandLine = expandPoint(pathPts, buffer[pathCount]);
                    newShape.lineTo(expandLine[0], expandLine[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    float[] expandQuad = expandPoint(pathPts, buffer[pathCount]);
                    newShape.quadTo(expandQuad[0], expandQuad[1], expandQuad[2], expandQuad[3]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    float[] expandCubic = expandPoint(pathPts, buffer[pathCount]);
                    newShape.curveTo(expandCubic[0], expandCubic[1], expandCubic[2], expandCubic[3], expandCubic[4], expandCubic[5]);
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
        //System.out.println(distance);
        //distance = distance/10;
        // Points come in multiples of 2, either 2, 4 or 6
        for (int i = 0; i < pts.length / 2; i += 2) {
            // Calculate the angle in radians between the centre and the point
            //double angle = Math.atan2(centreX - pts[i], centreY - pts[i + 1]);
            double angle = Math.atan2(centreY - pts[i + 1], centreX - pts[i]);
            // Convert the polar coordinates to cartesian
            double offsetX = distance * Math.cos(angle);
            double offsetY = distance * Math.sin(angle);

            //System.out.println("Offsets: "+ offsetX + " " + offsetY);

            expandedPts[i] = (float) (pts[i] + offsetX);
            expandedPts[i + 1] = (float) (pts[i + 1] + offsetY);
        }
        return expandedPts;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    //captureSound();
    }

    public void bufferFull() {
        // If the spacebar has just been pressed
        if (firstRun) {
            delayTime = System.currentTimeMillis();
            alterShape();
            firstRun = false;
        } else {
            // If the spacebar comes up
            if (!spaceDown) {
                deselect();
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

    // KEY EVENTS
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spaceDown = false;
            canvas.commitShapes();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Filter out the Key repeats
            if (!spaceDown) {
                System.out.println("SPACE BAR");
                spaceDown = true;
                firstRun = true;
                captureSound();
            }
        }
    }
}
