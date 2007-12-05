/**
 * AlcButton.java
 *
 * Created on November 26, 2007, 9:34 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import java.awt.Font;
import java.awt.Insets;
import java.net.URL;
import javax.swing.*;

public class AlcSubToggleButton extends JToggleButton{
    
    AlcToolBar parent;
    
    /**
     * Creates a new instance of AlcMainButton
     */
    public AlcSubToggleButton(AlcToolBar parent, String text, URL iconUrl) {
        
        this.parent = parent;
        
        if(iconUrl != null){
            // Set the main icon
            this.setIcon( parent.createImageIcon(iconUrl) );
            // Set the rollover icon
            URL rolloverIconUrl = parent.appendStringToUrl(iconUrl, "-over");
            this.setRolloverIcon(parent.createImageIcon(rolloverIconUrl));
            // Set the selected icon
            URL selectedIconUrl = parent.appendStringToUrl(iconUrl, "-on");
            this.setSelectedIcon(parent.createImageIcon(selectedIconUrl));
            // Set the rollover - selected icon
            URL rolloverSelectedIconUrl = parent.appendStringToUrl(iconUrl, "-on-over");
            this.setRolloverSelectedIcon(parent.createImageIcon(rolloverSelectedIconUrl));
        }
        
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));
        this.setText(text);
        // Insets(int top, int left, int bottom, int right)
        this.setMargin(new Insets(4, 8, 8, 4));
        
        this.setBorderPainted(false);    // Draw the button shape
        this.setContentAreaFilled(false);  // Draw the background behind the button
        this.setFocusPainted(false);       // Draw the highlight when focused
    }
    
}
