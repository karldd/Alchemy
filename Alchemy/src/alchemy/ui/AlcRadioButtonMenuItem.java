/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007 Karl D.D. Willis
 * 
 *  Alchemy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Alchemy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 * 
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
