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
package org.alchemy.affect;

import java.awt.event.*;
import org.alchemy.core.*;

/**
 * Blindness Alchemy Module
 * @author Karl D.D. Willis
 */
public class Blindness extends AlcModule implements AlcConstants {

    private boolean autoReveal = false;
    private AlcSubToolBarSection subToolBarSection;

    /** Creates a new instance of Blindness */
    public Blindness() {
    }

    protected void setup() {
//        canvas.assignBufferImage();
        canvas.setRedraw(false);

        // Create the toolbar section
        createSubToolBarSection();
        // Add the toolbar section to the main toolbar
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    protected void reselect() {
        // Readd the toolbar section
        toolBar.addSubToolBarSection(subToolBarSection);
//        canvas.assignBufferImage();
        canvas.setRedraw(false);
    }

    protected void deselect() {
        // Turn drawing back on and show what is underneath
        canvas.setRedraw(true);
        canvas.redraw();
    }

    protected void cleared() {
//        canvas.assignBufferImage();
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcSubToolBarSection(this);

        // Buttons
        AlcSubButton revealButton = new AlcSubButton("Reveal", AlcUtil.getUrlPath("reveal.png", getClassLoader()));
        revealButton.setToolTipText("Reveal the screen (r)");

        revealButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        canvas.forceRedraw();
//                        canvas.assignBufferImage();
                    // Save this to a buffer to stop the toolbar refreshing the canvas
                    }
                });
        subToolBarSection.add(revealButton);

        AlcSubToggleButton autoRevealButton = new AlcSubToggleButton("Auto-reveal", AlcUtil.getUrlPath("autoreveal.png", getClassLoader()));
        autoRevealButton.setSelected(true);
        autoReveal = true;
        autoRevealButton.setToolTipText("Reveal the screen after each shape is created");

        autoRevealButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        autoReveal = !autoReveal;
                    }
                });
        subToolBarSection.add(autoRevealButton);
    }

    // MOUSE EVENTS
    public void mouseReleased(MouseEvent e) {
        if (autoReveal) {
            // TODO - Deal with straight shapes and autoreveal bug
            canvas.forceRedraw();
//            canvas.assignBufferImage();
        }
    }

    // KEY EVENTS
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            canvas.forceRedraw();
//            canvas.assignBufferImage();
        }
    }
}
