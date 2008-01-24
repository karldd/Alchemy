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
package alchemy.create;

import alchemy.*;
import alchemy.ui.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

/**
 * Shape.java
 * @author  Karl D.D. Willis
 */
public class Shapes extends AlcModule implements AlcConstants {

    private boolean straightShapes = false;
    private boolean firstClick = true;
    private AlcSubToolBarSection subToolBarSection;
    private boolean secondClick;
    private Point lastPt;
    private int guideSize = -1;
    private GeneralPath secondPath = null;

    public Shapes() {
    }

    public void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void cleared() {
        reset();
    }

    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
        reset();
    }

    private void reset() {
        lastPt = null;
        firstClick = true;
        if (guideSize > 0) {
            canvas.guideShapes.remove(guideSize - 1);
        }
        guideSize = -1;
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Freeform Button
        AlcSubToggleButton freeformButton = new AlcSubToggleButton("Freeform/Straight", AlcUtil.getUrlPath("freeform.png", getClassLoader()));
        freeformButton.setToolTipText("Change the drawing mode to freeform or straight lines");
        freeformButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        straightShapes = !straightShapes;
                        root.canvas.guideShapes.clear();
                        root.canvas.redraw();
                        firstClick = true;
                    }
                });
        subToolBarSection.add(freeformButton);

    }

    public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 1) {
            if (!straightShapes) {
                Point p = e.getPoint();
                canvas.createShapes.add(makeShape(p));
                canvas.redraw();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        // Detect a doubleclick
        if (!e.isConsumed() && e.getButton() == 1 && e.getClickCount() > 1) {
            reset();
            canvas.redraw();
            canvas.commitShapes();
            e.consume();
        }
    }

    public void mouseMoved(MouseEvent e) {
        // If in freeform mode, not on the first click and if there is a valid lastPoint
        // then draw a guide line
        if (straightShapes && !firstClick && lastPt != null) {

            GeneralPath line = new GeneralPath(new Line2D.Float(lastPt.x, lastPt.y, e.getX(), e.getY()));
            AlcShape guide = new AlcShape(line, new Color(0, 255, 255), 100, LINE, 1);

            if (guideSize == canvas.guideShapes.size()) {
                if (secondPath != null) {
                    // If there is a secondPath defined then append it to the new shape
                    guide.getPath().append(secondPath, false);
                }
                canvas.setCurrentGuideShape(guide);
            } else if (guideSize == -1) {
                canvas.guideShapes.add(guide);
                guideSize = canvas.guideShapes.size();
            } else {
                // Clause here to determine if the guideshape array has increased 
                // i.e. it is being used by another module
                reset();
            }
            canvas.redraw();
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (!straightShapes) {
            Point p = e.getPoint();
            // Need to test if it is null incase the shape has been auto-cleared
            if (canvas.getCurrentCreateShape() != null) {
                canvas.getCurrentCreateShape().addCurvePoint(p);
                canvas.redraw();
            }
        } else {
            // If in freeform shape mode then draw the guide line using the mousemoved method
            mouseMoved(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Only if this is a single click
        if (e.getClickCount() <= 1) {

            if (straightShapes) {
                if (firstClick) {
                    canvas.createShapes.add(makeShape(p));
                    firstClick = false;
                    // If the style is solid we need to initially keep two guides
                    if (canvas.getStyle() == SOLID) {
                        secondClick = true;
                    }
                } else if (secondClick) {
                    // Only if is is not the same as the last point
                    if (!p.equals(lastPt)) {
                        secondClick = false;
                        AlcShape secondShape = ((AlcShape) canvas.guideShapes.get(guideSize - 1));
                        if (secondShape != null) {
                            secondShape.addLinePoint(p);
                        }
                        // Get the current shape and keep it as secondPath
                        secondPath = new GeneralPath(canvas.getCurrentGuideShape().getPath());
                        if (canvas.getCurrentCreateShape() != null) {
                            canvas.getCurrentCreateShape().addLinePoint(p);
                        }
                    }
                } else {
                    secondPath = null;
                    if (canvas.getCurrentCreateShape() != null) {
                        canvas.getCurrentCreateShape().addLinePoint(p);
                    }
                }
                lastPt = p;
            } else {
                // Need to test if it is null incase the shape has been auto-cleared
                if (canvas.getCurrentCreateShape() != null) {
                    canvas.getCurrentCreateShape().addLastPoint(p);
                }
            }
            canvas.redraw();
            if (!straightShapes) {
                canvas.commitShapes();
            }
        }
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }
}
