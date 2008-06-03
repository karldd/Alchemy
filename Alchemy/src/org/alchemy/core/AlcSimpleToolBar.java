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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * AlcSimpleToolBar.java
 */
public class AlcSimpleToolBar extends AlcAbstractToolBar implements AlcConstants {

    final ColourBox colourBox;

    AlcSimpleToolBar() {

        this.toolBarWidth = 150;
        // Left align layout
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, toolBarLineColour));
        this.setOpaque(true);
        this.setBackground(toolBarBgColour);
        this.setName("Toolbar");


        //////////////////////////////////////////////////////////////
        // LINE WEIGHT 
        //////////////////////////////////////////////////////////////
        ImageIcon lineWeightImage = getLineWidthImage(0);
        final JLabel lineWeightBox = new JLabel(lineWeightImage);
        lineWeightBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        lineWeightBox.setToolTipText(getS("lineWeightDescription"));
        // Create a rectangle for easy reference 
        final Rectangle lineWeightRect = new Rectangle(0, 0, lineWeightImage.getIconWidth(), lineWeightImage.getIconHeight());

        lineWeightBox.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                changeLineWeight(e.getPoint(), lineWeightRect, lineWeightBox);
            }
        });

        lineWeightBox.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                changeLineWeight(e.getPoint(), lineWeightRect, lineWeightBox);
            }
        });


        //////////////////////////////////////////////////////////////
        // STYLE BUTTON
        //////////////////////////////////////////////////////////////
        final AlcToggleButton styleButton = new AlcToggleButton();
        styleButton.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        AbstractAction styleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.canvas.toggleStyle();
                if (Alchemy.canvas.getStyle() == LINE) {
                    lineWeightBox.setVisible(true);
                } else {
                    lineWeightBox.setVisible(false);
                }
                // Only toogle the button manually if it is triggered by a key
                if (!e.getSource().getClass().getName().endsWith("AlcToggleButton")) {
                    styleButton.setSelected(!styleButton.isSelected());
                }
            }
        };

        styleButton.setAction(styleAction);
        styleButton.setup(null, getS("styleDescription"), AlcUtil.getUrlPath("simple-style.png"));

        // Shortcut - s
        Alchemy.shortcuts.setShortcut(styleButton, KeyEvent.VK_S, "styleTitle", styleAction);
        this.add(styleButton);

        this.add(lineWeightBox);


        //////////////////////////////////////////////////////////////
        // COLOUR BOX
        //////////////////////////////////////////////////////////////
        colourBox = new ColourBox(toolBarWidth, 25, Alchemy.canvas.getColour());
        colourBox.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, toolBarLineColour));

        //////////////////////////////////////////////////////////////
        // COLOUR PICKER
        //////////////////////////////////////////////////////////////
        // Get the icon for the label
        ImageIcon colourPickerIcon = AlcUtil.getImageIcon("simple-colour-picker.png");
        // Create a rectangle for easy reference 
        final Rectangle colourPickerRect = new Rectangle(0, 0, colourPickerIcon.getIconWidth(), colourPickerIcon.getIconHeight());

        // Create a blank image then draw into it rather than casting the image
        final BufferedImage colourPickerBuffImage = new BufferedImage(colourPickerRect.width, colourPickerRect.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = colourPickerBuffImage.createGraphics();
        g2.drawImage(colourPickerIcon.getImage(), colourPickerRect.x, colourPickerRect.y, colourPickerRect.width, colourPickerRect.height, null);
        g2.dispose();
        g2 = null;

        JLabel colourPicker = new JLabel(colourPickerIcon);
        colourPicker.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, toolBarLineColour));
        colourPicker.setToolTipText(getS("colourDescription"));
        final Cursor pickerCursor = AlcUtil.getCursor("cursor-picker.gif");
        colourPicker.setCursor(pickerCursor);
        colourPicker.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                if (colourPickerRect.contains(p)) {
                    Color c = new Color(colourPickerBuffImage.getRGB(p.x, p.y));
                    Alchemy.canvas.setColour(c);
                    colourBox.update(Alchemy.canvas.getColour());
                //System.out.println(c + " " + e.getPoint());
                }
            }
        });

        colourPicker.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();
                if (colourPickerRect.contains(p)) {
                    Color c = new Color(colourPickerBuffImage.getRGB(p.x, p.y));
                    Alchemy.canvas.setColour(c);
                    colourBox.update(Alchemy.canvas.getColour());
                }
            }
        });
        this.add(colourPicker);


        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        final AlcSliderCustom transparencySlider = new AlcSliderCustom(toolBarWidth, 25, 0, 255, 254);
        transparencySlider.setBorderPainted(false);
        transparencySlider.setFillPainted(false);
        transparencySlider.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, toolBarLineColour));
        transparencySlider.setToolTipText(getS("transparencyDescription"));

        GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(0, 0, 0, 0), toolBarWidth, 0, new Color(0, 0, 0, 255), true);
        BufferedImage gradientImage = new BufferedImage(toolBarWidth, 25, BufferedImage.TYPE_INT_ARGB);
        g2 = gradientImage.createGraphics();
        g2.setPaint(gradientPaint);
        g2.fillRect(0, 0, toolBarWidth, 25);
        g2.dispose();
        g2 = null;
        transparencySlider.setBgImage(gradientImage);

        transparencySlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        //if (!transparencySlider.getValueIsAdjusting()) {
                        Alchemy.canvas.setAlpha(transparencySlider.getValue());
                        colourBox.update(Alchemy.canvas.getColour());
                    //}
                    }
                });

        this.add(transparencySlider);

        this.add(colourBox);



        ColourBox separator = new ColourBox(toolBarWidth, 10, toolBarBgColour);
        separator.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, toolBarLineColour));

        //////////////////////////////////////////////////////////////
        // MODULES
        //////////////////////////////////////////////////////////////
        // Loaded from the preferences
        if (Alchemy.preferences.simpleModulesSet) {
            addModules(Alchemy.plugins.creates);
            this.add(separator);
            addModules(Alchemy.plugins.affects);
            this.add((ColourBox) separator.clone());

        // Loaded from the default list
        } else {
            addDefaultModules(Alchemy.plugins.creates);
            this.add(separator);
            addDefaultModules(Alchemy.plugins.affects);
            this.add((ColourBox) separator.clone());
        }


        //////////////////////////////////////////////////////////////
        // CLEAR
        //////////////////////////////////////////////////////////////
        AbstractAction clearAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.canvas.clear();
            }
        };
        final AlcButton clearButton = new AlcButton(clearAction);
        clearButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        clearButton.setup(null, getS("clearDescription"), AlcUtil.getUrlPath("simple-clear.png"));
        this.add(clearButton);

        // Shortcuts - Modifier Delete/Backspace
        Alchemy.shortcuts.setShortcut(clearButton, KeyEvent.VK_BACK_SPACE, "clearTitle", clearAction, MODIFIER_KEY);
        //Alchemy.canvas.getActionMap().put(clearTitle, clearAction);

        this.setVisible(true);
        
        //Alchemy.window.setFullscreen(true);
    }

    private void addModules(AlcModule[] modules) {
        boolean firstModule = true;
        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            boolean createModule = (currentModule.getModuleType() == CREATE) ? true : false;

            String moduleNodeName = Alchemy.preferences.simpleModulePrefix + currentModule.getName();
            if (AlcPreferences.prefs.getBoolean(moduleNodeName, false)) {
                addModuleButton(currentModule, buttonGroup, firstModule, createModule);
                firstModule = false;
            }
        }
    }

    private void addDefaultModules(AlcModule[] modules) {
        boolean firstModule = true;
        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            boolean createModule = (currentModule.getModuleType() == CREATE) ? true : false;


            String moduleName = currentModule.getName();
            // Check if this module is on the default list
            for (int j = 0; j < Alchemy.preferences.simpleDefaultModules.length; j++) {
                if (Alchemy.preferences.simpleDefaultModules[j].equals(moduleName)) {
                    addModuleButton(currentModule, buttonGroup, firstModule, createModule);
                    firstModule = false;
                    break;
                }
            }
        }
    }

    private void addModuleButton(final AlcModule currentModule, final ButtonGroup buttonGroup, final boolean firstModule, final boolean createModule) {

        AbstractAction moduleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcSimpleModuleToggleButton moduleButton = (AlcSimpleModuleToggleButton) e.getSource();
                // CREATE
                if (createModule) {
                    Alchemy.plugins.setCurrentCreate(currentModule.getIndex());
                    buttonGroup.setSelected(moduleButton.getModel(), true);
                // AFFECT
                } else {

                    // SELECTED
                    if (moduleButton.isSelected()) {
                        Alchemy.plugins.addAffect(currentModule.getIndex());

                    // DESELECTED
                    } else {
                        Alchemy.plugins.removeAffect(currentModule.getIndex());
                    }

                }
            }
        };

        AlcSimpleModuleToggleButton moduleButton = new AlcSimpleModuleToggleButton(moduleAction);
        moduleButton.setToolTipText(currentModule.getDescription());
        moduleButton.setup(AlcUtil.getUrlPath(currentModule.getIconName(), currentModule.getClassLoader()));

        if (createModule) {
            buttonGroup.add(moduleButton);
            if (firstModule) {
                buttonGroup.setSelected(moduleButton.getModel(), true);
            }
        }
        this.add(moduleButton);
    }

    @Override
    void resizeToolBar( Dimension windowSize) {
        this.setBounds(0, 0, 151, windowSize.height);
        this.windowSize = windowSize;
        this.revalidate();
        this.repaint();
    }

    @Override
    void refreshColourButton() {
        colourBox.update(Alchemy.canvas.getColour());
    }

    private void changeLineWeight(Point p, Rectangle lineWeightRect, JLabel lineWeightBox) {
        if (lineWeightRect.contains(p)) {
            //int lineWeight = (int) AlcMath.map(p.x, 0, toolBarWidth, 1, 50);
            int lineWeightInc = (int) AlcMath.map(p.x, 25, toolBarWidth, 1, 10);
            Alchemy.canvas.setLineWidth(lineWeightInc * 4 + 1);
            lineWeightBox.setIcon(getLineWidthImage(lineWeightInc));
        }
    }

    private ImageIcon getLineWidthImage(int lineWidth) {
        BufferedImage lineWeightImage = new BufferedImage(toolBarWidth, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lineWeightImage.createGraphics();

        for (int i = 0; i < 10; i++) {
            if (i == lineWidth) {
                g2.setColor(Color.BLACK);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
            }
            int inc = i + 1;
            g2.fillRect(inc * 13, 0, inc, 25);
        }
        g2.dispose();
        g2 = null;
        return new ImageIcon(lineWeightImage);
    }
}

class ColourBox extends JPanel implements Cloneable {

      
      
     

          
          
          
          
        final int width,  height;
    Color colour;

    ColourBox(int width, int height, Color colour) {
        this.width = width;
        this.height = height;
        this.colour = colour;
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(colour);
        g.fillRect(0, 0, width, height);
    }

    void update(Color colour) {
        this.colour = colour;
        this.repaint();
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
    } // clone
}
