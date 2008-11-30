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
import java.io.*;
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
    //  WINDOW LAYOUT 
    //////////////////////////////////////////////////////////////
    /** The preferences window */
    private JFrame prefsWindow;
    /** Background content panel for the window */
    private JPanel bgPanel;
    /** General content panel*/
    private JPanel generalPanel;
    /** Session content panel*/
    private JPanel sessionPanel;
    /** Modules content panel*/
    private JPanel modulesPanel;
    /** Default / Cancel / OK Button Pane */
    private JPanel buttonPane;
    /** Ok Button */
    private JButton okButton;
    //////////////////////////////////////////////////////////////
    //  MODULES
    //////////////////////////////////////////////////////////////    
    /** Scroll pane for the module listing */
    private JScrollPane scrollPane;
    /** Panel containing the modules */
    private JPanel modulePanel;
    /** If the modules have been customised */
    private boolean modulesSet;
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
    /** Session file naming components */
    private JLabel sessionFileRenameOutput;
    private JTextField sessionFileRenamePre,  sessionFileRenameDate;
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
    /** Canvas background colour */
    int bgColour;
    /** Colour */
    int colour;

    AlcPreferences() {
        loadPreferences();
    }

    /** Load the preference nodes */
    private void loadPreferences() {

        prefs = Preferences.userNodeForPackage(getClass());
        modulesSet = prefs.getBoolean("Modules Set", false);

        sessionRecordingState = prefs.getBoolean("Recording State", false);
        sessionRecordingWarning = prefs.getBoolean("Recording Warning", true);
        sessionPath = prefs.get("Session Path", DESKTOP_DIR);
        sessionRecordingInterval = prefs.getInt("Recording Interval", 30000);
        sessionAutoClear = prefs.getBoolean("Auto Clear Canvas", false);
        sessionLink = prefs.getBoolean("Link to Current Session", true);
        sessionFilePreName = prefs.get("Session File Pre Name", defaultSessionFilePreName);
        sessionFileDateFormat = prefs.get("Session File Date Format", defaultSessionFileDateFormat);

        shapesPath = prefs.get("Shapes Path", new File("shapes").getAbsolutePath());

        switchVectorApp = prefs.get("Switch Vector Application", null);
        switchBitmapApp = prefs.get("Switch Bitmap Application", null);

        paletteAttached = prefs.getBoolean("Palette Attached", false);
        paletteLocation = stringToPoint(prefs.get("Palette Location", null));
        canvasLocation = stringToPoint(prefs.get("Canvas Location", null));
        canvasSize = stringToDimension(prefs.get("Canvas Size", null));
        transparentFullscreen = prefs.getBoolean("Transparent Fullscreen", false);
        simpleToolBar = prefs.getBoolean("Simple ToolBar", false);

        smoothing = prefs.getBoolean("Smoothing", true);
        lineSmoothing = prefs.getBoolean("Line Smoothing", true);
        bgColour = prefs.getInt("Background Colour", 0xFFFFFF);
        colour = prefs.getInt("Colour", 0x000000);
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
        prefs.putBoolean("Smoothing", Alchemy.canvas.getSmoothing());
        prefs.putBoolean("Line Smoothing", AlcShape.lineSmoothing);
        prefs.putBoolean("Simple ToolBar", simpleToolBar);
        prefs.putBoolean("Transparent Fullscreen", transparentFullscreen);

        prefs.putInt("Background Colour", Alchemy.canvas.getBgColour().getRGB());
        prefs.putInt("Colour", Alchemy.canvas.getForegroundColour().getRGB());

        if (switchVectorApp != null) {
            prefs.put("Switch Vector Application", switchVectorApp);
        }
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

    /** Reset the modules to defaults */
    private void resetModules(AlcModule[] modules) {
        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            String moduleName = currentModule.getName();
            final String moduleNodeName = modulePrefix + moduleName;
            prefs.putBoolean(moduleNodeName, true);
        }
    }

    /** Initialise the preference window */
    void setupWindow(AlcWindow owner) {

        // TODO - Implement tabs
        // For mac - http://explodingpixels.wordpress.com/2008/05/02/sexy-swing-app-the-unified-toolbar/
        // http://explodingpixels.wordpress.com/2008/05/03/sexy-swing-app-the-unified-toolbar-now-fully-draggable/

        //////////////////////////////////////////////////////////////
        // WINDOW
        //////////////////////////////////////////////////////////////
        prefsWindow = new JFrame();
        final int javaVersion = new Integer(JAVA_VERSION_NAME.substring(6, 8));

        if (Alchemy.PLATFORM == MACOSX) {
            // Try and detect if this is OSX 10.5 
            // TODO - Fix OSX 10.5 detection
            if (javaVersion >= 13) {
                prefsWindow.getRootPane().putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);
            }
        }
        final Dimension prefsWindowSize = new Dimension(500, 450);
        prefsWindow.setPreferredSize(prefsWindowSize);
        String title = "Alchemy Preferences";
        if (Alchemy.PLATFORM == WINDOWS) {
            title = "Alchemy Options";
        }
        prefsWindow.setTitle(title);
        prefsWindow.setResizable(false);

        AlcUtil.registerWindowCloseKeys(prefsWindow.getRootPane(), new AbstractAction() {

            public void actionPerformed(ActionEvent actionEvent) {
                prefsWindow.setVisible(false);
            }
        });

        //////////////////////////////////////////////////////////////
        // MASTER PANEL
        //////////////////////////////////////////////////////////////
        JPanel masterPanel = new JPanel();
        // Turn off the layout manager just for the master panel
        masterPanel.setLayout(null);
        // Make this transparent so we can display the unified toolbar on OSX 10.5
        masterPanel.setOpaque(false);


        //////////////////////////////////////////////////////////////
        // TAB PANEL
        //////////////////////////////////////////////////////////////
        final int tabPanelHeight = 64;
        JPanel tabPanel = new JPanel() {

            final Color unifiedLineColour = new Color(64, 64, 64);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (g instanceof Graphics2D) {
                    Graphics2D g2 = (Graphics2D) g;
                    int targetWidth = getRootPane().getSize().width;
                    int heightMinusOne = tabPanelHeight - 1;

                    // OSX 10.5 Unified toolbar
                    if (Alchemy.PLATFORM == MACOSX && javaVersion >= 13) {
                        g2.setPaint(unifiedLineColour);
                        g2.drawLine(0, heightMinusOne, targetWidth, heightMinusOne);
                    } else {
                        GradientPaint gradientPaint = new GradientPaint(0, 0, AlcToolBar.toolBarBgStartColour, 0, tabPanelHeight, AlcToolBar.toolBarBgEndColour, true);
                        g2.setPaint(gradientPaint);
                        g2.fillRect(0, 0, targetWidth, tabPanelHeight);
                        g2.setPaint(AlcAbstractToolBar.toolBarLineColour);
                        g2.drawLine(0, heightMinusOne, targetWidth, heightMinusOne);
                    }
                }
            }
        };
        tabPanel.setOpaque(false);
        tabPanel.setBounds(0, 0, prefsWindowSize.width, tabPanelHeight);
        tabPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        //////////////////////////////////////////////////////////////
        // TAB BUTTONS
        //////////////////////////////////////////////////////////////

        ButtonGroup tabButtons = new ButtonGroup();
        AlcToggleButton styleTest = new AlcToggleButton("General", null, AlcUtil.getUrlPath("preferences-general.png"), true);
        styleTest.setSelected(true);
        AlcToggleButton underOverTest = new AlcToggleButton("Advanced", null, AlcUtil.getUrlPath("preferences-advanced.png"), true);
        tabButtons.add(styleTest);
        tabPanel.add(styleTest);
        tabButtons.add(underOverTest);
        tabPanel.add(underOverTest);

        masterPanel.add(tabPanel);

        // TODO - Divide up the prefs panels General / Session / Modules


        //////////////////////////////////////////////////////////////
        // BACKGROUND PANEL
        //////////////////////////////////////////////////////////////
        bgPanel = new JPanel();
        bgPanel.setOpaque(true);
        bgPanel.setBounds(0, tabPanelHeight, prefsWindowSize.width, prefsWindowSize.height - tabPanelHeight - 22);
        bgPanel.setLayout(new BoxLayout(bgPanel, BoxLayout.PAGE_AXIS));
        bgPanel.setBackground(AlcToolBar.toolBarBgStartColour);
        bgPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        masterPanel.add(bgPanel);

        //////////////////////////////////////////////////////////////
        // INTERFACE SELECTOR
        //////////////////////////////////////////////////////////////
        JPanel centreRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        centreRow.setOpaque(false);
        centreRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        centreRow.add(new JLabel(Alchemy.bundle.getString("interface") + ": "));


        String[] interfaceType = {Alchemy.bundle.getString("standard"), Alchemy.bundle.getString("simple")};

        final JComboBox interfaceBox = new JComboBox(interfaceType);
        interfaceBox.setFont(FONT_MEDIUM);
        interfaceBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // STANDARD                
                if (interfaceBox.getSelectedIndex() == 0) {
                    Alchemy.preferences.simpleToolBar = false;
                // SIMPLE
                } else {
                    Alchemy.preferences.simpleToolBar = true;
                }
                if (okButton != null) {
                    okButton.requestFocus();
                }
            }
        });

        if (Alchemy.preferences.simpleToolBar) {
            interfaceBox.setSelectedIndex(1);
        }
        centreRow.add(interfaceBox);

        JLabel restart = new JLabel("* " + Alchemy.bundle.getString("restartRequired"));
        restart.setFont(new Font("sansserif", Font.PLAIN, 10));
        restart.setForeground(Color.GRAY);
        centreRow.add(restart);

        bgPanel.add(centreRow);


        //////////////////////////////////////////////////////////////
        // SESSION FILE
        //////////////////////////////////////////////////////////////
        // Panel
        JPanel sessionFileRenamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        sessionFileRenamePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sessionFileRenamePanel.setOpaque(false);
        // Label
        JLabel sessionFileRenameLabel = new JLabel(Alchemy.bundle.getString("sessionPDFName") + ":");
        sessionFileRenamePanel.add(sessionFileRenameLabel);
        // PreName
        sessionFileRenamePre = new JTextField(sessionFilePreName);
        int textHeight = sessionFileRenamePre.getPreferredSize().height;
        sessionFileRenamePre.setPreferredSize(new Dimension(80, textHeight));
        sessionFileRenamePre.setFont(FONT_MEDIUM);


        sessionFileRenamePre.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        refreshSessionPDFNameOutput();
                    }
                });
        sessionFileRenamePanel.add(sessionFileRenamePre);

        // DateFormat
        sessionFileRenameDate = new JTextField(sessionFileDateFormat);
        sessionFileRenameDate.setToolTipText(getDateStampReference());
        sessionFileRenameDate.setPreferredSize(new Dimension(140, textHeight));
        sessionFileRenameDate.setFont(FONT_MEDIUM);
        sessionFileRenameDate.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        refreshSessionPDFNameOutput();
                    }
                });

        sessionFileRenamePanel.add(sessionFileRenameDate);

        // Extension
        JLabel sessionFileRenameExt = new JLabel(".pdf");
        sessionFileRenameExt.setFont(FONT_MEDIUM);
        sessionFileRenamePanel.add(sessionFileRenameExt);
        bgPanel.add(sessionFileRenamePanel);

        // Output Panel
        JPanel sessionFileRenameOuputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sessionFileRenameOuputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        sessionFileRenameOuputPanel.setOpaque(false);
        // Label
        sessionFileRenameOutput = new JLabel(
                Alchemy.bundle.getString("output") + ": " +
                sessionFilePreName +
                AlcUtil.dateStamp(sessionFileDateFormat) +
                ".pdf");
        sessionFileRenameOutput.setFont(FONT_SMALL);
        sessionFileRenameOutput.setForeground(Color.GRAY);
        sessionFileRenameOutput.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        sessionFileRenameOuputPanel.add(sessionFileRenameOutput);
        bgPanel.add(sessionFileRenameOuputPanel);


        //////////////////////////////////////////////////////////////
        // MODULES LABEL
        //////////////////////////////////////////////////////////////
        JPanel modulesLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modulesLabelPanel.setOpaque(false);
        modulesLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel modulesLabel = new JLabel(Alchemy.bundle.getString("modules") + ":");
        modulesLabelPanel.setOpaque(false);
        modulesLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        modulesLabelPanel.add(modulesLabel);
        // Restart required
        JLabel restart2 = new JLabel(restart.getText());
        restart2.setFont(FONT_SMALL);
        restart2.setForeground(Color.GRAY);
        restart2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        modulesLabelPanel.add(restart2);
        bgPanel.add(modulesLabelPanel);


        //////////////////////////////////////////////////////////////
        // RESTORE DEFAULT BUTTON
        //////////////////////////////////////////////////////////////
        JButton defaultButton = new JButton(Alchemy.bundle.getString("restoreDefaults"));
        defaultButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        sessionFilePreName = defaultSessionFilePreName;
                        sessionFileDateFormat = defaultSessionFileDateFormat;
                        sessionFileRenamePre.setText(sessionFilePreName);
                        sessionFileRenameDate.setText(sessionFileDateFormat);
                        resetModules(Alchemy.plugins.creates);
                        resetModules(Alchemy.plugins.affects);
                        refreshModulePanel();
                    }
                });


        //////////////////////////////////////////////////////////////
        // CANCEL BUTTON
        //////////////////////////////////////////////////////////////
        JButton cancelButton = new JButton(Alchemy.bundle.getString("cancel"));
        cancelButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        prefsWindow.setVisible(false);
                        sessionFileRenamePre.setText(sessionFilePreName);
                        sessionFileRenameDate.setText(sessionFileDateFormat);
                        refreshModulePanel();
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
                                    // Reset the session so next time a new file is created
                                    Alchemy.session.restartSession();
                                }

                            } catch (Exception ex) {
                                invalidDateFormat();
                                return;
                            }
                        }
                        changeModules();
                        prefsWindow.setVisible(false);
                    }
                });

        buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPane.add(defaultButton);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(okButton);

        //masterPanel.add(buttonPane);

        prefsWindow.getContentPane().add(masterPanel);
    //prefsWindow.pack();
    }

    void showWindow() {
        changeModules = false;
        if (scrollPane == null) {
            setupModulePanel();
            //Add the scroll pane to this panel.
            bgPanel.add(scrollPane);
            bgPanel.add(buttonPane);
            prefsWindow.pack();

        //prefsWindow.getRootPane().setDefaultButton(okButton);
        }

        Point loc = AlcUtil.calculateCenter(prefsWindow);
        prefsWindow.setLocation(loc.x, loc.y);
        prefsWindow.setVisible(true);
    }

    private void setupModulePanel() {
        modulePanel = new JPanel();
        modulePanel.setOpaque(true);
        modulePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        modulePanel.setBackground(AlcToolBar.toolBarBgStartColour);
        int plugins = Alchemy.plugins.creates.length + Alchemy.plugins.affects.length;
        modulePanel.setLayout(new GridLayout(plugins, 1, 5, 5));
        //JCheckBox[] checkBoxes = new JCheckBox[plugins];

        setupModules(Alchemy.plugins.creates);
        setupModules(Alchemy.plugins.affects);
        modulesSet = true;


        //Create the scroll pane and add the panel to it.
        scrollPane = new JScrollPane(modulePanel);
        //scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 200));

    }

    private void refreshModulePanel() {
        bgPanel.remove(scrollPane);
        setupModulePanel();
        bgPanel.add(scrollPane);
        bgPanel.add(buttonPane);
        bgPanel.revalidate();
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

    private void setupModules(AlcModule[] modules) {

        for (int i = 0; i < modules.length; i++) {
            AlcModule currentModule = modules[i];
            String moduleName = currentModule.getName();
            final String moduleNodeName = modulePrefix + moduleName;
            final JCheckBox checkBox = new JCheckBox(moduleName);
            checkBox.setBackground(AlcToolBar.toolBarBgStartColour);
            checkBox.setFont(FONT_MEDIUM);

            // Set the state of the checkbox
            checkBox.setSelected(prefs.getBoolean(moduleNodeName, true));

            checkBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    changeModules = true;
                //prefs.putBoolean(moduleNodeName, checkBox.isSelected());
                }
            });

            modulePanel.add(checkBox);
        }
    }

    private void changeModules() {
        // If there has actually been some changes
        if (changeModules) {
            Component[] components = modulePanel.getComponents();
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
