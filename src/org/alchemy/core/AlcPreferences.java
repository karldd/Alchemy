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
import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.*;

/**
 * Preference class used to store persistant data
 */
class AlcPreferences implements AlcConstants {

    /** Preferences package */
    static Preferences prefs;
    //////////////////////////////////////////////////////////////
    //  DEFINE CONSTANTS
    //////////////////////////////////////////////////////////////
    public final int FORMAT_PDF = 0;
    public final int FORMAT_SVG = 1;
    //////////////////////////////////////////////////////////////
    //  WINDOW LAYOUT 
    //////////////////////////////////////////////////////////////
    /** The preferences window */
    private JDialog prefsWindow;
    /** Background content panel for the window */
    private JPanel bgPanel;
    /** General content panel*/
    private JPanel generalPanel;
    /** Session content panel*/
    private JPanel sessionPanel;
    /** The general tab */
    private final int GENERAL = 1;
    /** The session tab */
    private final int SESSION = 2;
    /** The currently selected tab */
    private int currentTab = GENERAL;
    /** Ok Button */
    private JButton okButton;
    /** Size of the preferences window */
    private final Dimension prefsWindowSize = new Dimension(500, 500);
    /** Height of the tab panel */
    private final int tabPanelHeight = 65;
    //////////////////////////////////////////////////////////////
    // INTERFACE ELEMENTS
    //////////////////////////////////////////////////////////////
    private AlcToggleButton generalTabButton;
    private JComboBox interfaceBox;
    private JComboBox undoDepthBox;
    private JComboBox localeBox;
    private JCheckBox recordOnStartUp;
    private JTextField sessionDirectoryTextField;
    private JLabel sessionFileRenameOutput;
    private JTextField sessionFileRenamePre,  sessionFileRenameDate;
    //////////////////////////////////////////////////////////////
    //  MODULES
    //////////////////////////////////////////////////////////////    
    /** Scroll pane for the module listing */
    private JScrollPane scrollPane;
    /** If the modules have been customised */
    private boolean modulesSet;
    /** Panel containing the module check boxes */
    private JPanel modulesPanel;
    /** Prefix for the preference node name */
    final String modulePrefix = "Module - ";
    /** Change modules when the prefs window is closed */
    private boolean changeModules = false;
    //////////////////////////////////////////////////////////////
    // SESSION
    //////////////////////////////////////////////////////////////
    /** Recording on or off at startup */
    boolean sessionRecordingState;
    /** Recording warning on or off at startup */
    boolean sessionRecordingWarning;
    /** Directory to save session files too */
    String sessionPath;
    /** Time delay between recording a new page */
    int sessionRecordingInterval;
    /** Auto clean the canvas after saving */
    boolean sessionAutoClear;
    /** Link to current setting */
    boolean sessionLink;
    /** The start section of the session file name */
    String sessionFilePreName;
    /** Date format for the session pdf */
    String sessionFileDateFormat;
    /** The default start section of the session file name */
    private final String defaultSessionFilePreName = "Alchemy-";
    /** The default Date format for the session pdf */
    private final String defaultSessionFileDateFormat = "yyyy-MM-dd-HH-mm-ss";    
    //////////////////////////////////////////////////////////////
    // SHAPES 
    //////////////////////////////////////////////////////////////
    /** Directory to load shapes from */
    String shapesPath;
    //////////////////////////////////////////////////////////////
    // SWITCH
    //////////////////////////////////////////////////////////////
    /** Switch Vector Application */
    String switchVectorApp;
    /** What format when Switch to Vector Application */
    int switchVectorFormat;
    /** Switch Bitmap Application */
    String switchBitmapApp;
    //////////////////////////////////////////////////////////////
    // WINDOWS
    //////////////////////////////////////////////////////////////
    /** State of the palette- attached or not */
    boolean paletteAttached;
    /** Palette Location */
    Point paletteLocation;
    /** Canvas Window Location */
    Point canvasLocation;
    /** Canvas Window size */
    Dimension canvasSize;
    /** Transparent Fullscreen mode */
    boolean transparentFullscreen;
    /** Simplified toolbar for kids */
    boolean simpleToolBar;
    //////////////////////////////////////////////////////////////
    // DRAWING
    //////////////////////////////////////////////////////////////
    /** Canvas smoothing */
    boolean smoothing;
    /** Line smoothing */
    boolean lineSmoothing;
    /** Canvas background color */
    int bgColor;
    /** Color */
    int color;
    int undoDepth;
    String locale;
    //////////////////////////////////////////////////////////////
    // GENERAL
    //////////////////////////////////////////////////////////////
    /** Export directory loaded by default by the file chooser */
    String exportDirectory;

    AlcPreferences() {
        loadPreferences();
    }

    /** Load the preference nodes */
    private void loadPreferences() {

        prefs = Preferences.userNodeForPackage(getClass());
        modulesSet = prefs.getBoolean("Modules Set", false);

        sessionRecordingState = prefs.getBoolean("Recording State", false);
        sessionRecordingWarning = prefs.getBoolean("Recording Warning", true);
        sessionPath = prefs.get("Session Path", DIR_DESKTOP);
        sessionRecordingInterval = prefs.getInt("Recording Interval", 30000);
        sessionAutoClear = prefs.getBoolean("Auto Clear Canvas", false);
        sessionLink = prefs.getBoolean("Link to Current Session", true);
        sessionFilePreName = prefs.get("Session File Pre Name", defaultSessionFilePreName);
        sessionFileDateFormat = prefs.get("Session File Date Format", defaultSessionFileDateFormat);

        shapesPath = prefs.get("Shapes Path", new File("shapes").getAbsolutePath());

        switchVectorApp = prefs.get("Switch Vector Application", null);
        switchVectorFormat = prefs.getInt("Switch Vector Format", 0);
        switchBitmapApp = prefs.get("Switch Bitmap Application", null);

        paletteAttached = prefs.getBoolean("Palette Attached", false);
        paletteLocation = stringToPoint(prefs.get("Palette Location", null));
        canvasLocation = stringToPoint(prefs.get("Canvas Location", null));
        canvasSize = stringToDimension(prefs.get("Canvas Size", null));
        transparentFullscreen = prefs.getBoolean("Transparent Fullscreen", false);
        simpleToolBar = prefs.getBoolean("Simple ToolBar", false);

        smoothing = prefs.getBoolean("Smoothing", true);
        lineSmoothing = prefs.getBoolean("Line Smoothing", true);
        bgColor = prefs.getInt("Background Color", 0xFFFFFF);
        color = prefs.getInt("Color", 0x000000);
        
        exportDirectory = prefs.get("Export Directory", DIR_DESKTOP);
        undoDepth = prefs.getInt("Undo Depth", 0);
        locale = prefs.get("Locale", "system");

    }

    /** Save the changes on exit */
    void writeChanges() {

        prefs.putBoolean("Modules Set", modulesSet);

        prefs.putBoolean("Recording State", sessionRecordingState);
        prefs.putBoolean("Recording Warning", sessionRecordingWarning);
        prefs.put("Session Path", sessionPath);
        prefs.putInt("Recording Interval", sessionRecordingInterval);
        prefs.putBoolean("Auto Clear Canvas", sessionAutoClear);
        prefs.putBoolean("Link to Current Session", sessionLink);
        prefs.put("Session File Pre Name", sessionFilePreName);
        prefs.put("Session File Date Format", sessionFileDateFormat);

        prefs.put("Shapes Path", shapesPath);

        prefs.putBoolean("Palette Attached", paletteAttached);
        prefs.putBoolean("Smoothing", Alchemy.canvas.isSmoothing());
        prefs.putBoolean("Line Smoothing", AlcShape.isLineSmoothing());
        prefs.putBoolean("Simple ToolBar", simpleToolBar);
        prefs.putBoolean("Transparent Fullscreen", transparentFullscreen);

        prefs.putInt("Background Color", Alchemy.canvas.getBackgroundColor().getRGB());
        prefs.putInt("Color", Alchemy.canvas.getColor().getRGB());
        
        prefs.put("Export Directory", exportDirectory);
        
        prefs.putInt("Undo Depth", undoDepth);
        prefs.put("Locale", locale);
        
        if (switchVectorApp != null) {
            prefs.put("Switch Vector Application", switchVectorApp);
        }
        prefs.putInt("Switch Vector Format", switchVectorFormat);
        if (switchBitmapApp != null) {
            prefs.put("Switch Bitmap Application", switchBitmapApp);
        }
        if (paletteLocation != null) {
            prefs.put("Palette Location", pointToString(paletteLocation));
        }
        if (canvasLocation != null) {
            prefs.put("Canvas Location", pointToString(canvasLocation));
        }
        if (canvasSize != null) {
            prefs.put("Canvas Size", dimensionToString(canvasSize));
        }
    }

    /** Remove the preferences */
    private void removePreferences() {
        try {
            prefs.removeNode();

        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }

    /** Reset the preferences */
    private void resetPreferences() {
        try {
            prefs.removeNode();
            loadPreferences();

        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }

    /** Export the preferences as a *nicely formatted* XML file
     * 
     * @param file  The file to be created
     */
    private boolean exportPreferences(File file) {
        boolean result = false;
        // Export to an XML file
        try {
//            // Create the file if it does not exist
//            if (!preferencesFile.exists()) {
//                preferencesFile.createNewFile();
//            }
            // Write out the Preferences file as XML
            FileOutputStream outputStream = new FileOutputStream(file);
            prefs.exportSubtree(outputStream);

            // Format the XML so it is actually readable!
            if (file.exists()) {
                if (file.canRead()) {
                    final String dtd = "http://java.sun.com/dtd/preferences.dtd";
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    // factory.setValidating(false);
                    // Hack here to make sure we don't connect to the internet
                    builder.setEntityResolver(new EntityResolver() {

                        public InputSource resolveEntity(String publicId, String systemId) {
                            if (systemId.equals(dtd)) {
                                return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
                            } else {
                                return null;
                            }
                        }
                    });

                    InputSource in = new InputSource(new FileInputStream(file));
                    Document doc = builder.parse(in);

                    Transformer tf = TransformerFactory.newInstance().newTransformer();
                    tf.setOutputProperty(OutputKeys.INDENT, "yes");
                    tf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtd);
                    //tf.setOutputProperty("{http://xml. customer .org/xslt}indent-amount", "4");
                    tf.transform(new DOMSource(doc), new StreamResult(file));

                    System.out.println("Preferences saved to file.");

                    result = true;

                } else {
                    System.out.println("Error reading preferences file");
                }
            }

        } catch (Exception ex) {
            System.out.println("Error creating the preferences file");
            ex.printStackTrace();
        }
        return result;
    }

    /** Reset the module preferences to defaults */
    private void resetModules(AlcModule[] modules) {
        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            String moduleName = currentModule.getName();
            final String moduleNodeName = modulePrefix + moduleName;
            prefs.putBoolean(moduleNodeName, true);
        }
    }

    /** Initialise the preference window */
    void setupWindow() {

        // PREFERENCES WINDOW
        prefsWindow = getPrefsWindow();

        // MASTER PANEL 
        // The very top level panel in the window containing the tab panel and all content
        JPanel masterPanel = new JPanel();
        // Turn off the layout manager just for the master panel
        masterPanel.setLayout(null);
        // Make this transparent so we can display the unified toolbar on OSX 10.5
//        masterPanel.setOpaque(false);

        // TAB PANEL
        // Contains the buttons
        masterPanel.add(getTabPanel());

        // BACKGROUND PANEL
        // The colored panel that starts below the button panel
        // Content is added to this panel
        bgPanel = new JPanel();
        bgPanel.setOpaque(true);
        int bgPanelHeightOffset = 32;
        if (Alchemy.OS == OS_MAC) {
            bgPanelHeightOffset = 22;
        } else if (Alchemy.OS == OS_LINUX) {
            bgPanelHeightOffset = 0;
        }
        int bgPanelWidthOffset = 0;
        if (Alchemy.OS == OS_WINDOWS) {
            bgPanelWidthOffset = 4;
        }
        bgPanel.setBounds(0, tabPanelHeight, prefsWindowSize.width + bgPanelWidthOffset, prefsWindowSize.height - tabPanelHeight - bgPanelHeightOffset);
        bgPanel.setLayout(new BorderLayout());
        bgPanel.setBackground(COLOR_UI_HIGHLIGHT);
        bgPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        masterPanel.add(bgPanel);


        // GENERAL PANEL
        generalPanel = getGeneralPanel();

        // SESSION PANEL
        sessionPanel = getSessionPanel();

        // Add only the general panel
        bgPanel.add(generalPanel, BorderLayout.PAGE_START);
        prefsWindow.setTitle(Alchemy.bundle.getString("generalTitle"));

        // BUTTON PANEL
        bgPanel.add(getButtonPanel(), BorderLayout.PAGE_END);

        prefsWindow.getContentPane().add(masterPanel);
        prefsWindow.pack();
    }

    /** Show the preferences window */
    void showWindow() {
        changeModules = false;
        Point loc = AlcUtil.calculateCenter(prefsWindow);
        prefsWindow.setLocation(loc.x, loc.y);
        prefsWindow.setVisible(true);
    }

    /** Hide the preferences window */
    void hideWindow() {
        prefsWindow.setVisible(false);
        refreshModulePanel();
        sessionFileRenamePre.setText(sessionFilePreName);
        sessionFileRenameDate.setText(sessionFileDateFormat);
        sessionDirectoryTextField.setText(sessionPath);
        recordOnStartUp.setSelected(sessionRecordingState);
        if (Alchemy.preferences.simpleToolBar) {
            interfaceBox.setSelectedIndex(1);
        } else {
            interfaceBox.setSelectedIndex(0);
        }
        if (currentTab == SESSION) {
            bgPanel.remove(sessionPanel);
            bgPanel.add(generalPanel, BorderLayout.PAGE_START);
            prefsWindow.setTitle(Alchemy.bundle.getString("generalTitle"));
            currentTab = GENERAL;
            generalTabButton.setSelected(true);
        }
    }
    //////////////////////////////////////////////////////////////
    // WINDOW
    //////////////////////////////////////////////////////////////
    private JDialog getPrefsWindow() {

        JDialog w = new JDialog(Alchemy.window, true);
        // Brush Metal Look does not work with JDialog
        // Works with JFrame, but an owner can not be specified causing the menubar to disappear!
//        if (Alchemy.OS == MACOSX) {
//            // Try and detect if this is OSX 10.5 
//            if (JAVA_SUBVERSION >= 13) {
//                w.getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
//                System.out.println("Client Property: "+w.getRootPane().getClientProperty("apple.awt.brushMetalLook"));
//            }
//        }
        w.setPreferredSize(prefsWindowSize);
        w.setResizable(false);
        AlcUtil.registerWindowCloseKeys(w.getRootPane(), new AbstractAction() {

            public void actionPerformed(ActionEvent actionEvent) {
                hideWindow();
            }
        });
        return w;
    }

    //////////////////////////////////////////////////////////////
    // TAB PANEL
    //////////////////////////////////////////////////////////////
    private JPanel getTabPanel() {

        JPanel tabPanel = new JPanel() {

            // Draw the background color
            final Color unifiedLineColor = new Color(64, 64, 64);
//            final Color tabButtonStartColor = new Color(187, 187, 187);
//            final Color tabButtonEndColor = new Color(150, 150, 150);
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D) g;
                    int targetWidth = getRootPane().getSize().width;
                    int heightMinusOne = tabPanelHeight - 1;

//                    // OSX 10.5 Unified toolbar
//                    if (Alchemy.OS == MACOSX && JAVA_SUBVERSION >= 13) {
//                        g2.setPaint(unifiedLineColor);
//                        g2.drawLine(0, heightMinusOne, targetWidth, heightMinusOne);
//                    } else {
                    GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_UI_START, 0, tabPanelHeight, COLOR_UI_END, true);
                    g2.setPaint(gradientPaint);
                    g2.fillRect(0, 0, targetWidth, tabPanelHeight);
                    g2.setPaint(COLOR_UI_LINE);
                    g2.drawLine(0, heightMinusOne, targetWidth, heightMinusOne);
//                    }
                }
            }
        };
        tabPanel.setOpaque(false);
        tabPanel.setBounds(0, 0, prefsWindowSize.width, tabPanelHeight);
        tabPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        //////////////////////////////////////////////////////////////
        // TAB BUTTONS
        //////////////////////////////////////////////////////////////
        // Button Groupp
        ButtonGroup tabButtons = new ButtonGroup();
        // General button
        generalTabButton = new AlcToggleButton(Alchemy.bundle.getString("generalTitle"), null, AlcUtil.getUrlPath("preferences-general.png"), true);
        generalTabButton.setSelected(true);

        generalTabButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (currentTab == SESSION) {
                            bgPanel.remove(sessionPanel);
                            bgPanel.add(generalPanel, BorderLayout.PAGE_START);
                            prefsWindow.setTitle(Alchemy.bundle.getString("generalTitle"));
                            currentTab = GENERAL;
                            bgPanel.revalidate();
                            bgPanel.repaint();
                        }
                    }
                });

        tabButtons.add(generalTabButton);
        tabPanel.add(generalTabButton);
        // Session button
        AlcToggleButton sessionTabButton = new AlcToggleButton(Alchemy.bundle.getString("sessionTitle"), null, AlcUtil.getUrlPath("preferences-session.png"), true);

        sessionTabButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (currentTab == GENERAL) {
                            bgPanel.remove(generalPanel);
                            bgPanel.add(sessionPanel, BorderLayout.PAGE_START);
                            prefsWindow.setTitle(Alchemy.bundle.getString("sessionTitle"));
                            currentTab = SESSION;
                            bgPanel.revalidate();
                            bgPanel.repaint();
                        }
                    }
                });

        tabButtons.add(sessionTabButton);
        tabPanel.add(sessionTabButton);
        return tabPanel;
    }
    //////////////////////////////////////////////////////////////
    // GENERAL TAB
    //////////////////////////////////////////////////////////////
    private JPanel getGeneralPanel() {

        final JPanel gp = new JPanel();
        gp.setOpaque(false);
        gp.setLayout(new BoxLayout(gp, BoxLayout.PAGE_AXIS));

        // INTERFACE SELECTOR
        JPanel interfaceSelector = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        interfaceSelector.setOpaque(false);
        interfaceSelector.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        interfaceSelector.add(new JLabel(Alchemy.bundle.getString("interface") + ": "));

        String[] interfaceType = {Alchemy.bundle.getString("standard"), Alchemy.bundle.getString("simple")};
        interfaceBox = new JComboBox(interfaceType);
        if (Alchemy.preferences.simpleToolBar) {
            interfaceBox.setSelectedIndex(1);
        } else {
            interfaceBox.setSelectedIndex(0);
        }
        interfaceSelector.add(interfaceBox);

//        JLabel restart = new JLabel("* " + Alchemy.bundle.getString("restartRequired"));
//        restart.setFont(new Font("sansserif", Font.PLAIN, 10));
//        restart.setForeground(Color.GRAY);
//        interfaceSelector.add(restart);
        gp.add(interfaceSelector);
        
        
        // Undo Depth SELECTOR
        JPanel undoDepthSelector = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        undoDepthSelector.setOpaque(false);
        undoDepthSelector.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        undoDepthSelector.add(new JLabel(Alchemy.bundle.getString("undodepth") + ": "));

        String[] undoDepthString = {Alchemy.bundle.getString("disabled"),
                                    Alchemy.bundle.getString("single"),
                                    Alchemy.bundle.getString("unlimited")};
        undoDepthBox = new JComboBox(undoDepthString);
        if (Alchemy.preferences.undoDepth==0) {
            undoDepthBox.setSelectedIndex(0);
        }else if(Alchemy.preferences.undoDepth==1){
            undoDepthBox.setSelectedIndex(1);
        }else{
            undoDepthBox.setSelectedIndex(2);
        }
        undoDepthSelector.add(undoDepthBox);
        //undoDepthSelector.add(restart);
        gp.add(undoDepthSelector);
        
        
        // Locale Selector
        JPanel localeSelector = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        localeSelector.setOpaque(false);
        localeSelector.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        localeSelector.add(new JLabel(Alchemy.bundle.getString("locale") + ": "));

        //String[] localeString = {"System","de","en","es","fa","fi","fr","it","ja",};
        //localeBox = new JComboBox(localeString);
        localeBox = buildLocaleCombo();
        //if (Alchemy.preferences.undoDepth==0) {
        localeBox.setSelectedIndex(localeStringToInt(Alchemy.preferences.locale));
        //}else if(Alchemy.preferences.undoDepth==1){
        //    undoDepthBox.setSelectedIndex(1);
        //}else{
        //    undoDepthBox.setSelectedIndex(2);
        //}
        localeSelector.add(localeBox);
        //undoDepthSelector.add(restart);
        gp.add(localeSelector);
        

        // MODULES LABEL
        JPanel modulesLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modulesLabelPanel.setOpaque(false);
        modulesLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel modulesLabel = new JLabel(Alchemy.bundle.getString("modules") + ":");
        modulesLabelPanel.setOpaque(false);
        modulesLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        modulesLabelPanel.add(modulesLabel);
//        // Restart required
//        JLabel restart2 = new JLabel("* " + Alchemy.bundle.getString("restartRequired"));
//        restart2.setFont(FONT_SMALL);
//        restart2.setForeground(Color.GRAY);
//        restart2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
//        modulesLabelPanel.add(restart2);
        gp.add(modulesLabelPanel);
        gp.add(getModulesPane());
        
        JLabel restart5 = new JLabel("These Options Require Alchemy To Be Restarted");
        restart5.setFont(FONT_SMALL);
        restart5.setForeground(Color.GRAY);
        restart5.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        gp.add(Box.createVerticalStrut(3));
        gp.add(restart5);
        
        return gp;
    }

    private JScrollPane getModulesPane() {

        modulesPanel = getModulesPanel();
        //Create the scroll pane and add the panel to it.
        scrollPane = new JScrollPane(modulesPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setPreferredSize(new Dimension(prefsWindowSize.width - 30, 230));

        return scrollPane;
    }

    private JPanel getModulesPanel() {
        JPanel mp = new JPanel();
        //mp.setOpaque(true);
        mp.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        mp.setBackground(COLOR_UI_START);
        int plugins = Alchemy.plugins.creates.length + Alchemy.plugins.affects.length;
        mp.setLayout(new GridLayout(plugins, 1, 5, 5));
        //JCheckBox[] checkBoxes = new JCheckBox[plugins];

        setupModules(Alchemy.plugins.creates, mp);
        setupModules(Alchemy.plugins.affects, mp);
        modulesSet = true;
        return mp;
    }

    private void refreshModulePanel() {
        generalPanel.remove(scrollPane);
        generalPanel.add(getModulesPane(),3);
        generalPanel.revalidate();
    }
    //////////////////////////////////////////////////////////////
    // SESSION PANEL
    //////////////////////////////////////////////////////////////
    private JPanel getSessionPanel() {

        // Top Panel
        JPanel sp = new JPanel();
        sp.setOpaque(false);
        sp.setLayout(new BoxLayout(sp, BoxLayout.LINE_AXIS));
        sp.add(Box.createHorizontalGlue());

        // LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        if (Alchemy.OS != OS_MAC) {
            leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        // Session Recording
        JLabel sessionRecording = new JLabel(Alchemy.bundle.getString("sessionRecording") + ":");
        sessionRecording.setAlignmentX(Component.RIGHT_ALIGNMENT);
        leftPanel.add(sessionRecording);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 22)));
        //Session Directory
        JLabel sessionDirectory = new JLabel(Alchemy.bundle.getString("sessionDirectory") + ":");
        sessionDirectory.setAlignmentX(Component.RIGHT_ALIGNMENT);
        leftPanel.add(sessionDirectory);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 55)));
        //Session PDF Name
        JLabel sessionPDFName = new JLabel(Alchemy.bundle.getString("sessionPDFName") + ":");
        sessionPDFName.setAlignmentX(Component.RIGHT_ALIGNMENT);
        leftPanel.add(sessionPDFName);
        sp.add(leftPanel);
        sp.add(Box.createRigidArea(new Dimension(10, 0)));


        // RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        // Record on startup
        recordOnStartUp = new JCheckBox(Alchemy.bundle.getString("recordOnStartUp"));
        recordOnStartUp.setAlignmentX(Component.LEFT_ALIGNMENT);
        recordOnStartUp.setOpaque(false);
        recordOnStartUp.setSelected(sessionRecordingState);
        rightPanel.add(recordOnStartUp);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        // Session Directory
        sessionDirectoryTextField = new JTextField(sessionPath);
        sessionDirectoryTextField.setMaximumSize(new Dimension(300, 30));
        sessionDirectoryTextField.setEnabled(false);
        sessionDirectoryTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(sessionDirectoryTextField);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        // Select
        JButton selectButton = new JButton(Alchemy.bundle.getString("select") + "...");
        selectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                File file = AlcUtil.showFileChooser(true, prefsWindow);
                if (file != null) {
                    sessionDirectoryTextField.setText(file.getPath());
                }
            }
        });
        rightPanel.add(selectButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        // Panel
        JPanel sessionFileRenamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sessionFileRenamePanel.setOpaque(false);
        // PreName
        sessionFileRenamePre = new JTextField(sessionFilePreName);
        int textHeight = sessionFileRenamePre.getPreferredSize().height;
        sessionFileRenamePre.setPreferredSize(new Dimension(100, textHeight));
        sessionFileRenamePre.setFont(FONT_MEDIUM);
        // Actions to update the output on lose of focus or when enter is pressed
        AbstractAction pdfNameActionUpdate = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                refreshSessionPDFNameOutput();
            }
        };
        FocusAdapter pdfNameFocusUpdate = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                refreshSessionPDFNameOutput();
            }
        };
        sessionFileRenamePre.addActionListener(pdfNameActionUpdate);
        sessionFileRenamePre.addFocusListener(pdfNameFocusUpdate);
        sessionFileRenamePanel.add(sessionFileRenamePre);
        // DateFormat
        sessionFileRenameDate = new JTextField(sessionFileDateFormat);
        sessionFileRenameDate.setToolTipText(getDateStampReference());
        sessionFileRenameDate.setPreferredSize(new Dimension(160, textHeight));
        sessionFileRenameDate.setFont(FONT_MEDIUM);
        sessionFileRenameDate.addActionListener(pdfNameActionUpdate);
        sessionFileRenameDate.addFocusListener(pdfNameFocusUpdate);

        sessionFileRenamePanel.add(sessionFileRenameDate);

        // Extension
        JLabel sessionFileRenameExt = new JLabel(".pdf");
        sessionFileRenameExt.setFont(FONT_MEDIUM);
        sessionFileRenamePanel.add(sessionFileRenameExt);
        sessionFileRenamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(sessionFileRenamePanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Output Label
        sessionFileRenameOutput = new JLabel(
                Alchemy.bundle.getString("example") + ": " +
                sessionFilePreName +
                AlcUtil.dateStamp(sessionFileDateFormat) +
                ".pdf");
        sessionFileRenameOutput.setFont(FONT_SMALL);
        sessionFileRenameOutput.setForeground(Color.GRAY);
        rightPanel.add(sessionFileRenameOutput);

        sp.add(rightPanel);
        sp.add(Box.createHorizontalGlue());

        return sp;
    }

    /** Change the example output text */
    private void refreshSessionPDFNameOutput() {
        try {
            String dateStamp = AlcUtil.dateStamp(sessionFileRenameDate.getText());
            sessionFileRenameOutput.setText(Alchemy.bundle.getString("output") + ": " + sessionFileRenamePre.getText() + dateStamp + ".pdf");
        } catch (Exception ex) {
            invalidDateFormat();
        }
    }

    private JPanel getButtonPanel() {
        //////////////////////////////////////////////////////////////
        // RESTORE DEFAULT BUTTON
        //////////////////////////////////////////////////////////////
        JButton defaultButton = new JButton(Alchemy.bundle.getString("restoreDefaults"));
        defaultButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                resetModules(Alchemy.plugins.creates);
                resetModules(Alchemy.plugins.affects);
                refreshModulePanel();
                sessionFilePreName = defaultSessionFilePreName;
                sessionFileDateFormat = defaultSessionFileDateFormat;
                sessionFileRenamePre.setText(sessionFilePreName);
                sessionFileRenameDate.setText(sessionFileDateFormat);
                sessionPath = DIR_DESKTOP;
                sessionDirectoryTextField.setText(sessionPath);
                sessionRecordingState = false;
                recordOnStartUp.setSelected(sessionRecordingState);
                interfaceBox.setSelectedIndex(0);
                undoDepthBox.setSelectedIndex(0);
                localeBox.setSelectedIndex(0);
            }
        });


        //////////////////////////////////////////////////////////////
        // CANCEL BUTTON
        //////////////////////////////////////////////////////////////
        JButton cancelButton = new JButton(Alchemy.bundle.getString("cancel"));
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                hideWindow();
            }
        });

        //////////////////////////////////////////////////////////////
        // OK BUTTON
        //////////////////////////////////////////////////////////////
        okButton = new JButton(Alchemy.bundle.getString("ok"));
        okButton.setMnemonic(KeyEvent.VK_ENTER);

        okButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        boolean restart = false;
                        // Set the interface to simple or not
                        Alchemy.preferences.simpleToolBar = (interfaceBox.getSelectedIndex() == 1) ? true : false;
                        Alchemy.preferences.undoDepth=(undoDepthBox.getSelectedIndex());
                        Alchemy.preferences.locale=(localeIntToString(localeBox.getSelectedIndex()));
                        // If the session file name has changed
                        if (!sessionFileRenamePre.getText().equals(sessionFilePreName) || !sessionFileRenameDate.getText().equals(sessionFileDateFormat)) {
                            try {
                                // Check that the dateformat is valid
                                // and does not throw and exception
                                String dateFormat = AlcUtil.dateStamp(sessionFileRenameDate.getText());
                                // Check that both of the fields are not blank
                                if (!sessionFileRenamePre.getText().equals("") && !dateFormat.equals("")) {
                                    sessionFilePreName = sessionFileRenamePre.getText();
                                    sessionFileDateFormat = sessionFileRenameDate.getText();
                                    restart = true;
                                }

                            } catch (Exception ex) {
                                invalidDateFormat();
                                return;
                            }
                        }
                        // If the session directory has changed
                        if (!sessionDirectoryTextField.getText().equals(sessionPath)) {
                            sessionPath = sessionDirectoryTextField.getText();
                            restart = true;
                        }
                        // Restart the session so that next time a new file is created
                        if (restart) {
                            Alchemy.session.restartSession();
                        }
                        // IF the record on statup check box has changed
                        if (sessionRecordingState != recordOnStartUp.isSelected()) {
                            sessionRecordingState = recordOnStartUp.isSelected();
                        }
                        prefsWindow.setVisible(false);
                    }
                });

        JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPane.add(defaultButton);
        buttonPane.add(Box.createHorizontalGlue());
        if (Alchemy.OS == OS_MAC) {
            buttonPane.add(cancelButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(okButton);
        } else {
            buttonPane.add(okButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);
        }

        return buttonPane;
    }

    /** Show the user that the date format was invalid */
    private void invalidDateFormat() {
        // Incase the date format is not correct
        sessionFileRenameDate.setBackground(Color.PINK);
        sessionFileRenameDate.setText(Alchemy.bundle.getString("invalidDateFormat"));
        javax.swing.Timer timer = new javax.swing.Timer(1500, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sessionFileRenameDate.setBackground(Color.WHITE);
                sessionFileRenameDate.setText(sessionFileDateFormat);
            }
        });
        timer.start();
        timer.setRepeats(false);
    }
    private JComboBox buildLocaleCombo(){
        int lNum = 17;
        String[] lString = new String[lNum];
        int n = 0;
        while(n<lNum){
            Array.set(lString, n, localeIntToString(n));
            n++;
        }
        JComboBox lBox = new JComboBox(lString);
        return lBox;
    }
    private int localeStringToInt(String s){
        int n = 0;
        if(s.equals("system")){ n = 0; }
        else if(s.equals("de")){ n = 1; }
        else if(s.equals("en")){ n = 2; }
        else if(s.equals("es")){ n = 3; }
        else if(s.equals("fa")){ n = 4; }
        else if(s.equals("fi")){ n = 5; }
        else if(s.equals("fr")){ n = 6; }
        else if(s.equals("it")){ n = 7; }
        else if(s.equals("ja")){ n = 8; }
        else if(s.equals("nl")){ n = 9; }
        else if(s.equals("no")){ n = 10; }
        else if(s.equals("pl")){ n = 11; }
        else if(s.equals("pt")){ n = 12; }
        else if(s.equals("ru")){ n = 13; }
        else if(s.equals("tr")){ n = 14; }
        else if(s.equals("uk")){ n = 15; }
        else if(s.equals("zh")){ n = 16; }
        return n;
    }
    private String localeIntToString(int n){
        String s = "system";
        if(n == 0){ s = "system"; }
        else if(n == 1) { s = "de"; }
        else if(n == 2) { s = "en"; }
        else if(n == 3) { s = "es"; }
        else if(n == 4) { s = "fa"; }
        else if(n == 5) { s = "fi"; }
        else if(n == 6) { s = "fr"; }
        else if(n == 7) { s = "it"; }
        else if(n == 8) { s = "ja"; }
        else if(n == 9) { s = "nl"; }
        else if(n == 10){ s = "no"; }
        else if(n == 11){ s = "pl"; }
        else if(n == 12){ s = "pt"; }
        else if(n == 13){ s = "ru"; }
        else if(n == 14){ s = "tr"; }
        else if(n == 15){ s = "uk"; }
        else if(n == 16){ s = "zh"; }
        return s;
    }

    /** Returns a string with some examples of how to set the date format */
    private String getDateStampReference() {
        Date today = new Date();
        String[] dateFormats = {"EEEE, MMMM dd, yyyy",
            "hha '- Alchemy Time!'",
            "E, MMM d, yyyy",
            "yyyy.MM.dd 'at' HH.mm.ss"
        };
        String dates = "<html>" + Alchemy.bundle.getString("example") + ":<br>";
        for (int i = 0; i < dateFormats.length; i++) {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormats[i], LOCALE);
            dates += dateFormats[i];
            dates += " : ";
            dates += "<font color=#333333>" + formatter.format(today) + "</font>";
            dates += "<br>";
        }
        dates += "</html>";
        return dates;
    }

    private void setupModules(AlcModule[] modules, JPanel panel) {

        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            String moduleName = currentModule.getName();
            final String moduleNodeName = modulePrefix + moduleName;
            final JCheckBox checkBox = new JCheckBox(moduleName);
            checkBox.setBackground(COLOR_UI_START);
            checkBox.setFont(FONT_MEDIUM);

            // Set the state of the checkbox
            checkBox.setSelected(prefs.getBoolean(moduleNodeName, true));

            checkBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    changeModules = true;
                //prefs.putBoolean(moduleNodeName, checkBox.isSelected());
                }
            });

            panel.add(checkBox);
        }
    }

    private void changeModules() {
        // If there has actually been some changes
        if (changeModules) {
            Component[] components = modulesPanel.getComponents();
            int creates = Alchemy.plugins.getNumberOfCreateModules();
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) components[i];
                    String moduleName;
                    if (i < creates) {
                        moduleName = Alchemy.plugins.creates[i].getName();
                    //System.out.println("CREATE: " + checkBox.getText() + " " + Alchemy.plugins.creates[i].getName());
                    } else {
                        moduleName = Alchemy.plugins.affects[i - creates].getName();
                    //System.out.println("AFFECT: " + checkBox.getText() + " " + Alchemy.plugins.affects[i - creates].getName());
                    }
                    String moduleNodeName = modulePrefix + moduleName;
                    prefs.putBoolean(moduleNodeName, checkBox.isSelected());
                }

            }
        }
    }

    //////////////////////////////////////////////////////////////
    // UTILITIES
    //////////////////////////////////////////////////////////////
    /** Converts two numbers stored in the preferences such as:
     *  '10,30' into a Point
     *  
     * @param string    The numbers separated by a comma
     * @return          A Point object 
     */
    private static Point stringToPoint(String string) {
        if (string != null) {
            String[] splitString = string.split(",", 2);
            int x = new Integer(splitString[0]).intValue();
            int y = new Integer(splitString[1]).intValue();
            Point point = new Point(x, y);
            //System.out.println(point);
            return point;
        } else {
            return null;
        }
    }

    /** Converts a point into a string such as:
     *  '10,30'
     * @param point    The point to be converted
     * @return         A string with the points numbers
     */
    private static String pointToString(Point point) {
        if (point != null) {
            String x = String.valueOf(point.x);
            String y = String.valueOf(point.y);
            String xy = x + "," + y;
            //System.out.println(xy);
            return xy;
        } else {
            return null;
        }
    }

    /** Converts two numbers stored in the preferences such as:
     *  '10,30' into a Dimension
     *  
     * @param string    The numbers separated by a comma
     * @return          A Dimension object 
     */
    private static Dimension stringToDimension(String string) {
        if (string != null) {
            String[] splitString = string.split(",", 2);
            int width = new Integer(splitString[0]).intValue();
            int height = new Integer(splitString[1]).intValue();
            Dimension dimension = new Dimension(width, height);
            return dimension;
        } else {
            return null;
        }
    }

    /** Converts a Dimension into a string such as:
     *  '10,30'
     * @param point    The Dimension to be converted
     * @return         A string with the points numbers
     */
    private static String dimensionToString(Dimension dimension) {
        if (dimension != null) {
            String width = String.valueOf(dimension.width);
            String height = String.valueOf(dimension.height);
            String widthHeight = width + "," + height;
            //System.out.println(xy);
            return widthHeight;
        } else {
            return null;
        }
    }
}
