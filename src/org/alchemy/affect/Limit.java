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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

public class Limit extends AlcModule {

    private int defaultNumber = 10;
    private int limit = defaultNumber;
    private AlcToolBarSubSection subToolBarSection;

    @Override
    public void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        final AlcSubSpinner numberSpinner = new AlcSubSpinner("Number", 1, 100, defaultNumber, 1);
        numberSpinner.setToolTipText("The number of shapes to limit to");
        numberSpinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!numberSpinner.getValueIsAdjusting()) {
                            int value = numberSpinner.getValue();
                            limit = value;
                        }
                    }
                });
        subToolBarSection.add(numberSpinner);
    }

    @Override
    protected void affect() {

        // We only limit the main shapes list
        // so if that is empty then return
        if(canvas.shapes.size() == 0){
            return;
        }

        // The total number of shapes
        int totalShapes = canvas.shapes.size() + canvas.createShapes.size() + canvas.affectShapes.size();

        // If there are more shapes than the limit
        if (totalShapes > limit) {

//            System.out.println("Shapes: " + canvas.shapes.size() + " Create: " + canvas.createShapes.size() + " Affect: " + canvas.affectShapes.size());

            // The number of shapes to remove
            int shapesToRemove = totalShapes - limit;
//            System.out.println("Total: " + totalShapes + " Remove: " + shapesToRemove);

            // The end of the range to remove from the shapes list
            int end = shapesToRemove;
            // If there are not enough shapes in the shapes list
            if (shapesToRemove > canvas.shapes.size()) {
                end = canvas.shapes.size() - 1;
            }
            if(end == 0){
                canvas.shapes.remove(0);
                shapesToRemove--;
            } else if (end > 0) {
                canvas.shapes.subList(0, end).clear();
                shapesToRemove -= end + 1;
            }

            canvas.redraw(true);
        }

    }
}
