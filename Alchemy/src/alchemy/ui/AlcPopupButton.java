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
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class AlcPopupButton extends AlcButton implements AlcConstants {

    private final static int uiPopupMenuY = 47;
    private AlcPopupMenu popup;

    /** Creates a new instance of AlcPopupButton */
    public AlcPopupButton(String text, String toolTip, URL iconUrl) {
        super(text, toolTip, iconUrl);
        popup = new AlcPopupMenu();



        // Add a mouse listener to detect when the button is pressed and display the popup menu
        this.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                popup.show(e.getComponent(), 0, uiPopupMenuY);
            }
            });

    }

    /** Add an interface element to the popup menu */
    public void addItem(Component item) {
        popup.add(item);
    }

    /** Hide the popup menu */
    public void hidePopup() {
        popup.setVisible(false);
    }
}
