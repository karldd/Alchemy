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

public class AlcMainButton extends JButton{
    
    AlcToolBar parent;
    
    /**
     * Creates a new instance of AlcMainButton
     */
    public AlcMainButton(AlcToolBar parent, String text, String toolTip, URL iconUrl) {
        
        this.parent = parent;
        //this.setUI(new BasicButtonUI());
        
        if(iconUrl != null){
            // Set the main Icon
            this.setIcon( parent.createImageIcon(iconUrl) );
            //System.out.println(iconUrl);
            
            URL rolloverIconUrl = parent.appendStringToUrl(iconUrl, "-over");
            //System.out.println(rolloverIconUrl);
            this.setRolloverIcon(parent.createImageIcon(rolloverIconUrl));
            
        }
        
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        //this.setVerticalAlignment(SwingConstants.TOP);
        this.setText(text);
        this.setToolTipText(toolTip);
        
        // Insets(int top, int left, int bottom, int right)
        this.setMargin(new Insets(4, 8, 8, 4));
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
