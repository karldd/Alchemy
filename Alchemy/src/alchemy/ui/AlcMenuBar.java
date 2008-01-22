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
import javax.swing.*;

/** 
 * Menubar for Alchemy
 * Housing the usual file, print etc... commands 
 */
public class AlcMenuBar extends JMenuBar implements AlcConstants, ActionListener {

    private final AlcMain root;
    private final static int height = 27;
    private AlcMenu fileMenu,  sessionMenu,  viewMenu,  intervalMenu,  switchMenu,  helpMenu;
    private AlcMenuItem exitItem,  directoryItem,  switchVectorAppItem,  switchBitmapAppItem,  aboutItem;
    private AlcCheckBoxMenuItem defaultRecordingItem,  autoClearItem;
    private AlcRadioButtonMenuItem intervalItem;
    public Action exportAction,  printAction,  fullScreenAction,  recordingAction,  switchVectorAction,  switchBitmapAction;
    private File platformAppDir;
    //
    private PrinterJob printer = null;
    private PageFormat page = null;

    /** Creates a new instance of AlcMenuBar */
    public AlcMenuBar(final AlcMain root) {

        this.root = root;
        this.setBackground(AlcToolBar.toolBarAlphaHighlightColour);

        // Default applications directory depending on the platform
        switch (AlcMain.PLATFORM) {
            case MACOSX:
                platformAppDir = new File(File.separator + "Applications");
                break;
            case WINDOWS:
                platformAppDir = new File(File.separator + "Program Files");
                break;
        }

        //System.out.println(System.getProperty("user.dir"));

        //////////////////////////////////////////////////////////////
        // FILE MENU
        //////////////////////////////////////////////////////////////
        fileMenu = new AlcMenu("File");
        // New
        AlcMenuItem newItem = new AlcMenuItem(root.toolBar.clearAction);
        newItem.setup("New", KeyEvent.VK_N);
        fileMenu.add(newItem);

        fileMenu.add(new JSeparator());

        // Export
        String exportTitle = "Export...";
        exportAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                askExportPath();
            }
        };
        AlcMenuItem exportItem = new AlcMenuItem(exportAction);
        exportItem.setup(exportTitle, KeyEvent.VK_E);
        // Shortcut - Modifier e
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, MENU_SHORTCUT), exportTitle);
        root.canvas.getActionMap().put(exportTitle, exportAction);
        fileMenu.add(exportItem);

        fileMenu.add(new JSeparator());

        // Print
        String printTitle = "Print...";
        printAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                print();
            }
        };
        AlcMenuItem printItem = new AlcMenuItem(printTitle);
        printItem.setup(printTitle, KeyEvent.VK_P);
        // Shortcut - Modifier p
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, MENU_SHORTCUT), printTitle);
        root.canvas.getActionMap().put(printTitle, printAction);
        fileMenu.add(printItem);

        // Exit - not included on a MAC
        if (AlcMain.PLATFORM != MACOSX) {
            fileMenu.add(new JSeparator());

            exitItem = new AlcMenuItem("Exit");
            exitItem.addActionListener(this);
            fileMenu.add(exitItem);
        }

        this.add(fileMenu);

        //////////////////////////////////////////////////////////////
        // VIEW MENU
        //////////////////////////////////////////////////////////////
        viewMenu = new AlcMenu("View");
        // Fullscreen

        String fullScreenTitle = "Fullscreen";
        fullScreenAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                root.setFullscreen(!root.isFullscreen());
            }
        };
        AlcMenuItem fullScreenItem = new AlcMenuItem(fullScreenAction);
        fullScreenItem.setup(fullScreenTitle, KeyEvent.VK_F);
        // Shortcut - Modifier f
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, MENU_SHORTCUT), fullScreenTitle);
        root.canvas.getActionMap().put(fullScreenTitle, fullScreenAction);
        viewMenu.add(fullScreenItem);

        this.add(viewMenu);

        //////////////////////////////////////////////////////////////
        // SESSION MENU
        //////////////////////////////////////////////////////////////
        sessionMenu = new AlcMenu("Session");

        // Toggle Recording
        String recordingTitle = "Toggle Recording";
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
        // Shortcut - Modifier r
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT), recordingTitle);
        root.canvas.getActionMap().put(recordingTitle, recordingAction);

        recordingItem.setState(root.prefs.getRecordingState());
        if (root.prefs.getRecordingState()) {
            root.session.setRecording(true);
        }
        sessionMenu.add(recordingItem);

        sessionMenu.add(new JSeparator());

        // Default Recording
        defaultRecordingItem = new AlcCheckBoxMenuItem("Record on Startup", KeyEvent.VK_R);
        defaultRecordingItem.setState(root.prefs.getRecordingState());
        defaultRecordingItem.addActionListener(this);
        sessionMenu.add(defaultRecordingItem);
        // Interval submenu
        intervalMenu = new AlcMenu("Record Interval");
        // Set the opacity and colour of this to overide the defaults used for the top menus
        intervalMenu.setOpaque(true);
        intervalMenu.setBackground(AlcToolBar.toolBarHighlightColour);
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < root.session.recordingIntervalString.length; i++) {
            intervalItem = new AlcRadioButtonMenuItem(root.session.recordingInterval[i], root.session.recordingIntervalString[i]);
            // Set the default value to selected
            if (root.prefs.getRecordingInterval() == root.session.recordingInterval[i]) {
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
        // Auto Clear
        autoClearItem = new AlcCheckBoxMenuItem("Auto Clear Canvas");
        autoClearItem.setState(root.prefs.getAutoClear());
        autoClearItem.addActionListener(this);
        sessionMenu.add(autoClearItem);
        sessionMenu.add(new JSeparator());
        // Default Directory
        directoryItem = new AlcMenuItem("Set Session Directory...");
        directoryItem.addActionListener(this);
        sessionMenu.add(directoryItem);
        this.add(sessionMenu);


        //////////////////////////////////////////////////////////////
        // SWITCH MENU
        //////////////////////////////////////////////////////////////
        switchMenu = new AlcMenu("Switch");

        // Switch Vector
        String switchVectorTitle = "Switch Vector";
        switchVectorAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                switchVector();
            }
        };
        AlcMenuItem switchVectorItem = new AlcMenuItem(switchVectorAction);
        switchVectorItem.setup(switchVectorTitle, KeyEvent.VK_V);
        // Shortcut - Modifier v
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, MENU_SHORTCUT), switchVectorTitle);
        root.canvas.getActionMap().put(switchVectorTitle, switchVectorAction);
        switchMenu.add(switchVectorItem);

        // Switch Bitmaps
        String switchBitmapTitle = "Switch Bitmap";
        switchBitmapAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                switchBitmap();
            }
        };
        AlcMenuItem switchBitmapItem = new AlcMenuItem(switchBitmapAction);
        switchBitmapItem.setup(switchBitmapTitle, KeyEvent.VK_B);
        // Shortcut - Modifier v
        root.canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, MENU_SHORTCUT), switchBitmapTitle);
        root.canvas.getActionMap().put(switchBitmapTitle, switchBitmapAction);
        switchMenu.add(switchBitmapItem);

        switchMenu.add(new JSeparator());

        // Switch Vector App
        switchVectorAppItem = new AlcMenuItem("Set Vector Application...");
        switchVectorAppItem.addActionListener(this);
        switchMenu.add(switchVectorAppItem);
        // Switch Bitmap App
        switchBitmapAppItem = new AlcMenuItem("Set Bitmap Application...");
        switchBitmapAppItem.addActionListener(this);
        switchMenu.add(switchBitmapAppItem);
        this.add(switchMenu);

        //////////////////////////////////////////////////////////////
        // HELP MENU
        //////////////////////////////////////////////////////////////
        // About menuitem not included on a MAC
        if (AlcMain.PLATFORM != MACOSX) {
            helpMenu = new AlcMenu("Help");
            aboutItem = new AlcMenuItem("About");
            aboutItem.addActionListener(this);
            helpMenu.add(aboutItem);
            this.add(helpMenu);
        }
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
    /** Return the height of the menubar */
    public int getHeight() {
        return height;
    }

    /** Print the canvas */
    private void print() {
        if (printer == null) {
            printer = PrinterJob.getPrinterJob();
            page = printer.defaultPage();
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
                System.out.println("Didn't save???");
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
        
//        final Image image = AlcUtil.getImage("data/about.png", root);
//        final Dimension size = new Dimension(image.getWidth(root), image.getHeight(root));
//        final Window window = new Window(root) {
//
//            public void paint(Graphics g) {
//                g.drawImage(image, 0, 0, null);
//
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//
//                g.setFont(AlcToolBar.subToolBarFont);
//                g.setColor(Color.white);
//                g.drawString("COPYRIGHT © 2007-2008 KARL D.D. WILLIS", size.width / 2, 50);
//                g.drawString("ALPHA VERSION " + ALCHEMY_VERSION, size.width / 2, 65);
//            }
//        };
//        window.addMouseListener(new MouseAdapter() {
//
//            public void mousePressed(MouseEvent e) {
//                window.setVisible(false);
//                window.dispose();
//            }
//        });
//
//        window.setSize(size);
//        Point loc = AlcUtil.calculateCenter(window);
//        window.setBounds(loc.x, loc.y, size.width, size.height);
//        window.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
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

        } else if (e.getSource() == aboutItem) {
            //final AlcAbout about = new AlcAbout(root, "About Alchemy");
            showAboutBox();
        }


    }
}
