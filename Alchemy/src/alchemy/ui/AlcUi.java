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
    private int uiHeight = 50;
    /** Ui Background Colour */
    private static Color uiBgColour = new Color(230, 230, 230);
    
    /** Combo Box drop down menu with the list of create functions */
    JComboBox createComboBox;
    /** Combo Box drop down menu with the list of affect functions */
    JComboBox affectComboBox;
    /** Action command for 'creates' */
    private static String CREATE_COMMAND = "create";
    /** Action command for 'affects' */
    private static String AFFECT_COMMAND = "affect";
    
    /** Creates a new instance of AlcUi */
    public AlcUi(AlcMain root, Dimension windowSize, String[] createNames, String[] affectNames) {
        this.root = root;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton mybtn = new JButton("Do Something");
        
        this.add(mybtn);
        this.setBackground(uiBgColour);
        
        if(createNames != null){
            createComboBox = new JComboBox(createNames);
            //createComboBox.setSelectedIndex(2);
            createComboBox.setActionCommand(CREATE_COMMAND);
            createComboBox.addActionListener(this);
            this.add(createComboBox);
        }
        
        if(affectNames != null){
            affectComboBox = new JComboBox(affectNames);
            //affectComboBox.setSelectedIndex(2);
            //affectComboBox.setActionCommand(LAYER_COMMAND);
            //affectComboBox.addActionListener(this);
            this.add(affectComboBox);
        }
        
        setUiVisible(false);
        this.setLocation(0, 0);
        this.setBounds(0, 0, windowSize.width, uiHeight);
        
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
                visible = false;
            }
        }
        
    }
    
    public int getUiHeight(){
        return uiHeight;
    }
    
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if (CREATE_COMMAND.equals(cmd)) {
            
            root.setCurrentCreate( createComboBox.getSelectedIndex() );
            
        } else if (AFFECT_COMMAND.equals(cmd)) {
            
            
        }
    }
    
}
