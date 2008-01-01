/**
 * AlcToolBar.java
 *
 * Created on November 24, 2007, 3:08 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.ui;

import alchemy.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AlcToolBar extends JComponent implements AlcConstants {

    /** Reference to the root */
    private final AlcMain root;
    /** Visibility of the ToolBar */
    private boolean toolBarVisible = true;
    /** Height of the ToolBar */
    private final static int toolBarHeight = 60;
    /** Total height of all tool bars */
    private int totalHeight = toolBarHeight;
    /** Keep track of the windowSize */
    public Dimension windowSize;
    /** ToolBar Background Colour */
    public final static Color toolBarBgColour = new Color(225, 225, 225);
    public final static Color toolBarBgStartColour = new Color(235, 235, 235, 235);
    public final static Color toolBarBgEndColour = new Color(215, 215, 215, 235);
    public final static Color toolBarLineColour = new Color(140, 140, 140);
    public final static Color toolBarHighlightColour = new Color(231, 231, 231);
    /** ToolBar Font */
    public final static Font toolBarFont = new Font("sansserif", Font.PLAIN, 11);
    /** Popup buttons for the create and affect button in the toolbar - these are declared global so we can hide the popup when hiding the toolbar */
    private AlcPopupButton createButton,  affectButton;
    /** The main tool bar inside the toolbar */
    private AlcMainToolBar mainToolBar;
    /** The sub toolbars inside the toolbar */
    private AlcSubToolBar[] subToolBars;
    /** The number of subtoolbars modules currently loaded */
    private int numberOfCurrentSubToolBars = 0;

    /**
     * Creates a new instance of AlcToolBar
     */
    public AlcToolBar(AlcMain root) {

        // General Toolbar settings
        this.root = root;
        // Make this a Box Layout so all submenus are stacked below
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Intialise the array of subToolBars - maximum size is that off all the affect modules plus one (create module)
        subToolBars = new AlcSubToolBar[root.getNumberOfAffectModules() + 1];

        loadToolBar();

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
        AlcToggleButton lineButton = new AlcToggleButton(this, "Style", "Make marks as a lines or solid shapes", getUrlPath("data/style.png"));
        lineButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        root.canvas.toggleStyle();
                    }
                });
        mainToolBar.add(lineButton);

        //////////////////////////////////////////////////////////////
        // CLEAR BUTTON
        //////////////////////////////////////////////////////////////
        AlcButton clearButton = new AlcButton(this, "Clear", "Clear the screen (" + AlcMain.MODIFIER_KEY + "+BACKSPACE/DELETE)", getUrlPath("data/clear.png"));
        clearButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        root.canvas.clear();
                    }
                });
        mainToolBar.add(clearButton);

        //////////////////////////////////////////////////////////////
        // LINE WIDTH SPINNER
        //////////////////////////////////////////////////////////////
        // currentValue, min, max, stepsize
        SpinnerNumberModel lineWidthNumberModel = new SpinnerNumberModel(root.canvas.getLineWidth(), 0, 100, 1);
        AlcSpinner lineWidthSpinner = new AlcSpinner(this, "Line Weight", lineWidthNumberModel);
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
        AlcToggleButton bwButton = new AlcToggleButton(this, "Black/White", "Make marks in black or white", getUrlPath("data/style.png"));
        bwButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        root.canvas.toggleBlackWhite();
                    }
                });
        mainToolBar.add(bwButton);


        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        AlcSlider alphaSlider = new AlcSlider(this, "Transparency", 0, 255, 255);
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
        mainToolBar.add(new AlcSeparator(this));


        //////////////////////////////////////////////////////////////
        // CREATE
        //////////////////////////////////////////////////////////////
        createButton = new AlcPopupButton(this, "Create", "Create Shapes", getUrlPath("data/create.png"));
        // Button group for the radio buttons
        ButtonGroup group = new ButtonGroup();
        // Populate the Popup Menu
        for (int i = 0; i < root.creates.length; i++) {
            // The current module
            AlcModule currentModule = root.creates[i];
            AlcRadioButtonMenuItem createMenuItem = new AlcRadioButtonMenuItem(this, currentModule);
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
                                removeSubToolBar(0);
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
        affectButton = new AlcPopupButton(this, "Affect", "Affect Shapes", getUrlPath("data/create.png"));
        for (int i = 0; i < root.affects.length; i++) {
            // The current module
            AlcModule currentModule = root.affects[i];

            AlcCheckBoxMenuItem affectMenuItem = new AlcCheckBoxMenuItem(this, currentModule);
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
                                removeSubToolBar(source.getIndex() + 1);

                            }


                        }
                    });

            affectButton.addItem(affectMenuItem);
        }
        mainToolBar.add(affectButton);

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        mainToolBar.add(new AlcSeparator(this));

        this.add(mainToolBar);

    }

    public void resizeToolBar() {
        Dimension toolBarWindowSize = new Dimension(this.windowSize.width, getTotalHeight());
        resizeToolBar(toolBarWindowSize);
    }

    public void resizeToolBar(Dimension windowSize) {
        this.setBounds(0, 0, windowSize.width, getTotalHeight());
        this.windowSize = windowSize;
        this.revalidate();
    }

    /** Set the visibility of the UI Toolbar */
    public void setToolBarVisible(boolean b) {
        this.setVisible(b);
        // Turn off the popup(s) when we leave the toolbar area
        if (!b) {
            if (createButton != null) {
                createButton.hidePopup();
            }
            if (affectButton != null) {
                affectButton.hidePopup();
            }
        }
        toolBarVisible = b;
    }

    private void refreshToolBar() {
        // Recalculate the total height of the tool bar
        calculateTotalHeight();
        // Then resize it
        resizeToolBar();
    }

    /** Add a Create Module sub-toolbar */
    public void addSubToolBar(AlcSubToolBar subToolBar) {

        numberOfCurrentSubToolBars++;

        // Retrive the subtoolbars module index and add one to offset for the create toolbar
        int index = subToolBar.getIndex() + 1;
        int type = subToolBar.getModuleType();

        switch (type) {

            case CREATE:
                subToolBars[0] = subToolBar;
                subToolBars[0].setLocation(0, getToolBarHeight());
                this.add(subToolBars[0]);
                break;

            case AFFECT:
                if (subToolBars[index] != null) {
                    this.remove(subToolBars[index]);
                    subToolBars[index] = null;
                }
                subToolBars[index] = subToolBar;
                subToolBars[index].setLocation(0, totalHeight);
                this.add(subToolBars[index]);
                break;
        }

        // Refresh the toolbar with the new contents
        refreshToolBar();
    }

    public void removeSubToolBar(int index) {
        // If there there is a subtool bar loaded
        if (subToolBars[index] != null) {
            this.remove(subToolBars[index]);
            subToolBars[index] = null;
            numberOfCurrentSubToolBars--;
            // Refresh the toolbar with the new contents
            refreshToolBar();
        }
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

    // IMAGE LOADING FUNCTIONS
    /** Returns a URL from a String, or null if the path was invalid. */
    public URL getUrlPath(String path) {
        // Path to the file from the main class
        URL imgUrl = AlcMain.class.getResource(path);
        if (imgUrl != null) {
            return imgUrl;
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Returns an ImageIcon from a String, or null if the path was invalid. */
    public ImageIcon createImageIcon(String path) {

        URL imgUrl = AlcMain.class.getResource(path);
        if (imgUrl != null) {
            return createImageIcon(imgUrl);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /** Returns an ImageIcon from a URL, or null if the path was invalid. */
    public ImageIcon createImageIcon(URL imgUrl) {
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            // Check the icon actually exists - bit of a hack!
            if (icon.getIconWidth() > 0) {
                return icon;
            }
        }
        //System.err.println("Couldn't find file: " + imgUrl.toString());
        return null;
    }

    /** Calculate the total height of the toolbar and its subtoolbars */
    public void calculateTotalHeight() {
        // Start with the main toolbar height
        int newTotalHeight = mainToolBar.getHeight();

        // Only run the loop if there are subtoolbars loaded
        if (numberOfCurrentSubToolBars > 0) {
            for (int i = 0; i < subToolBars.length; i++) {
                if (subToolBars[i] != null) {
                    newTotalHeight += subToolBars[i].getHeight();
                }
            }
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
}
