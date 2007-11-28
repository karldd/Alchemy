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
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class AlcMenuItem extends JMenuItem {
    
    AlcUi parent;
    
    /** Creates a new instance of AlcMenuItem */
    public AlcMenuItem(AlcUi parent, String text) {
        URL nullUrl = null;
        setup(parent, text, nullUrl);
    }
    
    /** Creates a new instance of AlcMenuItem */
    public AlcMenuItem(AlcUi parent, String text, URL iconUrl) {
        setup(parent, text, iconUrl);
    }
    
    private void setup(AlcUi parent, String text, URL iconUrl){
     
        this.parent = parent;
     
        this.setText(text);
        if(iconUrl != null){
            // Set the main Icon
            this.setIcon( parent.createImageIcon(iconUrl) );
            
            // TODO Rollovers not working??
            //URL rolloverIconUrl = parent.appendStringToUrl(iconUrl, "-over");
            //System.out.println(rolloverIconUrl);
            //this.setRolloverIcon(parent.createImageIcon(rolloverIconUrl));
            
        }
        //this.setMargin(new Insets(100, 100, 100, 100));
        //this.setBorderPainted(false);
        
        
        //this.setPreferredSize(new Dimension(100, 50));
        
        this.setBackground(parent.getUiBgColour());
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getUiTextSize()));
        
    }
    
}
