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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Mirror Module
 * @author Karl D.D. Willis
 */
public class Mirror extends AlcModule implements AlcConstants {

    private AlcToolBarSubSection subToolBarSection;
    private boolean horizontal = true;
    private boolean vertical = false;
    private int baseHorizontalAxis, horizontalAxis, baseVerticalAxis, verticalAxis;
    private boolean selectAxis = false;
    private boolean firstSelect = false;
    private int shapeCount;

    /** Creates a new instance of Mirror */
    public Mirror() {
    }

    @Override
    protected void setup() {
        // Set the initial axis to the middle
        resetAxis();
        countShapes();
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
        resetAxis();
    }

    @Override
    protected void cleared() {
        firstSelect = true;
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Horizontal button
        AlcSubToggleButton horizontalButton = new AlcSubToggleButton("Horizontal", AlcUtil.getUrlPath("horizontal.png", getClassLoader()));
        horizontalButton.setSelected(true);
        horizontalButton.setToolTipText("Mirror the horizontal axis");

        horizontalButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        horizontal = !horizontal;
                        countShapes();
                    }
                });
        subToolBarSection.add(horizontalButton);

        // Vertical button
        AlcSubToggleButton verticalButton = new AlcSubToggleButton("Vertical", AlcUtil.getUrlPath("vertical.png", getClassLoader()));
        verticalButton.setToolTipText("Mirror the vertical axis");

        verticalButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        vertical = !vertical;
                        countShapes();
                    }
                });
        subToolBarSection.add(verticalButton);

        // Move Axis
        AlcSubButton moveAxisButton = new AlcSubButton("Move", AlcUtil.getUrlPath("move.png", getClassLoader()));
        moveAxisButton.setToolTipText("Move the location of the reflection axis");
        moveAxisButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        selectAxis = true;
                        firstSelect = true;
                        canvas.setCreateEvents(false);
                        canvas.commitShapes();
                    }
                });
        subToolBarSection.add(moveAxisButton);




        // Reset Button
        AlcSubButton resetButton = new AlcSubButton("Reset", AlcUtil.getUrlPath("reset.png", getClassLoader()));
        resetButton.setToolTipText("Reset the reflection axis to the centre");
        resetButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        resetAxis();
                    }
                });
        subToolBarSection.add(resetButton);

    }

    @Override
    protected void affect() {
        
        //If the canvas is zoomed, calculate the "zoomed" axis location
        if(canvas.isCanvasZoomed()){
            horizontalAxis = (int)canvas.calculateZoomedX(baseHorizontalAxis);
            verticalAxis = (int)canvas.calculateZoomedY(baseVerticalAxis);
        //Canvas is not zoomed, use the base axis location
        }else{
            horizontalAxis = baseHorizontalAxis;
            verticalAxis = baseVerticalAxis;
        }
        
        if (!selectAxis) {

            int numOfCreateShapes = canvas.createShapes.size();
            // int shapeTally = shapeCount * numOfCreateShapes;


            for (int i = 0; i < numOfCreateShapes; i++) {
                AlcShape shape = canvas.createShapes.get(i);
                // Original Path with which we reflect
                GeneralPath originalPath = shape.getPath();
                ArrayList<Point2D.Float> spine = shape.getSpine();

                if (horizontal) {
                    GeneralPath hPath = makeHorizontalReflectedShape(originalPath);
                    int index = i * shapeCount;

                    // ADD
                    if (canvas.affectShapes.size() == index) {
                        AlcShape cloneShape = null;
                        if (shape.hasSpine()) {
                            // Create a flipped spine and use a shallow copy the spine width
                            cloneShape = shape.customClone(makeHorizontalReflectedSpine(spine), shape.getSpineWidth());
                        } else {
                            cloneShape = shape.customClone(hPath);
                        }

                        // Make sure there is no transparency when the background is on
//                        if (canvas.isBackgroundColorActive()) {
//                            cloneShape.setAlpha(255);
//                        }
                        canvas.affectShapes.add(cloneShape);

                        // Also flip the gradient
                        GradientPaint gp = cloneShape.getGradientPaint();
                        if (gp != null) {
                            cloneShape.setGradientPaint(makeHorizontalReflectedGradientPaint(gp));
                        }

                    // REPLACE
                    } else {
                        AlcShape thisShape = (canvas.affectShapes.get(index));
                        thisShape.setPath(hPath);
                        cloneAttributes(shape, thisShape);
                        
                        
                        if (thisShape.hasSpine()) {
                            thisShape.setSpine(makeHorizontalReflectedSpine(spine));
                        }
                        // Make sure the points tally is up to date
                        thisShape.setTotalPoints(shape.getTotalPoints());
                    }
                }

                // Keep these handy incase we need to do another flip
                GeneralPath vPath = null;
                ArrayList<Point2D.Float> vSpine = null;
                GradientPaint vPaint = null;
                if (vertical) {
                    vPath = makeVerticalReflectedShape(originalPath);
                    int index = i * shapeCount;
                    // Add 1 on if horizontal is also on
                    index += horizontal ? 1 : 0;

                    // ADD
                    if (canvas.affectShapes.size() == index) {

                        AlcShape cloneShape = null;
                        if (shape.hasSpine()) {
                            vSpine = makeVerticalReflectedSpine(spine);
                            // Create a flipped spine and use a shallow copy the spine width
                            cloneShape = shape.customClone(vSpine, shape.getSpineWidth());
                        } else {
                            cloneShape = shape.customClone(vPath);
                        }

                        // Make sure there is no transparency when the background is on
//                        if (canvas.isBackgroundColorActive()) {
//                            cloneShape.setAlpha(255);
//                        }

                        // Also flip the gradient
                        GradientPaint gp = cloneShape.getGradientPaint();
                        if (gp != null) {
                            vPaint = makeVerticalReflectedGradientPaint(gp);
                            cloneShape.setGradientPaint(vPaint);
                        }

                        canvas.affectShapes.add(cloneShape);

                    // REPLACE    
                    } else {
                        AlcShape thisShape = (canvas.affectShapes.get(index));
                        thisShape.setPath(vPath);
                        cloneAttributes(shape, thisShape);

                        if (thisShape.hasSpine()) {
                            vSpine = makeVerticalReflectedSpine(spine);
                            thisShape.setSpine(vSpine);
                        }
                        // Make sure the points tally is up to date
                        thisShape.setTotalPoints(shape.getTotalPoints());
                    }
                }
                if (horizontal && vertical) {
                    GeneralPath hvPath = makeHorizontalReflectedShape(vPath);
                    int index = i * shapeCount + 2;

                    // ADD
                    if (canvas.affectShapes.size() == index) {
                        AlcShape cloneShape = null;
                        if (shape.hasSpine() && vSpine != null) {
                            // Create a flipped spine and use a shallow copy the spine width
                            cloneShape = shape.customClone(makeHorizontalReflectedSpine(vSpine), shape.getSpineWidth());
                        } else {
                            cloneShape = shape.customClone(hvPath);
                        }

                        // Make sure there is no transparency when the background is on
//                        if (canvas.isBackgroundColorActive()) {
//                            cloneShape.setAlpha(255);
//                        }

                        // Also flip the gradient
                        GradientPaint gp = cloneShape.getGradientPaint();
                        if (gp != null && vPaint != null) {
                            cloneShape.setGradientPaint(makeHorizontalReflectedGradientPaint(vPaint));
                        }
                        canvas.affectShapes.add(cloneShape);

                    // REPLACE    
                    } else {
                        AlcShape thisShape = (canvas.affectShapes.get(index));
                        thisShape.setPath(hvPath);
                        cloneAttributes(shape, thisShape);

                        if (thisShape.hasSpine() && vSpine != null) {
                            thisShape.setSpine(makeHorizontalReflectedSpine(vSpine));
                        }

                        // Make sure the points tally is up to date
                        thisShape.setTotalPoints(shape.getTotalPoints());
                    }
                }
            }
        }
    }

    /** Make a shape reflected through the horizontal axis */
    private GeneralPath makeHorizontalReflectedShape(GeneralPath original) {
        AffineTransform horizontalReflection = getHorizontalReflection(horizontalAxis);
        GeneralPath reflectedPath = (GeneralPath) original.createTransformedShape(horizontalReflection);
        return reflectedPath;
    }

    /** Make a spine reflected through the horizontal axis */
    private ArrayList<Point2D.Float> makeHorizontalReflectedSpine(ArrayList<Point2D.Float> spine) {
        // Create a new array of flipped points
        ArrayList<Point2D.Float> hSpine = new ArrayList<Point2D.Float>(1000);
        for (int j = 0; j < spine.size(); j++) {
            Point2D.Float p = spine.get(j);
            // Flip it over
            float x = horizontalAxis - (p.x - horizontalAxis);
            Point2D.Float newP = new Point2D.Float(x, p.y);
            hSpine.add(newP);
        }
        return hSpine;
    }

    /** Make a GradientPaint reflected through the horizontal axis */
    private GradientPaint makeHorizontalReflectedGradientPaint(GradientPaint gp) {
        float x1 = horizontalAxis - ((float) gp.getPoint1().getX() - horizontalAxis);
        float x2 = horizontalAxis - ((float) gp.getPoint2().getX() - horizontalAxis);
        GradientPaint newGp = new GradientPaint(
                x1,
                (float) gp.getPoint1().getY(),
                gp.getColor1(),
                x2,
                (float) gp.getPoint2().getY(),
                gp.getColor2());
        return newGp;
    }

    /** Updates the horizontal reflection transform based on the current window width */
    private AffineTransform getHorizontalReflection(int axis) {
        AffineTransform horizontalReflection = new AffineTransform();
        // Move the reflection into place
        horizontalReflection.translate(axis * 2, 0);
        // Reflect it using a negative scale
        horizontalReflection.scale(-1, 1);
        //horizontalReflection.setToTranslation(-axis, 0);
        return horizontalReflection;
    }

    /** Make a shape reflected through the vertical axis */
    private GeneralPath makeVerticalReflectedShape(GeneralPath original) {
        AffineTransform verticalReflection = getVerticalReflection(verticalAxis);
        GeneralPath reflectedPath = (GeneralPath) original.createTransformedShape(verticalReflection);
        return reflectedPath;
    }

    /** Make a spine reflected through the vertical axis */
    private ArrayList<Point2D.Float> makeVerticalReflectedSpine(ArrayList<Point2D.Float> spine) {
        // Create a new array of flipped points
        ArrayList<Point2D.Float> vSpine = new ArrayList<Point2D.Float>(1000);
        for (int j = 0; j < spine.size(); j++) {
            Point2D.Float p = spine.get(j);
            // Flip it over
            float y = verticalAxis - (p.y - verticalAxis);
            Point2D.Float newP = new Point2D.Float(p.x, y);
            vSpine.add(newP);
        }
        return vSpine;
    }

    /** Make a GradientPaint reflected through the vertical axis */
    private GradientPaint makeVerticalReflectedGradientPaint(GradientPaint gp) {
        float y1 = verticalAxis - ((float) gp.getPoint1().getY() - verticalAxis);
        float y2 = verticalAxis - ((float) gp.getPoint2().getY() - verticalAxis);
        GradientPaint newGp = new GradientPaint(
                (float) gp.getPoint1().getX(),
                y1,
                gp.getColor1(),
                (float) gp.getPoint2().getX(),
                y2,
                gp.getColor2());
        return newGp;
    }

    /** Updates the vertical reflection transform based on the current window width */
    private AffineTransform getVerticalReflection(int axis) {
        AffineTransform verticalReflection = new AffineTransform();
        // Move the reflection into place
        verticalReflection.translate(0, axis * 2);
        // Reflect it using a negative scale
        verticalReflection.scale(1, -1);
        return verticalReflection;
    }

    private void resetAxis() {
        Dimension size = canvas.getSize();
        baseHorizontalAxis = size.width / 2;
        baseVerticalAxis = size.height / 2;
    }

    private void countShapes() {
        // Add one for every axis currently on
        // And an extra one if they are both on
        shapeCount = 0;
        shapeCount += horizontal ? 1 : 0;
        shapeCount += vertical ? 1 : 0;
        shapeCount += horizontal && vertical ? 1 : 0;
    }

    /** Clone attributes of this shape */
    private void cloneAttributes(AlcShape shape, AlcShape cloneShape) {
//        cloneShape.setAlpha(shape.getAlpha());
        cloneShape.setStyle(shape.getStyle());
//        cloneShape.setLineWidth(shape.getLineWidth());
//        cloneShape.setColor(shape.getColor());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (selectAxis) {

            Dimension size = canvas.getSize();

            GeneralPath line = new GeneralPath(new Line2D.Float(e.getX(), 0, e.getX(), size.height));
            line.append(new Line2D.Float(0, e.getY(), size.width, e.getY()), false);

            if (firstSelect) {
                firstSelect = false;
                canvas.guideShapes.clear();
                AlcShape axis = new AlcShape(line, new Color(0, 255, 255), 100, STYLE_STROKE, 1);
                canvas.guideShapes.add(axis);
            } else {
                if (canvas.getCurrentGuideShape() != null) {
                    canvas.getCurrentGuideShape().setPath(line);
                }
            }
            canvas.redraw();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectAxis) {
            canvas.removeCurrentGuideShape();
            baseHorizontalAxis = e.getX();
            baseVerticalAxis = e.getY();
            selectAxis = false;
            canvas.setCreateEvents(true);
            canvas.redraw();
        }

    }
}