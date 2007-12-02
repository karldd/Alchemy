/**
 * AlcLabel.java
 *
 * Created on December 1, 2007, 12:39 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import java.awt.Font;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AlcLabel extends JLabel{
    
    AlcToolBar parent;
    
    /** Creates a new instance of AlcLabel */
    public AlcLabel(AlcToolBar parent, String text, URL iconUrl, String description) {
        
        //this.setVerticalAlignment(SwingConstants.TOP);
        
        if(iconUrl != null){
            // Set the sub toolbars' Icon
            this.setIcon( parent.createImageIcon(iconUrl) );
        }
        
        this.setFont(new Font("sansserif", Font.BOLD, parent.getToolBarTextSize()+1));
        this.setText(text);
        this.setToolTipText(description);
        //this.setVerticalTextPosition(SwingConstants.BOTTOM);
        
        // Insets(int top, int left, int bottom, int right)
        //this.setMargin(new Insets(4, 8, 8, 4));
        
        // Cant set the margins so make an empty border to adjust the spacing
        // EmptyBorder(int top, int left, int bottom, int right) 
        this.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 4));
        
        //this.setIconTextGap(10);
        
        //this.setBackground(parent.getUiBgColour());
        //this.setPreferredSize(new Dimension(100, 65));
        //this.setBorder(BorderFactory.createRaisedBevelBorder());
        
        
    }
    
}
