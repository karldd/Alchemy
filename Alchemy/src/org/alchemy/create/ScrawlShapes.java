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
package org.alchemy.create;

import java.awt.Point;
import java.awt.event.MouseEvent;
import org.alchemy.core.*;

/**
 * ScrawlShapes
 * @author Karl D.D. Willis
 */
public class ScrawlShapes extends AlcModule {

    private boolean newPath = true;
    private Point oldP;

    public ScrawlShapes() {

    }

    private void scrawl(Point p1, Point p2, int steps, float noise) {
        float xStep = (p2.x - p1.x) / steps;
        float yStep = (p2.y - p1.y) / steps;

        AlcShape shape = canvas.getCurrentCreateShape();

        if (shape != null) {

            for (int i = 0; i < steps; i++) {
                if (i < steps - 1) {
                    float x2 = p1.x += xStep + math.random(-noise, noise);
                    float y2 = p1.y += yStep + math.random(-noise, noise);
                    Point newPt = new Point((int) x2, (int) y2);
                    shape.addCurvePoint(newPt);
                }
            }
        //shape.addCurvePoint(p2);
        }
    }

//    @Override
//    public void mousePressed(MouseEvent e) {
////        if (e.getClickCount() == 1) {
////            System.out.println("SINGLE");
////        }
//    }
    @Override
    public void mouseClicked(MouseEvent e) {

        // DOUBLE CLICK
        if (!e.isConsumed() && e.getButton() == 1 && e.getClickCount() > 1) {

            //canvas.redraw();
            //canvas.commitShapes();
            e.consume();
            newPath = true;

        // SINGLE CLICK
        } else {

            Point p = e.getPoint();

            // First click
            if (newPath) {
                canvas.createShapes.add(new AlcShape(p));
                canvas.redraw();
                newPath = false;
                System.out.println("ADD Shape");

            // Second Click onwards
            } else {
                //System.out.println("Draw scrawl");
                scrawl(oldP, p, 50, 10F);

                canvas.redraw();
            }
            oldP = p;

        }
    }
}
