package alchemy.ui;

import alchemy.AlcConstants;
import javax.swing.BorderFactory;
import javax.swing.JMenu;

/**
 * Part of the Alchemy project - http://al.chemy.org
 * 
 * @author Karl D.D. Willis
 */
public class AlcMenu extends JMenu implements AlcConstants {

    public AlcMenu(String title) {

        this.setText(title);
        this.setContentAreaFilled(false);
        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");
        //this.setOpaque(false);
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 2));
        this.setBackground(AlcToolBar.toolBarHighlightColour);
        this.setFont(AlcToolBar.toolBarFont);

    }
}
