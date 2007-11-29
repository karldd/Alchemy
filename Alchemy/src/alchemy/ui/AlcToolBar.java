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
import javax.swing.*;

public class AlcToolBar extends JPanel implements ActionListener { // Extend JPanel rather than JComponent so the background can be set
    
    /** Reference to the root **/
    private AlcMain root;
    /** Visibility of the ToolBar */
    private boolean toolBarVisible = true;
    /** Height of the ToolBar */
    private int toolBarHeight = 60;
    
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
    private final int uiPopupMenuY = toolBarHeight - 10;
    
    /** Action command for Mark menu popup */
    private static String MARK_COMMAND = "mark";
    /** Action command for Create menu popup */
    private static String CREATE_COMMAND = "create";
    /** Action command for Affect menu popup */
    private static String AFFECT_COMMAND = "affect";
    
    /** Popup menu for the create button in the toolbar */
    private JPopupMenu createPopup;
    
    /** The main tool bar nestled inside the tool bar */
    private AlcMainToolBar mainToolBar;
    
    /** The create tool bar nestled inside the tool bar */
    private AlcSubToolBar createToolBar;
    
    
    /**
     * Creates a new instance of AlcToolBar
     */
    public AlcToolBar(AlcMain root) {
        
        // General Toolbar settings
        this.root = root;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.setBackground(toolBarBgColour);
        setToolBarVisible(false);
        //this.setLocation(0, 0);
        //this.setBounds(0, 0, root.getWindowSize().width, toolBarHeight);
        
        mainToolBar = new AlcMainToolBar(root, this);
        
        //mainToolBar.setBackground(toolBarBgColour);
        
        
        // Buttons
        AlcButton markButton = new AlcButton(this, "Marks", getUrlPath("../data/icon.png"));
        mainToolBar.add(markButton);
        
        AlcButton createButton = new AlcButton(this, "Create", getUrlPath("../data/icon.png"));
        mainToolBar.add(createButton);
        
        this.add(mainToolBar);
        
        createToolBar = new AlcSubToolBar(root, this, root.creates.get(0));
        createToolBar.setLocation(0, 100);
        this.add(createToolBar);
        
        
        
        // PopupMenus on the buttons
        if(root.creates != null){
            createPopup = new AlcPopupMenu(this);
            //createPopup.addSeparator();
            
            // Populate the Popup Menu
            for (int i = 0; i < root.creates.size(); i++) {
                // The current module
                AlcModule currentModule = root.creates.get(i);
                // The icon for this module
                //ImageIcon createIcon = createImageIcon();
                // Set the text and icon
                AlcMenuItem menuItem = new AlcMenuItem(this, currentModule.getName(), currentModule.getIconUrl());
                // Set the action command and listener
                menuItem.setActionCommand(CREATE_COMMAND);
                menuItem.addActionListener(this);
                createPopup.add(menuItem);
                //createPopup.addSeparator();
            }
            
            //createPopup.add(new JCheckBoxMenuItem("Hello", true));
            createPopup.add(new AlcMenuItem(this, "Something Else", getUrlPath("../data/icon.png")));
            //createPopup.addSeparator();
            //createPopup.add(new AlcMenuItem(this, "Yes Ok then", createImageIcon("../data/icon-over.png")));
            //createPopup.setBorderPainted(false);
            //createPopup.addSeparator();
            
        }
        
        
        
        //System.out.println("Width: "+createPopup.getInsets());
        
        
        
        createButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                createPopup.show(e.getComponent(), 0, uiPopupMenuY);
            }
        });
        
        //JToggleButton tbtn = new JToggleButton("Yes");
        //this.add(tbtn);
        
    }
    
    
    
    
    
    public void resizeUi(Dimension windowSize){
        this.setBounds(0, 0, windowSize.width, mainToolBar.getHeight() + createToolBar.getHeight());
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
    
    
    // GETTERS
    /** Return the visibility of the UI Toolbar */
    public boolean getToolBarVisible(){
        return toolBarVisible;
    }
    
    // TODO - make a function that dynamically calculates the height of all the toolbars and sets a variable
    /** Return the height of the UI Toolbar */
    public int getToolBarHeight(){
        return toolBarHeight;
    }
    
    /** Return the height of the UI Toolbar */
    public int getToolBarTextSize(){
        return toolBarTextSize;
    }
    
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
    
    public void loadCreate(int index){
        root.setCurrentCreate(index);
        
    }
    
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if(command.equals(MARK_COMMAND)){
            
            
        } else if(command.equals(CREATE_COMMAND)){
            
            AlcMenuItem source = (AlcMenuItem)(e.getSource());
            int index = createPopup.getComponentIndex(source);
            //System.out.println(index);
            loadCreate(index);
            
        } else if(command.equals(AFFECT_COMMAND)){
            
        }
        
    }
    
}
