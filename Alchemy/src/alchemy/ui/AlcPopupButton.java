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
import javax.swing.ButtonGroup;

public class AlcPopupButton extends AlcMainButton {
    
    //private ArrayList<AlcModule> moduleList;
    private final int uiPopupMenuY = parent.getToolBarHeight() - 10;
    AlcPopupMenu popup;
    
    /** Creates a new instance of AlcPopupButton */
    public AlcPopupButton(AlcToolBar parent, String text, String toolTip, URL iconUrl, AlcModule[] moduleList) {
        super(parent, text, toolTip, iconUrl);
        //this.moduleList = moduleList;
        
        // PopupMenus on the buttons
        if(moduleList != null){
            popup = new AlcPopupMenu(parent);
            
            // Get the name of the package
            String s = moduleList[0].getClass().getPackage().getName();
            // Get the last part of the package name - the type of module
            String command = s.substring(s.lastIndexOf(".")+1);
            
            ButtonGroup group = new ButtonGroup();
            
            // Populate the Popup Menu
            for (int i = 0; i < moduleList.length; i++) {
                // The current module
                AlcModule currentModule = moduleList[i];
                
                // Add check box menu items if it is an affect popup, else add normal menu items
                if(command.equals("affect")){
                    
                    AlcCheckBoxMenuItem menuItem = new AlcCheckBoxMenuItem(parent, currentModule.getName(), currentModule.getIconUrl());
                    menuItem.setToolTipText(currentModule.getDescription());
                    // Set the index and command to retrieve when the button is triggered
                    menuItem.setIndex(i);
                    menuItem.setCommand(command);
                    
                    // Set the action command and listener
                    // menuItem.setActionCommand(command + "-" + i);
                    menuItem.addItemListener(parent);
                    //menuItem.addActionListener(parent);
                    popup.add(menuItem);
                    
                } else {
                    
                    AlcRadioButtonMenuItem menuItem = new AlcRadioButtonMenuItem(parent, currentModule.getName(), currentModule.getIconUrl());
                    menuItem.setToolTipText(currentModule.getDescription());
                    //menuItem.
                    
                    // Set the action command and listener
                    // menuItem.setActionCommand(command + "-" + i);
                    menuItem.addActionListener(parent);
                    if(i == 0)
                        menuItem.setSelected(true);
                    
                    // Set the index and command to retrieve when the button is triggered
                    menuItem.setIndex(i);
                    menuItem.setCommand(command);
                    
                    group.add(menuItem);
                    popup.add(menuItem);
                    
                    // For testing purposes - REMOVE
                    AlcRadioButtonMenuItem menuItem2 = new AlcRadioButtonMenuItem(parent, "Something Else", parent.getUrlPath("../data/icon.png"));
                    group.add(menuItem2);
                    popup.add(menuItem2);
                    
                }
                
            }
            
            //createPopup.add(new JCheckBoxMenuItem("Hello", true));
            //popup.add(new AlcCheckBoxMenuItem(parent, "Something Else", parent.getUrlPath("../data/icon.png")));
            //createPopup.addSeparator();
            
            
            // Add a mouse listener to detect when the button is pressed and display the popup menu
            this.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    popup.show(e.getComponent(), 0, uiPopupMenuY);
                }
            });
            
        }
        
    }
    
    public void hidePopup(){
        popup.setVisible(false);
    }
    
    
}
