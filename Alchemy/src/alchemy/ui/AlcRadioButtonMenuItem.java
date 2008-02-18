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
import alchemy.AlcMain;
import alchemy.AlcModule;
import alchemy.AlcUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.BorderFactory;
import javax.swing.JRadioButtonMenuItem;
//import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.KeyStroke;

public class AlcRadioButtonMenuItem extends JRadioButtonMenuItem implements AlcConstants {

    private int index;
    private int moduleType = -1;
    private static int checkX;

    static {
        if (AlcMain.PLATFORM == MACOSX) {
            checkX = 0;
        } else {
            checkX = 6;
        }
    }
    Ellipse2D.Double toolCircle = new Ellipse2D.Double(checkX, 15, 8, 8);
    Ellipse2D.Double toolCircleLine = new Ellipse2D.Double(checkX + 1, 15, 6, 6);
    Ellipse2D.Double toolInnerCircle = new Ellipse2D.Double(checkX + 1, 16, 6, 6);
    Ellipse2D.Double menuCircle = new Ellipse2D.Double(checkX, 9, 8, 8);
    Ellipse2D.Double menuInnerCircle = new Ellipse2D.Double(checkX+1, 10, 6, 6);

    public AlcRadioButtonMenuItem(int index, String title) {
        setup(index, title);

    }

    public AlcRadioButtonMenuItem(int index, String title, int accelerator) {
        setup(index, title);
        if (accelerator > 0) {
            this.setAccelerator(KeyStroke.getKeyStroke(accelerator, MENU_SHORTCUT));
        }

    }

    public AlcRadioButtonMenuItem(AlcModule module) {

        setup(module.getIndex(), module.getName());
        this.moduleType = module.getModuleType();

        // Set the intial state to false
        //this.setState(true);

        // Set the main Icon
        this.setIcon(AlcUtil.createImageIcon(module.getIconUrl()));
    }

    private void setup(int index, String title) {
        this.index = index;
        this.setText(title);
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        this.setBackground(AlcToolBar.toolBarHighlightColour);
        this.setFont(AlcToolBar.toolBarFont);
    }

    public int getIndex() {
        return index;
    }

    public int getModuleType() {
        return moduleType;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // SELECTED
        if (!this.isSelected()) {
            g2.setColor(AlcToolBar.toolBarBoxColour);
            // This is the toolbar menu popup
            if (moduleType != -1) {
                g2.draw(toolCircleLine);

            // This is the menubar
            } else {
                g2.draw(menuInnerCircle);
            }
        // NOT SELECTED
        } else {
            if (moduleType != -1) {
                g2.setColor(this.getBackground());
                g2.fill(toolCircle);
                g2.setColor(Color.BLACK);
                g2.fill(toolInnerCircle);


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
