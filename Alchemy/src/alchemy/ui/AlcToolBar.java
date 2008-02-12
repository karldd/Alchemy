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
import javax.swing.plaf.basic.BasicToolBarUI;

/**
 * Alchemy Toolbar
 * The disappearing toolbar
 * Housing access to all modules and their sub toolbars
 */
public class AlcToolBar extends JToolBar implements AlcConstants, MouseListener {

    /** Reference to the root */
    private final AlcMain root;
    /** Keep track of the windowSize */
    public Dimension windowSize;
    //////////////////////////////////////////////////////////////
    // INTERFACE COLOURS
    //////////////////////////////////////////////////////////////
    public static final Color toolBarBgColour = new Color(225, 225, 225);
    public static final Color toolBarBgStartColour = new Color(235, 235, 235, 240);
    public static final Color toolBarBgEndColour = new Color(215, 215, 215, 240);
    public static final Color toolBarLineColour = new Color(140, 140, 140);
    public static final Color toolBarHighlightColour = new Color(231, 231, 231);
    public static final Color toolBarAlphaHighlightColour = new Color(231, 231, 231, 240);
    //////////////////////////////////////////////////////////////
    // FONTS
    //////////////////////////////////////////////////////////////
    public static final Font toolBarFont = new Font("sansserif", Font.PLAIN, 11);
    public static final Font subToolBarFont = new Font("sansserif", Font.PLAIN, 10);
    public static final Font subToolBarBoldFont = new Font("sansserif", Font.BOLD, 11);
    //////////////////////////////////////////////////////////////
    // TOOLBAR ELEMENTS
    //////////////////////////////////////////////////////////////
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
    public Action styleAction,  bwAction;
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
    public javax.swing.Timer toolBarTimer;
    /** Cursor inside toolbar or not */
    private boolean insideToolBar;
    /** Custom UI for the toolbar */
    private BasicToolBarUI toolBarUI;

    /**
     * Creates a new instance of AlcToolBar
     */
    public AlcToolBar(final AlcMain root) {

        toolBarUI = new BasicToolBarUI() {

//            protected RootPaneContainer createFloatingWindow(JToolBar toolbar) {
//                JFrame f = super.createFloatingFrame(toolbar);
//                //dialog.setModal(true);
//                //root.canvas.setMouseEvents(true);
////                JDialog d = (JDialog) f;
////                System.out.println("called ");
//                //JLayeredPane layeredPane = f.getLayeredPane();
//
//                //Component[] comps = layeredPane.getComponentsInLayer(JLayeredPane.FRAME_CONTENT_LAYER.intValue());
//                //System.out.println(comps);
//
////                for (int i = 0; i < comps.length; i++) {
////                    Component component = comps[i];
////
////                    if (component != f.getContentPane()) {
////                        component.setPreferredSize(new Dimension(12, 12));
////
////                        JComponent c = ((JComponent) component);
////
////                        Component[] subComponents = c.getComponents();
////
////                        for (int j = 0; j < subComponents.length; j++) {
////                            Component component2 = subComponents[j];
////
////                            if (component2 instanceof JButton) {
////                                JButton b = (JButton) component2;
////
////                                b.setIcon(UIManager.getIcon("InternalFrame.paletteCloseIcon"));
////
////                                b.setPreferredSize(new Dimension(8, 8));
////                                b.setMargin(new Insets(1, 1, 1, 1));
////                            }
////
////                        }
////                    }
////                }
//
//                //f.setBorder(new BevelBorder(BevelBorder.RAISED));
//                //JFrame.setDefaultLookAndFeelDecorated(true);
//                //JFrame.setDefaultLookAndFeelDecorated(true);
//                //f.setUndecorated(true);
//                //f.getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
//                return f;
//            }
//            protected BasicToolBarUI.DragWindow createDragWindow(JToolBar toolbar) {
//                return dragWindow;
//            }
            public boolean canDock(Component c, Point p) {

                if (isFloating()) {
                    if (p.y < 50) {
                        return true;
                    }
                } else {
                    // Hacky way to be sure the canvas is still drawable
                    root.canvas.setMouseEvents(true);
                }
                return false;
            //System.out.println(p);
            //System.out.println(c);
            //return true;
            }
//
//            public boolean isFloating() {
//                return true;
//            }
        };
        toolBarUI.setDockingColor(toolBarBgColour);
        toolBarUI.setFloatingColor(toolBarBgColour);
        this.setUI(toolBarUI);






        this.setOrientation(SwingConstants.HORIZONTAL);
        // General Toolbar settings
        this.root = root;
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.setOpaque(false);
        this.setName("Toolbar");
        //this.setVisible(false);
        this.addMouseListener(this);
        this.setFloatable(true);
        // Make this a Box Layout so all submenus are stacked below
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setLayout(new BorderLayout());

        loadToolBar();

        AlcPalette palette = new AlcPalette(root);
        palette.setPaletteSize(800, 75);
        palette.setPaletteLocation(100, 100);
        //palette.add(mainToolBar);

        loadSubToolBar();

        // Turn off the visibility until the mouse enters the top of the screen
        setToolBarVisible(false);

        System.out.println(getUI());
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
        String styleTitle = getS("styleTitle");
        final AlcToggleButton styleButton = new AlcToggleButton();
        styleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.canvas.toggleStyle();
                // Only toogle the button manually if it is triggered by a key
                if (e.getActionCommand().equals("s")) {
                    styleButton.setSelected(!styleButton.isSelected());
                }
            }
        };

        styleButton.setAction(styleAction);
        styleButton.setup(styleTitle, getS("styleDescription"), AlcUtil.getUrlPath("data/style.png"));
        // Shortcut - s
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('s'), styleTitle);
        root.canvas.getActionMap().put(styleTitle, styleAction);

        mainToolBar.add(styleButton);

        //////////////////////////////////////////////////////////////
        // CLEAR BUTTON
        //////////////////////////////////////////////////////////////
        String clearTitle = getS("clearTitle");
        AbstractAction clearAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.canvas.clear();
            }
        };
        AlcButton clearButton = new AlcButton(clearAction);
        clearButton.setup(clearTitle, getS("clearDescription") + " (" + AlcMain.MODIFIER_KEY + getS("clearKey") + ")", AlcUtil.getUrlPath("data/clear.png"));
        // Shortcuts - Modifier Delete/Backspace
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, MENU_SHORTCUT), clearTitle);
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, MENU_SHORTCUT), clearTitle);
        root.canvas.getActionMap().put(clearTitle, clearAction);
        mainToolBar.add(clearButton);

        //////////////////////////////////////////////////////////////
        // LINE WIDTH SPINNER
        //////////////////////////////////////////////////////////////
        // currentValue, min, max, stepsize
        SpinnerNumberModel lineWidthNumberModel = new SpinnerNumberModel((int) root.canvas.getLineWidth(), 1, 50, 1);
        AlcSpinner lineWidthSpinner = new AlcSpinner(getS("lineWeightTitle"), lineWidthNumberModel);
        lineWidthSpinner.setToolTipText(getS("lineWeightDescription"));
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
        String bwTitle = getS("bwTitle");
        final AlcToggleButton bwButton = new AlcToggleButton();
        bwAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.canvas.toggleBlackWhite();
                // Only toogle the button manually if it is triggered by a key
                if (e.getActionCommand().equals("x")) {
                    bwButton.setSelected(!bwButton.isSelected());
                }
            }
        };
        bwButton.setAction(bwAction);
        bwButton.setup(bwTitle, getS("bwDescription"), AlcUtil.getUrlPath("data/blackwhite.png"));
        // Shortcut - x
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('x'), bwTitle);
        root.canvas.getActionMap().put(bwTitle, bwAction);

        mainToolBar.add(bwButton);

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
        createButton = new AlcPopupButton(getS("createTitle"), getS("createDescription"), AlcUtil.getUrlPath("data/create.png"));
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

        mainToolBar.add(createButton);


        //////////////////////////////////////////////////////////////
        // AFFECT
        //////////////////////////////////////////////////////////////
        affectButton = new AlcPopupButton(getS("affectTitle"), getS("affectDescription"), AlcUtil.getUrlPath("data/affect.png"));
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
                            Point loc = source.getLocation();
                            int heightFromWindow = loc.y + 50;
                            toggleToolBar(heightFromWindow, true);
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
        this.repaint(0, 0, windowSize.width, totalHeight);
        this.revalidate();
    }

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
    public void toggleToolBar(int y) {
        toggleToolBar(y, false);
    }

    /** Function to control the display of the Ui toolbar 
     * 
     * @param y             The height of the mouse to check against
     * @param startTimer    To force start the timer
     */
    public void toggleToolBar(int y, boolean startTimer) {
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
    public void setToolBarVisible(boolean visible) {
        if (!toolBarUI.isFloating()) {
            if (visible != toolBarVisible) {
                //System.out.println("Visible: " + visible);
                this.setVisible(visible);
                toolBarVisible = visible;
                root.canvas.setMouseEvents(!visible);
                //System.out.println(!visible);
                if (!visible) {
                    createButton.hidePopup();
                    affectButton.hidePopup();
                }
            }
        }
    }

    /** Sets and manages a timer used to delay hiding of the toolbar */
    private void setTimer() {
        if (toolBarTimer == null) {
            //System.out.println("Timer created");
            toolBarTimer = new javax.swing.Timer(1000, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    //System.out.println("Timer called");

                    if (!insideToolBar) {
                        if (isPopupMenusVisible()) {
                            if (!createButton.isInside() && !affectButton.isInside()) {
                                //System.out.println("Timer setting visibility");
                                setToolBarVisible(false);
                                insideToolBar = false;
                            }
                        } else {
                            //System.out.println("Timer setting visibility");
                            setToolBarVisible(false);
                            insideToolBar = false;
                        }
                    //System.out.println(isPopupMenusVisible());
                    }
                    // Else when inside the toolbar just hide the popup menu
//                            } else {
//                                if (!createButton.isInside()) {
//                                    createButton.hidePopup();
//                                }
//                                if (!affectButton.isInside()) {
//                                    affectButton.hidePopup();
//                                }
//                            }
                    toolBarTimer.stop();
                    toolBarTimer = null;
                }
            });
            toolBarTimer.start();
        }
    }

    /** Check if the toolbar is part of the main window or seperate */
//    private void checkParentWindow() {
//        //System.out.println("Check Parent");
//        Container container = this.getTopLevelAncestor();
//        if (container != null) {
//            if (!container.getClass().getName().startsWith("alchemy")) {
//                if (toolBarAttached) {
//                    // JUST DETACHED
//                    //System.out.println("JUST DETACHED");
//                    toolBarAttached = false;
//                    System.out.println(container.getClass().getName());
//
//                    Window window = (Window) container;
//                    //window.setAlwaysOnTop(true);
//                    window.addWindowListener(new WindowAdapter() {
//
//                        public void windowClosing(WindowEvent e) {
//                            //System.out.println("Close button clicked");
//                            toolBarAttached = true;
//                        // TODO - Debug the disappearing toolbar on a mac bug
//                        }
//                    });
//                }
//            // Tool bar is reattached
//            } else {
//                //System.out.println(container.getClass().getName());
//                toolBarAttached = true;
//            }
//
//        }
//    }
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

        if (currentSubToolBarSections > 0) {
            //subToolBar.revalidate();
            subToolBar.setVisible(true);
        } else {
            subToolBar.setVisible(false);
        }

        refreshToolBar();
    }

    // GETTERS
    /** Get a string from the resource bundle */
    private String getS(String stringName) {
        return root.bundle.getString(stringName);
    }

    /** Return the visibility of the UI Toolbar */
    public boolean getToolBarVisible() {
        return toolBarVisible;
    }

    /** Check if any of the popup menus are visible */
    public boolean isPopupMenusVisible() {
        boolean visible = false;
        //if (createButton != null) {
        if (createButton.isPopupVisible()) {
            visible = true;
        }
        //}
        //if (affectButton != null) {
        if (affectButton.isPopupVisible()) {
            visible = true;
        }
        //}
        return visible;
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
    //checkParentWindow();
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
