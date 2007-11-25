/**
 * AlcUi.java
 *
 * Created on November 24, 2007, 3:08 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy.ui;

import javax.swing.*;

public class AlcUi {
    
    JPanel contentPanel;
    
    /** Creates a new instance of AlcUi */
    public AlcUi(JPanel contentPanel) {
        
        this.contentPanel = contentPanel;
        JButton mybtn = new JButton("Do Something");
        
        contentPanel.add(mybtn);
        //pack();
     }
    
}
