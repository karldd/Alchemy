/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2008 Karl D.D. Willis
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
import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Alchemy Toolbar
 * The disappearing toolbar
 * Housing access to all modules and their sub toolbars
 */
public class AlcToolBar extends AlcAbstractToolBar implements AlcConstants {

    //////////////////////////////////////////////////////////////
    // TOOLBAR ELEMENTS
    //////////////////////////////////////////////////////////////
    /** Popup buttons for the colour, create, amd affect buttons in the toolbar
     *  These are declared global so we can hide the popup when hiding the toolbar */
    private AlcPopupButton createButton,  affectButton,  colourButton;
    /** The main tool bar inside the toolbar */
    private AlcToolBarMain mainToolBar;
    /** The sub toolbar below the main toolbar */
    private AlcToolBarSub subToolBar;
    /** Container holding the main and sub toolbars */
    JPanel toolBars;
    /** Detach toolbar button */
    private JButton detachButton;
    /** Foreground Background Button */
    private AlcToggleButton fgbgButton;
    /** Sections within the sub toolbar - either loaded or not */
    private AlcToolBarSubSection[] affectSubToolBarSections;
    /** The create section within the sub toolbar - index of the loaded section */
    private AlcToolBarSubSection createSubToolBarSection;
    /** Number of current sub toolbar sections loaded */
    private int currentSubToolBarSections = 0;
    /** The number of rows in the sub toolbar */
//    private int subToolBarRows;
    /** Colour picker - gets updated each time the bg colour is changed */
    AlcColourPicker picker;
    //////////////////////////////////////////////////////////////
    // TOOLBAR CONTROL
    //////////////////////////////////////////////////////////////
    /** Visibility of the ToolBar */
    private boolean toolBarVisible = true;
    /** If the toolbar has been turned on by a key or not */
    private boolean toolBarKeyedOn = false;
    /** Height of the ToolBar */
    private static int toolBarHeight = 60;
    /** Total height of all tool bars */
    private int totalHeight = 60;
    /** Timer to delay the hiding of the toolbar */
    private javax.swing.Timer toolBarTimer;
    /** Cursor inside toolbar or not */
    private boolean insideToolBar;
    /** Schedule update for the Foreground/Background button */
    private boolean updateSwapButton = false;

    /**
     * Creates a new instance of AlcToolBar
     */
    AlcToolBar() {

        // General Toolbar settings
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(false);
        this.setName("Toolbar");
        this.setLayout(new BorderLayout());

        // Create a container for the two toolbars
        toolBars = new JPanel(new BorderLayout());
        toolBars.setOpaque(false);
        // Create and add the main toolbar
        mainToolBar = loadToolBar();
        toolBars.add("Center", mainToolBar);

        // Create and add the sub toolbar
        subToolBar = loadSubToolBar();
        // Make it invisible until it gets some content
        toolBars.add("South", subToolBar);
        subToolBar.setVisible(true);

        if (!Alchemy.preferences.paletteAttached) {
            this.add("South", toolBars);
        }

        // Hide the toolbar with the space key
        AbstractAction toolBarAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (!Alchemy.preferences.paletteAttached) {
                    if (toolBarVisible) {
                        setToolBarVisible(false);
                        toolBarKeyedOn = false;
                    } else {
                        setToolBarVisible(true);
                        toolBarKeyedOn = true;
                    }
                }
            }
        };

        // Shortcut - SPACE
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_SPACE, "toggleToolBar", toolBarAction);

        // Hide the cursor with the H key
        AbstractAction hideCursorAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (Alchemy.canvas.getCursor() == BLANK) {
                    Alchemy.canvas.setCursor(CROSS);
                } else {
                    Alchemy.canvas.setCursor(BLANK);
                }
            }
        };

        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_H, "toggleCursor", hideCursorAction);


        // Turn off the visibility until the mouse enters the top of the screen
        setToolBarVisible(false);

    }

    /** Load the tool bar */
    private AlcToolBarMain loadToolBar() {
        // Create the main toolbar
        AlcToolBarMain toolBarGroup = new AlcToolBarMain();

        JPanel toolBar = new JPanel();
        toolBar.setOpaque(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        //////////////////////////////////////////////////////////////
        // STYLE BUTTON
        //////////////////////////////////////////////////////////////
        String styleTitle = getS("styleTitle");
        final AlcToggleButton styleButton = new AlcToggleButton();
        AbstractAction styleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.canvas.toggleStyle();
                // Only toogle the button manually if it is triggered by a key
                if (!e.getSource().getClass().getName().endsWith("AlcToggleButton")) {
                    styleButton.setSelected(!styleButton.isSelected());
                }
            }
        };

        styleButton.setAction(styleAction);
        styleButton.setup(styleTitle, getS("styleDescription"), AlcUtil.getUrlPath("style.png"));

        // Shortcut - s
        Alchemy.shortcuts.setShortcut(styleButton, KeyEvent.VK_S, "styleTitle", styleAction);
        toolBar.add(styleButton);

        //////////////////////////////////////////////////////////////
        // CLEAR BUTTON
        //////////////////////////////////////////////////////////////
        String clearTitle = getS("clearTitle");
        AbstractAction clearAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.canvas.clear();
            }
        };
        AlcButton clearButton = new AlcButton(clearAction);
        clearButton.setup(clearTitle, getS("clearDescription"), AlcUtil.getUrlPath("clear.png"));
        // Shortcuts - Modifier Delete/Backspace
        Alchemy.shortcuts.setShortcut(clearButton, KeyEvent.VK_BACK_SPACE, "clearTitle", clearAction, MODIFIER_KEY);
        Alchemy.canvas.getActionMap().put(clearTitle, clearAction);
        toolBar.add(clearButton);

        //////////////////////////////////////////////////////////////
        // LINE WIDTH SPINNER
        //////////////////////////////////////////////////////////////
        // currentValue, min, max, stepsize
        final int lineWidthSpinnerMin = 1;
        final int lineWidthSpinnerMax = 75;

        SpinnerNumberModel lineWidthNumberModel = new SpinnerNumberModel((int) Alchemy.canvas.getLineWidth(), lineWidthSpinnerMin, lineWidthSpinnerMax, 1);
        AlcSpinner lineWidthSpinner = new AlcSpinner(getS("lineWeightTitle"), lineWidthNumberModel, getS("lineWeightDescription"));
        final SpinnerModel lineWidthSpinnerModel = lineWidthSpinner.spinner.getModel();
        lineWidthSpinner.spinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        Number number = (Number) lineWidthSpinnerModel.getValue();
                        int value = number.intValue();
                        Alchemy.canvas.setLineWidth(value);
                    }
                });

        AbstractAction lineWidthDownAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Number number = (Number) lineWidthSpinnerModel.getPreviousValue();
                if (number != null) {
                    int value = number.intValue();

                    lineWidthSpinnerModel.setValue(number);
                    Alchemy.canvas.setLineWidth(value);
                }
            }
        };

        AbstractAction lineWidthUpAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Number number = (Number) lineWidthSpinnerModel.getNextValue();
                if (number != null) {
                    int value = number.intValue();
                    lineWidthSpinnerModel.setValue(number);
                    Alchemy.canvas.setLineWidth(value);
                }
            }
        };

        Alchemy.shortcuts.setShortcut(lineWidthSpinner, KeyEvent.VK_OPEN_BRACKET, "lineWeightDownTitle", lineWidthDownAction);
        Alchemy.shortcuts.setShortcut(lineWidthSpinner, KeyEvent.VK_CLOSE_BRACKET, "lineWeightUpTitle", lineWidthUpAction);

        toolBar.add(lineWidthSpinner);

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        toolBar.add(new AlcSeparator());


        //////////////////////////////////////////////////////////////
        // COLOUR  BUTTON
        //////////////////////////////////////////////////////////////
        String colourTitle = getS("colourTitle");
        colourButton = new AlcPopupButton(colourTitle, getS("colourDescription"), AlcUtil.getUrlPath("colour.png"));
        picker = new AlcColourPicker();
        picker.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {

                // Check if the colour chooser needs to be launched
                if (e.getX() >= 75 && e.getY() <= 15) {

                    // Action to change the colour
                    ActionListener colorAction = new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            Alchemy.canvas.setColour(Alchemy.colourChooser.getColor());
                            refreshSwapButton();
                        }
                    };

                    // Set the current colour 
                    Alchemy.colourChooser.setColor(Alchemy.canvas.getColour());

                    // Dialog to hold the colour chooser
                    JDialog dialog = JColorChooser.createDialog(Alchemy.window, getS("colourTitle"), true, Alchemy.colourChooser, colorAction, null);
                    dialog.setBackground(AlcToolBar.toolBarBgColour);
                    dialog.setResizable(false);
                    dialog.setVisible(true);

                } else {
                    Alchemy.canvas.setColour(picker.getColor(e.getX(), e.getY()));
                    colourButton.hidePopup();
                    if (Alchemy.PLATFORM == MACOSX) {
                        Alchemy.canvas.restoreCursor();
                        //Alchemy.canvas.setCursor(CROSS);
                        setCursor(ARROW);
                    }
                    refreshSwapButton();
                }
            }
        });

        colourButton.addItem(picker);
        toolBar.add(colourButton);

        //////////////////////////////////////////////////////////////
        // FOREGROUND BACKGROUND BUTTON
        //////////////////////////////////////////////////////////////

        String fgTitle = getS("fgTitle");

        AbstractAction fgbgAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                // If this is the button calling
                if (!e.getSource().getClass().getName().endsWith("AlcToggleButton")) {
                    // Only toogle the button manually if it is triggered by a key
                    fgbgButton.setSelected(!fgbgButton.isSelected());
                }

                if (fgbgButton.isSelected()) {
                    fgbgButton.setText(getS("bgTitle"));
                } else {
                    fgbgButton.setText(getS("fgTitle"));
                }
                Alchemy.canvas.toggleColour();
                refreshSwapButton();
            }
        };

        fgbgButton = new AlcToggleButton(fgbgAction);
        fgbgButton.setup(fgTitle, getS("fgbgDescription"), null);
        // Set the swap buttons dynamic images to the current colour
        refreshSwapButton();

        // Hack here to make sure the text does not resize the button
        // Make sure it is set the the maximum size
        Dimension size = fgbgButton.getPreferredSize();
        fgbgButton.setText(getS("bgTitle"));
        Dimension newSize = fgbgButton.getPreferredSize();
        if (!size.equals(newSize)) {
            fgbgButton.setPreferredSize(newSize);
        }
        fgbgButton.setText(fgTitle);

        // Shortcut - X
        Alchemy.shortcuts.setShortcut(fgbgButton, KeyEvent.VK_X, "fgbgTitle", fgbgAction);
        toolBar.add(fgbgButton);

        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        AlcSlider alphaSlider = new AlcSlider(getS("transparencyTitle"), getS("transparencyDescription"), 0, 255, 255);
        alphaSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = (int) source.getValue();
                            Alchemy.canvas.setAlpha(value);
                            refreshSwapButton();
                        }
                    }
                });

        toolBar.add(alphaSlider);

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        toolBar.add(new AlcSeparator());

        //////////////////////////////////////////////////////////////
        // CREATE
        //////////////////////////////////////////////////////////////
        createButton = new AlcPopupButton(getS("createTitle"), getS("createDescription"), AlcUtil.getUrlPath("create.png"));
        // Button group for the radio buttons
        ButtonGroup group = new ButtonGroup();
        // Start the keyboard shortcuts from here
        int zero = KeyEvent.VK_0;

        // Populate the Popup Menu
        for (int i = 0; i < Alchemy.plugins.creates.length; i++) {
            // The current module

            final AlcRadioButtonMenuItem createMenuItem = new AlcRadioButtonMenuItem();

            AbstractAction createMenuItemAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    // Check that the module is not already selected
                    if (Alchemy.plugins.currentCreate != createMenuItem.getIndex()) {
                        // Remove the subtoolbar of the create module
                        removeSubToolBarSection(0);
                        Alchemy.plugins.setCurrentCreate(createMenuItem.getIndex());
                    }


                    // When triggered by a key toggle the check box
                    if (!e.getSource().getClass().getName().endsWith("AlcRadioButtonMenuItem")) {
                        createMenuItem.setSelected(!createMenuItem.isSelected());

                    } else {
                        Point loc = createMenuItem.getLocation();
                        //Rectangle butLoc = createButton.getBounds();
                        int heightFromWindow = loc.y + 50;
                        //System.out.println(loc + " " + heightFromWindow);
                        toggleToolBar(heightFromWindow, true);
                    }
                }
            };

            createMenuItem.setAction(createMenuItemAction);
            AlcModule currentModule = Alchemy.plugins.creates[i];
            createMenuItem.setup(currentModule);

            if (i == 0) {
                createMenuItem.setSelected(true);
            }

            group.add(createMenuItem);
            createButton.addItem(createMenuItem);

            // Range from 0 - 8 mapped to keys 1 - 9
            if (i < 9) {
                Alchemy.shortcuts.setShortcut(createMenuItem, zero + i + 1, currentModule.getName(), createMenuItemAction);

            // Last key is mapped to 0
            } else if (i == 9) {
                Alchemy.shortcuts.setShortcut(createMenuItem, zero, currentModule.getName(), createMenuItemAction);
            }

        }

        toolBar.add(createButton);


        //////////////////////////////////////////////////////////////
        // AFFECT
        //////////////////////////////////////////////////////////////
        if (Alchemy.plugins.getNumberOfAffectModules() > 0) {
            affectButton = new AlcPopupButton(getS("affectTitle"), getS("affectDescription"), AlcUtil.getUrlPath("affect.png"));
            for (int i = 0; i < Alchemy.plugins.affects.length; i++) {
                // The current module
                AlcModule currentModule = Alchemy.plugins.affects[i];

                final AlcCheckBoxMenuItem affectMenuItem = new AlcCheckBoxMenuItem();

                AbstractAction affectMenuItemAction = new AbstractAction() {

                    public void actionPerformed(ActionEvent e) {

                        if (!e.getSource().getClass().getName().endsWith("AlcCheckBoxMenuItem")) {
                            affectMenuItem.setSelected(!affectMenuItem.isSelected());
                        }

                        // SELECTED
                        if (affectMenuItem.isSelected()) {
                            Alchemy.plugins.addAffect(affectMenuItem.getIndex());

                        // DESELECTED
                        } else {
                            Alchemy.plugins.removeAffect(affectMenuItem.getIndex());
                            // Index is offset to allow for the create module to always be first
                            removeSubToolBarSection(affectMenuItem.getIndex() + 1);
                        }

                        // When triggered by a key toggle the check box
                        if (e.getSource().getClass().getName().endsWith("AlcCheckBoxMenuItem")) {
                            Point loc = affectMenuItem.getLocation();
                            int heightFromWindow = loc.y + 50;
                            toggleToolBar(heightFromWindow, true);
                        }
                    }
                };

                affectMenuItem.setAction(affectMenuItemAction);
                affectMenuItem.setup(currentModule);
                affectButton.addItem(affectMenuItem);

                // Range from 0 - 8 mapped to keys 1 - 9
                if (i < 9) {
                    Alchemy.shortcuts.setShortcut(affectMenuItem, zero + i + 1, currentModule.getName(), affectMenuItemAction, MODIFIER_KEY);
                }
            }
            toolBar.add(affectButton);
        }

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        toolBar.add(new AlcSeparator());

        //////////////////////////////////////////////////////////////
        // DETACH BUTTON
        //////////////////////////////////////////////////////////////

        JPanel topAlign = new JPanel();
        topAlign.setOpaque(
                false);
        topAlign.setLayout(
                new BoxLayout(topAlign, BoxLayout.PAGE_AXIS));

        detachButton = new JButton(AlcUtil.getImageIcon("palette-detach.png"));

        detachButton.setRolloverIcon(AlcUtil.getImageIcon("palette-detach-over.png"));
        detachButton.setToolTipText(
                "Detach the toolbar to a seperate palette");

        // Compensate for the windows border
        if (Alchemy.PLATFORM == MACOSX) {
            detachButton.setMargin(new Insets(2, 0, 0, 2));
        } else {
            detachButton.setMargin(new Insets(2, 0, 0, 7));
        }

        detachButton.setBorderPainted(false);

        detachButton.setContentAreaFilled(false);

        detachButton.setFocusPainted(false);

        detachButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Alchemy.window.setPalette(true);
                    }
                });

        topAlign.add(detachButton);

        toolBarGroup.add(toolBar, BorderLayout.LINE_START);
        toolBarGroup.add(topAlign, BorderLayout.LINE_END);

        return toolBarGroup;
    }

//////////////////////////////////////////////////////////////
// TOOLBAR
//////////////////////////////////////////////////////////////
    @Override
    void resizeToolBar() {
        Dimension toolBarWindowSize = new Dimension(this.windowSize.width, totalHeight);
        resizeToolBar(toolBarWindowSize);
    }

    @Override
    void resizeToolBar(Dimension windowSize) {
        this.setBounds(0, 0, windowSize.width, totalHeight);
        this.windowSize = windowSize;
        this.revalidate();
        this.repaint();
    }

    /** Refresh the toolbar */
    private void refreshToolBar() {
        // Recalculate the total height of the tool bar
        calculateTotalHeight();
        // Then resize it
        resizeToolBar();
    }

    /** Function to control the display of the Ui toolbar
     * 
     * @param y     The height of the mouse to check against
     */
    @Override
    void toggleToolBar( int y) {
        toggleToolBar(y, false);
    }

    /** Function to control the display of the Ui toolbar 
     * 
     * @param y             The height of the mouse to check against
     * @param startTimer    To force start the timer
     */
    @Override
    void toggleToolBar( int y, boolean startTimer) {
        if (y < 10) {
            // Show the toolbar
            setToolBarVisible(true);
            insideToolBar =
                    true;

        } else if (y > getTotalHeight() + 5) {
            // If rolling out of a popup menu set the toolbar to dissapear with a timer
            if (isPopupMenusVisible() || toolBarTimer != null || startTimer) {
                // Set the timer
                setTimer();

            } else {
                // If the toolbar has not been turned on with a shortcut key    
                if (!toolBarKeyedOn) {
                    setToolBarVisible(false);
                }

            }
            insideToolBar = false;

        // Inside the middle of the toolbar
        } else {
            insideToolBar = true;
            toolBarKeyedOn =
                    false;
        }

    }

    /** Set the visibility of the UI Toolbar */
    @Override
    void setToolBarVisible( boolean visible) {
        if (visible != toolBarVisible) {
            this.setVisible(visible);
            toolBarVisible = visible;
            Alchemy.canvas.setMouseEvents(!visible);
            if (!visible) {
                // Be sure to set the cursor back to the cross hair
                Alchemy.canvas.restoreCursor();
                //Alchemy.canvas.setCursor(CROSS);
                this.setCursor(ARROW);
                colourButton.hidePopup();
                createButton.hidePopup();
                if (affectButton != null) {
                    affectButton.hidePopup();
                }

            } else {
                // Update the colours of the fg/bg button if they have changed
                if (updateSwapButton) {
                    refreshSwapButton();
                    updateSwapButton = false;
                }

            }
        }
    }

    /** Return the visibility of the UI Toolbar */
    @Override
    boolean isToolBarVisible() {
        return toolBarVisible;
    }

    /** Return the height of the UI Toolbar */
    int getToolBarHeight() {
        return toolBarHeight;
    }

    /** Calculate the total height of the toolbar and its subtoolbars */
    @Override
    void calculateTotalHeight() {
        // Start with the main toolbar height
        int newTotalHeight = mainToolBar.getHeight();
        if (subToolBar.isVisible()) {
            newTotalHeight += subToolBar.getHeight();
        }

        if (Alchemy.PLATFORM != MACOSX) {
            // Add the height of the menubar if this is not a mac
            newTotalHeight += Alchemy.menuBar.getHeight();
        }

        this.totalHeight = newTotalHeight;
    }

    /** Return the total height of the toolbar and its subtoolbars */
    @Override
    int getTotalHeight() {
        return totalHeight;
    }

//////////////////////////////////////////////////////////////
// SUBTOOLBAR
//////////////////////////////////////////////////////////////
    private AlcToolBarSub loadSubToolBar() {
        // Initialise the references to the sub toolbar sections
        affectSubToolBarSections = new AlcToolBarSubSection[Alchemy.plugins.getNumberOfAffectModules()];
        // Set to a negative value to indicate no initially loaded sections
        createSubToolBarSection =
                null;

        // Add the SubToolBar
        AlcToolBarSub toolBar = new AlcToolBarSub();

        return toolBar;
    }

    /** Add a Create Module sub-toolbar */
    @Override
    public void addSubToolBarSection(AlcToolBarSubSection subToolBarSection) {

        subToolBarSection.revalidate();

        if (subToolBarSection.getModuleType() == CREATE) {
            createSubToolBarSection = subToolBarSection;

        // AFFECT
        } else {
            affectSubToolBarSections[subToolBarSection.getIndex()] = subToolBarSection;
        }

        currentSubToolBarSections++;
        // Refresh the sub toolbar with the new contents
        refreshSubToolBar();
    }

    void removeSubToolBarSection(int index) {

        // If the index is 0 then it is a create section
        if (index == 0) {
            // If not null then remove it and increment the count down
            if (createSubToolBarSection != null) {
                createSubToolBarSection = null;
                currentSubToolBarSections--;

            }

        // Otherwise it is an affect and we take away 1 for the offset
        } else {
            int offsetIndex = index - 1;
            // If not null then remove it and increment the count down
            if (affectSubToolBarSections[offsetIndex] != null) {
                affectSubToolBarSections[offsetIndex] = null;
                currentSubToolBarSections--;

            }


        }
        // Refresh the sub toolbar
        refreshSubToolBar();
    }

    private void refreshSubToolBar() {
        // Remove everythingœ
        subToolBar.removeAll();

        // If there is a create section add that first
        if (createSubToolBarSection != null) {

            subToolBar.add(createSubToolBarSection);
        }
        // Add the affect sections
        for (int i = 0; i <
                affectSubToolBarSections.length; i++) {

            if (affectSubToolBarSections[i] != null) {

                // If there is odd number of components then add a separator
                if ((subToolBar.getComponentCount() % 2) != 0) {
                    subToolBar.add(new AlcSubSeparator());
                }
                // Then add the section
                subToolBar.add(affectSubToolBarSections[i]);
            }

        }

        if (currentSubToolBarSections > 0) {

            // TODO - Check there is enough room for the subtoolbar and expand the window as required    
//            int layoutWidth = subToolBar.getLayoutWidth();
//            System.out.println("SubToolbar layout width:" + layoutWidth);
//            if (layoutWidth > windowSize.width) {
//                subToolBarRows = layoutWidth / windowSize.width + 1;
//                System.out.println(layoutWidth + " / " + windowSize.width + " + 1 = " + subToolBarRows);
//                subToolBar.setRows(subToolBarRows);
//                calculateTotalHeight();
//
//            } else {
//                if (subToolBarRows != 1) {
//                    subToolBarRows = 1;
//                    subToolBar.setRows(subToolBarRows);
//                    calculateTotalHeight();
//                }
//            }

            subToolBar.setVisible(true);
        } else {
            if (!Alchemy.preferences.paletteAttached) {
                subToolBar.setVisible(false);
            }

        }
        subToolBar.revalidate();
        subToolBar.repaint();
        refreshToolBar();
    }

//////////////////////////////////////////////////////////////
// POPUP MENUS
//////////////////////////////////////////////////////////////
    /** Check if any of the popup menus are visible */
    boolean isPopupMenusVisible() {

        if (colourButton.isPopupVisible()) {
            return true;
        }

//if (createButton != null) {
        if (createButton.isPopupVisible()) {
            return true;
        }
//}
        if (affectButton != null) {
            if (affectButton.isPopupVisible()) {
                return true;
            }

        }
        return false;
    }

    /** Sets and manages a timer used to delay hiding of the toolbar */
    private void setTimer() {
        if (toolBarTimer == null) {
            toolBarTimer = new javax.swing.Timer(1000, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (!insideToolBar) {
                        if (isPopupMenusVisible()) {
                            if (!colourButton.isInside() && !createButton.isInside() && !affectButton.isInside()) {
                                //System.out.println("Timer setting visibility");
                                setToolBarVisible(false);
                                insideToolBar =
                                        false;
                            }

                        } else {
                            setToolBarVisible(false);
                            insideToolBar =
                                    false;
                        }

                    }
                    toolBarTimer.stop();
                    toolBarTimer =
                            null;
                }
            });
            toolBarTimer.start();
        }

    }


    //////////////////////////////////////////////////////////////
    // PALETTE
    //////////////////////////////////////////////////////////////
    /** Called when detaching the toolbar into the palette */
    @Override
    void detachToolBar() {
        if (!subToolBar.isVisible()) {
            subToolBar.setVisible(true);
        }

        this.setToolBarVisible(false);
        this.remove(toolBars);
        this.remove(Alchemy.menuBar);
    }

    /** Called when attaching the toolbar from the palette */
    @Override
    void attachToolBar() {
        if (Alchemy.PLATFORM != MACOSX) {
            Alchemy.window.setJMenuBar(null);
            this.add("North", Alchemy.menuBar);
        }

        if (currentSubToolBarSections < 1) {
            subToolBar.setVisible(false);
            System.out.println("SET FALSE");
        }

        this.add("South", toolBars);
        this.calculateTotalHeight();
        this.detachButton.setVisible(true);
        this.revalidate();
        refreshToolBar();
        this.setToolBarVisible(true);
        // Request focus here to enable key mapping on windows
        this.requestFocus();
    }

    /** Toggle the visibility of the detach button */
    @Override
    void toggleDetachButton( boolean visible) {
        detachButton.setVisible(visible);
    }

    /** Add the toolbar content to the palette */
    @Override
    void addPaletteContent() {
        Alchemy.palette.addContent(toolBars);
    }


//////////////////////////////////////////////////////////////
// UTLITY
//////////////////////////////////////////////////////////////
    /** Get a string from the resource bundle */
    private String getS(String stringName) {
        return Alchemy.bundle.getString(stringName);
    }

    @Override
    void queueSwapButtonRefresh() {
        updateSwapButton = true;
    }

    /** Refreshes the colours of the Foreground/Background button */
    @Override
    void refreshSwapButton() {
        Color colour = Alchemy.canvas.getForegroundColour();
        // Make sure there is no transparency
        //Color fullColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 255);
        Color bgColour = Alchemy.canvas.getBgColour();

        BufferedImage swap = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics g = swap.createGraphics();

        g.setColor(bgColour);
        g.fillRect(6, 6, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(6, 6, 17, 17);

        g.setColor(colour);
        g.fillRect(0, 0, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(0, 0, 18, 18);

        fgbgButton.setIcon(new ImageIcon(swap));

        BufferedImage swapOn = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        g = swapOn.createGraphics();

        g.setColor(colour);
        g.fillRect(0, 0, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(0, 0, 18, 18);

        g.setColor(bgColour);
        g.fillRect(6, 6, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(6, 6, 17, 17);

        fgbgButton.setSelectedIcon(new ImageIcon(swapOn));
    }
}