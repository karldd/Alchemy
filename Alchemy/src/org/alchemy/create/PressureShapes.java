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
package org.alchemy.create;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 *
 * PressureShapes.java
 */
public class PressureShapes extends AlcModule {

    private AlcToolBarSubSection subToolBarSection;
    private int pressureAmount;
    private int startPressure = 25;

    @Override
    public void setup() {
        pressureAmount = startPressure;
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    public void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);


        final int pressureMin = 1;
        final int pressureMax = 200;

        final AlcSubSpinner pressureSpinner = new AlcSubSpinner(
                "Pressure",
                pressureMin,
                pressureMax,
                startPressure,
                1);

        pressureSpinner.setToolTip("Change the amount of pressure");
        pressureSpinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        pressureAmount = pressureSpinner.getValue();
                    }
                });

        subToolBarSection.add(pressureSpinner);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        AlcShape shape = new AlcShape();
        Point2D.Float p = canvas.getPenLocation();
        shape.spineTo(p, getPressure());
        canvas.createShapes.add(shape);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        AlcShape currentShape = canvas.getCurrentCreateShape();
        if (currentShape != null) {
            Point2D.Float p = canvas.getPenLocation();
            currentShape.spineTo(p, getPressure());
            canvas.redraw();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.commitShapes();
    }

    private float getPressure() {
        float pressure = 1;
        if (canvas.getPenType() != PEN_CURSOR) {
            pressure = canvas.getPenPressure() * (float) pressureAmount;
        }
        return pressure;
    }
}
