/*
 *   Part of the Alchemy project - http://al.chemy.org
 * 
 *   Copyright (c) 2007 Karl D.D. Willis
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General
 *   Public License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA  02111-1307  USA
 */
package alchemy;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * AlcAbout
 * 
 * 
 */
public class AlcAbout extends JDialog implements ActionListener {

    JPanel aboutPanel;
    
    public AlcAbout() {
        
        aboutPanel = new JPanel();
        aboutPanel.setBackground(new Color(100, 100, 0));
        this.getContentPane().add(aboutPanel);
        this.setResizable(false);


    }

    public void actionPerformed(ActionEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
