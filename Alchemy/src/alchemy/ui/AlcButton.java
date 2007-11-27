/**
 * AlcButton.java
 *
 * Created on November 26, 2007, 9:34 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

//import java.awt.Color;
//import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.*;
//import javax.swing.plaf.basic.*;

public class AlcButton extends JButton{
    
    AlcUi parent;
    
    /** Creates a new instance of AlcButton */
    public AlcButton(AlcUi parent, String buttonText) {
        
        this.parent = parent;
        //this.setUI(new BasicButtonUI());
        
        ImageIcon icon = createImageIcon("../resources/icon.png");
        ImageIcon iconOver = createImageIcon("../resources/icon-over.png");
        
        this.setIcon(icon);
        this.setRolloverIcon(iconOver);
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getUiTextSize()));
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        //this.setVerticalAlignment(SwingConstants.TOP);
        this.setText(buttonText);
        
        // Insets(int top, int left, int bottom, int right) 
        this.setMargin(new Insets(8, 8, 8, 8));
        //this.setIconTextGap(-24);
        
        //this.setBackground(parent.getUiBgColour());
        //this.setPreferredSize(new Dimension(100, 65));
        //this.setBorder(BorderFactory.createRaisedBevelBorder());
        
        //System.out.println(this.getUI());
        
        this.setBorderPainted(false);    // Draw the button shape
        this.setContentAreaFilled(false);  // Draw the background behind the button
        this.setFocusPainted(false);       // Draw the highlight when focused
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
