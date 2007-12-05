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
import java.awt.geom.GeneralPath;

import java.util.ArrayList;

public class AlcCanvas extends JComponent implements AlcConstants, MouseMotionListener, MouseListener{
    
    /** Reference to the root **/
    private AlcMain root;
    
    // GLOBAL SETTINGS
    /** 'Redraw' on or off **/
    private boolean redraw = true;
    /** Drawing on or off - stop mark making when inside the UI */
    private boolean draw = true;
    /** Smoothing on or off */
    private boolean smoothing = true;
    
    // SHAPE DEFAULTS
    /** Colour of this shape */
    private Color colour = Color.BLACK;
    /** Alpha of this shape */
    private int alpha = 255;
    /** Style of this shape - (1) LINE or (2) SOLID FILL */
    private int style = LINE;
    /** Line Weight if the style is line */
    private int lineWidth = 1;
    
    
    /** Array list to contain the shapes created */
    private ArrayList<AlcShape> shapes;
    /** Temporary Shape */
    private AlcShape tempShape;
    /** Graphics */
    Graphics2D g2;
    
    /** Creates a new instance of AlcCanvas */
    public AlcCanvas(AlcMain root) {
        this.root = root;
        
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setBounds(0, 0, root.getWindowSize().width, root.getWindowSize().height);
        
        shapes = new ArrayList<AlcShape>(100);
        shapes.ensureCapacity(100);
        
    }
    
    @Override public void paintComponent(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        
        g2 = (Graphics2D)g;
        if(smoothing) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        
        //... Paint background.
        g2.setPaint(root.getBgColour());
        g2.fillRect(0, 0, w, h);
        
        
        // Draw all the shapes
        if(shapes != null){
            for (int i = 0; i < shapes.size(); i++) {
                
                AlcShape currentShape = shapes.get(i);
                
                // LINE
                if(currentShape.getStyle() == LINE){
                    
                    float strokeWidth = (float)currentShape.getLineWidth();
                    g2.setStroke(new BasicStroke(strokeWidth));
                    g2.setPaint(currentShape.getColour());
                    g2.draw(currentShape.getShape());
                    
                    // SOLID
                } else{
                    
                    g2.setPaint(currentShape.getColour());
                    g2.fill(currentShape.getShape());
                    
                }
                
            }
        }
        
        
        
        // Draw the tempShape if present
        if(tempShape != null){
            
            // LINE
            if(tempShape.getStyle() == LINE){
                
                float strokeWidth = (float)tempShape.getLineWidth();
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.setPaint(tempShape.getColour());
                g2.draw(tempShape.getShape());
                
                // SOLID
            } else{
                
                g2.setPaint(tempShape.getColour());
                g2.fill(tempShape.getShape());
                
            }
            
        }
        
    }
    
    
    // CANVAS FUNCTIONALITY
    /** Redraw the canvas */
    public void redraw(){
        if(redraw){
            this.repaint();
        }
    }
    
    /** Resize the canvas - called when the window is resized */
    public void resizeCanvas(Dimension windowSize){
        this.setBounds(0, 0, windowSize.width, windowSize.height);
    }
    
    /** Clear the canvas */
    public void clear() {
        tempShape = null;
        shapes.clear();
        redraw();
    }
    
    /** Set the canvas redraw state */
    public void setRedraw(boolean redraw){
        this.redraw = redraw;
    }
    
    /** Get the canvas redraw state */
    public boolean getRedraw(){
        return redraw;
    }
    
    /** A temporary shape stored seperately.
     *  Used by shapeGenerators to browse generated shapes.
     *  Mouse Interaction adds this shape to the main shapes array
     */
    public void previewTempShape(GeneralPath tempGp){
        
        // Create the shape with the global characteristics
        AlcShape tempShape = new AlcShape(tempGp, colour, alpha, style, lineWidth);
        
        // If there is an affect selected
        if(root.hasCurrentAffects()){
            for (int i = 0; i < root.currentAffects.length; i++) {
                if(root.currentAffects[i])
                    root.affects[i].processShape(tempShape);
            }
        }
        
        this.tempShape = tempShape;
        redraw();
    }
    
    /** A temporary shape stored seperately.
     *  Used as a buffer before it is added to the shapes array.
     *  Stops the shapes from constantly adding to themselves while marks are being made.
     */
    public void addTempShape(AlcShape tempShape){
        this.tempShape = tempShape;
        //redraw();
    }
    
    /** Commit the temporary shape to the main shapes array */
    private void commitTempShape(){
        if(tempShape != null){
            shapes.add( tempShape );
            tempShape = null;
        }
    }
    
    /** Set Antialiasing */
    public void setSmoothing(boolean b) {
        if (b){ // ON
            if(!smoothing) smoothing = true;
            
        } else { // OFF
            if(smoothing) smoothing = false;
        }
    }
    
    /** Get Antialiasing */
    public boolean getSmoothing() {
        return smoothing;
    }
    
    
    /** Function to control the display of the Ui toolbar */
    private void toggleToolBar(MouseEvent e){
        int y = e.getY();
        if(y < 10){
            if(!root.toolBar.getToolBarVisible()){
                root.toolBar.setToolBarVisible(true);
                // Turn drawing off while in the toolbar
                draw = false;
            }
        } else if (y > root.toolBar.getTotalHeight()){
            if(root.toolBar.getToolBarVisible()){
                root.toolBar.setToolBarVisible(false);
                // Turn drawing on once out of the UI
                draw = true;
            }
        }
    }
    
    public void setColour(Color colour){
        this.colour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), alpha);
    }
    
    public void setAlpha(int alpha){
        this.alpha = alpha;
        setColour(this.colour);
    }
    
    public void setStyle(int style){
        this.style = style;
    }
    
    public void setLineWidth(int lineWidth){
        this.lineWidth = lineWidth;
    }
    
    
    // MOUSE EVENTS
    public void mouseMoved(MouseEvent e)    {
        
        // Toogle visibility of the Toolbar
        toggleToolBar(e);
        
    }
    
    public void mousePressed(MouseEvent e)  {
        
        commitTempShape();
        // Create a new shape
        if(draw) {
            shapes.add( new AlcShape(e.getPoint(), colour, alpha, style, lineWidth) );
            
            // Pass along the shape to the affect(s) to be initialised
            if(root.hasCurrentAffects()){
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if(root.currentAffects[i])
                        root.affects[i].initialiseShape( shapes.get(shapes.size()-1) );
                }
            }
            
        }
        
    }
    
    public void mouseClicked(MouseEvent e)  { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseExited(MouseEvent e)   { }
    
    public void mouseReleased(MouseEvent e) {
        commitTempShape();
    }
    
    public void mouseDragged(MouseEvent e)  {
        
        // Add points to the shape
        if(draw){
            AlcShape currentShape = shapes.get(shapes.size()-1);
            currentShape.drag(e.getPoint());
            
            // Pass this shape to the affect(s) be processed
            if(root.hasCurrentAffects()){
                for (int i = 0; i < root.currentAffects.length; i++) {
                    if(root.currentAffects[i])
                        root.affects[i].incrementShape(currentShape);
                }
            }
            
            // Redraw the screen
            redraw();
        }
        
    }
    
}
