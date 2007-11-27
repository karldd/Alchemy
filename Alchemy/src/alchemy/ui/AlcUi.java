/**
 * AlcUi.java
 *
 * Created on November 24, 2007, 3:08 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import alchemy.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AlcUi extends JPanel implements ActionListener { // Extend JPanel rather than JComponent so the background can be set
    
    /** Reference to the root **/
    private AlcMain root;
    /** Visibility of the Ui */
    private boolean visible = true;
    /** Height of the Ui */
    private int uiHeight = 65;
    /** Ui Background Colour */
    private static Color uiBgColour = new Color(230, 230, 230);
    /** Ui Text Size */
    private static final int uiTextSize = 10;
    /** Ui Popup Menu Y Location */
    private static final int uiPopupMenuY = 55;
    
    /** Combo Box drop down menu with the list of create functions */
    JComboBox createComboBox;
    /** Combo Box drop down menu with the list of affect functions */
    JComboBox affectComboBox;
    /** Action command for 'creates' */
    private static String CREATE_COMMAND = "create";
    /** Action command for 'affects' */
    private static String AFFECT_COMMAND = "affect";
    
    private JPopupMenu createPopup;
    
    /** Creates a new instance of AlcUi */
    public AlcUi(AlcMain root, Dimension windowSize, String[] createNames, String[] affectNames) {
        
        // General Toolbar settings
        this.root = root;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(uiBgColour);
        setUiVisible(false);
        this.setLocation(0, 0);
        this.setBounds(0, 0, windowSize.width, uiHeight);
        
        // Buttons
        AlcButton markButton = new AlcButton(this, "Marks");
        this.add(markButton);
        
        AlcButton createButton = new AlcButton(this, "Create");
        this.add(createButton);
        
        // PopupMenus on the buttons
        if(createNames != null){
            createPopup = new AlcPopupMenu(this);
            
            // Populate the Popup Menu
            for (int i = 0; i < createNames.length; i++) {
                JMenuItem menuItem = new JMenuItem(createNames[i]);
                menuItem.setActionCommand(CREATE_COMMAND);
                menuItem.addActionListener(this);
                createPopup.add(menuItem);
            }
        }
        
        /*
        //JMenuItem item;
        popup.add(new JMenuItem("Cut"));
        popup.addSeparator();
        popup.add(new JMenuItem("Copy"));
        popup.add(new JMenuItem("Copy"));
        popup.add(new JMenuItem("Copy"));
        popup.add(new JMenuItem("Copy"));
         
         */
        //popup.setBorderPainted(false);
        //popup.setInvoker(mybtn);
        
        createButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                createPopup.show(e.getComponent(), 0, uiPopupMenuY);
            }
        });
        
        //JToggleButton tbtn = new JToggleButton("Yes");
        //this.add(tbtn);
        
        
        /*
        if(createNames != null){
         
            createComboBox = new AlcComboBox(this, createNames);
         
            createComboBox.setActionCommand(CREATE_COMMAND);
            createComboBox.addActionListener(this);
            this.add(createComboBox);
        }
         */
        
        /*
        if(affectNames != null){
            affectComboBox = new JComboBox(affectNames);
            //affectComboBox.setSelectedIndex(2);
            //affectComboBox.setActionCommand(LAYER_COMMAND);
            //affectComboBox.addActionListener(this);
            this.add(affectComboBox);
        }
         */
        
        
        
    }
    
    public void resizeUi(Dimension windowSize){
        this.setBounds(0, 0, windowSize.width, uiHeight);
    }
    
    /** Set the Visibility of the Ui */
    public void setUiVisible(boolean toggle){
        
        if(toggle){ // TURN ON
            if(!visible) {
                this.setVisible(true);
                //System.out.println("ON");
                visible = true;
            }
            
            
        } else { // TURN OFF
            if(visible) {
                this.setVisible(false);
                //System.out.println("OFF");
                // Turn off the popupMenu too!
                if(createPopup != null) createPopup.setVisible(false);
                visible = false;
            }
        }
        
    }
    
    
    // GETTERS
    /** Return the height of the UI Toolbar */
    public int getUiHeight(){
        return uiHeight;
    }
    
    /** Return the colour of the UI toolbar background */
    public Color getUiBgColour(){
        return uiBgColour;
    }
    
    /** Return the height of the UI Toolbar */
    public int getUiTextSize(){
        return uiTextSize;
    }
    
    
    
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if (CREATE_COMMAND.equals(cmd)) {
            
            root.setCurrentCreate( createComboBox.getSelectedIndex() );
            
        } else if (AFFECT_COMMAND.equals(cmd)) {
            
            
        }
    }
    
}
