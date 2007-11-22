/**
 * AlcCanvas.java
 *
 * Created on November 16, 2007, 4:09 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import java.util.ArrayList;

import java.awt.geom.GeneralPath;

public class AlcCanvas extends JComponent implements AlcConstants{
    
    ArrayList<AlcShape> shapes;
    
    /** Creates a new instance of AlcCanvas */
    public AlcCanvas() {
        this.setPreferredSize(new Dimension(800, 600));  // size
    }
    
    @Override public void paintComponent(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //... Paint background.
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        
        if(shapes != null){
            for (int i = 0; i < shapes.size(); i++) {
                AlcShape currentShape = shapes.get(i);
                if(currentShape.getStyle() == 1){
                    float strokeWidth = (float)currentShape.getLineWidth();
                    g2.setStroke(new BasicStroke(strokeWidth));
                    g2.setColor(currentShape.getColour());
                } else{
                    g2.setColor(currentShape.getColour());
                }
                g2.draw(currentShape.getShape());
            }
        }
    }
    
    public void draw(ArrayList<AlcShape> shapes){
        this.shapes = shapes;
        this.repaint();
    }
    
}
