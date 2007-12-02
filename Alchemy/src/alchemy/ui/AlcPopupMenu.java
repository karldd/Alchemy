/**
 * AlcPopupMenu.java
 *
 * Created on November 27, 2007, 10:38 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

public class AlcPopupMenu extends JPopupMenu{
    
    AlcToolBar parent;
    
    /** Creates a new instance of AlcPopupMenu */
    public AlcPopupMenu(AlcToolBar parent) {
        
        this.parent = parent;
        
        // Set the colour for the bg
        this.setBackground(parent.toolBarHighlightColour);
        //this.setBorder(BorderFactory.createLineBorder(parent.toolBarLineColour, 1));
        
        // Add some 'padding' Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        //this.setPopupSize(100, 100);
        //this.setMinimumSize(new Dimension(200, 100));
        //this.setBorder(BorderFactory.createLineBorder(Color.black));
        //this.setUI(new BasicPopupMenuUI());
        //this.setLabel(label);
        //this.setBorderPainted(false);
        //this.add(new JMenuItem("yes"));
        
        
    }
    
}
