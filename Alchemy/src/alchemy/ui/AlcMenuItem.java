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
import javax.swing.JMenuItem;

public class AlcMenuItem extends JMenuItem {
    
    AlcToolBar parent;
    
    /** Creates a new instance of AlcMenuItem */
    public AlcMenuItem(AlcToolBar parent, String text) {
        URL nullUrl = null;
        setup(parent, text, nullUrl);
    }
    
    /** Creates a new instance of AlcMenuItem */
    public AlcMenuItem(AlcToolBar parent, String text, URL iconUrl) {
        setup(parent, text, iconUrl);
    }
    
    private void setup(AlcToolBar parent, String text, URL iconUrl){
     
        this.parent = parent;
     
        this.setText(text);
        if(iconUrl != null){
            // Set the main Icon
            this.setIcon( parent.createImageIcon(iconUrl) );
            
            // TODO - Rollovers not working??
            //URL rolloverIconUrl = parent.appendStringToUrl(iconUrl, "-over");
            //System.out.println(rolloverIconUrl);
            //this.setRolloverIcon(parent.createImageIcon(rolloverIconUrl));
            
        }
        
        this.setOpaque(true);
        //this.setMargin(new Insets(100, 100, 100, 100));
        //this.setBorderPainted(false);
        //this.setContentAreaFilled(false);
        //this.setFocusPainted(false);
        //
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        
        //this.setPreferredSize(new Dimension(100, 50));
        
        this.setBackground(parent.toolBarHighlightColour);
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));
        
    }
    
}