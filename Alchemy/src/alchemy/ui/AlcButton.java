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
import java.net.URL;
import javax.swing.*;
//import javax.swing.plaf.basic.*;

public class AlcButton extends JButton{
    
    AlcUi parent;
    
    /** Creates a new instance of AlcButton */
    public AlcButton(AlcUi parent, String text, URL iconUrl) {
        
        this.parent = parent;
        //this.setUI(new BasicButtonUI());
        
        if(iconUrl != null){
            // Set the main Icon
            this.setIcon( parent.createImageIcon(iconUrl) );
            System.out.println(iconUrl);
            
            URL rolloverIconUrl = parent.appendStringToUrl(iconUrl, "-over");
            System.out.println(rolloverIconUrl);
            this.setRolloverIcon(parent.createImageIcon(rolloverIconUrl));
            
        }
        
        
        // TODO Make a function to read image icons and append the correct file names
        //ImageIcon icon = parent.createImageIcon("../data/icon.png");
        //ImageIcon iconOver = parent.createImageIcon("../data/icon-over.png");
        //this.setIcon(icon);
        // this.setRolloverIcon(iconOver);
        
        
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getUiTextSize()));
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        //this.setVerticalAlignment(SwingConstants.TOP);
        this.setText(text);
        
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
    
}
