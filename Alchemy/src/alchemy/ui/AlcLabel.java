/**
 * AlcLabel.java
 *
 * Created on December 1, 2007, 12:39 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.ui;

import alchemy.AlcUtil;
import java.net.URL;
import javax.swing.JLabel;

public class AlcLabel extends JLabel {

    /** Creates a new instance of AlcLabel */
    public AlcLabel(String text, URL iconUrl) {

        //this.setVerticalAlignment(SwingConstants.TOP);

        if (iconUrl != null) {
            // Set the sub toolbars' Icon
            this.setIcon(AlcUtil.createImageIcon(iconUrl));
        }

        this.setFont(AlcToolBar.subToolBarFont);
        this.setText(text);
        
        //this.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Cant set the margins so make an empty border to adjust the spacing
        // EmptyBorder(int top, int left, int bottom, int right) 
        //this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

    //this.setIconTextGap(10);

    //this.setBackground(parent.getUiBgColour());
    //this.setPreferredSize(new Dimension(100, 65));
    //this.setBorder(BorderFactory.createRaisedBevelBorder());


    }
}
