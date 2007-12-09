/**
 * AlcSubToolBar.java
 *
 * Created on November 28, 2007, 6:45 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import alchemy.*;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class AlcSubToolBar extends JPanel{
    
    private AlcMain root;
    private AlcToolBar parent;
    private AlcModule module;
    private int height = 42;
    
    /** Creates a new instance of AlcSubToolBar */
    public AlcSubToolBar(AlcMain root, AlcModule module, String title, URL iconUrl, String description) {
        
        this.root = root;
        this.parent = root.toolBar;
        this.module = module;
        
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        //this.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        this.setPreferredSize(new Dimension(root.getWindowSize().width, height));
        
        this.add(new AlcLabel(parent, title, iconUrl, description));
        
        //this.setAlignmentY(Component.TOP_ALIGNMENT);
        
        //this.setBackground(Color.BLACK);
        //this.setLocation(0, parent.getToolBarHeight());
        //this.setBounds(500, root.getWindowSize().width, 600, 25);
        
        //JButton ok = new JButton("OK");
        //this.add(ok);
    }
    
    
    // Override the paint component to draw the gradient bg
    @Override protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        
        //int panelWidth = getWidth();
        
        GradientPaint gradientPaint = new GradientPaint( 0 , 0 , new Color(215, 215, 215) , 0 , this.height , new Color(207, 207, 207), true );
        if( g instanceof Graphics2D ) {
            Graphics2D g2 = (Graphics2D)g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setPaint( gradientPaint );
            g2.fillRect( 0 , 0 , root.getWindowSize().width , this.height );
            g2.setPaint( AlcToolBar.toolBarHighlightColour );
            g2.drawLine(0, 0, root.getWindowSize().width, 0);
            g2.setPaint( AlcToolBar.toolBarLineColour );
            g2.drawLine(0, this.height-1, root.getWindowSize().width, this.height-1);
        }
    }
    
    
    @Override
    public int getHeight(){
        return this.height;
    }
    
    public int getIndex(){
        return module.getIndex();
    }
    
    public int getModuleType(){
        return module.getModuleType();
    }
}
