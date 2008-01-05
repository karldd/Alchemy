/**
 * AlcSubLabel.java
 *
 * Created on December 1, 2007, 12:39 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class AlcSubLabel extends JLabel {

    /** Creates a new instance of AlcSubLabel */
    public AlcSubLabel(String text) {

        this.setFont(AlcToolBar.toolBarFont);
        this.setText(text);

        //this.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Cant set the margins so make an empty border to adjust the spacing
        // EmptyBorder(int top, int left, int bottom, int right) 
        this.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

    //this.setIconTextGap(10);

    //this.setBackground(parent.getUiBgColour());
    //this.setPreferredSize(new Dimension(100, 65));
    //this.setBorder(BorderFactory.createRaisedBevelBorder());


    }
}
