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
package org.alchemy.core;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

/** 
 * Menubar for Alchemy
 * Housing the usual file, print etc... commands 
 */
class AlcMenuBar extends JMenuBar implements AlcConstants {

    /** Reference to the windows help .chm file */
    private File tempHelp;
    private final static int height = 27;
    private File platformAppDir;
    private PrinterJob printer = null;
    private PageFormat page = null;
    private PageFormat defaultPage = null;

    /** Creates a new instance of AlcMenuBar */
    AlcMenuBar() {

        // Default applications directory depending on the platform
        switch (Alchemy.PLATFORM) {
            case MACOSX:
                platformAppDir = new File(File.separator + "Applications");
                this.setBorderPainted(false);
                break;
            case WINDOWS:
                platformAppDir = new File(File.separator + "Program Files");
                this.setBackground(AlcToolBar.toolBarHighlightColour);
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
                Alchemy.canvas.clear();
            }
        };
        AlcMenuItem newItem = new AlcMenuItem(newAction);
        newItem.setup(newTitle, KeyEvent.VK_N);
        // Shortcut - Modifier n
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_N, newTitle, newAction);
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
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_E, exportTitle, exportAction);
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
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_P, printTitle, printAction);
        fileMenu.add(printItem);


        // Exit - not included on a MAC
        if (Alchemy.PLATFORM != MACOSX) {
            fileMenu.add(new JSeparator());

            AbstractAction exitAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    Alchemy.window.exitAlchemy();
                }
            };

            AlcMenuItem exitItem = new AlcMenuItem(exitAction);
            exitItem.setup(getS("exitTitle"));
            fileMenu.add(exitItem);
        }

        this.add(fileMenu);

        //////////////////////////////////////////////////////////////
        // EDIT MENU
        //////////////////////////////////////////////////////////////

        AlcMenu editMenu = new AlcMenu(getS("editTitle"));

        // Copy
        String copyTitle = getS("copyTitle");
        AbstractAction copyAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                copy();
            }
        };
        AlcMenuItem copyItem = new AlcMenuItem(copyAction);
        copyItem.setup(copyTitle, KeyEvent.VK_C);
        // Shortcut - Modifier c
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_C, copyTitle, copyAction);
        editMenu.add(copyItem);

        this.add(editMenu);


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
                Alchemy.window.setFullscreen(!Alchemy.window.isFullscreen());
            }
        };

        fullScreenItem.setAction(fullScreenAction);
        fullScreenItem.setup(fullScreenTitle, KeyEvent.VK_F);
        // Shortcut - Modifier f
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_F, fullScreenTitle, fullScreenAction);
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
                Alchemy.session.manualSavePage();
            }
        };
        AlcMenuItem savePageItem = new AlcMenuItem(savePageAction);
        savePageItem.setup(savePageTitle, KeyEvent.VK_S);
        // Shortcut - Modifier s
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_S, savePageTitle, savePageAction);
        sessionMenu.add(savePageItem);

        // Save and clear PDF page
        String saveClearTitle = getS("saveClearTitle");
        AbstractAction saveClearPageAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.session.manualSaveClearPage();
            }
        };
        AlcMenuItem saveClearPageItem = new AlcMenuItem(saveClearPageAction);
        saveClearPageItem.setup(saveClearTitle, KeyEvent.VK_D);
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_D, saveClearTitle, saveClearPageAction);
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
                Alchemy.session.setRecording(recordingItem.getState());
                System.out.println("STATE: " + recordingItem.getState());

            }
        };
        recordingItem.setAction(recordingAction);
        recordingItem.setup(recordingTitle, KeyEvent.VK_R);

        // Shortcut - Modifier r
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_R, recordingTitle, recordingAction);

        recordingItem.setState(Alchemy.preferences.getRecordingState());
        if (Alchemy.preferences.getRecordingState()) {
            Alchemy.session.setRecording(true);
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
            if (Alchemy.preferences.getRecordingInterval() == recordingInterval[i]) {
                intervalItem.setSelected(true);
            }
            intervalItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    AlcRadioButtonMenuItem source = (AlcRadioButtonMenuItem) e.getSource();
                    Alchemy.session.setTimerInterval(source.getIndex());

                }
            });
            group.add(intervalItem);
            intervalMenu.add(intervalItem);
        }
        intervalMenu.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        sessionMenu.add(intervalMenu);

        //sessionMenu.add(new JSeparator());

        // Auto Clear
        AbstractAction autoClearAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                Alchemy.preferences.setAutoClear(source.getState());
            }
        };
        AlcCheckBoxMenuItem autoClearItem = new AlcCheckBoxMenuItem(autoClearAction);
        autoClearItem.setup(getS("autoClearTitle"));
        autoClearItem.setState(Alchemy.preferences.getAutoClear());
        sessionMenu.add(autoClearItem);

        // Default Recording
        AbstractAction defaultRecordingAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                Alchemy.preferences.setRecordingState(source.getState());
            }
        };
        AlcCheckBoxMenuItem defaultRecordingItem = new AlcCheckBoxMenuItem(defaultRecordingAction);
        defaultRecordingItem.setup(getS("recordStartUpTitle"));
        defaultRecordingItem.setState(Alchemy.preferences.getRecordingState());
        sessionMenu.add(defaultRecordingItem);

        sessionMenu.add(new JSeparator());

        // Restart Session
        String restartTitle = getS("restartTitle");
        AbstractAction restartAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.session.restartSession();
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
                    Alchemy.preferences.setSessionPath(file.getPath());
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
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_V, switchVectorTitle, switchVectorAction);
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
        Alchemy.shortcuts.setShortcut(KeyEvent.VK_B, switchBitmapTitle, switchBitmapAction);
        switchMenu.add(switchBitmapItem);

        switchMenu.add(new JSeparator());

        // Switch Vector App
        AbstractAction switchVectorAppAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = askLocation("Select Vector Application", platformAppDir);
                if (file != null) {
                    System.out.println(file.toString());
                    Alchemy.preferences.setSwitchVectorApp(file.toString());
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
                    Alchemy.preferences.setSwitchBitmapApp(file.toString());
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
                Alchemy.canvas.setSmoothing(source.getState());
                Alchemy.canvas.redraw();
            }
        };
        AlcCheckBoxMenuItem smoothingItem = new AlcCheckBoxMenuItem(smoothingAction);
        smoothingItem.setSelected(Alchemy.canvas.getSmoothing());
        smoothingItem.setup(getS("smoothingTitle"));
        settingsMenu.add(smoothingItem);

        // Background Colour
        AbstractAction bgColourAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                // Action to change the colour
                ActionListener colorAction = new ActionListener() {

                    public void actionPerformed(ActionEvent event) {
                        Alchemy.canvas.setBgColour(Alchemy.colourChooser.getColor());
                        Alchemy.canvas.redraw();
                    }
                };

                // Set the current colour to the bg
                Alchemy.colourChooser.setColor(Alchemy.canvas.getBgColour());
                // Dialog to hold the colour chooser
                JDialog dialog = JColorChooser.createDialog(Alchemy.window, getS("bgColourDialogTitle"), true, Alchemy.colourChooser, colorAction, null);
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

        AbstractAction helpAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {

                switch (Alchemy.PLATFORM) {
                    case MACOSX:
                        HelpHook.showHelp();
                        break;
                    case WINDOWS:

                        if (tempHelp == null) {

                            // English is the default
                            String helpFile = "AlchemyHelp.chm";
                            String helpFile_ja = "AlchemyHelp_ja.chm";
                            boolean useJapanese = false;

                            String locale = LOCALE.getLanguage().toLowerCase();
                            System.out.println(locale);
                            if (locale.startsWith("ja")) {
                                useJapanese = true;
                                System.out.println("Japanese Help");
                            } else {
                                System.out.println("English Help");
                            }

                            InputStream helpStream = null;

                            if (useJapanese) {
                                System.out.println("Loading Japanese Help");
                                helpStream = Alchemy.class.getResourceAsStream("../data/" + helpFile_ja);
                            }
                            // If set to English or Japanese help is not found
                            if (helpStream == null) {
                                System.out.println("Loading English Help");
                                helpStream = Alchemy.class.getResourceAsStream("../data/" + helpFile);
                            }
                            // Create temp file.
                            tempHelp = new File(TEMP_DIR, helpFile);

                            // Delete temp file when program exits.
                            tempHelp.deleteOnExit();


                            try {
                                AlcUtil.copyFile(helpStream, tempHelp);

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }

                        }

                        if (tempHelp.exists()) {
                            try {
                                Runtime.getRuntime().exec("hh.exe " + tempHelp.getAbsolutePath());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            System.err.println("ERROR - Help could not be copied to the temp dir: " + TEMP_DIR);
                        }


                        break;
                    default:
                        System.out.println("Alchemy Help on Linux is not currently supported");
                        break;
                    }
            }
            };

        AlcMenuItem helpItem = new AlcMenuItem(helpAction);
        helpItem.setup(getS("alchemyHelpTitle"));
        helpMenu.add(helpItem);
        helpMenu.add(new JSeparator());


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
        if (Alchemy.PLATFORM != MACOSX) {
            helpMenu.add(new JSeparator());

            AbstractAction aboutAction = new AbstractAction() {

                public void actionPerformed(ActionEvent e) {
                    Alchemy.window.showAbout();
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
        return Alchemy.bundle.getString(stringName);
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

        printer.setPrintable(Alchemy.canvas, page);

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

            if (Alchemy.canvas.saveSinglePdf(fileWithExtension)) {
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

    /** Copy the canvas to the clipboard as an image */
    private void copy() {
        // TODO - Implement Vector clipboard copy function
        // http://www.java2s.com/Code/Java/Swing-JFC/MimeClipboardTest.htm

        // A bug on mac with older versions of java can scramble the clipboard
        // fixed in Java 1.5 Release 3 (4238470)
        AlcUtil.setClipboard(new AlcImageTransferable(Alchemy.canvas.getBufferedImage()), Alchemy.window);
    }

    /** Make a temporary file, create a PDF, and then open it */
    private void switchVector() {
        try {
            File tempVector = File.createTempFile("AlchemyTempVectorFile", ".pdf");
            tempVector.deleteOnExit();
            if (Alchemy.canvas.saveSinglePdf(tempVector)) {
                openSwitch(tempVector.toString(), Alchemy.preferences.getSwitchVectorApp());
            } else {
                System.out.println("Didn't save???");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    /** Make a temporary file, create a PNG, and then open it */
    private void switchBitmap() {
        try {
            File tempBitmap = File.createTempFile("AlchemyTempBitmapFile", ".png");
            tempBitmap.deleteOnExit();
            if (Alchemy.canvas.savePng(tempBitmap)) {
                openSwitch(tempBitmap.toString(), Alchemy.preferences.getSwitchBitmapApp());
            } else {
                System.out.println("Didn't save???");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }

    }

    /** Open the appropriate application with the temp file and 'switch' */
    private void openSwitch(String file, String app) {
        File path = new File(app);
        try {
            String[] commands = null;
            switch (Alchemy.PLATFORM) {
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

    protected void paintComponent(Graphics g) {
        if (Alchemy.PLATFORM != MACOSX) {
            // Make sure this is not painted on Mac
            // The mac menubar is used instead
            super.paintComponent(g);
        }
    }
}
