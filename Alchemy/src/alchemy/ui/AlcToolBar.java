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
package alchemy.ui;

import alchemy.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Alchemy Toolbar
 * The disappearing toolbar
 * Housing access to all modules and their sub toolbars
 */
public class AlcToolBar extends JToolBar implements AlcConstants, MouseListener {

    /** Reference to the root */
    private final AlcMain root;
    /** Visibility of the ToolBar */
    private boolean toolBarVisible = true;
    /** Toolbar attached or not */
    private boolean toolBarAttached = true;
    /** Height of the ToolBar */
    private static int toolBarHeight = 60;
    /** Total height of all tool bars */
    private int totalHeight = 60;
    /** Keep track of the windowSize */
    public Dimension windowSize;
    /** ToolBar Background Colour */
    public static final Color toolBarBgColour = new Color(225, 225, 225);
    public static final Color toolBarBgStartColour = new Color(235, 235, 235, 240);
    public static final Color toolBarBgEndColour = new Color(215, 215, 215, 240);
    public static final Color toolBarLineColour = new Color(140, 140, 140);
    public static final Color toolBarHighlightColour = new Color(231, 231, 231);
    public static final Color toolBarAlphaHighlightColour = new Color(231, 231, 231, 240);
    /** ToolBar Font */
    public static final Font toolBarFont = new Font("sansserif", Font.PLAIN, 11);
    public static final Font subToolBarFont = new Font("sansserif", Font.PLAIN, 10);
    public static final Font subToolBarBoldFont = new Font("sansserif", Font.BOLD, 11);
    /** Popup buttons for the create and affect button in the toolbar - these are declared global so we can hide the popup when hiding the toolbar */
    private AlcPopupButton createButton,  affectButton;
    /** The main tool bar inside the toolbar */
    private AlcMainToolBar mainToolBar;
    /** The sub toolbar below the main toolbar */
    private AlcSubToolBar subToolBar;
    /** Sections within the sub toolbar - either loaded or not */
    private AlcSubToolBarSection[] affectSubToolBarSections;
    /** The create section within the sub toolbar - index of the loaded section */
    private AlcSubToolBarSection createSubToolBarSection;
    /** Number of current sub toolbar sections loaded */
    private int currentSubToolBarSections = 0;
    /** Actions used in the toolbar */
    public Action clearAction,  styleAction,  bwAction;

    /**
     * Creates a new instance of AlcToolBar
     */
    public AlcToolBar(final AlcMain root) {

        // General Toolbar settings
        this.root = root;
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(false);
        this.setName("Toolbar");
        //this.setVisible(false);
        this.addMouseListener(this);
        this.setFloatable(false);
        // Make this a Box Layout so all submenus are stacked below
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new BorderLayout());

        loadToolBar();
        loadSubToolBar();

        // Turn off the visibility until the mouse enters the top of the screen
        setToolBarVisible(false);

    }

    /** Load the tool bar */
    private void loadToolBar() {
        // Create the main toolbar
        mainToolBar = new AlcMainToolBar(root);

        // Buttons in the main toolbar
        // Align LEFT
        //JPanel toolBarLeft = new JPanel();
        //toolBarLeft.setOpaque(false);   // Turn off the background

        //////////////////////////////////////////////////////////////
        // STYLE BUTTON
        //////////////////////////////////////////////////////////////
        String styleTitle = "Style";
        final AlcToggleButton styleButton = new AlcToggleButton();
        styleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.canvas.toggleStyle();
                styleButton.setSelected(!styleButton.isSelected());
            }
        };

        styleButton.setAction(styleAction);
        styleButton.setup("Style", "Make marks as a lines or solid shapes (s)", AlcUtil.getUrlPath("data/style.png"));
        // Shortcut - s
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), styleTitle);
        root.canvas.getActionMap().put(styleTitle, styleAction);

        mainToolBar.add(styleButton);

        //////////////////////////////////////////////////////////////
        // CLEAR BUTTON
        //////////////////////////////////////////////////////////////
        String clearTitle = "Clear";
        clearAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.canvas.clear();
            }
        };
        AlcButton clearButton = new AlcButton(clearAction);
        clearButton.setup(clearTitle, "Clear the screen (" + AlcMain.MODIFIER_KEY + "+BACKSPACE/DELETE)", AlcUtil.getUrlPath("data/clear.png"));
        // Shortcuts - Modifier Delete/Backspace
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, MENU_SHORTCUT), clearTitle);
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, MENU_SHORTCUT), clearTitle);
        root.canvas.getActionMap().put(clearTitle, clearAction);
        mainToolBar.add(clearButton);

        //////////////////////////////////////////////////////////////
        // LINE WIDTH SPINNER
        //////////////////////////////////////////////////////////////
        // currentValue, min, max, stepsize
        SpinnerNumberModel lineWidthNumberModel = new SpinnerNumberModel((int)root.canvas.getLineWidth(), 1, 50, 1);
        AlcSpinner lineWidthSpinner = new AlcSpinner("Line Weight", lineWidthNumberModel);
        lineWidthSpinner.spinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        JSpinner source = (JSpinner) e.getSource();
                        Number number = (Number) source.getModel().getValue();
                        int value = number.intValue();

                        //System.out.println("Line Width: " + value);
                        root.canvas.setLineWidth(value);
                    }
                });

        mainToolBar.add(lineWidthSpinner);

        //////////////////////////////////////////////////////////////
        // BLACK WHITE BUTTON
        //////////////////////////////////////////////////////////////
        String bwTitle = "Black/White";
        final AlcToggleButton bwButton = new AlcToggleButton();
        bwAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.canvas.toggleBlackWhite();
                bwButton.setSelected(!bwButton.isSelected());
            }
        };
        bwButton.setAction(bwAction);
        bwButton.setup("Black/White", "Make marks in black or white (x)", AlcUtil.getUrlPath("data/blackwhite.png"));
        // Shortcut - x
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('x'), bwTitle);
        root.canvas.getActionMap().put(bwTitle, bwAction);

        mainToolBar.add(bwButton);

        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        AlcSlider alphaSlider = new AlcSlider("Transparency", 0, 255, 255);
        alphaSlider.slider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        JSlider source = (JSlider) e.getSource();
                        if (!source.getValueIsAdjusting()) {
                            int value = (int) source.getValue();
                            root.canvas.setAlpha(value);

                        }
                    }
                    });

        mainToolBar.add(alphaSlider);

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        mainToolBar.add(new AlcSeparator());


        //////////////////////////////////////////////////////////////
        // CREATE
        //////////////////////////////////////////////////////////////
        createButton = new AlcPopupButton("Create", "Create Shapes", AlcUtil.getUrlPath("data/create.png"));
        // Button group for the radio buttons
        ButtonGroup group = new ButtonGroup();
        // Populate the Popup Menu
        for (int i = 0; i < root.creates.length; i++) {
            // The current module
            AlcModule currentModule = root.creates[i];
            AlcRadioButtonMenuItem createMenuItem = new AlcRadioButtonMenuItem(currentModule);
            createMenuItem.setToolTipText(currentModule.getDescription());
            //menuItem.

            // Set the action listener
            createMenuItem.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            AlcRadioButtonMenuItem source = (AlcRadioButtonMenuItem) e.getSource();
                            // Check that the module is not already selected
                            if (root.currentCreate != source.getIndex()) {
                                // Remove the subtoolbar of the create module
                                removeSubToolBarSection(0);
                                root.setCurrentCreate(source.getIndex());
                            }

                        }
                    });

            if (i == 0) {
                createMenuItem.setSelected(true);
            }

            group.add(createMenuItem);
            createButton.addItem(createMenuItem);
        }

        mainToolBar.add(createButton);


        //////////////////////////////////////////////////////////////
        // AFFECT
        //////////////////////////////////////////////////////////////
        affectButton = new AlcPopupButton("Affect", "Affect Shapes", AlcUtil.getUrlPath("data/create.png"));
        for (int i = 0; i < root.affects.length; i++) {
            // The current module
            AlcModule currentModule = root.affects[i];

            AlcCheckBoxMenuItem affectMenuItem = new AlcCheckBoxMenuItem(currentModule);
            affectMenuItem.setToolTipText(currentModule.getDescription());
            affectMenuItem.addItemListener(
                    new ItemListener() {

                        public void itemStateChanged(ItemEvent e) {

                            AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getItemSelectable();

                            // SELECTED
                            if (e.getStateChange() == ItemEvent.SELECTED) {

                                //System.out.println( index );

                                root.addAffect(source.getIndex());

                            // DESELECTED
                            } else {
                                root.removeAffect(source.getIndex());
                                // Index is offset to allow for the create module to always be first
                                removeSubToolBarSection(source.getIndex() + 1);
                            }
                        }
                    });

            affectButton.addItem(affectMenuItem);
        }
        mainToolBar.add(affectButton);

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        mainToolBar.add(new AlcSeparator());

        this.add("Center", mainToolBar);

    }

    private void loadSubToolBar() {
        // Initialise the references to the sub toolbar sections
        affectSubToolBarSections = new AlcSubToolBarSection[root.getNumberOfAffectModules()];
        // Set to a negative value to indicate no initially loaded sections
        createSubToolBarSection = null;

        // Add the SubToolBar
        subToolBar = new AlcSubToolBar(root);
        this.add("South", subToolBar);
        // Make it invisible until it gets some content
        subToolBar.setVisible(false);
    }

    public void resizeToolBar() {
        Dimension toolBarWindowSize = new Dimension(this.windowSize.width, totalHeight);
        resizeToolBar(toolBarWindowSize);
    }

    public void resizeToolBar(Dimension windowSize) {
        this.setBounds(0, 0, windowSize.width, totalHeight);
        this.windowSize = windowSize;
        this.revalidate();
    }

    private void refreshToolBar() {
        // Recalculate the total height of the tool bar
        calculateTotalHeight();
        // Then resize it
        resizeToolBar();
    }

    /** Set the visibility of the UI Toolbar */
    public void setToolBarVisible(boolean b) {
        if (toolBarAttached) {
            //this.setVisible(b);

            // This is very hacky but...
            // To make sure the menubars shortcuts still work
            // we move the toolbar off the top of the scren rather
            // than making it invisible (which disables the shortcuts)
            // TODO - implement a better system to handle keyboard shortcuts
            // SEE: http://java.sun.com/docs/books/tutorial/uiswing/examples/misc/ActionDemoProject/src/misc/ActionDemo.java

            if (b) {
                //this.setLocation(0, 0);
                this.setVisible(true);
            } else {
                //this.setLocation(0, -1000);
                this.setVisible(false);
                // Turn off the popup(s) when we leave the toolbar area
                if (createButton != null) {
                    createButton.hidePopup();
                }
                if (affectButton != null) {
                    affectButton.hidePopup();
                }
            }
            toolBarVisible = b;
        }
    }

    private void checkParentWindow() {
        //System.out.println("Check Parent");
        Container container = this.getTopLevelAncestor();
        if (container != null) {
            if (!container.getClass().getName().startsWith("alchemy")) {
                if (toolBarAttached) {
                    // JUST DETACHED
                    //System.out.println("JUST DETACHED");
                    toolBarAttached = false;
                    System.out.println(container.getClass().getName());

                    Window window = (Window) container;
                    //window.setAlwaysOnTop(true);
                    window.addWindowListener(new WindowAdapter() {

                        public void windowClosing(WindowEvent e) {
                            //System.out.println("Close button clicked");
                            toolBarAttached = true;
                        // TODO - Debug the disappearing toolbar on a mac bug
                        }
                    });
                }
            } else {
                toolBarAttached = true;
            }

        }
    }

    /** Add a Create Module sub-toolbar */
    public void addSubToolBarSection(AlcSubToolBarSection subToolBarSection) {

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

    public void removeSubToolBarSection(int index) {

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
        //System.out.println("Sections:" + currentSubToolBarSections);
        // Remove everything
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
        // TODO - why doesn't the subtoolbar section display immediately when reloaded?

        //System.out.println("Count: " + subToolBar.getComponentCount());

        if (currentSubToolBarSections > 0) {
            //subToolBar.revalidate();
            subToolBar.setVisible(true);
        } else {
            subToolBar.setVisible(false);
        }

        refreshToolBar();
    }

    // GETTERS
    /** Return the visibility of the UI Toolbar */
    public boolean getToolBarVisible() {
        return toolBarVisible;
    }

    /** Return the height of the UI Toolbar */
    public int getToolBarHeight() {
        return toolBarHeight;
    }

    /** Calculate the total height of the toolbar and its subtoolbars */
    public void calculateTotalHeight() {
        // Start with the main toolbar height
        int newTotalHeight = mainToolBar.getHeight();
        if (subToolBar.isVisible()) {
            newTotalHeight += subToolBar.getHeight();
        }
        if (AlcMain.PLATFORM != MACOSX) {
            // Add the height of the menubar if this is not a mac
            newTotalHeight += root.menuBar.getHeight();
        }
        this.totalHeight = newTotalHeight;
    }

    /** Return the total height of the toolbar and its subtoolbars */
    public int getTotalHeight() {
        return totalHeight;
    }

    public void loadCreate(int index) {
        root.setCurrentCreate(index);
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        checkParentWindow();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /*
    public class ClearAction extends AbstractAction {
    public ClearAction() {
    //super("Clear", AlcUtil.getUrlPath("data/clear.png"));
    //putValue(SHORT_DESCRIPTION, "Description goes here");
    //putValue(NAME, "Clear");
    //putValue(SMALL_ICON, "data/clear.png");
    //putValue(MNEMONIC_KEY, mnemonic);
    //putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, MENU_SHORTCUT));
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, MENU_SHORTCUT));
    }
    public void actionPerformed(ActionEvent e) {
    System.out.println("CLEAR ACTION CALLED");
    root.canvas.clear();
    }
    }*/
}
