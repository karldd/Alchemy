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
package alchemy;

import alchemy.ui.AlcToolBar;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
/**
 *  About Window 
 */
public class AlcAbout extends JDialog implements ActionListener {

    private final JPanel aboutPanel;

    public AlcAbout(AlcMain root, String title) {

        super(root, title);
        aboutPanel = new JPanel();
        aboutPanel.setBackground(AlcToolBar.toolBarBgColour);

        JButton closeButton = new JButton("Close");
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        aboutPanel.add(closeButton);
        this.setPreferredSize(new Dimension(400, 300));

        this.setContentPane(aboutPanel);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(root);
        this.setVisible(true);

    }

    public void actionPerformed(ActionEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
