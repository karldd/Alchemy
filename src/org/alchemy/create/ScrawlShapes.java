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

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 * ScrawlShapes
 * @author Karl D.D. Willis
 */
public class ScrawlShapes extends AlcModule {

    private Point oldP;
    private int count = 0;
    private int flow = 10;
    private int detail = 10;
    private int noise = 10;
    private AlcToolBarSubSection subToolBarSection;

    public ScrawlShapes() {

    }

    @Override
    protected void setup() {
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        final AlcSubSlider flowSlider = new AlcSubSlider("Flow", 1, 25, flow);
        flowSlider.setToolTipText("Change the flow speed");
        flowSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!flowSlider.getValueIsAdjusting()) {
                            flow = flowSlider.getValue();
                        }
                    }
                });
        subToolBarSection.add(flowSlider);

        final AlcSubSlider detailSlider = new AlcSubSlider("Detail", 2, 50, detail);
        detailSlider.setToolTipText("Change the shape detail");
        detailSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!detailSlider.getValueIsAdjusting()) {
                            detail = detailSlider.getValue();
                        }
                    }
                });
        subToolBarSection.add(detailSlider);

        final AlcSubSlider noiseSlider = new AlcSubSlider("Noise", 1, 50, noise);
        noiseSlider.setToolTipText("Change the shape noise");
        noiseSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!noiseSlider.getValueIsAdjusting()) {
                            noise = noiseSlider.getValue();
                        }
                    }
                });
        subToolBarSection.add(noiseSlider);
    }

    private void scrawl(Point p1, Point p2, int steps, float noise) {
        float xStep = (p2.x - p1.x) / steps;
        float yStep = (p2.y - p1.y) / steps;

        //AlcShape shape = new AlcShape(p1);
        AlcShape shape = canvas.getCurrentCreateShape();

        if (shape != null) {

            int p1x = p1.x;
            int p1y = p1.y;

            for (int i = 0; i < steps; i++) {
                if (i < steps - 1) {
                    float x2 = p1x += xStep + math.random(-noise, noise);
                    float y2 = p1y += yStep + math.random(-noise, noise);
                    Point newPt = new Point((int) x2, (int) y2);
                    shape.curveTo(newPt);
                }
            }
        //shape.addCurvePoint(p2);
        }
    //canvas.setCurrentCreateShape(shape);
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        if (e.getClickCount() == 1) {
//            System.out.println("SINGLE");
//        }
        Point p = e.getPoint();
        canvas.createShapes.add(new AlcShape(p));
        oldP = p;
    //System.out.println(oldP);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (count % flow == 0) {
            Point p = e.getPoint();

            scrawl(oldP, p, detail, noise);
            oldP = p;
            canvas.redraw();
        }
        count++;

    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        Point p = e.getPoint();
//        scrawl(oldP, p, 50, 10F);
        //System.out.println(oldP + " " + p);
        canvas.redraw();
        canvas.commitShapes();
    }
}
