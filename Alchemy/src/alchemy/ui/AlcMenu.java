package alchemy.ui;

import alchemy.AlcConstants;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JMenu;

/**
 * Part of the Alchemy project - http://al.chemy.org
 * 
 * @author Karl D.D. Willis
 */
public class AlcMenu extends JMenu implements AlcConstants {

    private AlcToolBar parent;

    public AlcMenu(AlcToolBar parent, String title) {

        this.parent = parent;

        this.setText(title);

        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setBackground(AlcToolBar.toolBarHighlightColour);
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));

    }
}
