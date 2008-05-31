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
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

/** 
 * Menubar for Alchemy
 * Housing the usual file, print etc... commands 
 */
class AlcMenuBar extends JMenuBar implements AlcConstants {

    private final static int height = 27;
    private File platformAppDir;
    private PrinterJob printer = null;
    private PageFormat page = null;
    private PageFormat defaultPage = null;
    /** Session stuff global so it can be enabled/disabled */
    private AlcMenuItem nextPageItem,  previousPageItem,  unloadSessionItem;
    private AlcCheckBoxMenuItem linkSessionItem;

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
                break;
            case LINUX:
                platformAppDir = new File(File.separator + "usr" + File.separator + "bin");
                this.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                break;
        }
        this.setBackground(AlcToolBar.toolBarHighlightColour);

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
        // Shortcut - Modifier n
        int newKey = Alchemy.shortcuts.setShortcut(newItem, KeyEvent.VK_N, "newTitle", newAction, MODIFIER_KEY);
        newItem.setup(newTitle, newKey);
        fileMenu.add(newItem);

        fileMenu.add(new JSeparator());

        // Export
        String exportTitle = getS("exportTitle");
        AbstractAction exportAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                export();
            }
        };
        AlcMenuItem exportItem = new AlcMenuItem(exportAction);
        // Shortcut - Modifier e
        int exportKey = Alchemy.shortcuts.setShortcut(exportItem, KeyEvent.VK_E, "exportTitle", exportAction, MODIFIER_KEY);
        exportItem.setup(exportTitle, exportKey);

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
        // Shortcut - Modifier p
        int printKey = Alchemy.shortcuts.setShortcut(printItem, KeyEvent.VK_P, "printTitle", printAction, MODIFIER_KEY);
        printItem.setup(printTitle, printKey);
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
        // Shortcut - Modifier c
        int copyKey = Alchemy.shortcuts.setShortcut(copyItem, KeyEvent.VK_C, "copyTitle", copyAction, MODIFIER_KEY);
        copyItem.setup(copyTitle, copyKey);
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
                String source = e.getSource().getClass().getName();
                if (!source.equals("org.alchemy.core.AlcCheckBoxMenuItem")) {
                    fullScreenItem.setState(!fullScreenItem.getState());
                }
                Alchemy.window.setFullscreen(!Alchemy.window.isFullscreen());
            }
        };

        fullScreenItem.setAction(fullScreenAction);
        // Shortcut - Modifier f
        int fullScreenKey = Alchemy.shortcuts.setShortcut(fullScreenItem, KeyEvent.VK_F, "fullScreenTitle", fullScreenAction, MODIFIER_KEY);
        fullScreenItem.setup(fullScreenTitle, fullScreenKey);
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
        // Shortcut - Modifier s
        int savePageKey = Alchemy.shortcuts.setShortcut(savePageItem, KeyEvent.VK_S, "savePageTitle", savePageAction, MODIFIER_KEY);
        savePageItem.setup(savePageTitle, savePageKey);


        sessionMenu.add(savePageItem);

        // Save and clear PDF page
        String saveClearTitle = getS("saveClearTitle");
        AbstractAction saveClearPageAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.session.manualSaveClearPage();
            }
        };
        AlcMenuItem saveClearPageItem = new AlcMenuItem(saveClearPageAction);
        int saveClearKey = Alchemy.shortcuts.setShortcut(saveClearPageItem, KeyEvent.VK_D, "saveClearTitle", saveClearPageAction, MODIFIER_KEY);
        saveClearPageItem.setup(saveClearTitle, saveClearKey);
        sessionMenu.add(saveClearPageItem);

        sessionMenu.add(new JSeparator());

        // Recording
        String recordingTitle = getS("recordingTitle");
        final AlcCheckBoxMenuItem recordingItem = new AlcCheckBoxMenuItem();
        AbstractAction recordingAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                // If the source is from the key then we need to change the state of the menu item as well
                String source = e.getSource().getClass().getName();
                if (!source.equals("org.alchemy.core.AlcCheckBoxMenuItem")) {
                    //if (e.getActionCommand().equals("r")) {
                    recordingItem.setState(!recordingItem.getState());
                }
                Alchemy.session.setRecording(recordingItem.getState());
                System.out.println("STATE: " + recordingItem.getState());

            }
        };
        recordingItem.setAction(recordingAction);
        // Shortcut - Modifier r
        int recordingKey = Alchemy.shortcuts.setShortcut(recordingItem, KeyEvent.VK_R, "recordingTitle", recordingAction, MODIFIER_KEY);
        recordingItem.setup(recordingTitle, recordingKey);

        recordingItem.setState(Alchemy.preferences.sessionRecordingState);
        if (Alchemy.preferences.sessionRecordingState) {
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
            if (Alchemy.preferences.sessionRecordingInterval == recordingInterval[i]) {
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
                Alchemy.preferences.sessionAutoClear = source.getState();
            }
        };
        AlcCheckBoxMenuItem autoClearItem = new AlcCheckBoxMenuItem(autoClearAction);
        autoClearItem.setup(getS("autoClearTitle"));
        autoClearItem.setState(Alchemy.preferences.sessionAutoClear);
        sessionMenu.add(autoClearItem);

        // Default Recording
        AbstractAction defaultRecordingAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                Alchemy.preferences.sessionRecordingState = source.getState();
            }
        };
        AlcCheckBoxMenuItem defaultRecordingItem = new AlcCheckBoxMenuItem(defaultRecordingAction);
        defaultRecordingItem.setup(getS("recordStartUpTitle"));
        defaultRecordingItem.setState(Alchemy.preferences.sessionRecordingState);
        sessionMenu.add(defaultRecordingItem);

        sessionMenu.add(new JSeparator());

        // Load Session PDF
        final String loadSessionTitle = getS("loadSessionTitle");
        AbstractAction loadSessionAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = AlcUtil.showFileChooser(new File(DESKTOP_DIR), false);
                if (file != null && file.exists()) {
                    boolean loaded = Alchemy.session.loadSessionFile(file);
                    nextPageItem.setEnabled(loaded);
                    previousPageItem.setEnabled(loaded);
                    unloadSessionItem.setEnabled(loaded);
                    linkSessionItem.setEnabled(loaded);
                }
            }
        };
        AlcMenuItem loadSessionItem = new AlcMenuItem(loadSessionAction);
        loadSessionItem.setup(loadSessionTitle);
        sessionMenu.add(loadSessionItem);

        // Next Page
        final String nextPageTitle = getS("nextPageTitle");
        AbstractAction nextPageAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.session.nextPage();
            }
        };
        nextPageItem = new AlcMenuItem(nextPageAction);
        int nextPageKey = Alchemy.shortcuts.setShortcut(nextPageItem, KeyEvent.VK_RIGHT, "nextPageTitle", nextPageAction, MODIFIER_KEY);
        nextPageItem.setup(nextPageTitle, nextPageKey);
        sessionMenu.add(nextPageItem);
        nextPageItem.setEnabled(false);

        // Previous Page
        final String previousPageTitle = getS("previousPageTitle");
        AbstractAction previousPageAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.session.previousPage();
            }
        };
        previousPageItem = new AlcMenuItem(previousPageAction);
        int previousPageKey = Alchemy.shortcuts.setShortcut(previousPageItem, KeyEvent.VK_LEFT, "previousPageTitle", previousPageAction, MODIFIER_KEY);
        previousPageItem.setup(previousPageTitle, previousPageKey);
        sessionMenu.add(previousPageItem);
        previousPageItem.setEnabled(false);

        // Unload Session PDF
        final String unloadSessionTitle = getS("unloadSessionTitle");
        AbstractAction unloadSessionAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.session.unloadSessionFile();
                nextPageItem.setEnabled(false);
                previousPageItem.setEnabled(false);
                unloadSessionItem.setEnabled(false);
                linkSessionItem.setEnabled(false);
            }
        };
        unloadSessionItem = new AlcMenuItem(unloadSessionAction);
        unloadSessionItem.setup(unloadSessionTitle);
        sessionMenu.add(unloadSessionItem);
        unloadSessionItem.setEnabled(false);


        // Link Session
        String linkSessionTitle = getS("linkSessionTitle");
        linkSessionItem = new AlcCheckBoxMenuItem();
        AbstractAction linkSessionAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                Alchemy.preferences.sessionLink = source.getState();
            }
        };

        linkSessionItem.setAction(linkSessionAction);
        linkSessionItem.setup(linkSessionTitle);
        linkSessionItem.setSelected(Alchemy.preferences.sessionLink);
        sessionMenu.add(linkSessionItem);
        linkSessionItem.setEnabled(false);

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
        final String setSessionDirTitle = getS("setSessionDirTitle");
        AbstractAction directoryAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = AlcUtil.showFileChooser(true);
                if (file != null) {
                    System.out.println(file.getPath());
                    Alchemy.preferences.sessionPath = file.getPath();
                }
            }
        };
        AlcMenuItem directoryItem = new AlcMenuItem(directoryAction);
        directoryItem.setup(setSessionDirTitle);
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
        // Shortcut - Modifier v
        int vectorKey = Alchemy.shortcuts.setShortcut(switchVectorItem, KeyEvent.VK_V, "switchVectorTitle", switchVectorAction, MODIFIER_KEY);
        switchVectorItem.setup(switchVectorTitle, vectorKey);
        switchMenu.add(switchVectorItem);

        // Switch Bitmaps
        String switchBitmapTitle = getS("switchBitmapTitle");
        AbstractAction switchBitmapAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                switchBitmap();
            }
        };
        AlcMenuItem switchBitmapItem = new AlcMenuItem(switchBitmapAction);
        // Shortcut - Modifier v
        int bitmapKey = Alchemy.shortcuts.setShortcut(switchBitmapItem, KeyEvent.VK_B, "switchBitmapTitle", switchBitmapAction, MODIFIER_KEY);
        switchBitmapItem.setup(switchBitmapTitle, bitmapKey);
        switchMenu.add(switchBitmapItem);

        switchMenu.add(new JSeparator());

        // Switch Vector App
        final String setVectorApp = getS("setVectorApp");
        AbstractAction switchVectorAppAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = AlcUtil.showFileChooser(platformAppDir);
                if (file != null) {
                    System.out.println(file.toString());
                    Alchemy.preferences.switchVectorApp = file.toString();
                }
            }
        };
        AlcMenuItem switchVectorAppItem = new AlcMenuItem(switchVectorAppAction);
        switchVectorAppItem.setup(setVectorApp);
        switchMenu.add(switchVectorAppItem);


        // Switch Bitmap App
        final String setBitmapApp = getS("setBitmapApp");
        AbstractAction switchBitmapAppAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                File file = AlcUtil.showFileChooser(platformAppDir);
                if (file != null) {
                    System.out.println(file.toString());
                    Alchemy.preferences.switchBitmapApp = file.toString();
                }
            }
        };
        AlcMenuItem switchBitmapAppItem = new AlcMenuItem(switchBitmapAppAction);
        switchBitmapAppItem.setup(setBitmapApp);
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
        smoothingItem.setSelected(Alchemy.preferences.smoothing);
        smoothingItem.setup(getS("smoothingTitle"));
        settingsMenu.add(smoothingItem);

        // Line Smoothing
        AbstractAction lineSmoothingAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                AlcCheckBoxMenuItem source = (AlcCheckBoxMenuItem) e.getSource();
                AlcShape.lineSmoothing = source.getState();

            }
        };
        AlcCheckBoxMenuItem lineSmoothingItem = new AlcCheckBoxMenuItem(lineSmoothingAction);
        lineSmoothingItem.setSelected(Alchemy.preferences.lineSmoothing);
        AlcShape.lineSmoothing = Alchemy.preferences.lineSmoothing;
        lineSmoothingItem.setup(getS("lineSmoothingTitle"));
        settingsMenu.add(lineSmoothingItem);

        settingsMenu.add(new JSeparator());

        // Keyboard Shortcuts
        AbstractAction keyboardShortcutsAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Alchemy.shortcuts.showWindow();
            }
        };
        AlcMenuItem keyboardShortcutsItem = new AlcMenuItem(keyboardShortcutsAction);
        keyboardShortcutsItem.setup(getS("keyboardShortcutsTitle"));
        settingsMenu.add(keyboardShortcutsItem);

        this.add(settingsMenu);

        //////////////////////////////////////////////////////////////  
        // HELP MENU
        //////////////////////////////////////////////////////////////
        AlcMenu helpMenu = new AlcMenu(getS("helpTitle"));

        AbstractAction helpAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {

                File manual = new File("Alchemy.pdf");
                if (manual.exists()) {
                    AlcUtil.openPDF(manual);
                } else {
                    System.err.println("Error locating the Alchemy manual");

                    // Try now to open the Alchemy documentation website instead
                    // Check which site to send them to
                    String locale = LOCALE.getLanguage().toLowerCase();
                    if (locale.startsWith("ja")) {
                        AlcUtil.openURL("http://al.chemy.org/ja/documentation/");
                    } else {
                        AlcUtil.openURL("http://al.chemy.org/documentation/");
                    }
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
                // Check which site to send them to
                String locale = LOCALE.getLanguage().toLowerCase();
                if (locale.startsWith("ja")) {
                    AlcUtil.openURL("http://al.chemy.org/ja/");
                } else {
                    AlcUtil.openURL("http://al.chemy.org/");
                }
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
    @Override
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
    private void export() {

        //FileDialog fileDialog = new FileDialog(root, "Export Pdf", FileDialog.SAVE);
        //fileDialog.setVisible(true);
        //String fileString = fileDialog.getFile();


        final AlcFileChooser fc = new AlcFileChooser(DESKTOP_DIR);
        fc.setDialogTitle(Alchemy.bundle.getString("exportFileTitle"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new ExportFileFilter("PNG - Transparent"));
        fc.setFileFilter(new ExportFileFilter("PNG"));
        fc.setFileFilter(new ExportFileFilter("PDF"));
        fc.setSelectedFile(new File(Alchemy.bundle.getString("defaultFileName")));

        // in response to a button click:
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == AlcFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();
            String format = fc.getFileFilter().getDescription();

            if (format.equals("PDF")) {
                Alchemy.canvas.saveSinglePdf(file);
            } else if (format.equals("PNG")) {
                Alchemy.canvas.savePng(file);
            } else if (format.equals("PNG - Transparent")) {
                Alchemy.canvas.savePng(file, true);
            }
        }
    }

    /** Copy the canvas to the clipboard as an image */
    private void copy() {
        // TODO - Implement Vector clipboard copy function
        // http://www.java2s.com/Code/Java/Swing-JFC/MimeClipboardTest.htm

        // A bug on mac with older versions of java can scramble the clipboard
        // fixed in Java 1.5 Release 3 (4238470)
        Alchemy.canvas.setGuide(false);
        AlcUtil.setClipboard(new AlcImageTransferable(Alchemy.canvas.renderCanvas(true)), Alchemy.window);
        Alchemy.canvas.setGuide(true);
    }

    /** Make a temporary file, create a PDF, and then open it */
    private void switchVector() {
        try {
            File tempVector = File.createTempFile("AlchemyTempVectorFile", ".pdf");
            tempVector.deleteOnExit();
            if (Alchemy.canvas.saveSinglePdf(tempVector)) {
                openSwitch(tempVector.toString(), Alchemy.preferences.switchVectorApp);
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
                openSwitch(tempBitmap.toString(), Alchemy.preferences.switchBitmapApp);
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
                case LINUX:
                    commands = new String[]{path.getName(), file};
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

    @Override
    protected void paintComponent(Graphics g) {
        if (Alchemy.PLATFORM != MACOSX) {
            // Make sure this is not painted on Mac
            // The mac menubar is used instead
            super.paintComponent(g);
        }
    }
}

class ExportFileFilter extends javax.swing.filechooser.FileFilter {

    private final String format;

    ExportFileFilter(String format) {
        this.format = format;
    }

    public boolean accept(File f) {
        return true;
    }

    public String getDescription() {
        return format;
    }
}
