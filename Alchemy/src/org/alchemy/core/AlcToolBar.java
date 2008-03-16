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
public class AlcToolBar extends JPanel implements AlcConstants {

    /** Keep track of the windowSize */
    Dimension windowSize;
    //////////////////////////////////////////////////////////////
    // INTERFACE COLOURS
    //////////////////////////////////////////////////////////////
    static final Color toolBarBgColour = new Color(225, 225, 225);
    static final Color toolBarBgStartColour = new Color(235, 235, 235, 240);
    static final Color toolBarBgEndColour = new Color(215, 215, 215, 240);
    static final Color toolBarLineColour = new Color(140, 140, 140);
    static final Color toolBarSubLineColour = new Color(160, 160, 160);
    static final Color toolBarHighlightColour = new Color(231, 231, 231);
    static final Color toolBarAlphaHighlightColour = new Color(231, 231, 231, 240);
    static final Color toolBarBoxColour = new Color(190, 190, 190);
    //////////////////////////////////////////////////////////////
    // FONTS
    //////////////////////////////////////////////////////////////
    static final Font toolBarFont = new Font("sansserif", Font.PLAIN, 11);
    static final Font subToolBarFont = new Font("sansserif", Font.PLAIN, 10);
    static final Font subToolBarBoldFont = new Font("sansserif", Font.BOLD, 11);
    //////////////////////////////////////////////////////////////
    // TOOLBAR ELEMENTS
    //////////////////////////////////////////////////////////////
    /** Popup buttons for the colour, create, amd affect buttons in the toolbar
     *  These are declared global so we can hide the popup when hiding the toolbar */
    private AlcPopupButton createButton,  affectButton,  colourButton;
    /** The main tool bar inside the toolbar */
    private AlcMainToolBar mainToolBar;
    /** The sub toolbar below the main toolbar */
    private AlcSubToolBar subToolBar;
    /** Container holding the main and sub toolbars */
    JPanel toolBars;
    /** Detach toolbar button */
    JButton detachButton;
    /** Swap Button */
    private AlcToggleButton swapButton;
    /** Sections within the sub toolbar - either loaded or not */
    private AlcSubToolBarSection[] affectSubToolBarSections;
    /** The create section within the sub toolbar - index of the loaded section */
    private AlcSubToolBarSection createSubToolBarSection;
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
    /** Height of the ToolBar */
    private static int toolBarHeight = 60;
    /** Total height of all tool bars */
    private int totalHeight = 60;
    /** Timer to delay the hiding of the toolbar */
    javax.swing.Timer toolBarTimer;
    /** Cursor inside toolbar or not */
    private boolean insideToolBar;

    /**
     * Creates a new instance of AlcToolBar
     */
    AlcToolBar() {

        // Old JToolBar stuff
        //this.setOrientation(SwingConstants.HORIZONTAL);
        //this.setFloatable(true);

        // General Toolbar settings
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(false);
        this.setName("Toolbar");
        //this.setVisible(false);

        // Make this a Box Layout so all submenus are stacked below
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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

        if (!Alchemy.preferences.getPaletteAttached()) {
            this.add("South", toolBars);
        }


        // Turn off the visibility until the mouse enters the top of the screen
        setToolBarVisible(false);

    }

    /** Load the tool bar */
    private AlcMainToolBar loadToolBar() {
        // Create the main toolbar
        AlcMainToolBar toolBarGroup = new AlcMainToolBar();

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
                if (e.getActionCommand().equals("s")) {
                    styleButton.setSelected(!styleButton.isSelected());
                }
            }
        };

        styleButton.setAction(styleAction);
        styleButton.setup(styleTitle, getS("styleDescription"), AlcUtil.getUrlPath("style.png"));
        // Set the style buttons dynamic images to the current colour
        //refreshStyleButton();
        // Shortcut - s

        Alchemy.shortcuts.setShortcut(KeyEvent.VK_S, styleTitle, styleAction);
//        Alchemy.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), styleTitle);
//        Alchemy.canvas.getActionMap().put(styleTitle, styleAction);

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
        clearButton.setup(clearTitle, getS("clearDescription") + " (" + Alchemy.MODIFIER_KEY_STRING + " " + getS("clearKey") + ")", AlcUtil.getUrlPath("clear.png"));
        // Shortcuts - Modifier Delete/Backspace
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_BACK_SPACE, clearTitle, clearAction, true);
        //Alchemy.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, MODIFIER_KEY), clearTitle);
        //Alchemy.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, MODIFIER_KEY), clearTitle);
        Alchemy.canvas.getActionMap().put(clearTitle, clearAction);
        toolBar.add(clearButton);

        //////////////////////////////////////////////////////////////
        // LINE WIDTH SPINNER
        //////////////////////////////////////////////////////////////
        // currentValue, min, max, stepsize
        SpinnerNumberModel lineWidthNumberModel = new SpinnerNumberModel((int) Alchemy.canvas.getLineWidth(), 1, 50, 1);
        AlcSpinner lineWidthSpinner = new AlcSpinner(getS("lineWeightTitle"), lineWidthNumberModel);
        lineWidthSpinner.setToolTipText(getS("lineWeightDescription"));
        lineWidthSpinner.spinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        JSpinner source = (JSpinner) e.getSource();
                        Number number = (Number) source.getModel().getValue();
                        int value = number.intValue();

                        //System.out.println("Line Width: " + value);
                        Alchemy.canvas.setLineWidth(value);
                    }
                });

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
                        Alchemy.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        Alchemy.toolBar.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                    refreshSwapButton();
                }
            }
        });

        colourButton.addItem(picker);
        toolBar.add(colourButton);

        //////////////////////////////////////////////////////////////
        // SWAP BUTTON
        //////////////////////////////////////////////////////////////

        String swapTitle = getS("swapTitle");
        swapButton = new AlcToggleButton();
        AbstractAction swapAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.canvas.toggleColour();
                // Only toogle the button manually if it is triggered by a key
                // TODO deal with this hard coding of a keycode
                if (e.getActionCommand().equals("s")) {
                    swapButton.setSelected(!swapButton.isSelected());
                }
            }
        };

        swapButton.setAction(swapAction);
        swapButton.setup(swapTitle, getS("swapDescription"), null);
        // Set the swap buttons dynamic images to the current colour
        refreshSwapButton();
        // Shortcut - s
        //Alchemy.shortcuts.setShortcut(KeyEvent.VK_S, styleTitle, styleAction);
//        Alchemy.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), styleTitle);
//        Alchemy.canvas.getActionMap().put(styleTitle, styleAction);

        toolBar.add(swapButton);



        // TODO - Shortcut for toggling between foreground and background colour
        // Shortcut - x
//        Alchemy.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('x'), colourTitle);
//        Alchemy.canvas.getActionMap().put(colourTitle, colourAction);
        //Alchemy.shortcuts.setShortcut(KeyEvent.VK_BACK_SPACE, clearTitle, clearAction, true);
//        AbstractAction changeColourAction = new AbstractAction() {
//
//            public void actionPerformed(ActionEvent e) {
//            //Alchemy.canvas.clear();
//            }
//        };



        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        AlcSlider alphaSlider = new AlcSlider(getS("transparencyTitle"), 0, 255, 255);
        alphaSlider.setToolTipText(getS("transparencyDescription"));
        alphaSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = (int) source.getValue();
                            Alchemy.canvas.setAlpha(value);

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
        // Populate the Popup Menu
        for (int i = 0; i < Alchemy.plugins.creates.length; i++) {
            // The current module
            AlcModule currentModule = Alchemy.plugins.creates[i];
            AlcRadioButtonMenuItem createMenuItem = new AlcRadioButtonMenuItem(currentModule);
            createMenuItem.setToolTipText(currentModule.getDescription());
            //menuItem.

            // Set the action listener
            createMenuItem.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            AlcRadioButtonMenuItem source = (AlcRadioButtonMenuItem) e.getSource();
                            // Check that the module is not already selected
                            if (Alchemy.plugins.currentCreate != source.getIndex()) {
                                // Remove the subtoolbar of the create module
                                removeSubToolBarSection(0);
                                Alchemy.plugins.setCurrentCreate(source.getIndex());
                            }

                            Point loc = source.getLocation();
                            //Rectangle butLoc = createButton.getBounds();
                            int heightFromWindow = loc.y + 50;
                            //System.out.println(loc + " " + heightFromWindow);
                            toggleToolBar(heightFromWindow, true);
                        }
                    });

            if (i == 0) {
                createMenuItem.setSelected(true);
            }

            group.add(createMenuItem);
            createButton.addItem(createMenuItem);
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

                AlcCheckBoxMenuItem affectMenuItem = new AlcCheckBoxMenuItem(currentModule);
                affectMenuItem.setToolTipText(currentModule.getDescription());
                affectMenuItem.addItemListener(
                        new ItemListener() {

                            public void itemStateChanged(ItemEvent e) {

                                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getItemSelectable();

                                // SELECTED
                                if (e.getStateChange() == ItemEvent.SELECTED) {

                                    //System.out.println( index );

                                    Alchemy.plugins.addAffect(source.getIndex());

                                // DESELECTED
                                } else {
                                    Alchemy.plugins.removeAffect(source.getIndex());
                                    // Index is offset to allow for the create module to always be first
                                    removeSubToolBarSection(source.getIndex() + 1);
                                }
                                Point loc = source.getLocation();
                                int heightFromWindow = loc.y + 50;
                                toggleToolBar(heightFromWindow, true);
                            }
                        });

                affectButton.addItem(affectMenuItem);
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
        topAlign.setOpaque(false);
        topAlign.setLayout(new BoxLayout(topAlign, BoxLayout.PAGE_AXIS));

        detachButton = new JButton(AlcUtil.getImageIcon("palette-detach.png"));
        detachButton.setRolloverIcon(AlcUtil.getImageIcon("palette-detach-over.png"));
        detachButton.setToolTipText("Detach the toolbar to a seperate palette");

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
    void resizeToolBar() {
        Dimension toolBarWindowSize = new Dimension(this.windowSize.width, totalHeight);
        resizeToolBar(toolBarWindowSize);
    }

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
    void toggleToolBar(int y) {
        toggleToolBar(y, false);
    }

    /** Function to control the display of the Ui toolbar 
     * 
     * @param y             The height of the mouse to check against
     * @param startTimer    To force start the timer
     */
    void toggleToolBar(int y, boolean startTimer) {
        //int y = event.getY();
        if (y < 10) {
            setToolBarVisible(true);
            insideToolBar = true;
        } else if (y > getTotalHeight() + 5) {
            if (isPopupMenusVisible() || toolBarTimer != null || startTimer) {
                // Set the timer
                setTimer();

            } else {
                setToolBarVisible(false);
            }
            insideToolBar = false;
        // Inside the middle of the toolbar
        } else {
            //System.out.println("In middle");
            insideToolBar = true;
        }
    }

    /** Set the visibility of the UI Toolbar */
    void setToolBarVisible(boolean visible) {
        if (visible != toolBarVisible) {
            this.setVisible(visible);
            toolBarVisible = visible;
            Alchemy.canvas.setMouseEvents(!visible);
            if (!visible) {
                // Be sure to set the cursor back to the cross hair
                Alchemy.canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                colourButton.hidePopup();
                createButton.hidePopup();
                if (affectButton != null) {
                    affectButton.hidePopup();
                }
            }
        }
    }

    /** Return the visibility of the UI Toolbar */
    boolean getToolBarVisible() {
        return toolBarVisible;
    }

    /** Return the height of the UI Toolbar */
    int getToolBarHeight() {
        return toolBarHeight;
    }

    /** Calculate the total height of the toolbar and its subtoolbars */
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
    int getTotalHeight() {
        return totalHeight;
    }

    //////////////////////////////////////////////////////////////
    // SUBTOOLBAR
    //////////////////////////////////////////////////////////////
    private AlcSubToolBar loadSubToolBar() {
        // Initialise the references to the sub toolbar sections
        affectSubToolBarSections = new AlcSubToolBarSection[Alchemy.plugins.getNumberOfAffectModules()];
        // Set to a negative value to indicate no initially loaded sections
        createSubToolBarSection = null;

        // Add the SubToolBar
        AlcSubToolBar toolBar = new AlcSubToolBar();

        return toolBar;
    }

    /** Add a Create Module sub-toolbar */
    public void addSubToolBarSection(AlcSubToolBarSection subToolBarSection) {

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
        // Remove everything
        subToolBar.removeAll();

        // If there is a create section add that first
        if (createSubToolBarSection != null) {

            subToolBar.add(createSubToolBarSection);
        }
        // Add the affect sections
        for (int i = 0; i < affectSubToolBarSections.length; i++) {

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
            if (!Alchemy.preferences.getPaletteAttached()) {
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
                                insideToolBar = false;
                            }
                        } else {
                            setToolBarVisible(false);
                            insideToolBar = false;
                        }
                    }
                    toolBarTimer.stop();
                    toolBarTimer = null;
                }
            });
            toolBarTimer.start();
        }
    }


    //////////////////////////////////////////////////////////////
    // PALETTE
    //////////////////////////////////////////////////////////////
    /** Called when detaching the toolbar into the palette */
    void detachToolBar() {
        if (!subToolBar.isVisible()) {
            subToolBar.setVisible(true);
        }
        this.setToolBarVisible(false);
        this.remove(toolBars);
        this.remove(Alchemy.menuBar);
    }

    /** Called when attaching the toolbar from the palette */
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

    //////////////////////////////////////////////////////////////
    // UTLITY
    //////////////////////////////////////////////////////////////
    /** Get a string from the resource bundle */
    private String getS(String stringName) {
        return Alchemy.bundle.getString(stringName);
    }

    /** Refreshes the style buttons icons based on the current colour */
//    private void refreshStyleButton() {
//        // Get a full and half alpha version of the current colour
//        Color colour = Alchemy.canvas.getColour();
//        Color fullColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 255);
//        Color semiColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 100);
//
//        // STYLE BUTTON
//        BufferedImage style = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
//        Graphics g = style.createGraphics();
//        // Left hand line
//        g.setColor(fullColour);
//        g.fillRect(1, 1, 2, 23);
//        // Right hand rect
//        g.setColor(semiColour);
//        g.fillRect(8, 0, 16, 24);
//        g.dispose();
//        styleButton.setIcon(new ImageIcon(style));
//
//        // STYLE ON BUTTON
//        BufferedImage styleOn = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
//        g = styleOn.createGraphics();
//        // Left hand line
//        g.setColor(semiColour);
//        g.fillRect(0, 0, 2, 24);
//        // Right hand rect
//        g.setColor(fullColour);
//        g.fillRect(8, 0, 16, 24);
//        g.dispose();
//        styleButton.setSelectedIcon(new ImageIcon(styleOn));
//    }
    void refreshSwapButton() {
        Color colour = Alchemy.canvas.getColour();
        // Make sure there is no transparency
        Color fullColour = new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), 255);
        Color bgColour = Alchemy.canvas.getBgColour();

        BufferedImage swap = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics g = swap.createGraphics();

        g.setColor(bgColour);
        g.fillRect(6, 6, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(6, 6, 17, 17);

        g.setColor(fullColour);
        g.fillRect(0, 0, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(0, 0, 18, 18);

        swapButton.setIcon(new ImageIcon(swap));

        BufferedImage swapOn = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        g = swapOn.createGraphics();

        g.setColor(fullColour);
        g.fillRect(0, 0, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(0, 0, 18, 18);

        g.setColor(bgColour);
        g.fillRect(6, 6, 18, 18);
        g.setColor(AlcToolBar.toolBarLineColour);
        g.drawRect(6, 6, 17, 17);

        swapButton.setSelectedIcon(new ImageIcon(swapOn));
    }
}
