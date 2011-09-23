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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Random;
        
/**
 * Alchemy Toolbar
 * The disappearing toolbar
 * Housing access to all modules and their sub toolbars
 */
public class AlcToolBar extends AlcAbstractToolBar implements AlcConstants {

    //////////////////////////////////////////////////////////////
    // TOOLBAR ELEMENTS
    //////////////////////////////////////////////////////////////
    /** Popup buttons for the color, create, amd affect buttons in the toolbar
     *  These are declared global so we can hide the popup menus when hiding the toolbar */
    private AlcPopupButton createButton,  affectButton, swatchMenuButton;
    /** Modified version of AlColorButton which just holds a single color */
    AlcSingleColorButton colorButton;
    /** Saved swatch colors are built using this button */
    private JButton swatchButton;
    /** The main tool bar inside the toolbar */
    private AlcToolBarMain mainToolBar;
    /** The swatch tool bar inside the toolbar */
    private AlcToolBarMain swatchToolBar;
    /** The sub toolbar below the main toolbar */
    private AlcToolBarSub subToolBar;
    /** Container holding the main and sub toolbars */
    JPanel toolBars;
    /** Detach toolbar button */
    
    // The swatch toolbar area that holds saved colors
    JPanel swatchColors;
    AlcSwatchColorButton swatchColorButton;
   
    // the rest of the swatch toolbar
    JPanel swatchTools;
    Box addRemoveBox;
    Box transparencyBox;
    AlcButton removeFromSwatchButton;
    AlcButton moveColorLeftButton;
    AlcButton moveColorRightButton;
    
    AlcButton undoButton;
    
    
    private JButton detachButton;
    /** Transparency slider */
    private AlcSlider transparencySlider;
    /** Sections within the sub toolbar - either loaded or not */
    private AlcToolBarSubSection[] affectSubToolBarSections;
    /** The create section within the sub toolbar - index of the loaded section */
    private AlcToolBarSubSection createSubToolBarSection;
    /** Number of current sub toolbar sections loaded */
    private int currentSubToolBarSections = 0;
    /** Color picker */
    private AlcColorPicker lClickPicker,  rClickPicker;
    /** Foreground Background Button - gets updated when the colors are swapped */
    AlcToggleButton fgbgButton;
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
    
    private Random random = new Random();
    
    private boolean swatched=false;
    private AbstractAction toolBarFlipAction;
    
 
    /** Schedule update for the Foreground/Background button */
    //private boolean updateSwapButton = false;
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
        
        // Flip between main and swatch toolbars
        toolBarFlipAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                //is toolbar currently set to "swatch view"? intitially false.
                if(swatched){
                  toolBars.remove(swatchToolBar);
                  colorButton.hidePopup();
                  
                  toolBars.add("Center", mainToolBar);
                  
                  refreshSubToolBar();
                  swatched=false;
               } else {
                  toolBars.remove(mainToolBar);
                  subToolBar.setVisible(false);

                  createButton.hidePopup();
                  if (affectButton != null) {
                      affectButton.hidePopup();
                  }
                  toolBars.add("Center", swatchToolBar);
                  
                  swatched=true;
                  
                  refreshToolBar();
               }
               
               //set to "keyed on" when poped up with TAB key
               if (!Alchemy.preferences.paletteAttached) {
                    if (!toolBarVisible) {
                        setToolBarVisible(true);
                        toolBarKeyedOn = true;
                    }
               }
            }
            
        };
        
        // Shortcut - TAB
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_TAB, "FlipToolBar", toolBarFlipAction);
          
        // Create and add the main toolbar      
        mainToolBar = loadToolBar();
        toolBars.add("Center", mainToolBar);
        
        // Create the swatch toolbar, don't show till toolbar flip
        swatchToolBar = loadSwatchBar();
        
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
                if (Alchemy.canvas.getCursor() == CURSOR_BLANK) {
                    Alchemy.canvas.setCursor(CURSOR_CROSS);
                } else {
                    Alchemy.canvas.setCursor(CURSOR_BLANK);
                }
            }
        };

        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_H, "toggleCursor", hideCursorAction);

        // Start the eyedropper with the I key
        AbstractAction eyedropperAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {

                        lClickPicker.startEyeDropper();
 
            }
        };

        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_I, "startEyeDropper", eyedropperAction);


        this.windowSize = new Dimension(Alchemy.window.getWindowSize().width, mainToolBar.getHeight());

        // Turn off the visibility until the mouse enters the top of the screen
        setToolBarVisible(false);

    }
    
    private AlcToolBarMain loadSwatchBar(){

        AlcToolBarMain toolBarGroup = new AlcToolBarMain();

        JPanel toolBar = new JPanel();
        toolBar.setOpaque(false);  
        toolBar.setLayout(new BorderLayout());        
        
        swatchTools = new JPanel();
        swatchTools.setOpaque(false);
        swatchTools.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addRemoveBox = new Box(BoxLayout.Y_AXIS);
        transparencyBox = new Box(BoxLayout.X_AXIS);
        
        
        JPanel swatchColorPanel = new JPanel();
        swatchColorPanel.setLayout(new BorderLayout());
        swatchColorPanel.setOpaque(false);
        
        swatchColorButton = new AlcSwatchColorButton();
        
        //////////////////////////////////////////////////////////////
        // Switch to Tools Bar Button
        //////////////////////////////////////////////////////////////

        AlcButton deswatchButton = new AlcButton(toolBarFlipAction);
        deswatchButton.setup("", "switch back to toolbar", AlcUtil.getUrlPath("switch-swatch.png"));
        deswatchButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 6, 5));
        swatchTools.add(deswatchButton);
 
        //////////////////////////////////////////////////////////////
        // Swatch Menu
        //////////////////////////////////////////////////////////////       
        
        swatchMenuButton = new AlcPopupButton("Menu", getS("swatchMenuDescription"), AlcUtil.getUrlPath("swatchMenu.png"));
        
        //------------------------------------------------------------
        // Swatch Menu -> Set Background Color
        
        AlcMenuItem setBackgroundColor; 
         
        AbstractAction setBackgroundAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
                ActionListener colorAction = new ActionListener() {
                    public void actionPerformed(ActionEvent event) {             
                        Alchemy.canvas.setBackgroundColor(Alchemy.colorSelector.getColor());
                    }
                };
                
                Alchemy.colorSelector.show(colorAction, null, Alchemy.canvas.getBackgroundColor());
            }
        };
        setBackgroundColor = new AlcMenuItem(setBackgroundAction);
        setBackgroundColor.setup("Set Background Color");
        swatchMenuButton.addItem(setBackgroundColor);
        
        //------------------------------------------------------------
        // Swatch Menu -> Save Swatch
        AlcMenuItem saveSwatch; 
         
        AbstractAction saveSwatchAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
                Alchemy.colourIO.exportSwatch();                    
            }
        };
        saveSwatch = new AlcMenuItem(saveSwatchAction);
        saveSwatch.setup("Export Swatch...");
        swatchMenuButton.addItem(saveSwatch);
        
        //------------------------------------------------------------
        // Swatch Menu -> Load Swatch
        AlcMenuItem loadSwatch; 
         
        AbstractAction loadSwatchAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
                Alchemy.colourIO.importFileSwatch();
                swatchColorButton.refresh();
                setSwatchLRButtons();
                setRemoveColorButton();
                    
            }
        };
        loadSwatch = new AlcMenuItem(loadSwatchAction);
        loadSwatch.setup("Import Swatch...");
        swatchMenuButton.addItem(loadSwatch);
        //------------------------------------------------------------
        // Swatch Menu -> ColourLovers.com Swatch        
        
        AlcMenuItem setColourLovers; 
         
        AbstractAction setColourLoversAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
                Alchemy.colourIO.setCLSwatch(random.nextInt(3000)+1);
                swatchColorButton.refresh();
                setSwatchLRButtons();
                setRemoveColorButton();
                    
            }
        };
        setColourLovers = new AlcMenuItem(setColourLoversAction);
        setColourLovers.setup("get Colour Lovers");
        swatchMenuButton.addItem(setColourLovers);
        
        swatchTools.add(swatchMenuButton);
        swatchTools.add(new AlcSeparator());
        
        // ---------------
        // END SWATCH MENU
        // ---------------
        
        //////////////////////////////////////////////////////////////
        // TRANSPARENCY LOCK
        //////////////////////////////////////////////////////////////
        
        final AlcToggleButton lockButton = new AlcToggleButton();
        AbstractAction styleAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.canvas.toggleAlphaLocked();
                swatchColorButton.refresh();
            }
        };

        lockButton.setAction(styleAction);
        lockButton.setup("", getS("alphaLockDescription"), AlcUtil.getUrlPath("lock.png"));

        transparencyBox.add(lockButton);
        
        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        
        transparencySlider = new AlcSlider(getS("transparencyTitle"), getS("transparencyDescription"), 0, 255, 254);
        transparencySlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        //JSlider source = (JSlider) e.getSource();
                        if (!transparencySlider.getValueIsAdjusting()) {
                            Alchemy.canvas.setAlpha(transparencySlider.getValue());
                            refreshColorButton();
                            swatchColorButton.refresh();
                        }
                    }
                });

        transparencyBox.add(transparencySlider);
        swatchTools.add(transparencyBox);
                    
        String colorTitle = getS("colorTitle");
        colorButton = new AlcSingleColorButton(colorTitle, getS("colorDescription"), 64);
        lClickPicker = new AlcColorPicker(colorButton,0);
        rClickPicker = new AlcColorPicker(colorButton,1);
        colorButton.addlClickItem(lClickPicker);
        colorButton.addrClickItem(rClickPicker);
        //colorButton.addBgItem(bgPicker);
        swatchTools.add(colorButton);
        
        //////////////////////////////////////////////////////
        // SWATCH NUMBERPAD ACTIONS
        ////////////////////////////////////////////////////////////// 
        
        AbstractAction transparencyUpAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(Alchemy.canvas.getAlpha()<255){
                    if(Alchemy.canvas.getAlpha()>245){
                        Alchemy.canvas.setAlpha(255);
                    }else{
                        Alchemy.canvas.setAlpha(Alchemy.canvas.getAlpha()+10);
                    }
                    swatchColorButton.refresh();
                    refreshColorButton();
                }
            }
        };
        AbstractAction transparencyDownAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(Alchemy.canvas.getAlpha()>0){
                    if(Alchemy.canvas.getAlpha()<10){
                        Alchemy.canvas.setAlpha(0);
                    }else{
                        Alchemy.canvas.setAlpha(Alchemy.canvas.getAlpha()-10);
                    }
                    swatchColorButton.refresh();
                    refreshColorButton();
                }
            }
        };
        AbstractAction nextColorAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(Alchemy.canvas.activeSwatchIndex<Alchemy.canvas.swatch.size()-1){
                    Alchemy.canvas.activeSwatchIndex++;                
                }else{
                    Alchemy.canvas.activeSwatchIndex=0;
                }
                updateSwatchColorChange();
            }
        };
        AbstractAction prevColorAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if(Alchemy.canvas.activeSwatchIndex==0){
                    Alchemy.canvas.activeSwatchIndex=Alchemy.canvas.swatch.size()-1;                
                }else{
                    Alchemy.canvas.activeSwatchIndex--;
                }
                updateSwatchColorChange();
            }
        };
        
        
        //////////////////////////////////////////////////////////////
        // SWATCH NUMBERPAD SHORTCUTS
        ////////////////////////////////////////////////////////////// 
        
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_MULTIPLY, "transparency up", transparencyUpAction);
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_DIVIDE, "transparency down", transparencyDownAction);
        final int VK_ADD = 0x6B;
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_ADD,  "next color", nextColorAction);
        final int VK_SUBTRACT = 0x6D;
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_SUBTRACT, "prev color", prevColorAction);
        
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD0, "num0", buildSwatchQuickKey(0));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD1, "num1", buildSwatchQuickKey(1));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD2, "num2", buildSwatchQuickKey(2));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD3, "num3", buildSwatchQuickKey(3));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD4, "num4", buildSwatchQuickKey(4));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD5, "num5", buildSwatchQuickKey(5));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD6, "num6", buildSwatchQuickKey(6));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD7, "num7", buildSwatchQuickKey(7));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD8, "num8", buildSwatchQuickKey(8));
        Alchemy.shortcuts.setShortcut(null, KeyEvent.VK_NUMPAD9, "num9", buildSwatchQuickKey(9));
        
        //////////////////////////////////////////////////////////////
        // ADD COLOR TO SWATCH BUTTON
        //////////////////////////////////////////////////////////////
                
        AbstractAction addToSwatchAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
                Alchemy.canvas.addCurrentColorToSwatch();
                
                if(!removeFromSwatchButton.isEnabled()){
                    removeFromSwatchButton.setEnabled(true);
                }
                setSwatchLRButtons(); 
                swatchColorButton.refresh();
            }
        };
        AlcButton addToSwatchButton = new AlcButton(addToSwatchAction);
        addToSwatchButton.setup("", getS("addToSwatchDescription"), AlcUtil.getUrlPath("add.png"));

        addRemoveBox.add(addToSwatchButton);
        //////////////////////////////////////////////////////////////
        // REMOVE COLOR FROM SWATCH BUTTON
        //////////////////////////////////////////////////////////////
                
        AbstractAction removeFromSwatchAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
                
               Alchemy.canvas.swatch.remove(Alchemy.canvas.activeSwatchIndex);
               if(Alchemy.canvas.activeSwatchIndex>=Alchemy.canvas.swatch.size()){
                  Alchemy.canvas.activeSwatchIndex=Alchemy.canvas.swatch.size()-1;
               }
               
               setSwatchLRButtons(); 
               
               if(Alchemy.canvas.swatch.isEmpty()){
                  removeFromSwatchButton.setEnabled(false);
                  swatchColorButton.clear();
               }else{
                   swatchColorButton.refresh();
               }
               
            }
        };
        
        removeFromSwatchButton = new AlcButton(removeFromSwatchAction);
        removeFromSwatchButton.setup("", getS("removeFromSwatchDescription"), AlcUtil.getUrlPath("remove.png"));
        
        addRemoveBox.add(removeFromSwatchButton);
        addRemoveBox.add(Box.createVerticalStrut(5));
        swatchTools.add(addRemoveBox);
        
        //swatchTools.add(Box.createHorizontalStrut(10));
                //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        swatchTools.add(new AlcSeparator());
        //swatchTools.add(Box.createHorizontalStrut(10));
        //////////////////////////////////////////////////////////////
        // Move Active Color Left Button
        //////////////////////////////////////////////////////////////
                
        AbstractAction moveColorLeftAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
               if(Alchemy.canvas.activeSwatchIndex>0){ 
                   Alchemy.canvas.swatch.add(Alchemy.canvas.activeSwatchIndex-1,
                                             Alchemy.canvas.swatch.get(Alchemy.canvas.activeSwatchIndex));
                   Alchemy.canvas.swatch.remove(Alchemy.canvas.activeSwatchIndex+1);

                   Alchemy.canvas.activeSwatchIndex--;

                   setSwatchLRButtons();

                   swatchColorButton.refresh();               
               }
            }
        };
        
        moveColorLeftButton = new AlcButton(moveColorLeftAction);
        moveColorLeftButton.setup("", getS("removeFromSwatchDescription"), 
                                  AlcUtil.getUrlPath("swatchLeft.png"));
        
        swatchColorPanel.add(moveColorLeftButton, BorderLayout.WEST);
        
        
        
        //////////////////////////////////////////////////////////////
        // Move Active Color Right Button
        //////////////////////////////////////////////////////////////
                
        AbstractAction moveColorRightAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
               if(Alchemy.canvas.activeSwatchIndex<Alchemy.canvas.swatch.size()-1){ 
                   Alchemy.canvas.swatch.add(Alchemy.canvas.activeSwatchIndex+2,
                                             Alchemy.canvas.swatch.get(Alchemy.canvas.activeSwatchIndex));
                   Alchemy.canvas.swatch.remove(Alchemy.canvas.activeSwatchIndex);

                   Alchemy.canvas.activeSwatchIndex++;

                   setSwatchLRButtons();

                   swatchColorButton.refresh();               
               }
            }
        };
        
        moveColorRightButton = new AlcButton(moveColorRightAction);
        moveColorRightButton.setup("", getS("removeFromSwatchDescription"), 
                                  AlcUtil.getUrlPath("swatchRight.png"));
        
        swatchColorPanel.add(moveColorRightButton, BorderLayout.EAST);
        

        // Build Swatch if there are saved colors
        if (Alchemy.canvas.swatch.size()>0){   
            swatchColorButton.refresh();
        }
        
        setRemoveColorButton();
        setSwatchLRButtons();
               
        toolBar.add(swatchTools, BorderLayout.WEST);  
        swatchColorPanel.add(swatchColorButton, BorderLayout.CENTER);
        toolBar.add(swatchColorPanel, BorderLayout.CENTER);
        
        toolBarGroup.add(toolBar, BorderLayout.CENTER);
        return toolBarGroup;     
    }
    
    /** Load the tool bar */
    private AlcToolBarMain loadToolBar() {
        // Create the main toolbar
        AlcToolBarMain toolBarGroup = new AlcToolBarMain();

        JPanel toolBar = new JPanel();
        toolBar.setOpaque(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        
        //////////////////////////////////////////////////////////////
        // Switch to Tools Bar Button
        //////////////////////////////////////////////////////////////

        AlcButton reswatchButton = new AlcButton(toolBarFlipAction);
        reswatchButton.setup("", "switch back to toolbar", AlcUtil.getUrlPath("switch-tools.png"));
        reswatchButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        toolBar.add(reswatchButton);

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
        // UNDER OVER BUTTON
        //////////////////////////////////////////////////////////////
        String underOverTitle = getS("overTitle");
        final AlcToggleButton underOverButton = new AlcToggleButton();

        AbstractAction underOverAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {

                if (!e.getSource().getClass().getName().endsWith("AlcToggleButton")) {
                    // Only toogle the button manually if it is triggered by a key
                    underOverButton.setSelected(!underOverButton.isSelected());
                }

                if (underOverButton.isSelected()) {
                    underOverButton.setText(getS("underTitle"));
                } else {
                    underOverButton.setText(getS("overTitle"));
                }


                Alchemy.canvas.setDrawUnder(!Alchemy.canvas.getDrawUnder());
            }
        };
        underOverButton.setAction(underOverAction);
        underOverButton.setup(underOverTitle, getS("underOverDescription"), AlcUtil.getUrlPath("underOver.png"));

        // Hack here to make the sizes the same
        Dimension underOverButtonSize = underOverButton.getPreferredSize();
        underOverButton.setText(getS("underTitle"));
        Dimension underOverButtonNewSize = underOverButton.getPreferredSize();
        if (underOverButtonSize.width > underOverButtonNewSize.width) {
            underOverButton.setPreferredSize(underOverButtonSize);
        } else {
            underOverButton.setPreferredSize(underOverButtonNewSize);
        }
        underOverButton.setText(underOverTitle);

        // Shortcut - d
        Alchemy.shortcuts.setShortcut(styleButton, KeyEvent.VK_D, "underOverTitle", underOverAction);

        toolBar.add(underOverButton);


        //////////////////////////////////////////////////////////////
        // LINE WIDTH SPINNER
        //////////////////////////////////////////////////////////////
        final int lineWidthSpinnerMin = 1;
        final int lineWidthSpinnerMax = 75;

        final AlcSpinner lineWidthSpinner = new AlcSpinner(
                getS("lineWeightTitle"),
                lineWidthSpinnerMin,
                lineWidthSpinnerMax,
                (int) Alchemy.canvas.getLineWidth(),
                1);

        lineWidthSpinner.setToolTip(getS("lineWeightDescription"));

        lineWidthSpinner.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        Alchemy.canvas.setLineWidth(lineWidthSpinner.getValue());
                    }
                });

        AbstractAction lineWidthDownAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                lineWidthSpinner.setPreviousValue();
                Alchemy.canvas.setLineWidth(lineWidthSpinner.getValue());
            }
        };

        AbstractAction lineWidthUpAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                lineWidthSpinner.setNextValue();
                Alchemy.canvas.setLineWidth(lineWidthSpinner.getValue());
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
        // COLOR  BUTTON
        //////////////////////////////////////////////////////////////
        //String colorTitle = getS("colorTitle");
        //colorButton = new AlcColorButton(colorTitle, getS("colorDescription"));
        //fgPicker = new AlcColorPicker(colorButton);
        //bgPicker = new AlcColorPicker(colorButton, true);
        //colorButton.addFgItem(fgPicker);
        //colorButton.addBgItem(bgPicker);
        //toolBar.add(colorButton);

        //AbstractAction fgbgAction = new AbstractAction() {

          //  public void actionPerformed(ActionEvent e) {
          //      colorButton.switchColors();
          //  }
        //};

        // Shortcut - X
        //Alchemy.shortcuts.setShortcut(colorButton, KeyEvent.VK_X, "fgbgTitle", fgbgAction);
/*
        //////////////////////////////////////////////////////////////
        // TRANSPARENCY SLIDER
        //////////////////////////////////////////////////////////////
        transparencySlider = new AlcSlider(getS("transparencyTitle"), getS("transparencyDescription"), 0, 255, 254);
        transparencySlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {

                        //JSlider source = (JSlider) e.getSource();
                        if (!transparencySlider.getValueIsAdjusting()) {
                            Alchemy.canvas.setAlpha(transparencySlider.getValue());
                            refreshColorButton();
                        }
                    }
                });

        toolBar.add(transparencySlider);

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        toolBar.add(new AlcSeparator());
*/
        //////////////////////////////////////////////////////////////
        // CREATE
        //////////////////////////////////////////////////////////////
        createButton = new AlcPopupButton(getS("createTitle"), getS("createDescription"), AlcUtil.getUrlPath("create.png"));
        // Button group for the radio buttons
        ButtonGroup group = new ButtonGroup();
        // Start the keyboard shortcuts from here
        int zero = KeyEvent.VK_0;
        int createCount = 0;

        // Populate the Popup Menu
        for (int i = 0; i < Alchemy.plugins.creates.length; i++) {

            // The current module
            AlcModule currentModule = Alchemy.plugins.creates[i];

            // Check if this module should be loaded
            if (loadModule(currentModule)) {

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
                createMenuItem.setup(currentModule);

                if (createCount == 0) {
                    createMenuItem.setSelected(true);
                }

                group.add(createMenuItem);
                createButton.addItem(createMenuItem);

                // The first 9 modules are mapped to keys 1 - 9
                if (createCount < 9) {
                    Alchemy.shortcuts.setShortcut(createMenuItem, zero + createCount + 1, currentModule.getName(), createMenuItemAction);

                // The 10th module is mapped to the 0 key
                } else if (createCount == 9) {
                    Alchemy.shortcuts.setShortcut(createMenuItem, zero, currentModule.getName(), createMenuItemAction);

                // The next 9 modules are mapped to keys 1 - 9 with the ALT key
                } else if (createCount > 9) {
                    Alchemy.shortcuts.setShortcut(createMenuItem, zero + (createCount - 9), currentModule.getName(), createMenuItemAction, KeyEvent.ALT_MASK);
                }
                createCount++;
            }
        }

        toolBar.add(createButton);


        //////////////////////////////////////////////////////////////
        // AFFECT
        //////////////////////////////////////////////////////////////
        if (Alchemy.plugins.getNumberOfAffectModules() > 0) {
            affectButton = new AlcPopupButton(getS("affectTitle"), getS("affectDescription"), AlcUtil.getUrlPath("affect.png"));

            int affectCount = 0;


            for (int i = 0; i < Alchemy.plugins.affects.length; i++) {
                // The current module
                AlcModule currentModule = Alchemy.plugins.affects[i];

                // To load or not
                boolean load = loadModule(currentModule);

                if (load) {
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
                    if (affectCount < 9) {
                        Alchemy.shortcuts.setShortcut(affectMenuItem, zero + affectCount + 1, currentModule.getName(), affectMenuItemAction, KEY_MODIFIER);
                    // The 10th module is mapped to the 0 key
                    } else if (affectCount == 9) {
                        Alchemy.shortcuts.setShortcut(affectMenuItem, zero, currentModule.getName(), affectMenuItemAction, KEY_MODIFIER);
                    // The next 9 modules are mapped to keys 1 - 9 with the SHIFT key
                    } else if (affectCount > 9) {
                        Alchemy.shortcuts.setShortcut(affectMenuItem, zero + (affectCount - 9), currentModule.getName(), affectMenuItemAction, KeyEvent.SHIFT_MASK);
                    }
                    affectCount++;
                }
            }
            toolBar.add(affectButton);
        }

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        toolBar.add(new AlcSeparator());
        
        //////////////////////////////////////////////////////////////
        // UNDO BUTTON
        //////////////////////////////////////////////////////////////
        
        String undoTitle = getS("undoTitle");
          
        AbstractAction undoAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {                
                Alchemy.canvas.removeShapeGroup();
                if(Alchemy.canvas.getUndoDepth()==1){          
                    disableUndo();
                }
            }
        };
        undoButton = new AlcButton(undoAction);
        undoButton.setup(undoTitle, getS("undoDescription"), AlcUtil.getUrlPath("undo.png"));
        // Shortcuts - Modifier Delete/Backspace
        Alchemy.shortcuts.setShortcut(undoButton, KeyEvent.VK_Z, "undoTitle", undoAction, KEY_MODIFIER);
        Alchemy.canvas.getActionMap().put(undoTitle, undoAction);
        
        if(Alchemy.canvas.getUndoDepth()>0){
            toolBar.add(undoButton);
        }
        undoButton.setEnabled(false);
        
        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        //toolBar.add(new AlcSeparator());

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
        Alchemy.shortcuts.setShortcut(clearButton, KeyEvent.VK_BACK_SPACE, "clearTitle", clearAction, KEY_MODIFIER);
        Alchemy.canvas.getActionMap().put(clearTitle, clearAction);
        toolBar.add(clearButton);

        //////////////////////////////////////////////////////////////
        // SEPARATOR
        //////////////////////////////////////////////////////////////
        // toolBar.add(new AlcSeparator());


        //////////////////////////////////////////////////////////////
        // DETACH BUTTON
        //////////////////////////////////////////////////////////////

        JPanel topAlign = new JPanel();
        topAlign.setOpaque(false);
        topAlign.setLayout(new BoxLayout(topAlign, BoxLayout.PAGE_AXIS));

        detachButton = new JButton(AlcUtil.getImageIcon("palette-detach.png"));
        detachButton.setRolloverIcon(AlcUtil.getImageIcon("palette-detach-over.png"));
        detachButton.setToolTipText(
                "Detach the toolbar to a seperate palette");

        // Compensate for the windows border
        if (Alchemy.OS == OS_MAC) {
            detachButton.setMargin(new Insets(2, 0, 0, 2));
        } else {
            detachButton.setMargin(new Insets(2, 0, 0, 7));
        }

        detachButton.setBorderPainted(false);
        detachButton.setContentAreaFilled(false);
        detachButton.setFocusPainted(false);

        detachButton.addActionListener(new ActionListener() {

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
    void resizeToolBar( Dimension windowSize) {
        this.setBounds(0, 0, windowSize.width, totalHeight);
        this.windowSize = windowSize;
        this.revalidate();
        this.repaint();
        checkSubSections();
        
        if(!Alchemy.canvas.swatch.isEmpty()){         
            swatchColorButton.refresh();
        }

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
            insideToolBar = true;

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
            toolBarKeyedOn = false;
        }

    }

    /** Set the visibility of the UI Toolbar */
    @Override
    void setToolBarVisible( boolean visible) {
        if (visible != toolBarVisible) {
            this.setVisible(visible);
            toolBarVisible = visible;
            Alchemy.canvas.setEvents(!visible);
            if (!visible) {
                // Be sure to set the cursor back to the cross hair
                Alchemy.canvas.restoreCursor();
                //Alchemy.canvas.setCursor(CURSOR_CROSS);
                this.setCursor(CURSOR_ARROW);
                colorButton.hidePopup();
                createButton.hidePopup();
                if (affectButton != null) {
                    affectButton.hidePopup();
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

        if (Alchemy.OS != OS_MAC) {
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

    /** 
     * Add a Create Module sub-toolbar
     * @param subSection     The subtoolbar section to be added
     */
    @Override
    public void addSubToolBarSection(AlcToolBarSubSection subSection) {

        //subSection.revalidate();
        subSection.setContentVisible(false);

        if (subSection.getModuleType() == MODULE_CREATE) {
            createSubToolBarSection = subSection;

        // AFFECT
        } else {
            affectSubToolBarSections[subSection.getIndex()] = subSection;
        }

        currentSubToolBarSections++;

        toggleSubSection(subSection);

        // Refresh the sub toolbar with the new contents
        refreshSubToolBar();
    }

    /** Remove a subtoolbar section at the specified index */
    @Override
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

            subToolBar.add(createSubToolBarSection.panel);
        }
        // Add the affect sections
        for (int i = 0; i < affectSubToolBarSections.length; i++) {

            if (affectSubToolBarSections[i] != null) {

                // If there is odd number of components then add a separator
                if ((subToolBar.getComponentCount() % 2) != 0) {
                    subToolBar.add(new AlcSubSeparator());
                }
                // Then add the section
                subToolBar.add(affectSubToolBarSections[i].panel);
            }
        }

        if (currentSubToolBarSections > 0) {
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

    /** Check if the sub sections are overflowing and trim them as required*/
    private void checkSubSections() {
        if (isSubSectionOverflow()) {
            trimSubSection();
        }
    }

    /** Test if the sub sections are overflowing */
    private boolean isSubSectionOverflow() {
        int layoutWidth = subToolBar.getContentWidth();
        //System.out.println("SubToolbar layout width:" + layoutWidth + "/" + windowSize.width);
        if (layoutWidth > windowSize.width) {
            //System.out.println("Bigger");
            return true;
        } else {
            //System.out.println("Smaller");
            return false;
        }
    }

    /** Toggle the visibility of a subsection */
    @Override
    void toggleSubSection( AlcToolBarSubSection subSection) {

        // Hide the section
        if (subSection.isContentVisible()) {
            subSection.setContentVisible(false);
        //System.out.println("Hide - Content Visible");

        // Show the section?
        } else {

            //System.out.println(subToolBar.getContentWidth() + subSection.getContentWidth() + " vs " +  windowSize.width);

            // YES IT WILL FIT so make it visible
            if (subToolBar.getContentWidth() + subSection.getContentWidth() < windowSize.width) {
                subSection.setContentVisible(true);

            //System.out.println("Show - No Overflow");

            // NO IT WON"T FIT so hide some other sections
            } else {
                if (currentSubToolBarSections > 1) {
                    // Loop backwards and try and collapse the affect modules first
                    for (int i = affectSubToolBarSections.length - 1; i >= 0; i--) {
                        // If the section exists and is visible, hide it
                        if (affectSubToolBarSections[i] != null) {
                            if (affectSubToolBarSections[i].isContentVisible()) {
                                affectSubToolBarSections[i].setContentVisible(false);
                                //System.out.println("Hide " + Alchemy.plugins.affects[i].getName());
                                // Check if everything will fit in
                                if (subToolBar.getContentWidth() + subSection.getContentWidth() < windowSize.width) {
                                    //System.out.println("Show " + Alchemy.plugins.affects[subSection.getIndex()].getName() + " - Others hidden so its ok");
                                    subSection.setContentVisible(true);
                                    return;
                                }
                            }
                        }
                    }
                    if (createSubToolBarSection.isContentVisible()) {
                        createSubToolBarSection.setContentVisible(false);
                        if (subToolBar.getContentWidth() + subSection.getContentWidth() < windowSize.width) {
                            subSection.setContentVisible(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    /** Loop over the sub sections and hide their content until it all fits in */
    private void trimSubSection() {
        if (currentSubToolBarSections > 1) {
            // Loop backwards and try and collapse the affect modules first
            for (int i = affectSubToolBarSections.length - 1; i >= 0; i--) {
                // If the section exists and is visible, hide it
                if (affectSubToolBarSections[i] != null) {
                    if (affectSubToolBarSections[i].isContentVisible()) {
                        affectSubToolBarSections[i].setContentVisible(false);
                        if (!isSubSectionOverflow()) {
                            return;
                        }
                    }
                }
            }
            if (createSubToolBarSection.isContentVisible()) {
                createSubToolBarSection.setContentVisible(false);
            }
        }
    }

//////////////////////////////////////////////////////////////
// POPUP MENUS
//////////////////////////////////////////////////////////////
    /** Check if any of the popup menus are visible */
    boolean isPopupMenusVisible() {

        if (colorButton.isPopupVisible()) {
            return true;
        }

        if (createButton.isPopupVisible()) {
           return true;
        }
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
                            if (!colorButton.isInside() && !createButton.isInside() && !affectButton.isInside()) {
                                //System.out.println("Timer setting visibility");
                                setToolBarVisible(false);
                                insideToolBar = false;
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
        if (Alchemy.OS != OS_MAC) {
            Alchemy.window.setJMenuBar(null);
            this.add("North", Alchemy.menuBar);
        }

        if (currentSubToolBarSections < 1) {
            subToolBar.setVisible(false);
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
    /** Refreshes the colors of the Foreground/Background button */
    @Override
    void refreshColorButton() {
        colorButton.refresh();
    }

    @Override
    void refreshTransparencySlider() {
        transparencySlider.setValue(Alchemy.canvas.getAlpha());
    }
    @Override
    void setSwatchLRButtons(){
        if(Alchemy.canvas.activeSwatchIndex<=0){
            moveColorLeftButton.setEnabled(false);
        }else{
            moveColorLeftButton.setEnabled(true);
        }
        if(Alchemy.canvas.activeSwatchIndex==Alchemy.canvas.swatch.size()-1){
            moveColorRightButton.setEnabled(false);
        }else{
            moveColorRightButton.setEnabled(true);
        }
    }
    void setRemoveColorButton(){
        if(Alchemy.canvas.swatch.isEmpty()){
            removeFromSwatchButton.setEnabled(false);
        }else{
            removeFromSwatchButton.setEnabled(true);

        }
    }
    @Override
    void disableUndo() {
        if (undoButton.isEnabled()){
        undoButton.setEnabled(false);
        }
    }
    @Override
    void enableUndo() { 
        if (!undoButton.isEnabled()){
        undoButton.setEnabled(true);
        }
    }
    
    AbstractAction buildSwatchQuickKey(final int i){
        AbstractAction sKA = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {               
                if(Alchemy.canvas.swatch.size()>i){
                    Alchemy.canvas.activeSwatchIndex=i;
                    updateSwatchColorChange();
                }
            }
        };
        return sKA;
    }
    private void updateSwatchColorChange(){
        Alchemy.canvas.setColor(Alchemy.canvas.swatch.get(Alchemy.canvas.activeSwatchIndex));
        if(!Alchemy.canvas.isAlphaLocked()){
           Alchemy.canvas.setAlpha(Alchemy.canvas.swatch.get(Alchemy.canvas.activeSwatchIndex).getAlpha());
        }
        setSwatchLRButtons(); 
        swatchColorButton.refresh();
        colorButton.refresh();
    }
    public void refreshRClickPicker(){
        rClickPicker.refreshRClick();
    }
}