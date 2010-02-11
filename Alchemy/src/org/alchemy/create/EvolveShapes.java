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
package org.alchemy.create;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import org.alchemy.core.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * EvolveShapes
 * @author Karl D.D. Willis
 */
public class EvolveShapes extends AlcModule {


    private AlcShape[] shapes;
    private AlcShape[] numbers;
    private int[] numberWeights;

    private AlcToolBarSubSection subToolBarSection;
    private float crossbreed;
    private float mutation;

    // Interface
    private int shapeClicked = -1;
    private Point pointClicked = null;


    @Override
    protected void setup() {

        mutation = 0.5F;
        crossbreed = 0.5F;

        try {
            shapes = AlcUtil.getPDFShapesAsArray(new File("shapes/population.pdf"), true, 100);
            numbers = new AlcShape[shapes.length];
            numberWeights = new int[shapes.length];

            if(shapes != null && shapes.length > 0){
                int xGap = 25;
                int yGap = 35;
                int x = xGap;
                int y = yGap;
                int maxY = 0;
                for (int i = 0; i < shapes.length; i++) {
                    Rectangle bounds = shapes[i].getBounds();
                    // Drop down a row
                    if(x + bounds.width > canvas.getWidth()){
                        x = xGap;
                        y += maxY + yGap;
                        maxY = 0;
                    }
                    shapes[i].move(x, y);

                    AlcShape number = getText("0", new Point(x + bounds.width / 2, y - 10));
                    numbers[i] = number;
                    canvas.guideShapes.add(number);

                    x += bounds.width + xGap;
                    canvas.createShapes.add(shapes[i]);
                    if(bounds.height > maxY){
                        maxY = bounds.height;
                    }

                }
            }

//            AlcShape newShape = breedShapes(shapes[1], shapes[0], crossbreed);
//            newShape.move(10, 250);
//            canvas.createShapes.add(newShape);
//
//            AlcShape newShape1 = breedShapes2(shapes[1], shapes[0], crossbreed);
//            newShape1.move(150, 250);
//            canvas.createShapes.add(newShape1);
//
//            AlcShape mutatedShape = mutateShape(shapes[0]);
//            mutatedShape.move(300, 250);
//            canvas.createShapes.add(mutatedShape);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void cleared() {
    }


    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Distortion Slider
        final AlcSubSlider distortionSlider = new AlcSubSlider("Morph", 1, 100, 50);

        distortionSlider.setToolTipText("Morph the shapes together");
        distortionSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        int value = distortionSlider.getValue();
                        crossbreed = value / 100f;
                        mutation = crossbreed;

//                        AlcShape newShape = breedShapes(shapes[1], shapes[0], crossbreed);
//                        newShape.move(10, 250);
//                        canvas.createShapes.set(canvas.createShapes.size() - 3, newShape);
//
//                        AlcShape newShape1 = breedShapes2(shapes[1], shapes[0], crossbreed);
//                        newShape1.move(150, 250);
//                        canvas.createShapes.set(canvas.createShapes.size() - 2, newShape1);
//
//                        AlcShape mutatedShape = mutateShape(shapes[0]);
//                        mutatedShape.move(300, 250);
//                        canvas.createShapes.set(canvas.createShapes.size() - 1, mutatedShape);

                        canvas.redraw();

                    }
                });
        subToolBarSection.add(distortionSlider);

    }


    private AlcShape breedShapes(AlcShape s1, AlcShape s2, float mix){

        ArrayList<Point2D.Float> pts1 = s1.getPoints();
        ArrayList<Point2D.Float> pts2 = s2.getPoints();
        // The number of points for the crossbred shape
        int newPointTotal = Math.round((pts1.size() * mix) + (pts2.size() * (1f - mix)));
        GeneralPath newPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, newPointTotal);

//        System.out.println("Total Points: " + pts1.size() + " " + pts2.size());
//        System.out.println("Point Total: " + newPointTotal);
//        System.out.println("Divider: " + pts1Divider + " " + pts2Divider);

        // For the number of points in the new shape
        for (int i = 0; i < newPointTotal; i++) {
            // Current points from shape 1 & 2
            int current1 = (int) Math.floor( AlcMath.map(i, 0, newPointTotal - 1, 0, pts1.size() - 1));
            int current2 = (int) Math.floor( AlcMath.map(i, 0, newPointTotal - 1, 0, pts2.size() - 1));

//            System.out.println(i + " : " + current1 + " " + current2);

            Point2D.Float newPoint = breedPoints(pts1.get(current1), pts2.get(current2), mix);

            if(i == 0){
                newPath.moveTo(newPoint.x, newPoint.y);
            } else {
                newPath.lineTo(newPoint.x, newPoint.y);
            }

        }

        AlcShape newShape = s1.customClone(newPath);
        
        return newShape;
    }

        private AlcShape breedShapes2(AlcShape s1, AlcShape s2, float mix){

        ArrayList<Point2D.Float> pts1 = s1.getPoints();
        ArrayList<Point2D.Float> pts2 = s2.getPoints();
        // The number of points for the crossbred shape
        int newPointTotal = Math.round((pts1.size() * mix) + (pts2.size() * (1f - mix)));
        GeneralPath newPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, newPointTotal);

        
        ArrayList<Point2D.Float> lessPoints = pts2;
        ArrayList<Point2D.Float> morePoints = pts1;

        if (pts1.size() > pts2.size()) {
            lessPoints = pts1;
            morePoints = pts2;
            mix = 1 - mix;
        }

        // For the number of points in the new shape
        for (int i = 0; i < newPointTotal; i++) {

            int count = (int) Math.floor( AlcMath.map(i, 0, newPointTotal - 1, 0, morePoints.size() - 1));

            Point2D.Float currentPoint = morePoints.get(count);
            Point2D.Float closestPoint = getClosestPoint(lessPoints, currentPoint);
            // lessPoints.remove(closestPoint);
            Point2D.Float newPoint = breedPoints(currentPoint, closestPoint, mix);
//            System.out.println(currentPoint + " " + closestPoint + " " + newPoint);
           

            if(i == 0){
                newPath.moveTo(newPoint.x, newPoint.y);
            } else {
                newPath.lineTo(newPoint.x, newPoint.y);
            }

        }

        AlcShape newShape = s1.customClone(newPath);

        return newShape;
    }

    private Point2D.Float breedPoints(Point2D.Float pt1, Point2D.Float pt2, float mix){
        Point2D.Float newPoint = new Point2D.Float(pt1.x, pt1.y);
        float xDiff = pt2.x - pt1.x;
        float yDiff = pt2.y - pt1.y;
        float imix = 1f - mix;
        newPoint.x += xDiff * imix;
        newPoint.y += yDiff * imix;
        return newPoint;
    }

    private Point2D.Float getClosestPoint(ArrayList<Point2D.Float> points, Point2D.Float point){
        Point2D.Float bestPoint = null;
        double bestDistance = Double.MAX_VALUE;

        for(Point2D.Float thisPoint : points){
            double thisDistance = point.distance(thisPoint);
            if(thisDistance < bestDistance){
                bestDistance = thisDistance;
                bestPoint = thisPoint;
            }
        }
        return bestPoint;
    }

    private AlcShape mutateShape(AlcShape shape){

        ArrayList<Point2D.Float> points = shape.getPoints();
        GeneralPath newPath = new GeneralPath();
        int mutationRate  = (int) AlcMath.map(mutation, 0, 1, 10, 1);
        int mutateBitRange = (int) AlcMath.map(mutationRate, 1, 100, 26, 12);

        for (int i = 0; i < points.size(); i++) {
            Point2D.Float thisPoint = points.get(i);

            // Mutate this point
            if((int) math.random(mutationRate) == 0){

                int randomXBit = (int) math.random(0, mutateBitRange);
                int xMask = 1 << randomXBit;
                int xBits = Float.floatToRawIntBits(thisPoint.x);
                xBits ^= xMask;
                thisPoint.x = Float.intBitsToFloat(xBits);
            }
             if((int) math.random(mutationRate) == 0){
                int randomYBit = (int) math.random(0, mutateBitRange);
                int yMask = 1 << randomYBit;
                int yBits = Float.floatToRawIntBits(thisPoint.y);
                yBits ^= yMask;
                thisPoint.y = Float.intBitsToFloat(yBits);
             }

            if(i == 0){
                newPath.moveTo(thisPoint.x, thisPoint.y);
            } else {
                newPath.lineTo(thisPoint.x, thisPoint.y);
            }

        }
        
       return shape.customClone(newPath);
    }

    /** Return an AlcShape with the given text at the given location */
    private AlcShape getText(String string, Point location){
        AffineTransform transform = new AffineTransform();
        transform.translate(location.x, location.y);
        FontRenderContext fontRenderContext = new FontRenderContext(transform, false, false);
        GlyphVector gv = AlcUtil.FONT_LARGE.createGlyphVector(fontRenderContext, string);
        Shape shape = gv.getOutline();
        GeneralPath gp = new GeneralPath(shape);
        gp = (GeneralPath) gp.createTransformedShape(transform);
        return new AlcShape(gp, Color.BLACK, 255, STYLE_FILL, 1);
    }


    @Override
    public void mousePressed(MouseEvent e) {

        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i].getBounds().contains(e.getPoint())) {
                shapeClicked = i;
                pointClicked = e.getPoint();
                break;
            }
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (shapeClicked >= 0 && pointClicked != null) {
            int distance = (int) pointClicked.distance(e.getPoint()) / 2;
            Rectangle bounds = numbers[shapeClicked].getBounds();
            Point location = new Point(bounds.x, bounds.y + 9 );
            canvas.guideShapes.set(shapeClicked, getText(distance + "", location));
//            numbers[shapeClicked] = getText(distance + "", location);
            numberWeights[shapeClicked] = distance;
            canvas.redraw();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        shapeClicked = -1;
        pointClicked = null;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                break;
        }
    }
}
