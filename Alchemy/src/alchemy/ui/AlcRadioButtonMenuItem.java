/**
 * AlcMenuItem.java
 *
 * Created on November 28, 2007, 1:48 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import alchemy.AlcModule;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JRadioButtonMenuItem;
//import javax.swing.plaf.basic.BasicMenuItemUI;

public class AlcRadioButtonMenuItem extends JRadioButtonMenuItem {
    
    // TODO - find some way to avoid the ugly round selected circle mark - possibly by adding a normal radiobutton see here:
    // http://www.onjava.com/pub/a/onjava/excerpt/swing_14/index6.html?page=2
    
    private AlcToolBar parent;
    private AlcModule module;
    private int index, moduleType;
    
    /** Creates a new instance of AlcCheckBoxMenuItem */
    public AlcRadioButtonMenuItem(AlcToolBar parent, AlcModule module) {
        
        this.parent = parent;
        this.index = module.getIndex();
        this.moduleType = module.getModuleType();
        
        // Set the intial state to false
        //this.setState(true);
        
        this.setText(module.getName());
        // Set the main Icon
        this.setIcon( parent.createImageIcon( module.getIconUrl() ) );
        
        
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        this.setBackground(parent.toolBarHighlightColour);
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));
        
    }
    
    public int getIndex(){
        return index;
    }
    
    public int getModuleType(){
        return moduleType;
    }
    
}