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

import javax.swing.BorderFactory;
import javax.swing.JMenu;

class AlcMenu extends JMenu implements AlcConstants {

    AlcMenu(String title) {

        this.setText(title);
        //this.setContentAreaFilled(false);
        //this.setOpaque(false);
        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 2));
        //this.setBackground(AlcToolBar.toolBarAlphaHighlightColor);
        if (Alchemy.OS != OS_LINUX) {
            this.setBackground(COLOR_UI_HIGHLIGHT);
        }
        // Hacky work around - because the JMenu is layered on top of the JMenuBar - doubling the opacity  
        //this.setBackground(new Color(0, 0, 0, 0));
        //this.setBackground(new Color(231, 231, 231, 150));
        //this.setBackground(AlcToolBar.toolBarAlphaHighlightColor);
        this.setFont(FONT_MEDIUM);

    //JPopupMenu popup = super.getPopupMenu();

    //popup.setOpaque(false);
    //popup.setBackground(new Color(0, 0, 0, 0));

    }
}
