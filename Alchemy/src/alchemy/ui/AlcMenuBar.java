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
import javax.swing.*;
import javax.help.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/** 
 * Menubar for Alchemy
 * Housing the usual file, print etc... commands 
 */
public class AlcMenuBar extends JMenuBar implements AlcConstants {

    private final AlcMain root;
    private final static int height = 27;
    private File platformAppDir;
    private PrinterJob printer = null;
    private PageFormat page = null;
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

        // Recording interval array in milliseconds
        int[] recordingInterval = {5000, 15000, 30000, 60000, 120000, 300000, 600000};
        // Recording interval array in readable form
        String[] recordingIntervalString = new String[recordingInterval.length];
        // Initialise the array of recording intervals from the bundle
        for (int i = 0; i < recordingIntervalString.length; i++) {
            recordingIntervalString[i] = getS("interval" + recordingInterval[i]);
        }

        //////////////////////////////////////////////////////////////
        // FILE MENU
        //////////////////////////////////////////////////////////////
        AlcMenu fileMenu = new AlcMenu(getS("fileTitle"));

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
        AbstractAction exportAction = new AbstractAction() {

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
        AbstractAction printAction = new AbstractAction() {

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

            AbstractAction exitAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    root.exitAlchemy();
                }
            };

            AlcMenuItem exitItem = new AlcMenuItem(exitAction);
            exitItem.setup(getS("exitTitle"));
            fileMenu.add(exitItem);
        }

        this.add(fileMenu);

        //////////////////////////////////////////////////////////////
        // VIEW MENU
        //////////////////////////////////////////////////////////////
        AlcMenu viewMenu = new AlcMenu(getS("viewTitle"));
        // Fullscreen

        String fullScreenTitle = getS("fullScreenTitle");
        final AlcCheckBoxMenuItem fullScreenItem = new AlcCheckBoxMenuItem();
        AbstractAction fullScreenAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("f")) {
                    fullScreenItem.setState(!fullScreenItem.getState());
                }
                root.setFullscreen(!root.isFullscreen());
            }
        };

        fullScreenItem.setAction(fullScreenAction);
        fullScreenItem.setup(fullScreenTitle, KeyEvent.VK_F);
        // Shortcut - Modifier f
        root.setHotKey(KeyEvent.VK_F, fullScreenTitle, fullScreenAction);
        viewMenu.add(fullScreenItem);

        this.add(viewMenu);

        //////////////////////////////////////////////////////////////
        // SESSION MENU
        //////////////////////////////////////////////////////////////
        AlcMenu sessionMenu = new AlcMenu(getS("sessionTitle"));

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
        AbstractAction recordingAction = new AbstractAction() {

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

        // Shortcut - Modifier r
        root.setHotKey(KeyEvent.VK_R, recordingTitle, recordingAction);

        recordingItem.setState(root.prefs.getRecordingState());
        if (root.prefs.getRecordingState()) {
            root.session.setRecording(true);
        }
        sessionMenu.add(recordingItem);

        // Interval submenu
        AlcMenu intervalMenu = new AlcMenu(getS("recordIntervalTitle"));
        // Set the opacity and colour of this to overide the defaults used for the top menus
        intervalMenu.setOpaque(true);
        intervalMenu.setBackground(AlcToolBar.toolBarHighlightColour);
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < recordingIntervalString.length; i++) {
            AlcRadioButtonMenuItem intervalItem = new AlcRadioButtonMenuItem(recordingInterval[i], recordingIntervalString[i]);
            // Set the default value to selected
            if (root.prefs.getRecordingInterval() == recordingInterval[i]) {
                intervalItem.setSelected(true);
            }
            intervalItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    AlcRadioButtonMenuItem source = (AlcRadioButtonMenuItem) e.getSource();
                    root.session.setTimerInterval(source.getIndex());


                }
            });
            group.add(intervalItem);
            intervalMenu.add(intervalItem);
        }
        intervalMenu.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        sessionMenu.add(intervalMenu);

        sessionMenu.add(new JSeparator());

        // Auto Clear
        AbstractAction autoClearAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                root.prefs.setAutoClear(source.getState());
            }
        };
        AlcCheckBoxMenuItem autoClearItem = new AlcCheckBoxMenuItem(autoClearAction);
        autoClearItem.setup(getS("autoClearTitle"));
        autoClearItem.setState(root.prefs.getAutoClear());
        sessionMenu.add(autoClearItem);

        // Default Recording
        AbstractAction defaultRecordingAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                root.prefs.setRecordingState(source.getState());
            }
        };
        AlcCheckBoxMenuItem defaultRecordingItem = new AlcCheckBoxMenuItem(defaultRecordingAction);
        defaultRecordingItem.setup(getS("recordStartUpTitle"));
        defaultRecordingItem.setState(root.prefs.getRecordingState());
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
        AbstractAction directoryAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = askLocation("Select Session Directory", true);
                if (file != null) {
                    System.out.println(file.getPath());
                    root.prefs.setSessionPath(file.getPath());
                }
            }
        };
        AlcMenuItem directoryItem = new AlcMenuItem(directoryAction);
        directoryItem.setup(getS("setSessionDirTitle"));
        sessionMenu.add(directoryItem);
        this.add(sessionMenu);


        //////////////////////////////////////////////////////////////
        // SWITCH MENU
        //////////////////////////////////////////////////////////////
        AlcMenu switchMenu = new AlcMenu(getS("switchTitle"));

        // Switch Vector
        String switchVectorTitle = getS("switchVectorTitle");
        AbstractAction switchVectorAction = new AbstractAction() {

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
        AbstractAction switchBitmapAction = new AbstractAction() {

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
        AbstractAction switchVectorAppAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = askLocation("Select Vector Application", platformAppDir);
                if (file != null) {
                    System.out.println(file.toString());
                    root.prefs.setSwitchVectorApp(file.toString());
                }
            }
        };
        AlcMenuItem switchVectorAppItem = new AlcMenuItem(switchVectorAppAction);
        switchVectorAppItem.setup(getS("setVectorApp"));
        switchMenu.add(switchVectorAppItem);


        // Switch Bitmap App
        AbstractAction switchBitmapAppAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = askLocation("Select Bitmap Application", platformAppDir);
                if (file != null) {
                    System.out.println(file.toString());
                    root.prefs.setSwitchBitmapApp(file.toString());
                }
            }
        };
        AlcMenuItem switchBitmapAppItem = new AlcMenuItem(switchBitmapAppAction);
        switchBitmapAppItem.setup(getS("setBitmapApp"));
        switchMenu.add(switchBitmapAppItem);
        this.add(switchMenu);

        //////////////////////////////////////////////////////////////
        // SETTINGS MENU
        //////////////////////////////////////////////////////////////
        AlcMenu settingsMenu = new AlcMenu(getS("settingsTitle"));

        // Smoothing
        AbstractAction smoothingAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                root.canvas.setSmoothing(source.getState());
                root.canvas.redraw();
            }
        };
        AlcCheckBoxMenuItem smoothingItem = new AlcCheckBoxMenuItem(smoothingAction);
        smoothingItem.setSelected(root.canvas.getSmoothing());
        smoothingItem.setup(getS("smoothingTitle"));
        settingsMenu.add(smoothingItem);

        // Background Colour
        AbstractAction bgColourAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {

                // Swing Colour Chooser
                final JColorChooser cc = new JColorChooser(Color.WHITE);
                //cc.setBackground(AlcToolBar.toolBarBgColour);
                // Just want to show the HSB panel
                AbstractColorChooserPanel[] panels = cc.getChooserPanels();
                // Get the panels and search for the HSB one
                for (int i = 0; i < panels.length; i++) {
                    String name = panels[i].getClass().getName();
                    if (name.contains("HSB")) {
                        // Add the HSB panel, replacing the others
                        AbstractColorChooserPanel[] hsb = {panels[i]};
                        cc.setChooserPanels(hsb);
                        break;
                    }
                }

                cc.setPreviewPanel(new JPanel());
                // Action to change the colour
                ActionListener colorAction = new ActionListener() {

                    public void actionPerformed(ActionEvent event) {
                        root.canvas.setBgColour(cc.getColor());
                        root.canvas.redraw();
                    }
                };
                // Dialog to hold the colour chooser
                JDialog dialog = JColorChooser.createDialog(root, getS("bgColourDialogTitle"), true, cc, colorAction, null);
                dialog.setBackground(AlcToolBar.toolBarBgColour);
                dialog.setResizable(false);
                dialog.setVisible(true);

            }
        };
        AlcMenuItem bgColourItem = new AlcMenuItem(bgColourAction);
        bgColourItem.setup(getS("bgColourTitle"));
        settingsMenu.add(bgColourItem);

        this.add(settingsMenu);

        //////////////////////////////////////////////////////////////
        // HELP MENU
        //////////////////////////////////////////////////////////////
        AlcMenu helpMenu = new AlcMenu(getS("helpTitle"));

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
}
