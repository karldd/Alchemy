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

import org.alchemy.core.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
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
//    private boolean addShape = true;
    private AlcToolBarSubSection subToolBarSection;
    private Point oldP;
    private float size = 2.5F;
    private boolean keys = false;
//    private AlcShape mouseShape;
    // Timing
    private long mouseDelayGap = 50;
    private boolean mouseFirstRun = true;
    private long mouseDelayTime;

    /** Creates a new instance of TypeShapes */
    public TypeShapes() {
    }

    @Override
    protected void setup() {

        loadFonts();

        // Call the canvas to preview the returned random shape
        //generate();

        Font f = randomFont();
        System.out.println(f.toString());
        AffineTransform affineTransform = f.getTransform();
        fontRenderContext = new FontRenderContext(affineTransform, false, false);


        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    @Override
    protected void reselect() {
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void deselect() {
        //canvas.commitTempShape();
    }

    @Override
    protected void cleared() {
//        addShape = true;
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);


//
//        // Add Button
//        AlcSubButton addButton = new AlcSubButton("Add", AlcUtil.getUrlPath("add.png", getClassLoader()));
//        addButton.setToolTipText("Add the current shape (Enter/Return)");
//        addButton.addActionListener(
//                new ActionListener() {
//
//                    public void actionPerformed(ActionEvent e) {
//                        add();
//                    }
//                });
//        subToolBarSection.add(addButton);


        // Distortion Slider
        int initialSliderValue = 50;
        final float levelOffset = 0.01F;
        distortion = initialSliderValue * levelOffset;
        //System.out.println(distortion);
        final AlcSubSlider distortionSlider = new AlcSubSlider("Distortion", 0, 100, initialSliderValue);
        distortionSlider.setToolTipText("Adjust the amount of shape distortion");
        distortionSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!distortionSlider.getValueIsAdjusting()) {
                            int value = distortionSlider.getValue();
                            distortion = value * levelOffset;
                            System.out.println(distortion);
                        }
                    }
                });
        subToolBarSection.add(distortionSlider);

        // Run Button
        AlcSubButton runButton = new AlcSubButton("Auto-generate", AlcUtil.getUrlPath("run.png", getClassLoader()));
        runButton.setToolTipText("Create Type Shapes (Space)");
        runButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        generate();
                    }
                });
        subToolBarSection.add(runButton);

        // Size Slider
        final AlcSubSlider sizeSlider = new AlcSubSlider("Size", 1, 50, (int) size * 10);
        sizeSlider.setToolTipText("Adjust the size of shapes created");
        sizeSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!sizeSlider.getValueIsAdjusting()) {
                            size = 0.1F + sizeSlider.getValue() / 10F;
                            System.out.println(size);
                        //System.out.println(distortion);
                        }
                    }
                });
        subToolBarSection.add(sizeSlider);


        // Keyboard Button
        AlcSubToggleButton keyboardButton = new AlcSubToggleButton("Key Drawing", AlcUtil.getUrlPath("keys.png", getClassLoader()));
        keyboardButton.setToolTipText("Use the keys to create shapes (Caution: may conflict with other shortcut keys)");
        keyboardButton.setSelected(keys);
        keyboardButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        keys = !keys;
                    }
                });
        subToolBarSection.add(keyboardButton);

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

//    private void add() {
//        // Commit the shape
//        canvas.commitShapes();
//        addShape = true;
//    }
//
//    private void remove() {
//        canvas.removeCurrentCreateShape();
//        canvas.removeCurrentAffectShape();
//        addShape = true;
//        canvas.redraw();
//    }
    private void generate() {

        // Create a new shape with default properties
        AlcShape shape = new AlcShape(randomShape());

        // Set the number of points
        shape.recalculateTotalPoints();
//        if (addShape) {
        canvas.createShapes.add(shape);
//            addShape = false;
//        } else {
//            //canvas.removeCurrentAffectShape();
//            // Clear any affect shapes that may be floating around, then replace the current shape
//            canvas.affectShapes.clear();
//            canvas.setCurrentCreateShape(shape);
//        }
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
            size = (int) math.random(100, 300);
        }
        return new Font(fonts[(int) math.random(0, fonts.length)], Font.PLAIN, size);
    }

    /** Returns a random letter */
    private String randomLetter() {
        return Character.toString(letters.charAt((int) math.random(letters.length())));
    }

    private void shuffle() {
        scale = (int) math.random(2, 6);
        doubleScale = scale << 1;

        explode = (int) math.random(1, 5);
        noisiness = math.random(0.00001F, distortion);
    }

    private GeneralPath randomShape() {
        //randX = quarterWidth + (int) math.random(halfWidth);
        //randY = quarterHeight + (int) math.random(halfHeight);

        shuffle();

        //System.out.println(noisiness);

        //f = new Font("Helvetica", Font.PLAIN, 150);
        Font f = randomFont();
        System.out.println(f.toString());
        AffineTransform affineTransform = f.getTransform();
        fontRenderContext = new FontRenderContext(affineTransform, false, false);

        //Area union = new Area(makeTypeShape(f));
        GeneralPath union = new GeneralPath(makeTypeShape(f));

        int iterations = (int) math.random(5, 12);
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
        centre.translate(math.random(canvas.getSize().width), math.random(canvas.getSize().height));
        union = (GeneralPath) union.createTransformedShape(centre);

        // Convert the random shape into a general path
        GeneralPath gp = new GeneralPath((Shape) union);
        //AlcShape alcShape = new AlcShape(gp, SOLID);
        return gp;
    }

    private GeneralPath makeTypeShape(Font font) {
        return makeTypeShape(font, null, null);
    }

    private GeneralPath makeTypeShape(Font font, String letter) {
        return makeTypeShape(font, letter, null);
    }

    private GeneralPath makeTypeShape(Font font, String letter, Point location) {
        // Make a string from one random char from the letters string
        boolean auto = false;
        if (letter == null) {
            auto = true;
            letter = randomLetter();
        }

        GlyphVector gv = font.createGlyphVector(fontRenderContext, letter);
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
                if (pointCount < 50) {
                    switch (cutType) {
                        case PathIterator.SEG_MOVETO:
                            newShape.moveTo(mess(cutPts[0]), mess(cutPts[1]));
                            //newShape.moveTo(cutPts[0], cutPts[1]);
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

            if (auto) {
                // Move the shape to the middle of the screen
                int offsetX = canvas.getSize().width >> explode;
                int offsetY = canvas.getSize().width >> explode;
                newTr.translate(math.random(offsetX * -1, offsetX), math.random(offsetY * -1, offsetY));
            } else {
                // Move the shape to a random location
                int offsetX = canvas.getSize().width;
                int offsetY = canvas.getSize().width;
                newTr.translate(math.random(0, offsetX), math.random(0, offsetY));
            }

        } else {
            newTr.translate(location.x, location.y);
        }
        // Rotate the shape randomly - this would normall be 360deg
        // i.e. TWO_PI but we want to make sure it is not in the same position
        newTr.rotate(math.random(0.3F, 6.0F));
        //Area newA = new Area(newShape);
        return (GeneralPath) newShape.createTransformedShape(newTr);

    }

    private float mess(float f) {
        noiseScale += noisiness;
        float n = (math.noise(noiseScale) * doubleScale) - scale;
        return n * f;
    }

    private static int getCursorSpeed(Point p1, Point p2) {
        int diffX = Math.abs(p1.x - p2.x);
        int diffY = Math.abs(p1.y - p2.y);
        return diffX + diffY;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        shuffle();
        makePressShape(p, (int) (size * 50F));
        oldP = p;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();

        if (mouseFirstRun) {
            mouseDelayTime = System.currentTimeMillis();
            mouseFirstRun = false;
            makeDragShape(p);
        } else {
            // If enough time has elapsed
            if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                mouseDelayTime = System.currentTimeMillis();
                makeDragShape(p);
            }
        }
        oldP = p;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        oldP = null;
    //canvas.commitShapes();
    }

    private void makePressShape(Point p, int fontSize) {
        makeMouseShape(p, fontSize);
    }

    private void makeDragShape(Point p) {
        makeMouseShape(p, -1);
    }

    private void makeMouseShape(Point p, int fontSize) {
        if (fontSize < 0) {
            fontSize = (int) (getCursorSpeed(p, oldP) * size);
        }
        AlcShape mouseShape = new AlcShape(makeTypeShape(randomFont(fontSize), randomLetter(), p));
        mouseShape.recalculateTotalPoints();
        canvas.createShapes.add(mouseShape);
        canvas.redraw();
        canvas.commitShapes();
    }

    // KEY EVENTS
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        String keyText = KeyEvent.getKeyText(keyCode);

        switch (keyCode) {
            case KeyEvent.VK_SPACE:

                try {
                    //System.out.println("MEMORY BEFORE: " + Runtime.getRuntime().totalMemory());
                    generate();
                //System.out.println("MEMORY AFTER: " + Runtime.getRuntime().totalMemory());
                } catch (OutOfMemoryError error) {
                    System.out.println("OUT OF MEMORY: " + Runtime.getRuntime().totalMemory());
                    System.out.println(error);
                    System.gc();

                } catch (InternalError error) {
                    System.out.println("INTERNAL ERROR: " + error);
                    //System.out.println("MEMORY AFTER CATCH: " + Runtime.getRuntime().totalMemory());
                    System.gc();
                    generate();
                }

                break;
//
//            case KeyEvent.VK_ENTER:
//                add();
//                break;

//            case KeyEvent.VK_DELETE:
//            case KeyEvent.VK_BACK_SPACE:
//                remove();
//                break;

            default:

                if (keys) {
                    // If longer than a single character then take the first
                    if (keyText.length() > 1) {
                        keyText = keyText.substring(0, 1);
                    }
                    shuffle();
                    //AlcShape shape = makeShape(makeTypeShape(randomFont(), keyText, canvas.getMouseLoc()));
                    AlcShape shape = new AlcShape(makeTypeShape(randomFont(), keyText));
                    shape.recalculateTotalPoints();
                    canvas.createShapes.add(shape);
                    canvas.redraw();
                    canvas.commitShapes();
                }
                //System.out.println(keyText);
                break;
        }

    }
}
