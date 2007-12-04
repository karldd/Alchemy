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
import javax.swing.JRadioButtonMenuItem;
//import javax.swing.plaf.basic.BasicMenuItemUI;

public class AlcRadioButtonMenuItem extends JRadioButtonMenuItem {
    
    // TODO - find some way to avoid the ugly round selected circle mark - possibly by adding a normal radiobutton see here:
    // http://www.onjava.com/pub/a/onjava/excerpt/swing_14/index6.html?page=2
    
    AlcToolBar parent;
    private int index;
    private String command;
    
    /**
     * Creates a new instance of AlcRadioButtonMenuItem
     */
    public AlcRadioButtonMenuItem(AlcToolBar parent, String text) {
        URL nullUrl = null;
        setup(parent, text, nullUrl);
    }
    
    /**
     * Creates a new instance of AlcRadioButtonMenuItem
     */
    public AlcRadioButtonMenuItem(AlcToolBar parent, String text, URL iconUrl) {
        setup(parent, text, iconUrl);
    }
    
    private void setup(AlcToolBar parent, String text, URL iconUrl){
        
        //this.setUI(new BasicMenuItemUI());
        
        this.parent = parent;
        
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
    
    
    public void setIndex(int index){
        this.index = index;
    }
    
    public int getIndex(){
        return index;
    }
    
    public void setCommand(String command){
        this.command = command;
    }
    
    public String getCommand(){
        return command;
    }
    
}