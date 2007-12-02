/**
 * AlcMenuItem.java
 *
 * Created on November 28, 2007, 1:48 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import java.awt.Font;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;

public class AlcCheckBoxMenuItem extends JCheckBoxMenuItem {
    
    AlcToolBar parent;
    
    /** Creates a new instance of AlcCheckBoxMenuItem */
    public AlcCheckBoxMenuItem(AlcToolBar parent, String text) {
        URL nullUrl = null;
        setup(parent, text, nullUrl);
    }
    
    /** Creates a new instance of AlcCheckBoxMenuItem */
    public AlcCheckBoxMenuItem(AlcToolBar parent, String text, URL iconUrl) {
        setup(parent, text, iconUrl);
    }
    
    private void setup(AlcToolBar parent, String text, URL iconUrl){
     
        this.parent = parent;
        // Set the intial state to false
        //this.setState(true);
        
        this.setText(text);
        if(iconUrl != null){
            // Set the main Icon
            this.setIcon( parent.createImageIcon(iconUrl) );
        }
        
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        this.setBackground(parent.toolBarHighlightColour);
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));
        
    }
    
}