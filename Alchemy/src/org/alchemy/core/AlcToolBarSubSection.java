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
import javax.swing.*;

/**
 * A 'section' added to the subtoolbar by a module<br>
 * Contaning subbuttons, subsliders, sublabels etc...
 * 
 */
public class AlcToolBarSubSection {

    private final AlcModule module;
    /** The top panel added to the subtoolbar */
    final JPanel panel;
    /** The content panel that can be shown/hidden */
    private final JPanel contentPanel;
    /** Reference to this */
    private final AlcToolBarSubSection me;
    /** Full width of the content even when it is hidden */
    private int contentWidth;
    /** Arrow to show when the section is hidden */
    private ImageIcon arrow = AlcUtil.getImageIcon("sub-section-arrow.png");
    /** The clickable title button to hide/show the content */
    private AlcSubButton titleButton;
    private final int titleButtonWidth;

    public AlcToolBarSubSection(final AlcModule module) {

        this.module = module;
        this.me = this;

        panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        contentPanel.setOpaque(false);

        // TITLE BUTTON
        titleButton = new AlcSubButton(module.getName());
        titleButton.setFont(AlcConstants.FONT_SMALL_BOLD);
        titleButton.setVerticalTextPosition(AbstractButton.CENTER);
        titleButton.setHorizontalTextPosition(AbstractButton.LEADING);
        titleButtonWidth = titleButton.getPreferredSize().width;
        // Set the height to the maximum 25 so all other components are correctly centred1
        titleButton.setPreferredSize(new Dimension(titleButtonWidth, 25));
        
        titleButton.setIconTextGap(4);

        titleButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                // Toggle the sub section on/off
                Alchemy.toolBar.toggleSubSection(me);
            }
        });

        panel.add(titleButton);

        // CONTENT PANEL
        contentPanel.setOpaque(false);
        panel.add(contentPanel);

    }

    /** Show/Hide the content panel
     * @param visible   Boolean to show/hide the content panel
     */
    void setContentVisible(boolean visible) {
        contentPanel.setVisible(visible);
        if (visible) {
            titleButton.setIcon(null);
            titleButton.setPreferredSize(new Dimension(titleButtonWidth, 25));
            
        } else {
            titleButton.setIcon(arrow);
            titleButton.setPreferredSize(new Dimension(titleButtonWidth + 7, 25)); // Textgap + arrow width
            
        }
    }

    /** Check if the content panel is visible
     * @return  Visibility state of the content panel
     */
    boolean isContentVisible() {
        return contentPanel.isVisible();
    }

    /** Get the width of the content panel 
     * @return int of the content panel width
     */
    int getContentWidth() {
        return contentWidth;
    }

    /** Add content
     * @param comp  The component to add
     */
    public void add(Component comp) {
        contentPanel.add(comp);
        contentWidth = contentPanel.getLayout().preferredLayoutSize(contentPanel).width;
    }

    /** Remove content
     * @param comp  The component to remove
     */
    public void remove(Component comp) {
        contentPanel.remove(comp);
        contentWidth = contentPanel.getLayout().preferredLayoutSize(panel).width;
    }

    /** Revalidate the content */
    public void revalidate() {
        contentPanel.revalidate();
    }

    int getWidth() {
        Dimension layoutSize = panel.getLayout().preferredLayoutSize(panel);
        // Plus extra to account for padding on the sides
        return layoutSize.width;
    }

    int getIndex() {
        return module.getIndex();
    }

    int getModuleType() {
        return module.getModuleType();
    }
}
