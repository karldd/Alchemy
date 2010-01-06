/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
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
package org.alchemy.core;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

class AlcMenuItem extends JMenuItem implements AlcShortcutInterface, AlcConstants {

    AlcMenuItem(Action action) {
        this.setAction(action);
    }

    AlcMenuItem(String title) {
        setup(title, -1);
    }

    AlcMenuItem(String title, int accelerator) {
        setup(title, accelerator);
    }

    void setup(String title) {
        setup(title, -1);
    }

    void setup(String title, int accelerator) {

        //this.setUI(new AlcMenuItemUI());
        //System.out.println(this.getUI());
        //this.setOpaque(true);


        this.setText(title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, KEY_MODIFIER));
        }

        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        this.setOpaque(true);
        if(Alchemy.OS != OS_LINUX){
            this.setBackground(COLOR_UI_HIGHLIGHT);
        }
        //this.setBackground(AlcToolBar.toolBarAlphaHighlightColor);
        this.setFont(FONT_MEDIUM);

    }

    public void refreshShortcut(int key, int modifier) {
        this.setAccelerator(KeyStroke.getKeyStroke(key, modifier));
    }
}
