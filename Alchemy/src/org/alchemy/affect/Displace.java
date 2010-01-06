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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 * Displace
 * @author Karl D.D. Willis
 */
public class Displace extends AlcModule implements AlcConstants {

    private AlcToolBarSubSection subToolBarSection;
    private Point oldP;
    private int speed;
    private int displacement = 7;
    private boolean mouseDown = false;

    @Override
    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void cleared() {
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);


        final AlcSubSlider speedSlider = new AlcSubSlider("Displacement", 1, 15, displacement);
        speedSlider.setToolTipText("Change the amount of displacement");
        speedSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!speedSlider.getValueIsAdjusting()) {
                            displacement = speedSlider.getValue();
                        }
                    }
                });
        subToolBarSection.add(speedSlider);
    }

    @Override
    protected void affect() {
        if (mouseDown) {
            for (int i = 0; i < canvas.createShapes.size(); i++) {
                AlcShape shape = canvas.createShapes.get(i);
                GeneralPath originalPath = shape.getPath();
                Point2D.Float lastPt = (Float) originalPath.getCurrentPoint();
                //Point2D.Float lastPt = new Point2D.Float(oldP.x, oldP.y);


                if (shape.hasSpine()) {
                    ArrayList<Point2D.Float> spine = shape.getSpine();
                    if (spine.size() > 1) {
                        for (int j = 0; j < spine.size(); j++) {
                            Point2D.Float p = spine.get(j);
                            float[] displacedMove = getAngle(p, lastPt, speed);
                            spine.set(j, new Point2D.Float(displacedMove[0], displacedMove[1]));

                        }
                        shape.createSpine();
                    }
                } else {

                    GeneralPath newPath = new GeneralPath();
                    PathIterator iterator = originalPath.getPathIterator(null);
                    float[] currentPoints = new float[6];
                    int currentPointType;

                    while (!iterator.isDone()) {
                        currentPointType = iterator.currentSegment(currentPoints);

                        switch (currentPointType) {
                            case PathIterator.SEG_MOVETO:
                                float[] displacedMove = getAngle(new Point2D.Float(currentPoints[0], currentPoints[1]), lastPt, speed);
                                //System.out.println("MOVE");
                                newPath.moveTo(displacedMove[0], displacedMove[1]);
                                break;
                            case PathIterator.SEG_LINETO:
                                //System.out.println("LINE");
                                float[] displacedLine = getAngle(new Point2D.Float(currentPoints[0], currentPoints[1]), lastPt, speed);
                                newPath.lineTo(displacedLine[0], displacedLine[1]);
                                break;
                            case PathIterator.SEG_QUADTO:
                                //System.out.println("QUAD");
                                float[] displacedQuad1 = getAngle(new Point2D.Float(currentPoints[0], currentPoints[1]), lastPt, speed);
                                float[] displacedQuad2 = getAngle(new Point2D.Float(currentPoints[2], currentPoints[3]), lastPt, speed);
                                newPath.quadTo(displacedQuad1[0], displacedQuad1[1], displacedQuad2[0], displacedQuad2[1]);
                                break;
                            case PathIterator.SEG_CUBICTO:
                                newPath.curveTo(currentPoints[0], currentPoints[1], currentPoints[2], currentPoints[3], currentPoints[4], currentPoints[5]);
                                break;
                            case PathIterator.SEG_CLOSE:
                                newPath.closePath();
                                break;
                        }
                        iterator.next();
                    }
                    shape.setPath(newPath);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        Point p = e.getPoint();
        canvas.redraw();
        oldP = p;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        speed = displacement - getCursorSpeed(p, oldP);
        oldP = p;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
        oldP = null;
    }

    private static int getCursorSpeed(Point p1, Point p2) {
        int diffX = Math.abs(p1.x - p2.x);
        int diffY = Math.abs(p1.y - p2.y);
        return diffX + diffY;
    }

    private float[] getAngle(Point2D.Float p1, Point2D.Float p2, double distance) {
        // Calculate the angle between the last point and the new point
        double angle = Math.atan2(p1.y - p2.y, p1.x - p2.x);
        // Convert the polar coordinates to cartesian
        double x = p1.getX() + (distance * Math.cos(angle));
        double y = p1.getY() + (distance * Math.sin(angle));
        float[] points = {(float) x, (float) y};
        return points;
    }
}
