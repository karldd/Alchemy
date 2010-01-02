/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2009 Karl D.D. Willis
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
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * AlcColorButton
 *
 * Custom color button
 *
 * @author Karl D.D. Willis
 */
class AlcColorButton extends JComponent implements MouseListener, AlcPopupInterface, AlcConstants {

    private AlcPopupMenu fgPopup,  bgPopup;
    //
    // COLOR PANEL
    private JComponent colorPanel;
    private Image colorPanelImage;
    private final Image colorFg = AlcUtil.getImage("color-fg.png");
    private final Image colorBg = AlcUtil.getImage("color-bg.png");
    private final Image colorSwitch = AlcUtil.getImage("color-switch.png");
    private final Color highlightColor = new Color(0, 0, 0, 50);
    private final int FOREGROUND = 1;
    private final int BACKGROUND = 2;
    private final int SWITCH = 3;
    private int highlight = 0;
    private final int swatchSize = 17;
    //    
    /** Creates a new instance of AlcColorButton */
    AlcColorButton(String text, String toolTip) {

        //this.setOpaque(true);
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // COLOR PANEL
        colorPanel = new JComponent() {

            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(colorPanelImage, 0, 0, null);
            }
        };

        colorPanel.setPreferredSize(new Dimension(34, 24));
        colorPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorPanel.addMouseListener(this);

        this.add(colorPanel);

        // TEXT LABEL
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(FONT_MEDIUM);
        label.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        // Clicking on the label brings up the Foreground color popup
        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                highlight = FOREGROUND;
                refresh();
                if (fgPopup.clickOk) {
                    fgPopup.clickOk = false;
                    fgPopup.show(e.getComponent(), -2, 21);
                }
            }
        });

        this.add(label);

        colorPanel.setToolTipText(toolTip);
        label.setToolTipText(toolTip);
        this.setToolTipText(toolTip);


        fgPopup = new AlcPopupMenu();
        fgPopup.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                highlight = 0;
                refresh();
            }
        });

        bgPopup = new AlcPopupMenu();
        bgPopup.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                highlight = 0;
                refresh();
            }
        });

    }

    /** Add an interface element to the foreground popup menu */
    void addFgItem(Component item) {
        fgPopup.add(item);
    }

    /** Add an interface element to the background popup menu */
    void addBgItem(Component item) {
        bgPopup.add(item);
    }

    /** Refresh the color panel */
    void refresh() {
        colorPanelImage = getColorPanelImage();
        colorPanel.repaint();
    }

    /** Switch the foreground and background colors */
    void switchColors() {
        Alchemy.canvas.setBackgroundColorActive(!Alchemy.canvas.isBackgroundColorActive());
        refresh();
    }

    /** Get visibility of the popup menu */
    public boolean isPopupVisible() {
        if (fgPopup.isVisible() || bgPopup.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInside() {
        if (!Alchemy.canvas.isBackgroundColorActive()) {
            return fgPopup.inside;
        } else {
            return bgPopup.inside;
        }
    }

    public void hidePopup() {
        fgPopup.setVisible(false);
        bgPopup.setVisible(false);
        highlight = 0;
        refresh();
    }

    /** Get the area that has been clicked */
    private int getArea(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // SWITCH    
        if (x >= 24 && y <= 12) {
            return SWITCH;
        }

        // Foreground Active
        if (!Alchemy.canvas.isBackgroundColorActive() && x >= 7 && x < swatchSize && y >= swatchSize ||
                !Alchemy.canvas.isBackgroundColorActive() && x >= swatchSize && x <= 23 && y >= 7) {
            return BACKGROUND;

        // Background Active    
        } else if (Alchemy.canvas.isBackgroundColorActive() && x >= 7 && x <= 23 && y >= 7) {
            return BACKGROUND;

        } else {
            return FOREGROUND;
        }
    }

    /** Get the color panel image */
    private Image getColorPanelImage() {
        BufferedImage image = new BufferedImage(35, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();

        // Foreground Active
        if (!Alchemy.canvas.isBackgroundColorActive()) {

            // Draw the foreground color
            g.setColor(Alchemy.canvas.getForegroundColor());
            g.fillRect(2, 2, 13, 13);
            // Draw the background color
            g.setColor(Alchemy.canvas.getBackgroundColor());
            // bottom left
            g.fillRect(9, 17, 7, 5);
            // right
            g.fillRect(16, 9, 6, 13);

            // Draw the base image
            g.drawImage(colorFg, 0, 0, null);

        // Background Active
        } else {
            // Draw the foreground color
            g.setColor(Alchemy.canvas.getForegroundColor());
            // top
            g.fillRect(2, 2, 13, 5);
            // left
            g.fillRect(2, 7, 6, 10);
            // Draw the background color
            g.setColor(Alchemy.canvas.getBackgroundColor());
            g.fillRect(9, 9, 13, 13);

            // Draw the base image
            g.drawImage(colorBg, 0, 0, null);

        }

        // Draw the highlight when clicked
        switch (highlight) {

            case FOREGROUND:
                g.setColor(highlightColor);
                if (!Alchemy.canvas.isBackgroundColorActive()) {
                    g.fillRect(0, 0, swatchSize, swatchSize);
                } else {
                    g.fillRect(0, 0, swatchSize, 7);
                    g.fillRect(0, 7, 7, 10);
                }
                break;

            case BACKGROUND:
                g.setColor(highlightColor);
                if (!Alchemy.canvas.isBackgroundColorActive()) {
                    g.fillRect(7, swatchSize, 10, 7);
                    g.fillRect(swatchSize, 7, 7, swatchSize);
                } else {
                    g.fillRect(7, 7, swatchSize, swatchSize);
                }
                break;

            case SWITCH:
                g.drawImage(colorSwitch, 24, 0, null);
                break;
        }
        return image;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int area = getArea(e);
        switch (area) {
            case SWITCH:
                highlight = SWITCH;
                Alchemy.canvas.setBackgroundColorActive(!Alchemy.canvas.isBackgroundColorActive());
                refresh();
                break;

            case FOREGROUND:
                highlight = FOREGROUND;
                refresh();
                if (fgPopup.clickOk) {
                    fgPopup.clickOk = false;
                    fgPopup.show(e.getComponent(), 0, 45);
                } else {
                    highlight = 0;
                    refresh();
                }
                break;

            case BACKGROUND:
                highlight = BACKGROUND;
                refresh();
                if (bgPopup.clickOk) {
                    bgPopup.clickOk = false;
                    bgPopup.show(e.getComponent(), 7, 45);
                } else {
                    highlight = 0;
                    refresh();
                }
                break;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (highlight == SWITCH) {
            highlight = 0;
            refresh();
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
