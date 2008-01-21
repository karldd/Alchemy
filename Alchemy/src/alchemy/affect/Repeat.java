/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007 Karl D.D. Willis
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
package alchemy.affect;

import alchemy.*;
import java.awt.*;
import java.awt.event.*;
import alchemy.ui.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import javax.swing.JSlider;
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
    private AlcSubToolBarSection subToolBarSection;
    // Margin before the mouse falls outside of the shape once inside
    private int margin = 10;

    //private int activeShape = -1;
    public Repeat() {

    }

    public void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    public void deselect() {

    }

    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Repeat button
        AlcSubToggleButton repeatButton = new AlcSubToggleButton("Repeat", AlcUtil.getUrlPath("repeat.png", getClassLoader()));
        repeatButton.setSelected(true);
        repeatButton.setToolTipText("Toggle repeat on/off (Press and hold the b key)");

        repeatButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        repeat = !repeat;
                    }
                });
        subToolBarSection.add(repeatButton);


        // Repeat Speed Slider
        int initialSliderValue = 50;
        AlcSubSlider speedSlider = new AlcSubSlider("Repeat Speed", 0, 100, initialSliderValue);
        speedSlider.setToolTipText("Adjust the repeat speed");
        speedSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = source.getValue();
                            mouseDelayGap = value * 2 + 50;
                        //System.out.println(volume);
                        }
                    }
                });
        subToolBarSection.add(speedSlider);

    }

    private void repeatShape(Point pt, int activeShape) {
        AlcShape originalShape = (AlcShape) canvas.shapes.get(activeShape);
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
        // TODO - Adjust the offset so the shape repeats correctly

        AffineTransform moveTransform = new AffineTransform();
        moveTransform.translate(offset.x, offset.y);
        GeneralPath movedPath = (GeneralPath) path.createTransformedShape(moveTransform);
        shape.setPath(movedPath);
        canvas.shapes.add(shape);

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
        for (int i = 0; i < canvas.shapes.size(); i++) {
            AlcShape thisShape = (AlcShape) canvas.shapes.get(i);
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
            if (outside > 2) {
                update = true;
            }
        //System.out.println(outside);
        }
    }

    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    //            canvas.setCurrentShape(randomiseShape(currentShape));
    }

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

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            repeat = false;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            repeat = true;
        }
    }
}
