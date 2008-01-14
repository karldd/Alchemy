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
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JMenu;

public class AlcMenu extends JMenu implements AlcConstants {

    public AlcMenu(String title) {

        this.setText(title);
        //this.setContentAreaFilled(false);
        this.setOpaque(false);
        //this.setMnemonic(KeyEvent.VK_A);
        //this.getAccessibleContext().setAccessibleDescription("Some decription text");
        //this.setOpaque(false);
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 2));
        //this.setBackground(AlcToolBar.toolBarAlphaHighlightColour);
        //this.setBackground(AlcToolBar.toolBarAlphaHighlightColour);
        
        // Hacky work around - because the JMenu is layered on top of the JMenuBar - doubling the opacity  
        this.setBackground(new Color(0, 0, 0, 0));
        this.setFont(AlcToolBar.toolBarFont);

    }
    
    
    /*
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
          
            g2.setPaint(AlcToolBar.toolBarAlphaHighlightColour);
            g2.fillRect(0, 0, getWidth(), getHeight());

        }
    }*/
    
}
