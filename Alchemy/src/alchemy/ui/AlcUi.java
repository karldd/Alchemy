/**
 * AlcUi.java
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

public class AlcUi extends JPanel implements ActionListener { // Extend JPanel rather than JComponent so the background can be set
    
    /** Reference to the root **/
    private AlcMain root;
    /** Visibility of the Ui */
    private boolean uiVisible = true;
    /** Height of the Ui */
    private int uiHeight = 65;
    
    /** Ui Background Colour */
    private static Color uiBgColour = new Color(225, 225, 225);
    private static Color uiBgStartColour = new Color(235, 235, 235);
    private static Color uiBgEndColour = new Color(215, 215, 215);
    private static Color uiBgLineColour = new Color(140, 140, 140);
    
    
    /** Ui Text Size */
    private static final int uiTextSize = 10;
    /** Ui Popup Menu Y Location */
    private static final int uiPopupMenuY = 55;
    
    /** Action command for Mark menu popup */
    private static String MARK_COMMAND = "mark";
    /** Action command for Create menu popup */
    private static String CREATE_COMMAND = "create";
    /** Action command for Affect menu popup */
    private static String AFFECT_COMMAND = "affect";
    
    
    
    
    
    /** Combo Box drop down menu with the list of create functions */
    JComboBox createComboBox;
    /** Combo Box drop down menu with the list of affect functions */
    JComboBox affectComboBox;
    
    private JPopupMenu createPopup;
    
    /** Creates a new instance of AlcUi */
    public AlcUi(AlcMain root) {
        
        // General Toolbar settings
        this.root = root;
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBackground(uiBgColour);
        setUiVisible(false);
        this.setLocation(0, 0);
        this.setBounds(0, 0, root.getWindowSize().width, uiHeight);
        
        // Buttons
        AlcButton markButton = new AlcButton(this, "Marks", getUrlPath("../data/icon.png"));
        this.add(markButton);
        
        AlcButton createButton = new AlcButton(this, "Create", getUrlPath("../data/icon.png"));
        this.add(createButton);
        
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
            createPopup.setBorderPainted(false);
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
    
    // Override the paint component to draw the gradient bg
    @Override protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        int panelHeight = getHeight();
        int panelWidth = getWidth();
        GradientPaint gradientPaint = new GradientPaint( 0 , 0 , uiBgStartColour , 0 , panelHeight , uiBgEndColour, true );
        if( g instanceof Graphics2D ) {
            Graphics2D g2 = (Graphics2D)g;
            // Turn on text antialias - windows does not use it by default
            //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setPaint( gradientPaint );
            g2.fillRect( 0 , 0 , panelWidth , panelHeight );
            g2.setPaint( uiBgLineColour );
            g2.drawLine(0, panelHeight-1, panelWidth, panelHeight-1);
        }
    }
    
    public void resizeUi(Dimension windowSize){
        this.setBounds(0, 0, windowSize.width, uiHeight);
    }
    
    
    /** Set the visibility of the UI Toolbar */
    public void setUiVisible(boolean b){
        this.setVisible(b);
        // Turn off the popup(s) when we leave the toolbar area
        if(!b){
            if(createPopup != null) createPopup.setVisible(false);
        }
        uiVisible = b;
    }
    
    
    // GETTERS
    /** Return the visibility of the UI Toolbar */
    public boolean getUiVisible(){
        return uiVisible;
    }
    
    /** Return the height of the UI Toolbar */
    public int getUiHeight(){
        return uiHeight;
    }
    
    /** Return the colour of the UI toolbar background */
    public Color getUiBgColour(){
        return uiBgColour;
    }
    
    /** Return the colour of the UI toolbar background line */
    public Color getUiBgLineColour(){
        return uiBgLineColour;
    }
            
            
    
    /** Return the height of the UI Toolbar */
    public int getUiTextSize(){
        return uiTextSize;
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
    
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if(command.equals(MARK_COMMAND)){
            
            
        } else if(command.equals(CREATE_COMMAND)){
            
            AlcMenuItem source = (AlcMenuItem)(e.getSource());
            int index = createPopup.getComponentIndex(source);
            //System.out.println(index);
            root.setCurrentCreate(index);
            
        } else if(command.equals(AFFECT_COMMAND)){
            
        }
        
        
        
        /*
        //System.out.println(source.getText());
        // Get the type of command
        String commandType = command.substring(0, command.lastIndexOf("-"));
        System.out.println(commandType);
        // Get the index of the
        String index = command.substring(command.lastIndexOf("-")+1);
        int i = Integer.valueOf(index).intValue();
         */
        
        
    }
    
}
