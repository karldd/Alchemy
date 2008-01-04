/**
 * AlcMenuItem.java
 *
 * Created on November 28, 2007, 1:48 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.ui;

import alchemy.AlcConstants;
import alchemy.AlcModule;
import alchemy.AlcUtil;
import javax.swing.BorderFactory;
import javax.swing.JRadioButtonMenuItem;
//import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.KeyStroke;

public class AlcRadioButtonMenuItem extends JRadioButtonMenuItem implements AlcConstants {

    // TODO - find some way to avoid the ugly round selected circle mark - possibly by adding a normal radiobutton see here:
    // http://www.onjava.com/pub/a/onjava/excerpt/swing_14/index6.html?page=2
    private int index,  moduleType;

    public AlcRadioButtonMenuItem(int index, String title) {
        setup(index, title);

    }

    public AlcRadioButtonMenuItem(int index, String title, int accelerator) {
        setup(index, title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, MENU_SHORTCUT));
        }

    }

    public AlcRadioButtonMenuItem(AlcModule module) {

        setup(module.getIndex(), module.getName());
        this.moduleType = module.getModuleType();

        // Set the intial state to false
        //this.setState(true);

        // Set the main Icon
        this.setIcon(AlcUtil.createImageIcon(module.getIconUrl()));
    }

    private void setup(int index, String title) {
        this.index = index;
        this.setText(title);
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        this.setBackground(AlcToolBar.toolBarHighlightColour);
        this.setFont(AlcToolBar.toolBarFont);
    }

    public int getIndex() {
        return index;
    }

    public int getModuleType() {
        return moduleType;
    }
}
