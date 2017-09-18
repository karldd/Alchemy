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

import org.alchemy.core.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Repeat a given shape on the canvas at a given rate
 * @author Karl D.D. Willis
 */
public class Repeat extends AlcModule {

    // Timing
    private long mouseDelayGap = 150;
    private boolean mouseFirstRun = true;
    private long mouseDelayTime;
    private boolean mouseDown = false;
    //
    private Dimension shapeSize = null;
    private Point shapeOffset = null;
    private int outside = 0;
    private boolean update = false;
    private boolean repeat = true;
    private AlcToolBarSubSection subToolBarSection;
    // Margin before the mouse falls outside of the shape once inside
    private int margin = 10;
    private AlcSubToggleButton repeatButton;

    //private int activeShape = -1;
    public Repeat() {

    }

    @Override
    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    @Override
    public void deselect() {

    }

    @Override
    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Repeat button
        repeatButton = new AlcSubToggleButton("Repeat", AlcUtil.getUrlPath("repeat.png", getClassLoader()));
        repeatButton.setSelected(true);
        repeatButton.setToolTipText("Toggle repeat on/off");

        repeatButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        repeat = !repeat;
                    }
                });
        subToolBarSection.add(repeatButton);


        // Repeat Speed Slider
        int initialSliderValue = 50;
        final AlcSubSlider speedSlider = new AlcSubSlider("Interval", 0, 100, initialSliderValue);
        speedSlider.setToolTipText("Adjust the repeat interval");
        speedSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!speedSlider.getValueIsAdjusting()) {
                            int value = speedSlider.getValue();
                            mouseDelayGap = value * 2 + 50;
                        //System.out.println(volume);
                        }
                    }
                });
        subToolBarSection.add(speedSlider);

    }

    private void repeatShape(Point pt, int activeShape) {
        AlcShape originalShape = canvas.shapes.get(activeShape);
        // Make a completely new shape
        AlcShape shape = (AlcShape) originalShape.clone();
        GeneralPath path = shape.getPath();
        Rectangle bounds = path.getBounds();

        // If null or a different sized shape - reset the offset
        if (shapeSize == null || !similarSize(bounds.getSize()) || update) {
            shapeSize = bounds.getSize();
            //shapeBounds = bounds;
            shapeOffset = new Point(pt.x - bounds.x, pt.y - bounds.y);
            if (update) {
                update = false;
            }
        //System.out.println("Changed");
        }

        Point offset = new Point(pt.x - (bounds.x + shapeOffset.x), pt.y - (bounds.y + shapeOffset.y));
        //Point centre = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        //Point thisOffset = new Point(pt.x - offset.x, pt.y - offset.y);

        //System.out.println(offset);

        //System.out.println(offset);
        //
        //Point finalOffset = new Point(offset.x - bounds.width/2, offset.y - bounds.height/2);

        AffineTransform moveTransform = new AffineTransform();
        moveTransform.translate(offset.x, offset.y);
        GeneralPath movedPath = (GeneralPath) path.createTransformedShape(moveTransform);
        shape.setPath(movedPath);
        canvas.affectShapes.add(shape);

        //GeneralPath randomisedShape = randomise(shape.getShape(), currentLoc);
        //shape.setPath(randomisedShape);
        canvas.redraw();
    }

    /** Check if the current shape is a similar size to the old one */
    private boolean similarSize(Dimension newSize) {
        int difference = Math.abs(newSize.width - shapeSize.width) + Math.abs(newSize.height - shapeSize.height);
        // Give a margin of 10 pixels else it is a 'new shape' and return false
        if (difference > 10) {
            return false;
        } else {
            return true;
        }
    }

    private void mouseInside(Point p) {
        int currentActiveShape = -1;
        for (int i = canvas.shapes.size() - 1; i >= 0; i--) {
            AlcShape thisShape = canvas.shapes.get(i);
            GeneralPath currentPath = thisShape.getPath();
            Rectangle bounds = new Rectangle(currentPath.getBounds());
            // If already repeating a shape
            if (outside == 0) {
                // Check that it is still within X pixels of the original
                bounds.grow(margin, margin);
            //System.out.println("GROW");
            }
            if (bounds.contains(p)) {
                currentActiveShape = i;
                // Break once we find the first matching shape
                break;
            }
        }
        // Inside a shape
        if (currentActiveShape >= 0) {
            outside = 0;

            if (repeat) {
                repeatShape(p, currentActiveShape);
            }

        // Outside a shape
        } else {
            outside++;
            // if outside the shape for a certain period then update the offset next time
            if (outside == 3) {
                canvas.commitShapes();
                update = true;
            }
        //System.out.println(outside);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
        // If clicked when still repeating
        if (outside <= 2) {
            canvas.commitAffectShapes();
            update = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    //            canvas.setCurrentShape(randomiseShape(currentShape));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!mouseDown) {
            // Dispatch checking for intersection at a slow rate
            if (mouseFirstRun) {
                mouseDelayTime = System.currentTimeMillis();
                mouseInside(e.getPoint());
                mouseFirstRun = false;
            } else {
                if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                    mouseDelayTime = System.currentTimeMillis();
                    //System.out.println(e.getPoint());
                    mouseInside(e.getPoint());
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_B) {
            repeat = !repeat;
            repeatButton.setSelected(repeat);
        }
    }
}
