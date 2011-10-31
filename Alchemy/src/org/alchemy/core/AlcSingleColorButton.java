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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * AlcSingleColorButton
 *
 * Custom color button - really rough placeholder,
 *  
 * based on AlcColorButton by Karl D.D. Willis
 */
class AlcSingleColorButton extends JComponent implements MouseListener, AlcPopupInterface, AlcConstants {

    private AlcPopupMenu lClickMenu;
    private AlcPopupMenu rClickMenu;
    //
    // COLOR PANEL
    private JComponent colorPanel;
    private Image colorPanelImage;
   
    /** Creates a new instance of AlcColorButton */
    AlcSingleColorButton(String text, String toolTip, int buttWidth) {

        //this.setOpaque(true);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // COLOR PANEL
        colorPanel = new JComponent() {

            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(colorPanelImage, 0, 0, null);
            }
        };

        colorPanel.setPreferredSize(new Dimension(64, 60));
        colorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorPanel.addMouseListener(this);

        this.add(colorPanel);

        colorPanel.setToolTipText(toolTip);
        this.setToolTipText(toolTip);


        lClickMenu = new AlcPopupMenu();
        lClickMenu.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //highlight = 0;
                //refresh();
            }
        });
                
        rClickMenu = new AlcPopupMenu();
        rClickMenu.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //highlight = 0;
                //refresh();
            }
        });

        
    }

    /** Add an interface element to the foreground popup menu */
    void addlClickItem(Component item) {
        lClickMenu.add(item);
    }
    void addrClickItem(Component item) {
        rClickMenu.add(item);
    }

       /** Refresh the color panel */
    void refresh() {
        colorPanelImage = getColorPanelImage();
        colorPanel.repaint();
    }


    /** Get visibility of the popup menu */
    public boolean isPopupVisible() {
        if (lClickMenu.isVisible()|| rClickMenu.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInside() {
        if (lClickMenu.inside || rClickMenu.inside){
            return true;
        }else{
            return false;
        }
    }

    public void hidePopup() {
        lClickMenu.setVisible(false);
        rClickMenu.setVisible(false);
        refresh();
    }

    /** Get the color panel image */
    private Image getColorPanelImage() {
        BufferedImage image = new BufferedImage(64, 59, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();

        g.setColor(Alchemy.canvas.getColor());
        g.fillRect(5, 5, 59, 46);
        return image;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1){
            if (lClickMenu.clickOk) {
                lClickMenu.clickOk = false;
                lClickMenu.show(e.getComponent(), 0, 55);
            } else {
                refresh();
            }
        }else {
            Alchemy.toolBar.refreshRClickPicker();
            if (rClickMenu.clickOk) {
                rClickMenu.clickOk = false;
                rClickMenu.show(e.getComponent(), 0, 55);
            } else {
                refresh();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
