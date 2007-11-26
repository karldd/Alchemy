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

import java.awt.geom.*;
import java.awt.Shape;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;


public class TypeShapes extends Create implements AlcConstants{
    
    // SHAPE GENERATION
    Font fonts[];
    FontRenderContext fontRenderContext;
    Area union;
    int inc, scale, doubleScale, randX, randY, halfWidth, halfHeight, quarterWidth, quarterHeight;
    
    float noiseScale = 0.0F;
    // All ASCII characters, sorted according to their visual density
    String letters =
            ".`-_':,;^=+/\"|)\\<>)iv%xclrs{*}I?!][1taeo7zjLu" +
            "nT#JCwfy325Fp6mqSghVd4EgXPGZbYkOA&8U$@KHDBWNMR0Q";
    
    
    AlcMath math = new AlcMath();
    
    /** Creates a new instance of TypeShapes */
    public TypeShapes() {
    }
    
    public void setup(AlcMain root){
        
        this.root = root;
        setLoaded(true);
        
        halfWidth = root.getWindowSize().width/2;
        halfHeight = root.getWindowSize().height/2;
        quarterWidth = root.getWindowSize().width/4;
        quarterHeight = root.getWindowSize().height/4;
        
        loadFonts();
        
        // Call the canvas to preview the returned random shape
        root.canvas.previewShape(randomShape());
        
    }
    
    public void refocus(){
        //System.out.println("Refocus Called");
    }
    
    /** Load all available system fonts into an array */
    public void loadFonts(){
        GraphicsEnvironment graphicsenvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fonts = graphicsenvironment.getAllFonts();
    }
    
    /** Returns a random font from the list */
    public Font randomFont(){
        return new Font(fonts[(int)math.random(0, fonts.length)].getName(), Font.PLAIN, (int)math.random(100, 200));
    }
    
    public AlcShape randomShape(){
        randX = quarterWidth + (int)math.random(halfWidth);
        randY = quarterHeight + (int)math.random(halfHeight);
        
        inc = 0;
        scale = (int)math.random(2, 8);
        //root.println(scale);
        doubleScale = scale * 2;
        
        //f = new Font("Helvetica", Font.PLAIN, 150);
        Font f = randomFont();
        System.out.println(f.toString());
        AffineTransform affineTransform = f.getTransform();
        fontRenderContext = new FontRenderContext(affineTransform, false, false);
        
        union = makeShape(f);
        
        int iterations = (int)math.random(5, 15);
        System.out.println("Iterations: "+iterations+ " Scale: "+scale);
        for(int i = 0; i < iterations; i++) {
            Area a = makeShape(f);
            union.add(a);
        }
        
        // Convert the random shape into a general path
        GeneralPath gp = new GeneralPath((Shape)union);
        AlcShape alcShape = new AlcShape(gp, SOLID);
        
        return alcShape;
    }
    
    public Area makeShape(Font font){
        // Make a string from one random char from the letters string
        String randomLetter = Character.toString(letters.charAt((int)math.random(letters.length())));
        
        GlyphVector gv = font.createGlyphVector(fontRenderContext, randomLetter);
        Shape shp = gv.getOutline();
        //Shape shp = gv.getOutline(math.random(150), math.random(150));
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
        
        // Move the shape to the middle of the screen
        AffineTransform newTr = new AffineTransform();
        newTr.translate(math.random(root.getWindowSize().width), math.random(root.getWindowSize().height));
        
        // Rotate the shape randomly
        newTr.rotate(math.random(TWO_PI));
        Area newA = new Area(newShape);
        return newA.createTransformedArea(newTr);
        
    }
    
    public float mess(float f){
        noiseScale += 0.002F;
        float n =  (math.noise(noiseScale) * doubleScale) - scale;
        //System.out.println(n);
        return n * f;
    }
    
    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        //root.println(keyCode);
        
        switch(keyCode){
            case BACKSPACE:
            case DELETE:
                
                //System.out.println("DELETE");
                //root.canvas.clear();
                break;
                
            case SPACE:
                
                //System.out.println("SPACE");
                root.canvas.previewShape(randomShape());
                break;
                
        }
    }
    
}
