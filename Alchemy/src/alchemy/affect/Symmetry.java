/**
 * Symmetry.java
 *
 * Created on December 2, 2007, 9:55 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
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

public class Symmetry extends AlcModule implements AlcConstants {

    private AlcSubToolBarSection subToolBarSection;
    private AffineTransform reflectionAxis = null;
    private boolean horizontal = true;
    private boolean defaultReflection = true;
    private boolean selectAxis = false;
    private boolean firstSelect = false;
    private ArrayList<Integer> xReflections = new ArrayList<Integer>(20);
    private ArrayList<Integer> yReflections = new ArrayList<Integer>(20);

    /** Creates a new instance of Symmetry */
    public Symmetry() {
    }

    @Override
    protected void setup() {
        // Add a default reflection
        //xReflections.add(new Integer(root.getWindowSize().width/2));
        xReflections.add(new Integer(100));
        xReflections.add(new Integer(200));
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
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

    @Override
    protected void incrementShape() {
        System.out.println(canvas.affectShapes.size());
        int xAffectSize = canvas.createShapes.size() + (xReflections.size()* canvas.createShapes.size());
        //System.out.println(xAffectSize);
        for (int i = 0; i < canvas.createShapes.size(); i++) {
            AlcShape shape = canvas.createShapes.get(i);
            GeneralPath offsetPath = shape.getShape();
            // For every x axis
            for (int j = 0; j < xReflections.size(); j++) {
                int xValue = xReflections.get(j).intValue();
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
                    AlcShape thisAffectShape = canvas.affectShapes.get(index);
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
        horizontalReflection.translate(axis / 2, 0);
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

    @Override
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

    @Override
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
