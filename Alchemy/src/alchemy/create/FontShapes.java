package alchemy.create;

import alchemy.*;

import processing.core.PApplet;
import processing.core.PFont;

import org.java.plugin.Plugin;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.awt.geom.*;
import java.awt.Shape;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.GraphicsEnvironment;

public class FontShapes extends Create {
    
    // GENERAL
    boolean firstPress = false;
    boolean cleared = false;
    
    // SHAPE GENERATION
    Font fonts[];
    FontRenderContext fontRenderContext;
    Area union;
    int inc, scale, doubleScale, randX, randY, halfWidth, halfHeight, quarterWidth, quarterHeight;
    PFont myFont;
    float noiseScale = 0.0F;
    // All ASCII characters, sorted according to their visual density
    String letters =
            ".`-_':,;^=+/\"|)\\<>)iv%xclrs{*}I?!][1taeo7zjLu" +
            "nT#JCwfy325Fp6mqSghVd4EgXPGZbYkOA&8U$@KHDBWNMR0Q";
    
    // USER DRAWN SHAPE
    Vector<Object> shapes;
    int currentShape;
    
    public FontShapes(){
    }
    
    public void setup(PApplet r){
        root = r;
        root.println("Module " + id + " Loaded");
        
        cursor = root.CROSS;
        smooth = true;
        setSmooth(smooth);
        
        loop = false;
        setLoop(loop);
        
        shapes = new Vector<Object>();
        shapes.ensureCapacity(100);
        
        halfWidth = root.width/2;
        halfHeight = root.height/2;
        quarterWidth = root.width/4;
        quarterHeight = root.height/4;
        
        myFont = root.createFont("Helvetica", 12, true);
        
        loadFonts();
        randomShape();
        
    }
    
    public void draw(){
        //root.noFill();
        //root.stroke(0);
        resetSmooth();
        
        processPathIterator(union);
        
        root.fill(0);
        root.noStroke();
        
        // Draw the lines
        for(int i = 0; i < shapes.size(); i++) {
            //((AlcSketchPath)shapes.get(i)).draw();
        }
        
    }
    
    public void refocus(){
        firstPress = false;
        root.redraw();
    }
    
    public void loadFonts(){
        //root.println("Start");
        GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fonts = graphicsenvironment.getAllFonts();
        root.hint(root.ENABLE_NATIVE_FONTS);
        //root.println("End");
    }
    
    public Font randomFont(){
        return new Font(fonts[(int)root.random(0, fonts.length)].getName(), Font.PLAIN, (int)root.random(100, 200));
    }
    
    public void randomShape(){
        randX = quarterWidth + (int)root.random(halfWidth);
        randY = quarterHeight + (int)root.random(halfHeight);
        
        inc = 0;
        scale = (int)root.random(2, 8);
        //root.println(scale);
        doubleScale = scale * 2;
        
        //f = new Font("Helvetica", Font.PLAIN, 150);
        Font f = randomFont();
        root.println(f.toString());
        AffineTransform affineTransform = f.getTransform();
        fontRenderContext = new FontRenderContext(affineTransform, false, false);
        
        union = makeShape(f);
        
        int iterations = (int)root.random(5, 15);
        root.println("Iterations: "+iterations+ " Scale: "+scale);
        for(int i = 0; i < iterations; i++) {
            Area a = makeShape(f);
            union.add(a);
        }
        
    }
    
    public Area makeShape(Font font){
        // Make a string from one random char from the letters string
        String randomLetter = Character.toString(letters.charAt((int)root.random(letters.length())));
        
        GlyphVector gv = font.createGlyphVector(fontRenderContext, randomLetter);
        Shape shp = gv.getOutline();
        //Shape shp = gv.getOutline(root.random(150), root.random(150));
        PathIterator count = shp.getPathIterator(null);
        
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
            if(cutType == PathIterator.SEG_MOVETO){
                segCount++;
            }
            
            // Only add the first segment
            if(segCount == 1){
                if(pointCount < 20){
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
                            newShape.curveTo(cutPts[0], cutPts[1], cutPts[2], cutPts[3], cutPts[4], cutPts[5]);
                            break;
                        case PathIterator.SEG_CLOSE:
                            newShape.closePath();
                            break;
                    }
                } else{
                    if(close){
                        newShape.closePath();
                        close = false;
                    }
                }
                pointCount++;
            }
            cut.next();
            
        }
        AffineTransform newTr = AffineTransform.getTranslateInstance(root.random(200), root.random(200));
        //newTr.translate(root.random(200), root.random(200));
        newTr.rotate(root.random(7));
        Area newA = new Area(newShape);
        return newA.createTransformedArea(newTr);
        /*}
         
        AffineTransform tr = AffineTransform.getRotateInstance(root.random(7));
        Area a =  new Area(shp);
        return a.createTransformedArea(tr);
         */
    }
    
    // Draw the final unioned shape
    void processPathIterator(Area a) {
        PathIterator iter = a.getPathIterator(null);
        float[] pts = new float[6];
        int type;
        
        root.pushMatrix();
        root.translate(randX, randY);
        
        while (!iter.isDone()) {
            type = iter.currentSegment(pts);
            
            switch (type) {
                
                case PathIterator.SEG_MOVETO:
                    //root.println("Start");
                    /*
                    root.fill(0);
                    root.textFont(myFont);
                    root.text(inc, pts[0], pts[1]);
                    inc++;
                     */
                    root.beginShape();
                    root.vertex(pts[0], pts[1]);
                    
                    //drawPoint(pts[0], pts[1]);
                    break;
                    
                case PathIterator.SEG_LINETO:
                    root.vertex(pts[0],pts[1]);
                    
                    //drawPoint(pts[0], pts[1]);
                    break;
                    
                case PathIterator.SEG_QUADTO:
                    root.bezierVertex(pts[0], pts[1], pts[2], pts[3], pts[2], pts[3]);
                    
                    //drawPoint(pts[2], pts[3]);
                    break;
                    
                case PathIterator.SEG_CLOSE:
                    
                    root.fill(0);
                    root.noStroke();
                    root.endShape(root.CLOSE);
                    //root.println("End");
                    break;
            }
            iter.next();
        }
        root.popMatrix();
        root.rectMode(root.CORNER);
    }
    
    public float mess(float f){
        noiseScale += 0.002F;
        float n =  (root.noise(noiseScale) * doubleScale) - scale;
        //root.println(n);
        return n * f;
        //return f * root.random(2);
    }
    
    public void drawPoint(float x, float y){
        root.noStroke();
        root.fill(0, 255, 255);
        root.rectMode(root.CENTER);
        root.rect(x, y, 4, 4);
    }
    
    public void clear(){
        shapes.removeAllElements();
        if(root.mousePressed) cleared = true;
        root.redraw();
    }
    
    // MOUSE EVENTS
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        //shapes.add(new AlcSketchPath(root, x, y));
        currentShape = shapes.size() - 1;
        
        firstPress = true;
    }
    
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress && !cleared){
            ((AlcSketchPath)shapes.get(currentShape)).drag(x, y);
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if(firstPress && !cleared){
            ((AlcSketchPath)shapes.get(currentShape)).release(x, y);
        }
        cleared = false;
    }
    
    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        //root.println(keyCode);
        
        switch(keyCode){
            case 8: // Backspace
            case 127: // Delete
                // Is this cross platform?
                clear();
                break;
            case 32: // Build Shapes
                // root.println("Space");
                //processPathIterator(union);
                randomShape();
                root.redraw();
                break;
                
        }
    }
    
}