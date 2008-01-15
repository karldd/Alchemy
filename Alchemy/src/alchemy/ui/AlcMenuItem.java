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
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class AlcMenuItem extends JMenuItem implements AlcConstants {

    public AlcMenuItem(Action action) {
        this.setAction(action);
    }
    
    public AlcMenuItem(String title) {
        setup(title, -1);
    }

    public AlcMenuItem(String title, int accelerator) {
        setup(title, accelerator);
    }

    public void setup(String title, int accelerator) {

        //this.setUI(new AlcMenuItemUI());
        //System.out.println(this.getUI());
        //this.setOpaque(false);
        

        this.setText(title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, MENU_SHORTCUT));
        }

        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        this.setBackground(AlcToolBar.toolBarHighlightColour);
        //this.setBackground(AlcToolBar.toolBarAlphaHighlightColour);
        this.setFont(AlcToolBar.toolBarFont);

    }
}
