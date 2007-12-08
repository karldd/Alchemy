package alchemy.ui;

import alchemy.AlcConstants;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Part of the Alchemy project - http://al.chemy.org
 * 
 * @author Karl D.D. Willis
 */
public class AlcMenuItem extends JMenuItem implements AlcConstants{
    
        private AlcToolBar parent;

    public AlcMenuItem(AlcToolBar parent, String title) {
        setup(parent, title, -1);
    }

    public AlcMenuItem(AlcToolBar parent, String title, int accelerator) {
        setup(parent, title, accelerator);
    }

    private void setup(AlcToolBar parent, String title, int accelerator) {
        this.parent = parent;

        this.setText(title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, MENU_SHORTCUT));
        }

        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        this.setBackground(AlcToolBar.toolBarHighlightColour);
        this.setFont(new Font("sansserif", Font.PLAIN, parent.getToolBarTextSize()));

    }

}
