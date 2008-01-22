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
package alchemy.affect;

import alchemy.*;
import alchemy.ui.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Blindness Alchemy Module
 * @author Karl D.D. Willis
 */
public class Blindness extends AlcModule implements AlcConstants {

    private boolean autoRedraw = false;
    private AlcSubToolBarSection subToolBarSection;

    /** Creates a new instance of Blindness */
    public Blindness() {
    }

    public void setup() {
        canvas.setRedraw(false);

        // Create the toolbar section
        createSubToolBarSection();
        // Add the toolbar section to the main toolbar
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    public void reselect() {
        // Readd the toolbar section
        toolBar.addSubToolBarSection(subToolBarSection);
        canvas.setRedraw(false);
    }

    public void deselect() {
        // Turn drawing back on and show what is underneath
        canvas.setRedraw(true);
        canvas.redraw();
    }

    private void redrawOnce() {
        canvas.setRedraw(true);
        canvas.redraw();
        canvas.setRedraw(false);
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Buttons
        AlcSubButton redrawButton = new AlcSubButton("Redraw", AlcUtil.getUrlPath("redraw.png", getClassLoader()));
        redrawButton.setToolTipText("Redraw the screen (r)");

        redrawButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        redrawOnce();
                    }
                });
        subToolBarSection.add(redrawButton);

        AlcSubToggleButton autoRedrawButton = new AlcSubToggleButton("Autoredraw", AlcUtil.getUrlPath("autoredraw.png", getClassLoader()));
        autoRedrawButton.setToolTipText("Redraw the screen after each shape");

        autoRedrawButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        toggleAutoRedraw();
                    }
                });
        subToolBarSection.add(autoRedrawButton);
    }

    private void toggleAutoRedraw() {
        if (autoRedraw) {
            autoRedraw = false;
        } else {
            autoRedraw = true;
        }
    }

    // MOUSE EVENTS
    public void mouseReleased(MouseEvent e) {
        if (autoRedraw) {
            redrawOnce();
        }
    }

    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            redrawOnce();
        }
    }
}
