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
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * TypeShapes.java
 * @author  Karl D.D. Willis
 */
public class TypeShapes extends AlcModule implements AlcConstants {

    // SHAPE GENERATION
    private String fonts[];
    private FontRenderContext fontRenderContext;
    private int scale,  doubleScale,  explode;
    private float noisiness,  distortion;
    private float noiseScale = 0.0F;
    // All ASCII characters, sorted according to their visual density
    private String letters =
            ".`-_':,;^=+/\"|)\\<>)iv%xclrs{*}I?!][1taeo7zjLu" +
            "nT#JCwfy325Fp6mqSghVd4EgXPGZbYkOA&8U$@KHDBWNMR0Q";
    private boolean addShape = true;
    private AlcSubToolBarSection subToolBarSection;
    private Point oldP;
    private AlcShape mouseShape;

    /** Creates a new instance of TypeShapes */
    public TypeShapes() {
    }

    protected void setup() {

        // TODO - Create a seperate Typeshapes mode using key presses and the pen
        // to generate shapes at specific locations

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

    private void createSubToolBarSection() {
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

        // Distortion Slider
        int initialSliderValue = 50;
        final float levelOffset = 0.1F;
        distortion = initialSliderValue * levelOffset;
        //System.out.println(distortion);
        AlcSubSlider distortionSlider = new AlcSubSlider("Distortion", 0, 100, initialSliderValue);
        distortionSlider.setToolTipText("Adjust the amount of shape distortion");
        distortionSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = source.getValue();
                            distortion = value * levelOffset;
                        //System.out.println(distortion);
                        }
                    }
                });
        subToolBarSection.add(distortionSlider);

//        // Remove Button
//        AlcSubButton removeButton = new AlcSubButton("Remove", AlcUtil.getUrlPath("remove.png", getClassLoader()));
//        removeButton.setToolTipText("Remove the current shape (Delete/Backspace)");
//        removeButton.addActionListener(
//                new ActionListener() {
//
//                    public void actionPerformed(ActionEvent e) {
//                        remove();
//                    }
//                });
//        subToolBarSection.add(removeButton);
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
        AlcShape shape = makeShape(randomShape());

        // Set the number of points
        shape.recalculateTotalPoints();
        if (addShape) {
            canvas.createShapes.add(shape);
            addShape = false;
        } else {
            //canvas.removeCurrentAffectShape();
            // Clear any affect shapes that may be floating around, then replace the current shape
            canvas.affectShapes.clear();
            canvas.setCurrentCreateShape(shape);
        }
        canvas.redraw();
    }

    /** Load all available system fonts into an array */
    private void loadFonts() {
        GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //fonts = graphicsenvironment.getAllFonts(); // Slow
        fonts = graphicsenvironment.getAvailableFontFamilyNames();
    }

    /** Returns a random font from the list with a randomly given size */
    private Font randomFont() {
        return randomFont(-1);
    }

    /** Returns a random font from the list
     * 
     * @param size  Size of the font
     * @return      A random font from the font list
     */
    private Font randomFont(int size) {
        if (size < 0) {
            size = (int) root.math.random(100, 300);
        }
        return new Font(fonts[(int) root.math.random(0, fonts.length)], Font.PLAIN, size);
    }

    /** Returns a random letter */
    private String randomLetter() {
        return Character.toString(letters.charAt((int) root.math.random(letters.length())));
    }

    private void shuffle() {
        scale = (int) root.math.random(2, 6);
        doubleScale = scale << 1;

        explode = (int) root.math.random(1, 5);
        noisiness = root.math.random(0.00001F, distortion);
    }

    private GeneralPath randomShape() {
        //randX = quarterWidth + (int) root.math.random(halfWidth);
        //randY = quarterHeight + (int) root.math.random(halfHeight);

        shuffle();

        //System.out.println(noisiness);

        //f = new Font("Helvetica", Font.PLAIN, 150);
        Font f = randomFont();
        System.out.println(f.toString());
        AffineTransform affineTransform = f.getTransform();
        fontRenderContext = new FontRenderContext(affineTransform, false, false);

        //Area union = new Area(makeTypeShape(f));
        GeneralPath union = new GeneralPath(makeTypeShape(f));

        int iterations = (int) root.math.random(5, 12);
        System.out.println("Iterations: " + iterations + " Scale: " + scale);

        // There is a bug here when using union causing OutofMemory Errors: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4667078
        // Need to find some hack to stop causing this
        // Allocating more memory using [-ms50m -mx100m] does nothing
        for (int i = 0; i < iterations; i++) {
            GeneralPath shape = makeTypeShape(f);

            if (!shape.intersects(union.getBounds2D())) {

                //System.out.println("SIMPLE MISS");
                union.append(shape, false);

            } else {


                Area hitTestArea = new Area(union);
                Area newArea = new Area(shape);
                hitTestArea.intersect(newArea);

                if (hitTestArea.isEmpty()) {
                    //System.out.println("MISS");
                    union.append(shape, false);


                } else {
                    //System.out.println("HIT " + Runtime.getRuntime().totalMemory());

                    Area mainArea;
                    try {
                        mainArea = new Area(union);
                        mainArea.add(newArea);
                        union = new GeneralPath((Shape) mainArea);

                    } finally {
                        mainArea = null;
                        hitTestArea = null;
                        newArea = null;

                    }

                }
            }

        }

        AffineTransform centre = new AffineTransform();
        centre.translate(root.math.random(root.getWindowSize().width), root.math.random(root.getWindowSize().height));
        union = (GeneralPath) union.createTransformedShape(centre);

        // Convert the random shape into a general path
        GeneralPath gp = new GeneralPath((Shape) union);
        //AlcShape alcShape = new AlcShape(gp, SOLID);

        return gp;
    }

    private GeneralPath makeTypeShape(Font font) {
        return makeTypeShape(font, null, null);
    }

    private GeneralPath makeTypeShape(Font font, String randomLetter, Point location) {
        // Make a string from one random char from the letters string
        if (randomLetter == null) {
            randomLetter = randomLetter();
        }

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
//        boolean close = true;

        while (!cut.isDone()) {
            cutType = cut.currentSegment(cutPts);

            // Count the number of new segments
            if (cutType == PathIterator.SEG_MOVETO) {
                segCount++;
            }

            // Only add the first segment
            if (segCount == 1) {
                if (pointCount < 20) {
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
                            //System.out.println("CUBIC " + pointCount);
                            //newShape.curveTo(mess(cutPts[0]), mess(cutPts[1]), mess(cutPts[2]), mess(cutPts[3]), mess(cutPts[4]), mess(cutPts[5]));
                            newShape.curveTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3], cutPts[4], cutPts[5]);
                            break;
                        case PathIterator.SEG_CLOSE:
                            newShape.closePath();
                            break;
                    }
                    pointCount++;
                } else {
                    //System.out.println("BROKE");
                    break;
                }

            }
            cut.next();

        }

        AffineTransform newTr = new AffineTransform();
        if (location == null) {
            // Move the shape to the middle of the screen

            int offsetX = root.getWindowSize().width >> explode;
            int offsetY = root.getWindowSize().width >> explode;
            newTr.translate(root.math.random(offsetX * -1, offsetX), root.math.random(offsetY * -1, offsetY));
        } else {
            newTr.translate(location.x, location.y);
        }
        // Rotate the shape randomly - this would normall be 360deg
        // i.e. TWO_PI but we want to make sure it is not in the same position
        newTr.rotate(root.math.random(0.3F, 6.0F));
        //Area newA = new Area(newShape);
        return (GeneralPath) newShape.createTransformedShape(newTr);

    }

    private float mess(float f) {
        noiseScale += noisiness;
        float n = (root.math.noise(noiseScale) * doubleScale) - scale;
        return n * f;
    }

    private static int getCursorSpeed(Point p1, Point p2) {
        int diffX = Math.abs(p1.x - p2.x);
        int diffY = Math.abs(p1.y - p2.y);
        return diffX + diffY;
    }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        shuffle();
        mouseShape = makeShape(makeTypeShape(randomFont(), randomLetter(), p));
        mouseShape.recalculateTotalPoints();
        canvas.createShapes.add(mouseShape);
        canvas.redraw();
        oldP = p;
    }

    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        if (mouseShape != null) {
            int speed = getCursorSpeed(p, oldP) * 5;
            GeneralPath newPath = makeTypeShape(randomFont(speed), randomLetter(), p);

            if (!newPath.intersects(mouseShape.getPath().getBounds2D())) {
                mouseShape.append(newPath, false);
                canvas.redraw();
            }
        }
//            } else {
//                Area hitTestArea = new Area(mouseShape.getPath());
//                System.out.println("SIMPLE MISS");
//                Area newArea = new Area(newPath);
//                hitTestArea.intersect(newArea);
//
//                if (hitTestArea.isEmpty()) {
//                    System.out.println("MISS");
//                    mouseShape.append(newPath, false);
//                    canvas.redraw();
//                }
//            }


//                    //System.out.println("HIT " + Runtime.getRuntime().totalMemory());
//
//                    Area mainArea, newArea;
//                    try {
//                        mainArea = new Area(mouseShape.getPath());
//                        newArea = new Area(newPath);
//                        mainArea.add(newArea);
//                        mouseShape.setPath(new GeneralPath((Shape) mainArea));
//
//                    } finally {
//                        
//                        mainArea = null;
////                        hitTestArea = null;
////                        newArea = null;
//
//                    }
////                }
//
//            }
//
//            //mouseShape.append(makeTypeShape(randomFont(speed), randomLetter(), p), false);
//
//            mouseShape.recalculateTotalPoints();

//        }
//        //canvas.createShapes.add(shape);
//
        oldP = p;
    }

    public void mouseReleased(MouseEvent e) {
        oldP = null;
        canvas.commitShapes();
//        Point p = e.getPoint();
//        // Need to test if it null incase the shape has been auto-cleared
//        if (canvas.getCurrentCreateShape() != null) {
//            canvas.getCurrentCreateShape().addLastPoint(p);
//            canvas.redraw();
//            canvas.commitShapes();
//        }
    }

    private AlcShape makeShape(GeneralPath path) {
        // Make a new shape with the globally defined style etc...
        return new AlcShape(path, canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
    }

    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        String keyText = KeyEvent.getKeyText(keyCode);

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

            default:

                // If longer than a single character then take the first
                if (keyText.length() > 1) {
                    keyText = keyText.substring(0, 1);
                }

                AlcShape shape = makeShape(makeTypeShape(randomFont(), keyText, canvas.getMouseLoc()));
                shape.recalculateTotalPoints();
                canvas.createShapes.add(shape);
                canvas.redraw();
                 canvas.commitShapes();

                //System.out.println(keyText);
                break;
        }

    }
}
