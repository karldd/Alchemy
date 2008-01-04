/**
 * TypeShapes.java
 *
 * Created on November 26, 2007, 11:03 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TypeShapes extends AlcModule implements AlcConstants {

    // SHAPE GENERATION
    Font fonts[];
    FontRenderContext fontRenderContext;
    Area union;
    int inc, scale, doubleScale, randX, randY, halfWidth, halfHeight, quarterWidth, quarterHeight, explode;
    int pointTally = 0;
    float noisiness;
    float noiseScale = 0.0F;
    // All ASCII characters, sorted according to their visual density
    String letters =
            ".`-_':,;^=+/\"|)\\<>)iv%xclrs{*}I?!][1taeo7zjLu" +
            "nT#JCwfy325Fp6mqSghVd4EgXPGZbYkOA&8U$@KHDBWNMR0Q";

    /** Creates a new instance of TypeShapes */
    public TypeShapes() {
    }

    @Override
    protected void setup() {

        halfWidth = root.getWindowSize().width / 2;
        halfHeight = root.getWindowSize().height / 2;
        quarterWidth = root.getWindowSize().width / 4;
        quarterHeight = root.getWindowSize().height / 4;

        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(createSubToolBarSection());

        loadFonts();

        // Call the canvas to preview the returned random shape
        generate();

    }

    @Override
    protected void reselect() {
        // Add this modules toolbar to the main ui
        toolBar.addSubToolBarSection(createSubToolBarSection());
    }

    @Override
    protected void deselect() {
        canvas.commitTempShape();
    }

    public AlcSubToolBarSection createSubToolBarSection() {
        AlcSubToolBarSection subToolBarSection = new AlcSubToolBarSection(this);

        // Buttons
        AlcSubButton runButton = new AlcSubButton("Create", AlcUtil.getUrlPath("run.png", getClassLoader()));
        //System.out.println(getClassLoader());
        runButton.setToolTipText("Create Type Shapes (Space)");

        runButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        generate();
                    }
                });


        subToolBarSection.add(runButton);
        return subToolBarSection;
    }

    private void generate() {
        // Reset the point tally for the new shape
        pointTally = 0;
        // Create a new shape with default properties
        AlcShape shape = new AlcShape(randomShape(), canvas.getColour(), canvas.getAlpha(), canvas.getStyle(), canvas.getLineWidth());
        // Set the number of points
        shape.setTotalPoints(pointTally);
        canvas.setTempShape(shape);
        canvas.redraw();
    }

    /** Load all available system fonts into an array */
    public void loadFonts() {
        GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fonts = graphicsenvironment.getAllFonts();
    }

    /** Returns a random font from the list */
    public Font randomFont() {
        return new Font(fonts[(int) root.math.random(0, fonts.length)].getName(), Font.PLAIN, (int) root.math.random(100, 300));
    }

    public GeneralPath randomShape() {
        randX = quarterWidth + (int) root.math.random(halfWidth);
        randY = quarterHeight + (int) root.math.random(halfHeight);

        inc = 0;
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
        // Keep track of how many points in the shape
        pointTally += pointCount;

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

    @Override
    public void mousePressed(MouseEvent e) {
        // Commit the shape when the mouse is pressed
        canvas.commitTempShape();
    }

    // KEY EVENTS
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_SPACE:

                //System.out.println("SPACE");
                generate();
                break;

        }
    }
}
