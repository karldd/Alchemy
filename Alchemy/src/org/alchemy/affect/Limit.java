/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 *
 *  Copyright (c) 2007-2009 Karl D.D. Willis
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
    private int limit = defaultNumber - 1;

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
                            limit = value - 1;
                        }
                    }
                });
        subToolBarSection.add(numberSpinner);
    }


    @Override
    protected void affect() {

        if(canvas.shapes.size() > limit){

            int end = canvas.shapes.size() - limit;
            canvas.shapes.subList(0, end).clear();
            canvas.redraw(true);

        }

    }
}
