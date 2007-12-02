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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;

public class AlcToolBar extends JPanel implements ActionListener { // Extend JPanel rather than JComponent so the background can be set
    
    /** Reference to the root **/
    private AlcMain root;
    /** Visibility of the ToolBar */
    private boolean toolBarVisible = true;
    /** Height of the ToolBar */
    private int toolBarHeight = 60;
    /** Total height of all tool bars */
    private int totalHeight = toolBarHeight;
    
    /** Keep track of the windowSize */
    public Dimension windowSize;
    
    /** ToolBar Background Colour */
    public static Color toolBarBgColour = new Color(225, 225, 225);
    public static Color toolBarBgStartColour = new Color(235, 235, 235);
    public static Color toolBarBgEndColour = new Color(215, 215, 215);
    public static Color toolBarLineColour = new Color(140, 140, 140);
    public static Color toolBarHighlightColour = new Color(231, 231, 231);
    
    
    /** ToolBar Text Size */
    private final int toolBarTextSize = 10;
    /** ToolBar Popup Menu Y Location */
    public final int uiPopupMenuY = toolBarHeight - 10;
    
    /** Popup menu for the create button in the toolbar */
    private JPopupMenu createPopup;
    
    /** The main tool bar inside the tool bar */
    private AlcMainToolBar mainToolBar;
    /** The create tool bar inside the tool bar */
    private AlcSubToolBar createToolBar;
    /** The afect tool bars inside the tool bar */
    private ArrayList<AlcSubToolBar> affectToolBars;
    
    /**
     * Creates a new instance of AlcToolBar
     */
    public AlcToolBar(AlcMain root) {
        
        // General Toolbar settings
        this.root = root;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Turn off the visibility untill the mouse enters the top of the screen
        setToolBarVisible(false);
        
        // Create the main toolbar
        mainToolBar = new AlcMainToolBar(root);
        
        // Buttons in the main toolbar
        // Align LEFT
        JPanel toolBarLeft = new JPanel();
        toolBarLeft.setOpaque(false);   // Turn off the background
        AlcMainButton markButton = new AlcMainButton(this, "Marks", "Change the settings for making Marks", getUrlPath("../data/icon.png"));
        toolBarLeft.add(markButton);
        
        // TODO - Add Marks Button set
        //JToggleButton tbtn = new JToggleButton("Yes");
        //toolBarLeft.add(tbtn);
        mainToolBar.add(toolBarLeft, BorderLayout.LINE_START);
        
        
        // Align Right
        JPanel toolBarRight = new JPanel();
        toolBarRight.setOpaque(false);
        // Create
        AlcPopupButton createButton = new AlcPopupButton(this, "Create", "Create Shapes", getUrlPath("../data/create.png"), root.creates);
        toolBarRight.add(createButton);
        // Affect
        AlcPopupButton affectButton = new AlcPopupButton(this, "Affect", "Affect Shapes", getUrlPath("../data/create.png"), root.affects);
        toolBarRight.add(affectButton);
        
        mainToolBar.add(toolBarRight, BorderLayout.CENTER);
        this.add(mainToolBar);
        
        /*
        // PopupMenus on the buttons
        if(root.creates != null){
            createPopup = new AlcPopupMenu(this);
         
            // Populate the Popup Menu
            for (int i = 0; i < root.creates.size(); i++) {
                // The current module
                AlcModule currentModule = root.creates.get(i);
         
                // Set the text and icon
                AlcMenuItem menuItem = new AlcMenuItem(this, currentModule.getName(), currentModule.getIconUrl());
                menuItem.setToolTipText(currentModule.getDescription());
         
                // Set the action command and listener
                menuItem.setActionCommand(CREATE_COMMAND);
                menuItem.addActionListener(this);
                createPopup.add(menuItem);
         
            }
            //createPopup.add(new JCheckBoxMenuItem("Hello", true));
            createPopup.add(new AlcMenuItem(this, "Something Else", getUrlPath("../data/icon.png")));
            //createPopup.addSeparator();
        }
         
        // Add a mouse listner to detect when the button is pressed and display the popup menu
        createButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                createPopup.show(e.getComponent(), 0, uiPopupMenuY);
            }
        });
         
         */
        
        
        
        
        
        
        //JToggleButton tbtn = new JToggleButton("Yes");
        //this.add(tbtn);
        
    }
    
    public void resizeToolBar(){
        Dimension windowSize = new Dimension(this.windowSize.width, getTotalHeight());
        resizeToolBar(windowSize);
    }
    
    public void resizeToolBar(Dimension windowSize){
        this.setBounds(0, 0, windowSize.width, getTotalHeight());
        this.windowSize = windowSize;
    }
    
    
    /** Set the visibility of the UI Toolbar */
    public void setToolBarVisible(boolean b){
        this.setVisible(b);
        // Turn off the popup(s) when we leave the toolbar area
        if(!b){
            if(createPopup != null) createPopup.setVisible(false);
        }
        toolBarVisible = b;
    }
    
    /** Add a Create Module sub-toolbar */
    public void addCreateSubToolBar(AlcSubToolBar subToolBar){
        
        createToolBar = subToolBar;
        createToolBar.setLocation(0, 100);
        this.add(createToolBar);
        
        // Recalculate the total height of the tool bar
        calculateTotalHeight();
        // Then resize it
        resizeToolBar();
        // And refresh it
        this.revalidate();
        
    }
    
    
    // GETTERS
    /** Return the visibility of the UI Toolbar */
    public boolean getToolBarVisible(){
        return toolBarVisible;
    }
    
    /** Return the height of the UI Toolbar */
    public int getToolBarHeight(){
        return toolBarHeight;
    }
    
    /** Return the height of the UI Toolbar */
    public int getToolBarTextSize(){
        return toolBarTextSize;
    }
    
    
    
    // IMAGE LOADING FUNCTIONS
    /** Returns a URL from a String, or null if the path was invalid. */
    public URL getUrlPath(String path){
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return imgUrl;
        } else {
            System.err.println("Couldn't find file: " + imgUrl.toString());
            return null;
        }
    }
    
    /** Function to append a string to the end of a given URL */
    public static URL appendStringToUrl(URL url, String append){
        String urlString = url.toString();
        URL newUrl = null;
        // Look for a file extension
        int dot = url.toString().lastIndexOf(".");
        if(dot == -1){
            try {
                // If no file extension return as is
                newUrl = new URL(urlString + append);
            } catch (MalformedURLException ex) {
                System.err.println(ex);
            }
        } else{
            try {
                // Append the string before the file extension
                newUrl = new URL(urlString.substring(0, dot) + append + urlString.substring(dot));
            } catch (MalformedURLException ex) {
                System.err.println(ex);
            }
        }
        return newUrl;
    }
    
    
    /** Returns an ImageIcon from a String, or null if the path was invalid. */
    public ImageIcon createImageIcon(String path){
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return createImageIcon(imgUrl);
        } else {
            System.err.println("Couldn't find file: " + imgUrl.toString());
            return null;
        }
    }
    
    /** Returns an ImageIcon from a URL, or null if the path was invalid. */
    public ImageIcon createImageIcon(URL imgUrl) {
        // TODO - somehow test that this is a valid file
        //URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            //System.out.println(imgUrl.toString());
            return new ImageIcon(imgUrl);
        } else {
            System.err.println("Couldn't find file: " + imgUrl.toString());
            return null;
        }
    }
    
    
    /** Calculate the total height of the toolbar and its subtoolbars */
    public void calculateTotalHeight(){
        // Start with the main toolbar height
        int totalHeight = mainToolBar.getHeight();
        // Add the create toolbar height
        if(createToolBar != null)
            totalHeight +=  createToolBar.getHeight();
        // Add the height of each affect toolbar
        if(affectToolBars != null) {
            for (int i = 0; i < affectToolBars.size(); i++) {
                totalHeight += affectToolBars.get(i).getHeight();
            }
        }
        
        this.totalHeight = totalHeight;
    }
    
    /** Return the total height of the toolbar and its subtoolbars */
    public int getTotalHeight(){
        return totalHeight;
    }
    
    
    public void loadCreate(int index){
        root.setCurrentCreate(index);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        String rawCommand = e.getActionCommand();
        
        // Get the type of command
        String commandType = rawCommand.substring(0, rawCommand.lastIndexOf("-"));

        // Get the index
        int index = Integer.parseInt( rawCommand.substring(rawCommand.lastIndexOf("-")+1) );
        System.out.println("INDEX :" + index);
        
        if(commandType.equals("mark")){
            
        } else if(commandType.equals("create")){
            
            //AlcMenuItem source = (AlcMenuItem)(e.getSource());
            //int index = createPopup.getComponentIndex(source);
            //System.out.println(index);
            loadCreate(index);
            
        } else if(commandType.equals("affect")){
            // Do something
        }
        
        
    }
    
}
