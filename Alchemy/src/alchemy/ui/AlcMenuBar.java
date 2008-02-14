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
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import javax.help.*;

/** 
 * Menubar for Alchemy
 * Housing the usual file, print etc... commands 
 */
public class AlcMenuBar extends JMenuBar implements AlcConstants, ActionListener {

    private final AlcMain root;
    private final static int height = 27;
    private AlcMenu fileMenu,  sessionMenu,  viewMenu,  intervalMenu,  switchMenu,  helpMenu;
    private AlcMenuItem exitItem,  directoryItem,  switchVectorAppItem,  switchBitmapAppItem;
    private AlcCheckBoxMenuItem defaultRecordingItem,  autoClearItem;
    private AlcRadioButtonMenuItem intervalItem;
    public AbstractAction exportAction,  printAction,  fullScreenAction,  recordingAction,  switchVectorAction,  switchBitmapAction;
    private File platformAppDir;
    //
    /** Recording interval array in milliseconds */
    private int[] recordingInterval = {5000, 15000, 30000, 60000, 120000, 300000, 600000};
    /** Recording interval array in readable form */
    private String[] recordingIntervalString = new String[recordingInterval.length];
    //
    private PrinterJob printer = null;
    private PageFormat page = null;
    private PrintRequestAttributeSet aset = null;
    private PageFormat defaultPage = null;

    /** Creates a new instance of AlcMenuBar */
    public AlcMenuBar(final AlcMain root) {

        this.root = root;
        this.setBackground(AlcToolBar.toolBarHighlightColour);

        // Default applications directory depending on the platform
        switch (AlcMain.PLATFORM) {
            case MACOSX:
                platformAppDir = new File(File.separator + "Applications");
                break;
            case WINDOWS:
                platformAppDir = new File(File.separator + "Program Files");
                break;
        }


        // Initialise the array of recording intervals from the bundle
        for (int i = 0; i < recordingIntervalString.length; i++) {
            recordingIntervalString[i] = getS("interval" + recordingInterval[i]);
        }

        //////////////////////////////////////////////////////////////
        // FILE MENU
        //////////////////////////////////////////////////////////////
        fileMenu = new AlcMenu(getS("fileTitle"));

        // New
        String newTitle = getS("newTitle");
        AbstractAction newAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.canvas.clear();
            }
        };
        AlcMenuItem newItem = new AlcMenuItem(newAction);
        newItem.setup(newTitle, KeyEvent.VK_N);
        // Shortcut - Modifier n
        root.setHotKey(KeyEvent.VK_N, newTitle, newAction);
        fileMenu.add(newItem);


        fileMenu.add(new JSeparator());

        // Export
        String exportTitle = getS("exportTitle");
        exportAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                askExportPath();
            }
        };
        AlcMenuItem exportItem = new AlcMenuItem(exportAction);
        exportItem.setup(exportTitle, KeyEvent.VK_E);
        // Shortcut - Modifier e
        root.setHotKey(KeyEvent.VK_E, exportTitle, exportAction);
        fileMenu.add(exportItem);

        fileMenu.add(new JSeparator());

        // Page Setup
        AbstractAction pageAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                pageSetup();
            }
        };
        AlcMenuItem pageItem = new AlcMenuItem(pageAction);
        pageItem.setup(getS("pageSetupTitle"));
        fileMenu.add(pageItem);

        // Print
        String printTitle = getS("printTitle");
        printAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                print();
            }
        };
        AlcMenuItem printItem = new AlcMenuItem(printAction);
        printItem.setup(printTitle, KeyEvent.VK_P);
        // Shortcut - Modifier p
        root.setHotKey(KeyEvent.VK_P, printTitle, printAction);
        fileMenu.add(printItem);


        // Exit - not included on a MAC
        if (AlcMain.PLATFORM != MACOSX) {
            fileMenu.add(new JSeparator());

            exitItem = new AlcMenuItem(getS("exitTitle"));
            exitItem.addActionListener(this);
            fileMenu.add(exitItem);
        }

        this.add(fileMenu);

        //////////////////////////////////////////////////////////////
        // VIEW MENU
        //////////////////////////////////////////////////////////////
        viewMenu = new AlcMenu(getS("viewTitle"));
        // Fullscreen

        String fullScreenTitle = getS("fullScreenTitle");
        fullScreenAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.setFullscreen(!root.isFullscreen());
            }
        };
        AlcMenuItem fullScreenItem = new AlcMenuItem(fullScreenAction);
        fullScreenItem.setup(fullScreenTitle, KeyEvent.VK_F);
        // Shortcut - Modifier f
        root.setHotKey(KeyEvent.VK_F, fullScreenTitle, fullScreenAction);
        viewMenu.add(fullScreenItem);

        this.add(viewMenu);

        //////////////////////////////////////////////////////////////
        // SESSION MENU
        //////////////////////////////////////////////////////////////
        sessionMenu = new AlcMenu(getS("sessionTitle"));

        // Save PDF page
        String savePageTitle = getS("savePageTitle");
        AbstractAction savePageAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.session.manualSavePage();
            }
        };
        AlcMenuItem savePageItem = new AlcMenuItem(savePageAction);
        savePageItem.setup(savePageTitle, KeyEvent.VK_S);
        // Shortcut - Modifier s
        root.setHotKey(KeyEvent.VK_S, savePageTitle, savePageAction);
        sessionMenu.add(savePageItem);

        // Save and clear PDF page
        String saveClearTitle = getS("saveClearTitle");
        AbstractAction saveClearPageAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.session.manualSaveClearPage();
            }
        };
        AlcMenuItem saveClearPageItem = new AlcMenuItem(saveClearPageAction);
        saveClearPageItem.setup(saveClearTitle, KeyEvent.VK_D);
        root.setHotKey(KeyEvent.VK_D, saveClearTitle, saveClearPageAction);
        sessionMenu.add(saveClearPageItem);


        sessionMenu.add(new JSeparator());

        // Toggle Recording
        String recordingTitle = getS("recordingTitle");
        final AlcCheckBoxMenuItem recordingItem = new AlcCheckBoxMenuItem();
        recordingAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                // If the command has come from the key then we need to change the state of the menu item as well
                if (e.getActionCommand().equals("r")) {
                    recordingItem.setState(!recordingItem.getState());
                }
                root.session.setRecording(recordingItem.getState());
                System.out.println("STATE: " + recordingItem.getState());

            }
        };
        recordingItem.setAction(recordingAction);
        recordingItem.setup(recordingTitle, KeyEvent.VK_R);
        // recordingItem.setToolTipText("Start/Finish recording of a session. " +
        // "Switch on to begin the session, and toggle off to finish the session and view the save PDF file");
        // Shortcut - Modifier r
        root.setHotKey(KeyEvent.VK_R, recordingTitle, recordingAction);

        recordingItem.setState(root.prefs.getRecordingState());
        if (root.prefs.getRecordingState()) {
            root.session.setRecording(true);
        }
        sessionMenu.add(recordingItem);

        // Interval submenu
        intervalMenu = new AlcMenu(getS("recordIntervalTitle"));
        // Set the opacity and colour of this to overide the defaults used for the top menus
        intervalMenu.setOpaque(true);
        intervalMenu.setBackground(AlcToolBar.toolBarHighlightColour);
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < recordingIntervalString.length; i++) {
            intervalItem = new AlcRadioButtonMenuItem(recordingInterval[i], recordingIntervalString[i]);
            // Set the default value to selected
            if (root.prefs.getRecordingInterval() == recordingInterval[i]) {
                intervalItem.setSelected(true);
            }
            intervalItem.addActionListener(this);
            // Send the interval as command
            intervalItem.setActionCommand("Interval");
            group.add(intervalItem);
            intervalMenu.add(intervalItem);
        }
        intervalMenu.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        sessionMenu.add(intervalMenu);

        sessionMenu.add(new JSeparator());

        // Auto Clear
        autoClearItem = new AlcCheckBoxMenuItem(getS("autoClearTitle"));
        autoClearItem.setState(root.prefs.getAutoClear());
        autoClearItem.addActionListener(this);
        sessionMenu.add(autoClearItem);
        // Default Recording
        defaultRecordingItem = new AlcCheckBoxMenuItem(getS("recordStartUpTitle"));
        defaultRecordingItem.setState(root.prefs.getRecordingState());
        defaultRecordingItem.addActionListener(this);
        sessionMenu.add(defaultRecordingItem);

        sessionMenu.add(new JSeparator());

        // Restart Session
        String restartTitle = getS("restartTitle");
        AbstractAction restartAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.session.restartSession();
            }
        };
        AlcMenuItem restartItem = new AlcMenuItem(restartAction);
        restartItem.setup(restartTitle);
        sessionMenu.add(restartItem);

        // Default Directory
        directoryItem = new AlcMenuItem(getS("setSessionDirTitle"));
        directoryItem.addActionListener(this);
        sessionMenu.add(directoryItem);
        this.add(sessionMenu);


        //////////////////////////////////////////////////////////////
        // SWITCH MENU
        //////////////////////////////////////////////////////////////
        switchMenu = new AlcMenu(getS("switchTitle"));

        // Switch Vector
        String switchVectorTitle = getS("switchVectorTitle");
        switchVectorAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                switchVector();
            }
        };
        AlcMenuItem switchVectorItem = new AlcMenuItem(switchVectorAction);
        switchVectorItem.setup(switchVectorTitle, KeyEvent.VK_V);
        // Shortcut - Modifier v
        root.setHotKey(KeyEvent.VK_V, switchVectorTitle, switchVectorAction);
        switchMenu.add(switchVectorItem);

        // Switch Bitmaps
        String switchBitmapTitle = getS("switchBitmapTitle");
        switchBitmapAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                switchBitmap();
            }
        };
        AlcMenuItem switchBitmapItem = new AlcMenuItem(switchBitmapAction);
        switchBitmapItem.setup(switchBitmapTitle, KeyEvent.VK_B);
        // Shortcut - Modifier v
        root.setHotKey(KeyEvent.VK_B, switchBitmapTitle, switchBitmapAction);
        switchMenu.add(switchBitmapItem);

        switchMenu.add(new JSeparator());

        // Switch Vector App
        switchVectorAppItem = new AlcMenuItem(getS("setVectorApp"));
        switchVectorAppItem.addActionListener(this);
        switchMenu.add(switchVectorAppItem);
        // Switch Bitmap App
        switchBitmapAppItem = new AlcMenuItem(getS("setBitmapApp"));
        switchBitmapAppItem.addActionListener(this);
        switchMenu.add(switchBitmapAppItem);
        this.add(switchMenu);

        //////////////////////////////////////////////////////////////
        // HELP MENU
        //////////////////////////////////////////////////////////////
        helpMenu = new AlcMenu(getS("helpTitle"));

        // Javahelp 
        // TODO - Implement Native help rather than Swing
        // OSX: http://developer.apple.com/qa/qa2001/qa1022.html
        //      http://informagen.com/JarBundler/HelpBook.html
        try {

            final URL url = AlcMain.class.getResource("help/help-hs.xml");
            //System.out.println(url);
            final HelpSet hs = new HelpSet(null, url);
            final HelpBroker hb = hs.createHelpBroker();

            AbstractAction helpAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    //System.out.println("CALLED");
                    hb.setDisplayed(true);
                //new CSH.DisplayHelpFromSource(hb);
                }
            };

            AlcMenuItem helpItem = new AlcMenuItem(helpAction);
            helpItem.setup(getS("alchemyHelpTitle"));
            helpMenu.add(helpItem);
            helpMenu.add(new JSeparator());

        } catch (Exception e) {
            System.err.println(e);
        }


        // Link to the Alchemy Website                
        AbstractAction wwwAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcUtil.openURL("http://al.chemy.org");
            }
        };
        AlcMenuItem wwwItem = new AlcMenuItem(wwwAction);
        wwwItem.setup(getS("alchemyWebsiteTitle"));
        helpMenu.add(wwwItem);
        // Link to the Alchemy forum           
        AbstractAction forumAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcUtil.openURL("http://al.chemy.org/forum/");
            }
        };
        AlcMenuItem forumItem = new AlcMenuItem(forumAction);
        forumItem.setup(getS("alchemyForumTitle"));
        helpMenu.add(forumItem);

        // About menuitem not included on a MAC
        if (AlcMain.PLATFORM != MACOSX) {
            helpMenu.add(new JSeparator());

            AbstractAction aboutAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    showAboutBox();
                }
            };
            AlcMenuItem aboutItem = new AlcMenuItem(aboutAction);
            aboutItem.setup(getS("aboutAlchemyTitle"));
            helpMenu.add(aboutItem);
        }

        this.add(helpMenu);
    }

    /*
    // Override the paint component to draw the gradient bg
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    //int panelWidth = getWidth();
    //GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(215, 215, 215), 0, this.getHeight(), new Color(207, 207, 207), true);
    if (g instanceof Graphics2D) {
    Graphics2D g2 = (Graphics2D) g;
    // Turn on text antialias - windows does not use it by default
    //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    //g2.setPaint(gradientPaint);
    g2.setPaint(AlcToolBar.toolBarAlphaHighlightColour);
    g2.fillRect(0, 0, root.getWindowSize().width, height);
    //g2.setPaint(AlcToolBar.toolBarHighlightColour);
    //g2.drawLine(0, 0, root.getWindowSize().width, 0);
    //g2.setPaint(AlcToolBar.toolBarLineColour);
    //g2.drawLine(0, height - 1, root.getWindowSize().width, height - 1);
    }
    }*/
    /** Get a string from the resource bundle */
    private String getS(String stringName) {
        return root.bundle.getString(stringName);
    }

    /** Return the height of the menubar */
    public int getHeight() {
        return height;
    }

    /** Print the canvas */
    private void print() {
        if (printer == null) {
            printer = PrinterJob.getPrinterJob();
        }
        if (page == null) {
            page = printer.defaultPage();
            page.setOrientation(PageFormat.LANDSCAPE);
        }

        printer.setPrintable(root.canvas, page);

        if (printer.printDialog()) {
            try {
                printer.print();
            } catch (Exception e) {
                System.err.println(e);
            }
        }

    }

    private void pageSetup() {
        if (printer == null) {
            printer = PrinterJob.getPrinterJob();
        }
        if (defaultPage == null) {
            //defaultPage = new PageFormat();
            defaultPage = printer.defaultPage();
            defaultPage.setOrientation(PageFormat.LANDSCAPE);
        } else {
            defaultPage = page;
        }
        //aset = new HashPrintRequestAttributeSet();
        page = printer.pageDialog(defaultPage);
    }

    /** Ask for a path and filename to export a PDF to */
    private void askExportPath() {

        //FileDialog fileDialog = new FileDialog(root, "Export Pdf", FileDialog.SAVE);
        //fileDialog.setVisible(true);
        //String fileString = fileDialog.getFile();

        final AlcFileChooser fc = new AlcFileChooser();
        fc.setDialogTitle("Export Pdf");
        // in response to a button click:
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == AlcFileChooser.APPROVE_OPTION) {

            // Make sure that something was selected
            //if (fileString != null) {
            //String directory = fileDialog.getDirectory();
            //File file = new File(directory, fileString);

            File file = fc.getSelectedFile();
            File fileWithExtension = AlcUtil.addFileExtension(file, "pdf");

            if (root.canvas.saveSinglePdf(fileWithExtension)) {
                System.out.println(fileWithExtension.toString());
            } else {
                System.out.println("Didn't save??? : " + fileWithExtension.toString());
            }

        }
    }

    private File askLocation(String title, boolean foldersOnly) {
        return askLocation(title, null, foldersOnly);
    }

    private File askLocation(String title, File defaultDir) {
        return askLocation(title, defaultDir, false);
    }

    /** Ask for a location with a file chooser. 
     *  @param  title               the name of the popup title
     *  @param  foldersOnly         to select only folders or not
     *  @param defaultDir           the default directory
     *  @return                     file/folder selected by the user
     */
    private File askLocation(String title, File defaultDir, boolean foldersOnly) {
        AlcFileChooser fc = null;

        if (defaultDir != null && defaultDir.exists()) {
            fc = new AlcFileChooser(defaultDir);
        } else {
            fc = new AlcFileChooser();
        }

        if (foldersOnly) {
            fc.setFileSelectionMode(AlcFileChooser.DIRECTORIES_ONLY);
        }

        fc.setDialogTitle(title);

        // in response to a button click:
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == AlcFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();

        } else {
            return null;
        }
    }

    private void switchVector() {
        // Make a temporary file, create a PDF, and then open it
        try {
            File tempVector = File.createTempFile("AlchemyTempVectorFile", ".pdf");
            tempVector.deleteOnExit();
            if (root.canvas.saveSinglePdf(tempVector)) {
                openSwitch(tempVector.toString(), root.prefs.getSwitchVectorApp());
            } else {
                System.out.println("Didn't save???");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    private void switchBitmap() {
        // Make a temporary file, create a PNG, and then open it
        try {
            File tempBitmap = File.createTempFile("AlchemyTempBitmapFile", ".png");
            tempBitmap.deleteOnExit();
            if (root.canvas.savePng(tempBitmap)) {
                openSwitch(tempBitmap.toString(), root.prefs.getSwitchBitmapApp());
            } else {
                System.out.println("Didn't save???");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    private void openSwitch(String file, String app) {
        File path = new File(app);
        try {
            String[] commands = null;
            switch (AlcMain.PLATFORM) {
                case MACOSX:
                    commands = new String[]{"open", "-a", path.getName(), file};
                    break;
                case WINDOWS:
                    commands = new String[]{"cmd", "/c", "start \"" + path.getName() + "\"", "\"Alchemy\"", file};
                    break;
            }
            if (commands != null) {
                Runtime.getRuntime().exec(commands);
            }
        //Runtime.getRuntime().exec("open "+file);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Show the About box.
     * 
     */
    public void showAboutBox() {
        final AlcAbout aboutWindow = new AlcAbout(root);
    }

    public void actionPerformed(ActionEvent e) {
        // TODO - Convert these to anonymous actons

        if (e.getSource() == exitItem) {
            root.exitAlchemy();

        } else if (e.getActionCommand().equals("Interval")) {
            AlcRadioButtonMenuItem source = (AlcRadioButtonMenuItem) e.getSource();
            root.session.setTimerInterval(source.getIndex());

        } else if (e.getSource() == defaultRecordingItem) {
            // Set the recording state reference
            root.prefs.setRecordingState(defaultRecordingItem.getState());

        } else if (e.getSource() == directoryItem) {

            File file = askLocation("Select Session Directory", true);
            if (file != null) {
                System.out.println(file.getPath());
                root.prefs.setSessionPath(file.getPath());
            }

        } else if (e.getSource() == autoClearItem) {
            // Set the recording state reference
            root.prefs.setAutoClear(autoClearItem.getState());

        } else if (e.getSource() == switchVectorAppItem) {
            File file = askLocation("Select Vector Application", platformAppDir);
            if (file != null) {
                System.out.println(file.toString());
                root.prefs.setSwitchVectorApp(file.toString());
            }

        } else if (e.getSource() == switchBitmapAppItem) {
            File file = askLocation("Select Bitmap Application", platformAppDir);
            if (file != null) {
                System.out.println(file.toString());
                root.prefs.setSwitchBitmapApp(file.toString());
            }

        }


    }
}
