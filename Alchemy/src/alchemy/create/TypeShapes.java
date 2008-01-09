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
import alchemy.AlcShape;
import alchemy.ui.*;

import java.awt.geom.*;
import java.awt.Shape;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * TypeShapes.java
 * @author  Karl D.D. Willis
 */


public class TypeShapes extends AlcModule implements AlcConstants {

    // SHAPE GENERATION
    private String fonts[];
    private FontRenderContext fontRenderContext;
    private Area union;
    private int scale,  doubleScale,  explode;
    private float noisiness;
    private float noiseScale = 0.0F;
    // All ASCII characters, sorted according to their visual density
    private String letters =
            ".`-_':,;^=+/\"|)\\<>)iv%xclrs{*}I?!][1taeo7zjLu" +
            "nT#JCwfy325Fp6mqSghVd4EgXPGZbYkOA&8U$@KHDBWNMR0Q";
    private boolean addShape = true;
    private AlcSubToolBarSection subToolBarSection;

    /** Creates a new instance of TypeShapes */
    public TypeShapes() {
    }

    
    protected void setup() {

//        halfWidth = root.getWindowSize().width / 2;
//        halfHeight = root.getWindowSize().height / 2;
//        quarterWidth = root.getWindowSize().width / 4;
//        quarterHeight = root.getWindowSize().height / 4;

        loadFonts();

        // Call the canvas to preview the returned random shape
        generate();

        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    
    protected void reselect() {
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    
    protected void deselect() {
    //canvas.commitTempShape();
    }

    
    protected void cleared() {
        addShape = true;
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Run Button
        AlcSubButton runButton = new AlcSubButton("Create", AlcUtil.getUrlPath("run.png", getClassLoader()));
        runButton.setToolTipText("Create Type Shapes (Space)");
        runButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        generate();
                    }
                });
        subToolBarSection.add(runButton);

        // Add Button
        AlcSubButton addButton = new AlcSubButton("Add", AlcUtil.getUrlPath("add.png", getClassLoader()));
        addButton.setToolTipText("Add the current shape (Enter/Return)");
        addButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        add();
                    }
                });
        subToolBarSection.add(addButton);

        // Remove Button
        AlcSubButton removeButton = new AlcSubButton("Remove", AlcUtil.getUrlPath("remove.png", getClassLoader()));
        removeButton.setToolTipText("Remove the current shape (Delete/Backspace)");
        removeButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        remove();
                    }
                });
        subToolBarSection.add(removeButton);
    }

    private void add() {
        // Commit the shape
        canvas.commitShapes();
        addShape = true;
    }

    private void remove() {
        canvas.removeCurrentCreateShape();
        canvas.removeCurrentAffectShape();
        addShape = true;
        canvas.redraw();
    }

    private void generate() {
        // Create a new shape with default properties
        AlcShape shape = new AlcShape(randomShape(), canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
        // Set the number of points
        shape.recalculateTotalPoints();
        if (addShape) {
            canvas.createShapes.add(shape);
            addShape = false;
        } else {
            canvas.removeCurrentAffectShape();
            canvas.setCurrentCreateShape(shape);
        }
        canvas.redraw();
    }

    /** Load all available system fonts into an array */
    public void loadFonts() {
        GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //fonts = graphicsenvironment.getAllFonts(); // Slow
        fonts = graphicsenvironment.getAvailableFontFamilyNames();
    }

    /** Returns a random font from the list */
    public Font randomFont() {
        return new Font(fonts[(int) root.math.random(0, fonts.length)], Font.PLAIN, (int) root.math.random(100, 300));
    }

    public GeneralPath randomShape() {
        //randX = quarterWidth + (int) root.math.random(halfWidth);
        //randY = quarterHeight + (int) root.math.random(halfHeight);

        scale = (int) root.math.random(2, 8);
        doubleScale = scale << 1;

        explode = (int) root.math.random(1, 5);
        noisiness = root.math.random(0.0001F, 0.5F);
        //System.out.println(noisiness);

        //f = new Font("Helvetica", Font.PLAIN, 150);
        Font f = randomFont();
        System.out.println(f.toString());
        AffineTransform affineTransform = f.getTransform();
        fontRenderContext = new FontRenderContext(affineTransform, false, false);

        union = makeShape(f);

        int iterations = (int) root.math.random(5, 15);
        System.out.println("Iterations: " + iterations + " Scale: " + scale);

        for (int i = 0; i < iterations; i++) {
            Area a = makeShape(f);
            union.add(a);
        }

        AffineTransform centre = new AffineTransform();
        centre.translate(root.math.random(root.getWindowSize().width), root.math.random(root.getWindowSize().height));
        union = union.createTransformedArea(centre);

        // Convert the random shape into a general path
        GeneralPath gp = new GeneralPath((Shape) union);
        //AlcShape alcShape = new AlcShape(gp, SOLID);

        return gp;
    }

    public Area makeShape(Font font) {
        // Make a string from one random char from the letters string
        String randomLetter = Character.toString(letters.charAt((int) root.math.random(letters.length())));

        GlyphVector gv = font.createGlyphVector(fontRenderContext, randomLetter);
        Shape shp = gv.getOutline();
        //Shape shp = gv.getOutline(math.random(150), math.random(150));
        //PathIterator count = shp.getPathIterator(null);

        /*
        int numberOfSegments = 0;
        float[] pts = new float[6];
        int type;
        while (!count.isDone()) {
        type = count.currentSegment(pts);
        switch (type) {
        case PathIterator.SEG_MOVETO:
        //root.println("Start");
        numberOfSegments++;
        break;
        }
        count.next();
        }
         */
        //root.println(randomLetter + " " + numberOfSegments);

        //if(numberOfSegments > 1){
        // Make a new shape
        GeneralPath newShape = new GeneralPath();
        PathIterator cut = shp.getPathIterator(null);
        float[] cutPts = new float[6];
        int cutType;
        int segCount = 0;
        int pointCount = 0;
        boolean close = true;

        while (!cut.isDone()) {
            cutType = cut.currentSegment(cutPts);

            // Count the number of new segments
            if (cutType == PathIterator.SEG_MOVETO) {
                segCount++;
            }

            // TODO - review this code to see what causes the: "java.lang.InternalError: Odd number of new curves!" error
            // Only add the first segment
            if (segCount == 1) {
                if (pointCount < 15) {
                    switch (cutType) {
                        case PathIterator.SEG_MOVETO:
                            newShape.moveTo(cutPts[0], cutPts[1]);
                            break;
                        case PathIterator.SEG_LINETO:
                            newShape.lineTo(mess(cutPts[0]), mess(cutPts[1]));
                            //newShape.lineTo(cutPts[0], cutPts[1]);
                            break;
                        case PathIterator.SEG_QUADTO:
                            newShape.quadTo(mess(cutPts[0]), mess(cutPts[1]), mess(cutPts[2]), mess(cutPts[3]));
                            //newShape.quadTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3]);
                            break;
                        case PathIterator.SEG_CUBICTO:
                            // Randomising the curves tends to generate errors and unresposiveness
                            //newShape.curveTo(mess(cutPts[0]), mess(cutPts[1]), mess(cutPts[2]), mess(cutPts[3]), mess(cutPts[4]), mess(cutPts[5]));
                            newShape.curveTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3], cutPts[4], cutPts[5]);
                            break;
                        case PathIterator.SEG_CLOSE:
                            newShape.closePath();
                            break;
                    }
                    pointCount++;
                } else {
                    if (close) {
                        newShape.closePath();
                        close = false;
                    }
                }

            }
            cut.next();

        }

        // Move the shape to the middle of the screen
        AffineTransform newTr = new AffineTransform();
        int offsetX = root.getWindowSize().width >> explode;
        int offsetY = root.getWindowSize().width >> explode;
        newTr.translate(root.math.random(offsetX * -1, offsetX), root.math.random(offsetY * -1, offsetY));

        // Rotate the shape randomly
        newTr.rotate(root.math.random(TWO_PI));
        Area newA = new Area(newShape);
        return newA.createTransformedArea(newTr);

    }

    public float mess(float f) {
        noiseScale += noisiness;
        float n = (root.math.noise(noiseScale) * doubleScale) - scale;
        return n * f;
    }

    
    public void mousePressed(MouseEvent e) {
        add();
        Point p = e.getPoint();
        canvas.createShapes.add(makeShape(p));
        canvas.redraw();
    }

    
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            canvas.getCurrentCreateShape().addCurvePoint(p);
            canvas.redraw();
        }

    }

    
    public void mouseReleased(MouseEvent e) {
        Point p = e.getPoint();
        // Need to test if it null incase the shape has been auto-cleared
        if (canvas.getCurrentCreateShape() != null) {
            canvas.getCurrentCreateShape().addLastPoint(p);
            canvas.redraw();
            canvas.commitShapes();
        }
    }

    private AlcShape makeShape(Point p) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(p, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    // KEY EVENTS
    
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                generate();
                break;

            case KeyEvent.VK_ENTER:
                add();
                break;

            case KeyEvent.VK_DELETE:
            case KeyEvent.VK_BACK_SPACE:
                remove();
                break;

        }
    }
}
