/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2010 Karl D.D. Willis
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
 * Simple toolbar (left side bar style)<br>
 * Used for kids workshops and what not
 */
public class AlcSimpleToolBar extends AlcAbstractToolBar implements AlcConstants {

    final ColorBox colorBox;
    AlcSliderCustom transparencySlider;

    AlcSimpleToolBar() {

        this.toolBarWidth = 150;
        // Left align layout
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_UI_LINE));
        this.setOpaque(true);
        this.setBackground(COLOR_UI_BG);
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
        styleButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        AbstractAction styleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.canvas.toggleStyle();
                if (Alchemy.canvas.getStyle() == STYLE_STROKE) {
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
        // COLOR BOX
        //////////////////////////////////////////////////////////////
        colorBox = new ColorBox(toolBarWidth, 25, Alchemy.canvas.getColor());
        colorBox.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_UI_LINE));

        //////////////////////////////////////////////////////////////
        // COLOR PICKER
        //////////////////////////////////////////////////////////////
        // Get the icon for the label
        ImageIcon colorPickerIcon = AlcUtil.getImageIcon("simple-color-picker.png");
        // Create a rectangle for easy reference 
        final Rectangle colorPickerRect = new Rectangle(0, 0, colorPickerIcon.getIconWidth(), colorPickerIcon.getIconHeight());

        // Create buffered image to get the color using .getRGB()
        final BufferedImage colorPickerBuffImage = AlcUtil.getBufferedImage(colorPickerIcon.getImage());

        JLabel colorPicker = new JLabel(colorPickerIcon);
        colorPicker.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_UI_LINE));
        colorPicker.setToolTipText(getS("colorDescription"));
        final Cursor pickerCursor = AlcUtil.getCursor("cursor-circle.png");
        colorPicker.setCursor(pickerCursor);
        colorPicker.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                if (colorPickerRect.contains(p)) {
                    Color c = new Color(colorPickerBuffImage.getRGB(p.x, p.y));
                    Alchemy.canvas.setColor(c);
                    colorBox.update(Alchemy.canvas.getColor());
                //System.out.println(c + " " + e.getPoint());
                }
            }
        });

        colorPicker.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = e.getPoint();
                if (colorPickerRect.contains(p)) {
                    Color c = new Color(colorPickerBuffImage.getRGB(p.x, p.y));
                    Alchemy.canvas.setColor(c);
                    colorBox.update(Alchemy.canvas.getColor());
                }
            }
        });
        this.add(colorPicker);


        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        transparencySlider = new AlcSliderCustom(null, toolBarWidth, 25, 0, 255, 254);
        transparencySlider.setBorderPainted(false);
        transparencySlider.setFillPainted(false);
        transparencySlider.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, COLOR_UI_LINE));
        transparencySlider.setToolTipText(getS("transparencyDescription"));

        GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(0, 0, 0, 0), toolBarWidth, 0, new Color(0, 0, 0, 255), true);
        BufferedImage gradientImage = new BufferedImage(toolBarWidth, 25, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = gradientImage.createGraphics();
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
                        colorBox.update(Alchemy.canvas.getColor());
                    //}
                    }
                });

        this.add(transparencySlider);

        this.add(colorBox);



        ColorBox separator = new ColorBox(toolBarWidth, 10, COLOR_UI_BG);
        separator.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_UI_LINE));

        //////////////////////////////////////////////////////////////
        // MODULES
        //////////////////////////////////////////////////////////////

        addModules(Alchemy.plugins.creates);
        this.add(separator);
        addModules(Alchemy.plugins.affects);
        this.add((ColorBox) separator.clone());


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
        Alchemy.shortcuts.setShortcut(clearButton, KeyEvent.VK_BACK_SPACE, "clearTitle", clearAction, KEY_MODIFIER);
        //Alchemy.canvas.getActionMap().put(clearTitle, clearAction);

        this.setVisible(true);

    //Alchemy.window.setFullscreen(true);
    }

    private void addModules(AlcModule[] modules) {

        ButtonGroup buttonGroup = new ButtonGroup();
        int count = 0;
        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            if (loadModule(currentModule)) {
                addModuleButton(currentModule, buttonGroup, count);
                count++;
            }
        }
    }

    private void addModuleButton(final AlcModule currentModule, final ButtonGroup buttonGroup, int count) {

        final boolean createModule = (currentModule.getModuleType() == MODULE_CREATE) ? true : false;
        final AlcSimpleModuleToggleButton moduleButton = new AlcSimpleModuleToggleButton();

        AbstractAction moduleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                // CREATE
                if (createModule) {

                    if (Alchemy.plugins.currentCreate != currentModule.getIndex()) {
                        Alchemy.plugins.setCurrentCreate(currentModule.getIndex());
                        buttonGroup.setSelected(moduleButton.getModel(), true);
                    }

                // AFFECT
                } else {
                    // If triggered by a key
                    if (!e.getSource().getClass().getName().endsWith("AlcSimpleModuleToggleButton")) {
                        moduleButton.setSelected(!moduleButton.isSelected());
                    }

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

        moduleButton.setAction(moduleAction);
        moduleButton.setup(AlcUtil.getUrlPath(currentModule.getIconName(), currentModule.getClassLoader()), currentModule.getName());

        // Range from 0 - 8 mapped to keys 1 - 9
        if (count < 9) {
            if (createModule) {
                Alchemy.shortcuts.setShortcut(moduleButton, KeyEvent.VK_0 + count + 1, currentModule.getName(), moduleAction);
            } else {
                Alchemy.shortcuts.setShortcut(moduleButton, KeyEvent.VK_0 + count + 1, currentModule.getName(), moduleAction, KEY_MODIFIER);
            }

        // Last key is mapped to 0
        } else if (count == 9) {
            if (createModule) {
                Alchemy.shortcuts.setShortcut(moduleButton, KeyEvent.VK_0, currentModule.getName(), moduleAction);
            } else {
                Alchemy.shortcuts.setShortcut(moduleButton, KeyEvent.VK_0, currentModule.getName(), moduleAction, KEY_MODIFIER);
            }
        }

        if (createModule) {
            buttonGroup.add(moduleButton);
            if (count == 0) {
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
    void refreshColorButton() {
        colorBox.update(Alchemy.canvas.getColor());
    }

    @Override
    void refreshTransparencySlider() {
        transparencySlider.setValue(Alchemy.canvas.getAlpha());
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

class ColorBox extends JPanel implements Cloneable {

    final int width,  height;
    Color color;

    ColorBox(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }

    void update(Color color) {
        this.color = color;
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
