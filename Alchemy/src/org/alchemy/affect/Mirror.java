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
package org.alchemy.affect;

import org.alchemy.core.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

/**
 * Mirror Module
 * @author Karl D.D. Willis
 */
public class Mirror extends AlcModule implements AlcConstants {

    private AlcSubToolBarSection subToolBarSection;
    private boolean horizontal = true;
    private boolean vertical = false;
    private int horizontalAxis,  verticalAxis;
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
    }

    @Override
    protected void cleared() {
        firstSelect = true;
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

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
                        canvas.setCreateMouseEvents(false);
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
        if (!selectAxis) {

            int numOfCreateShapes = canvas.createShapes.size();
            // int shapeTally = shapeCount * numOfCreateShapes;


            for (int i = 0; i < numOfCreateShapes; i++) {
                AlcShape shape = (AlcShape) canvas.createShapes.get(i);
                // Original Path with which we reflect
                GeneralPath originalPath = shape.getPath();

                if (horizontal) {
                    GeneralPath hPath = makeHorizontalReflectedShape(originalPath);
                    int index = i * shapeCount;
                    if (canvas.affectShapes.size() == index) {
                        canvas.affectShapes.add(shape.customClone(hPath));
                    } else {
                        AlcShape thisShape = ((AlcShape) canvas.affectShapes.get(index));
                        thisShape.setPath(hPath);
                        // Make sure the points tally is up to date
                        thisShape.setTotalPoints(shape.getTotalPoints());
                    }
                }

                GeneralPath vPath = null;
                if (vertical) {
                    vPath = makeVerticalReflectedShape(originalPath);
                    int index = i * shapeCount;
                    // Add 1 on if horizontal is also on
                    index += horizontal ? 1 : 0;
                    if (canvas.affectShapes.size() == index) {
                        canvas.affectShapes.add(shape.customClone(vPath));
                    } else {
                        AlcShape thisShape = ((AlcShape) canvas.affectShapes.get(index));
                        thisShape.setPath(vPath);
                        // Make sure the points tally is up to date
                        thisShape.setTotalPoints(shape.getTotalPoints());
                    }
                }
                if (horizontal && vertical) {
                    GeneralPath hvPath = makeHorizontalReflectedShape(vPath);
                    int index = i * shapeCount + 2;
                    if (canvas.affectShapes.size() == index) {
                        canvas.affectShapes.add(shape.customClone(hvPath));
                    } else {
                        AlcShape thisShape = ((AlcShape) canvas.affectShapes.get(index));
                        thisShape.setPath(hvPath);
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
        horizontalAxis = size.width / 2;
        verticalAxis = size.height / 2;
    }

    private void countShapes() {
        // Add one for every axis currently on
        // And an extra one if they are both on
        shapeCount = 0;
        shapeCount += horizontal ? 1 : 0;
        shapeCount += vertical ? 1 : 0;
        shapeCount += horizontal && vertical ? 1 : 0;
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
                AlcShape axis = new AlcShape(line, new Color(0, 255, 255), 100, LINE, 1);
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
            horizontalAxis = e.getX();
            verticalAxis = e.getY();
            selectAxis = false;
            canvas.setCreateMouseEvents(true);
            canvas.redraw();
        }

    }
}
