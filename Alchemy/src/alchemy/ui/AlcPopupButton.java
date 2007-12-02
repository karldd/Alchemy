/**
 * AlcPopupButton.java
 *
 * Created on December 2, 2007, 10:27 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import alchemy.AlcModule;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;

public class AlcPopupButton extends AlcMainButton {
    
    ArrayList<AlcModule> moduleList;
    private final int uiPopupMenuY = parent.getToolBarHeight() - 10;
    AlcPopupMenu popup;
    
    /** Creates a new instance of AlcPopupButton */
    public AlcPopupButton(AlcToolBar parent, String text, String toolTip, URL iconUrl, ArrayList<AlcModule> moduleList) {
        super(parent, text, toolTip, iconUrl);
        //this.moduleList = moduleList;
        
        // PopupMenus on the buttons
        if(moduleList != null){
            popup = new AlcPopupMenu(parent);
            
            // Get the name of the package
            String s = moduleList.get(0).getClass().getPackage().getName();
            // Get the last part of the package name - the type of module
            String command = s.substring(s.lastIndexOf(".")+1);
            
            // Populate the Popup Menu
            for (int i = 0; i < moduleList.size(); i++) {
                // The current module
                AlcModule currentModule = moduleList.get(i);
                
                // Set the text and icon
                AlcMenuItem menuItem = new AlcMenuItem(parent, currentModule.getName(), currentModule.getIconUrl());
                menuItem.setToolTipText(currentModule.getDescription());
                
                // Set the action command and listener
                menuItem.setActionCommand(command + "-" + i);
                menuItem.addActionListener(parent);
                popup.add(menuItem);
                
            }
            //createPopup.add(new JCheckBoxMenuItem("Hello", true));
            popup.add(new AlcMenuItem(parent, "Something Else", parent.getUrlPath("../data/icon.png")));
            //createPopup.addSeparator();
            
            
            // Add a mouse listener to detect when the button is pressed and display the popup menu
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    popup.show(e.getComponent(), 0, uiPopupMenuY);
                }
            });
            
        }
        
        
        
    }
    
    
    
}
