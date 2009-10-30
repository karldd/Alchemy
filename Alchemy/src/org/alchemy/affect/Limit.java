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

import org.alchemy.core.*;

public class Limit extends AlcModule {

    private int limit = 4;

    @Override
    protected void affect() {

//        System.out.print("Create: " + canvas.createShapes.size());
//        System.out.print(" Affect: " + canvas.affectShapes.size());
//        System.out.println(" Shapes: " + canvas.shapes.size());

        if(canvas.shapes.size() > limit){

            int end = canvas.shapes.size() - limit;

            for (int i = 0; i < end; i++) {
//                System.out.println("Removing " + i);
                canvas.shapes.remove(i);
            }

            canvas.redraw(true);

        }

    }
}
