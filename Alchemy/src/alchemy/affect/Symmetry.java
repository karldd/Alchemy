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
import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Symmetry Module
 * @author Karl D.D. Willis
 */
public class Symmetry extends AlcModule implements AlcConstants {

    private AlcSubToolBarSection subToolBarSection;
    private AffineTransform reflectionAxis = null;
    private boolean horizontal = true;
    private boolean defaultReflection = true;
    private boolean selectAxis = false;
    private boolean firstSelect = false;
    private ArrayList xReflections = new ArrayList(20);
    private ArrayList yReflections = new ArrayList(20);

    /** Creates a new instance of Symmetry */
    public Symmetry() {
    }

    protected void setup() {
        // Add a default reflection
        xReflections.add(new Integer(root.getWindowSize().width / 2));
        //xReflections.add(new Integer(100));
        //xReflections.add(new Integer(200));
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Axis button
        AlcSubToggleButton axisButton = new AlcSubToggleButton("Axis", AlcUtil.getUrlPath("axis.png", getClassLoader()));
        axisButton.setToolTipText("Toggle between a horizontal and vertical reflection axis");

        axisButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        horizontal = !horizontal;
                    }
                });
        subToolBarSection.add(axisButton);

        // Add Button
        AlcSubButton addButton = new AlcSubButton("Add", AlcUtil.getUrlPath("add.png", getClassLoader()));
        addButton.setToolTipText("Add a custom reflection axis");
        addButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        selectAxis = true;
                        firstSelect = true;
                    }
                });
        subToolBarSection.add(addButton);

        // Clear Button
        AlcSubButton clearButton = new AlcSubButton("Clear", AlcUtil.getUrlPath("clear.png", getClassLoader()));
        clearButton.setToolTipText("Clear the reflection axis");
        clearButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        xReflections.clear();
                        yReflections.clear();
                    }
                });
        subToolBarSection.add(clearButton);


    }

    protected void affectShape() {
        //System.out.println(canvas.affectShapes.size());
        int xAffectSize = canvas.createShapes.size() + (xReflections.size() * canvas.createShapes.size());
        //System.out.println(xAffectSize);
        for (int i = 0; i < canvas.createShapes.size(); i++) {
            AlcShape shape = (AlcShape) canvas.createShapes.get(i);
            GeneralPath offsetPath = shape.getShape();
            // For every x axis
            for (int j = 0; j < xReflections.size(); j++) {
                int xValue = ((Integer)xReflections.get(j)).intValue();
                if (j > 0) {
                    // Get the latest reflected path
                    offsetPath = canvas.getCurrentAffectShape().getShape();

                }

                reflectionAxis = getHorizontalReflection(xValue);
                GeneralPath reflectedPath = (GeneralPath) offsetPath.createTransformedShape(reflectionAxis);
                // 
                if (canvas.affectShapes.size() < xAffectSize) {
                    canvas.affectShapes.add(shape.customClone(reflectedPath));
                //System.out.println(j);
                } else {
                    int index = i + j;
                    AlcShape thisAffectShape = (AlcShape)canvas.affectShapes.get(index);
                    //System.out.println(i + " " + j + " " + canvas.affectShapes.size());
                    thisAffectShape.setShape(reflectedPath);
                //canvas.affectShapes.set(i + j, shape.setShape(reflectedPath));
                }
            }

        /*
        // For every y axis
        for (int k = 0; k < yReflections.size(); k++) {
        reflectionAxis = getVerticalReflection(yReflections.get(k).intValue());
        GeneralPath reflectedPath = (GeneralPath) shape.getShape().createTransformedShape(reflectionAxis);
        // 
        if (canvas.affectShapes.size() < canvas.createShapes.size()) {
        canvas.affectShapes.add(shape.customClone(reflectedPath));
        } else {
        canvas.affectShapes.set(i, shape.customClone(reflectedPath));
        }
        }
         */
        }

    }

    /* Updates the horizontal reflection transform based on the current window width */
    private AffineTransform getHorizontalReflection(int axis) {
        AffineTransform horizontalReflection = new AffineTransform();
        horizontalReflection.translate(axis, 0);
        // Reflect it using a negative scale
        horizontalReflection.scale(-1, 1);
        // Move the reflection into place
        horizontalReflection.translate(-axis, 0);
        return horizontalReflection;
    }

    /* Updates the vertical reflection transform based on the current window width */
    private AffineTransform getVerticalReflection(int axis) {
        AffineTransform verticalReflection = new AffineTransform();
        // Reflect it using a negative scale
        verticalReflection.scale(1, -1);
        // Move the reflection into place
        verticalReflection.translate(0, axis * -1);
        return verticalReflection;
    }

    public void mouseMoved(MouseEvent e) {
        if (selectAxis) {
            GeneralPath line = null;
            if (horizontal) {
                line = new GeneralPath(new Line2D.Float(e.getX(), 0, e.getX(), root.getWindowSize().height));
            } else {
                line = new GeneralPath(new Line2D.Float(0, e.getY(), root.getWindowSize().width, e.getY()));
            }
            if (firstSelect) {
                firstSelect = false;
                AlcShape axis = new AlcShape(line, new Color(255, 0, 0), 100, LINE, 1);
                canvas.affectShapes.add(axis);

            } else {
                canvas.getCurrentAffectShape().setShape(line);
            }
            canvas.redraw();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (selectAxis) {
            canvas.removeCurrentAffectShape();
            System.out.println(e.getPoint());
            // If the default reflection is present then clear it
            if (defaultReflection) {
                xReflections.clear();
                defaultReflection = false;
            }
            if (horizontal) {
                Integer xaxis = new Integer(e.getX());
                xReflections.add(xaxis);
                System.out.println("Size " + xReflections.size());
            } else {
                Integer yaxis = new Integer(e.getY());
                yReflections.add(yaxis);
            }
            selectAxis = false;
        }
    }
}
