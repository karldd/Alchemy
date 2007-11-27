/**
 * AlcPopupMenu.java
 *
 * Created on November 27, 2007, 10:38 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class AlcPopupMenu extends JPopupMenu{
    
    AlcUi parent;
    
    /** Creates a new instance of AlcPopupMenu */
    public AlcPopupMenu(AlcUi parent) {
        
        this.parent = parent;
        
        
    }
    
}
