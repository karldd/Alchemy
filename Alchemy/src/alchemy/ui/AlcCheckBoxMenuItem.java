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
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

public class AlcCheckBoxMenuItem extends JCheckBoxMenuItem implements AlcConstants {

    private AlcToolBar parent;
    private AlcModule module;
    private int index,  moduleType;

    public AlcCheckBoxMenuItem(AlcToolBar parent, String title) {
        setup(parent, title);

    }

    public AlcCheckBoxMenuItem(AlcToolBar parent, String title, int accelerator) {
        setup(parent, title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, MENU_SHORTCUT));
        }

    }

    public AlcCheckBoxMenuItem(AlcToolBar parent, AlcModule module) {

        setup(parent, module.getName());
        this.index = module.getIndex();
        this.moduleType = module.getModuleType();

        // Set the intial state to false
        //this.setState(true);

        // Set the main Icon
        this.setIcon(parent.createImageIcon(module.getIconUrl()));
    }

    private void setup(AlcToolBar parent, String title) {
        this.parent = parent;
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
