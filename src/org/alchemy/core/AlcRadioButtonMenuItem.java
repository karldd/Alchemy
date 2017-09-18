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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.BorderFactory;
import javax.swing.JRadioButtonMenuItem;
//import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.KeyStroke;

class AlcRadioButtonMenuItem extends JRadioButtonMenuItem implements AlcShortcutInterface, AlcConstants {

    private int index;
    private int moduleType = -1;
    private static int checkX;
    private String toolTip;

    static {
        if (Alchemy.OS == OS_MAC) {
            checkX = 7;
        } else {
            checkX = 6;
        }
    }
    Ellipse2D.Double toolCircle = new Ellipse2D.Double(checkX, 13, 8, 8);
    Ellipse2D.Double toolCircleLine = new Ellipse2D.Double(checkX + 1, 15, 6, 6);
    Ellipse2D.Double toolInnerCircle = new Ellipse2D.Double(checkX + 1, 14, 6, 6);
    Ellipse2D.Double menuCircle = new Ellipse2D.Double(checkX, 7, 8, 8);
    Ellipse2D.Double menuInnerCircle = new Ellipse2D.Double(checkX + 1, 8, 6, 6);

    AlcRadioButtonMenuItem() {
    }

    AlcRadioButtonMenuItem(int index, String title) {
        setup(index, title);

    }

    AlcRadioButtonMenuItem(int index, String title, int accelerator) {
        setup(index, title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, KEY_MODIFIER));
        }
    }

    void setup(AlcModule module) {
        setup(module.getIndex(), module.getName());
        this.moduleType = module.getModuleType();
        this.toolTip = module.getDescription();
        this.setToolTipText(toolTip);
        // Set the intial state to false
        //this.setState(true);

        // Set the main Icon
        this.setIcon(AlcUtil.getImageIcon(module.getIconUrl()));
    }

    private void setup(int index, String title) {
        this.index = index;
        this.setText(title);
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        if (Alchemy.OS != OS_LINUX) {        
            this.setBackground(COLOR_UI_HIGHLIGHT);
        }
        this.setFont(FONT_MEDIUM);
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
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // NOT SELECTED
            if (!this.isSelected()) {
                g2.setColor(COLOR_UI_BOX);
                // This is the toolbar menu popup
                if (moduleType != -1) {
                    g2.draw(toolCircleLine);

                // This is the menubar
                } else {
                    g2.draw(menuInnerCircle);
                }
            // SELECTED
            } else {
                if (moduleType != -1) {
                    if (Alchemy.OS != OS_MAC) {
                        g2.setColor(this.getBackground());
                        g2.fill(toolCircle);
                        g2.setColor(Color.BLACK);
                        g2.fill(toolInnerCircle);
                    }


                // This is the menubar
                } else {
                    g2.setColor(this.getBackground());
                    g2.fill(menuCircle);
                    g2.setColor(Color.BLACK);
                    g2.fill(menuInnerCircle);
                }
            }
        }
    }

    public void refreshShortcut(int key, int modifier) {
        this.setToolTipText(AlcShortcuts.getShortcutString(key, modifier, toolTip));
    }
}
