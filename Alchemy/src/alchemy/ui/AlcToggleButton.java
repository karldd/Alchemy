/**
 * AlcButton.java
 *
 * Created on November 26, 2007, 9:34 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import alchemy.AlcUtil;
import java.awt.Insets;
import java.net.URL;
import javax.swing.*;

public class AlcToggleButton extends JToggleButton{
    
    AlcToolBar parent;
    
    /**
     * Creates a new instance of AlcButton
     */
    public AlcToggleButton(AlcToolBar parent, String text, String toolTip, URL iconUrl) {
        
        this.parent = parent;
        
        if(iconUrl != null){
            // Set the main icon
            this.setIcon( parent.createImageIcon(iconUrl) );
            // Set the rollover icon
            URL rolloverIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-over");
            this.setRolloverIcon(parent.createImageIcon(rolloverIconUrl));
            // Set the selected icon
            URL selectedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-on");
            this.setSelectedIcon(parent.createImageIcon(selectedIconUrl));
            // Set the rollover - selected icon
            URL rolloverSelectedIconUrl = AlcUtil.appendStringToUrl(iconUrl, "-on-over");
            this.setRolloverSelectedIcon(parent.createImageIcon(rolloverSelectedIconUrl));
        }
        
        
        this.setFont(AlcToolBar.toolBarFont);
        this.setVerticalTextPosition(SwingConstants.BOTTOM);
        this.setHorizontalTextPosition(SwingConstants.CENTER);
        this.setText(text);
        this.setToolTipText(toolTip);
        
        // Insets(int top, int left, int bottom, int right)
        this.setMargin(new Insets(4, 8, 8, 4));
        this.setBorderPainted(false);    // Draw the button shape
        this.setContentAreaFilled(false);  // Draw the background behind the button
        this.setFocusPainted(false);       // Draw the highlight when focused
    }
    
}
