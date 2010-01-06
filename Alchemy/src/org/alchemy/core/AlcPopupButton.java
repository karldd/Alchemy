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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

class AlcPopupButton extends AlcButton implements AlcPopupInterface, AlcConstants {

    
    private AlcPopupMenu popup;

    /** Creates a new instance of AlcPopupButton */
    AlcPopupButton(String text, String toolTip, URL iconUrl) {
        super(text, toolTip, iconUrl);
        makePopup(text);
    }

    void makePopup(final String text) {
        popup = new AlcPopupMenu();

        // Add a mouse listener to detect when the button is pressed and display the popup menu
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (popup.clickOk) {
                    popup.clickOk = false;
                    popup.show(e.getComponent(), 0, AlcPopupMenu.uiPopupMenuY);
                    Alchemy.canvas.setTempCursor(CURSOR_ARROW);
                }
            }
        });
    }

    /** Add an interface element to the popup menu */
    void addItem(Component item) {
        popup.add(item);
    }

    /** Get visibility of the popup menu */
    public boolean isPopupVisible() {
        return popup.isVisible();
    }

    public boolean isInside() {
        return popup.inside;
    }

    public void hidePopup() {
        popup.setVisible(false);
    }
}
