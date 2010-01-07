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

import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.KeyStroke;

class AlcCheckBoxMenuItem extends JCheckBoxMenuItem implements AlcShortcutInterface, AlcConstants {

    private int index;
    private int moduleType = -1;
    private static int checkX;
    private String toolTip;
    

    static {
        if (Alchemy.OS == OS_MAC) {
            checkX = 6;
        } else {
            checkX = 4;
        }
    }

    AlcCheckBoxMenuItem() {
    }

    AlcCheckBoxMenuItem(Action action) {
        this.setAction(action);
    }

    AlcCheckBoxMenuItem(String title) {
        setup(title, -1);
    }

    AlcCheckBoxMenuItem(String title, int accelerator) {
        setup(title, accelerator);
    }

    void setup(AlcModule module) {

        setup(module.getName(), -1);
        this.index = module.getIndex();
        this.moduleType = module.getModuleType();
        this.toolTip = module.getDescription();
        this.setToolTipText(toolTip);

        // Set the intial state to false
        //this.setState(true);

        // Set the main Icon
        this.setIcon(AlcUtil.getImageIcon(module.getIconUrl()));
    }

    void setup(String title) {
        setup(title, -1);
    }

    void setup(String title, int accelerator) {

        this.setText(title);

        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        this.setOpaque(true);
        if (Alchemy.OS != OS_LINUX) {
            this.setBackground(AlcToolBar.COLOR_UI_HIGHLIGHT);
        }
        this.setFont(FONT_MEDIUM);

        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, KEY_MODIFIER));
        }
    }

    int getIndex() {
        return index;
    }

    int getModuleType() {
        return moduleType;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (Alchemy.OS != OS_LINUX && !Alchemy.OS_IS_VISTA) {
            if (!this.getState()) {
                g.setColor(AlcToolBar.COLOR_UI_BOX);
                // This is the toolbar menu popup
                if (moduleType > 0) {
                    g.drawRect(checkX, 14, 7, 7);

                // This is the menubar
                } else {
                    g.drawRect(checkX, 8, 7, 7);
                }
            }
        }
    }

    public void refreshShortcut(int key, int modifier) {
        if (moduleType < 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(key, modifier));
        } else {
            this.setToolTipText(AlcShortcuts.getShortcutString(key, modifier, toolTip));
        }
    }
}
