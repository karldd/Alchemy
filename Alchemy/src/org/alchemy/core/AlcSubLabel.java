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
package org.alchemy.core;

import javax.swing.JLabel;

public class AlcSubLabel extends JLabel {

    /** Creates a new instance of AlcSubLabel */
    public AlcSubLabel(String text) {

        this.setFont(AlcToolBar.subToolBarBoldFont);
        this.setText(text);

        //this.setVerticalTextPosition(SwingConstants.BOTTOM);

        // Cant set the margins so make an empty border to adjust the spacing
        // EmptyBorder(int top, int left, int bottom, int right) 
        //this.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

    //this.setIconTextGap(10);

    //this.setBackground(parent.getUiBgColour());
    //this.setPreferredSize(new Dimension(100, 65));
    //this.setBorder(BorderFactory.createRaisedBevelBorder());


    }
}
