package alchemy.ui;

import alchemy.AlcConstants;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Part of the Alchemy project - http://al.chemy.org
 * 
 * @author Karl D.D. Willis
 */
public class AlcMenuItem extends JMenuItem implements AlcConstants {

    public AlcMenuItem(String title) {
        setup(title, -1);
    }

    public AlcMenuItem(String title, int accelerator) {
        setup(title, accelerator);
    }

    private void setup(String title, int accelerator) {

        this.setText(title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, MENU_SHORTCUT));
        }

        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        this.setBackground(AlcToolBar.toolBarHighlightColour);
        this.setFont(AlcToolBar.toolBarFont);

    }
}
