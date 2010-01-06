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
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;
import java.awt.event.*;

class AlcPopupMenu extends JPopupMenu implements AlcConstants {

    final static int uiPopupMenuY = 47;
    boolean inside = false;
    boolean clickOk = true;

    /** Creates a new instance of AlcPopupMenu */
    AlcPopupMenu() {

        // Set the color for the bg
        this.setBackground(COLOR_UI_HIGHLIGHT);
        // Make sure the popup is opaque
        this.setOpaque(true);
        Border outline = BorderFactory.createLineBorder(COLOR_UI_LINE, 1);
        Border empty = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        // Compound border combining the above two
        Border compound = BorderFactory.createCompoundBorder(outline, empty);
        this.setBorder(compound);

        this.addMouseListener(createMouseListener(this));
        this.addPopupMenuListener(createPopupMenuListener());

    }

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        // Get rid of the window shadow on Mac
        if (Alchemy.OS == OS_MAC) {
            this.getRootPane().putClientProperty("Window.shadow", Boolean.FALSE);
        }
    }

    PopupMenuListener createPopupMenuListener() {
        return new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Buggy hack here but 
                // When the popup is clicked on then clicked off again
                // It is first set to invisible then mousePressed() is called
                // Use a timer to delay the reseting of the clickOk variable
                javax.swing.Timer initialDelay = new javax.swing.Timer(50, new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {

                        clickOk = true;
                        //System.out.println("clickOK = True");
                    }
                });

                initialDelay.setRepeats(false);
                initialDelay.start();
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
    }

    MouseListener createMouseListener(final AlcPopupMenu popup) {
        return new MouseListener() {

            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
                inside = popup.contains(e.getPoint());
            }

            public void mouseExited(MouseEvent e) {
                inside = popup.contains(e.getPoint());
            }
        };
    }
}
