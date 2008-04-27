/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2008 Karl D.D. Willis
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
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
//import javax.swing.plaf.basic.BasicPopupMenuUI;
class AlcPopupMenu extends JPopupMenu {

    /** Creates a new instance of AlcPopupMenu */
    AlcPopupMenu() {

        //this.setUI(new AlcPopupMenuUI());
        //this.setLightWeightPopupEnabled(false);
        //this.setUI(new BasicPopupMenuUI());
        //this.setOpaque(true);
        // Set the colour for the bg
        this.setBackground(AlcToolBar.toolBarHighlightColour);


        Border outline = BorderFactory.createLineBorder(AlcToolBar.toolBarLineColour, 1);
        Border empty = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        // Compound border combining the above two
        Border compound = BorderFactory.createCompoundBorder(outline, empty);
        this.setBorder(compound);
    }
}
