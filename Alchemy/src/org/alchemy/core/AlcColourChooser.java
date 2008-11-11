/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2008 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.alchemy.core;

import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * A custom colour chooser
 * @author Karl D.D. Willis
 */
public class AlcColourChooser extends JColorChooser implements AlcConstants {

    AlcColourChooser(Color initialColor) {
        super(initialColor);

        if (Alchemy.PLATFORM != MACOSX) {
            String singleChooser = "DefaultHSBChooserPanel";
            // Just want to show the HSB panel
            AbstractColorChooserPanel[] panels = this.getChooserPanels();
            // Get the panels and search for the HSB one
            for (int i = 0; i < panels.length; i++) {
                String name = panels[i].getClass().getName();
                if (name.endsWith(singleChooser)) {
                    //Add the HSB panel, replacing the others
                    AbstractColorChooserPanel[] hsb = {panels[i]};
                    this.setChooserPanels(hsb);
                    break;
                }
            }
            this.setPreviewPanel(new JPanel());
        }
    }

    /** Creates and shows a JDialog with the Alchemy colour pane and the given actions 
     * 
     * @param okListener        The ActionListener for the OK button
     * @param cancelListener    The ActionListener for the CANCEL button
     */
    public void show(ActionListener okListener, ActionListener cancelListener) {
        JDialog colourChooser = JColorChooser.createDialog(Alchemy.window,
                Alchemy.bundle.getString("colourTitle"),
                true,
                this,
                okListener,
                cancelListener);

        colourChooser.setBackground(AlcToolBar.toolBarBgColour);
        colourChooser.setResizable(false);
        colourChooser.setVisible(true);
    }
}
