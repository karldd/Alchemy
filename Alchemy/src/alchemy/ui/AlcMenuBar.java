/**
 * AlcMenuBar.java
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
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class AlcMenuBar extends JMenuBar implements AlcConstants, ActionListener {

    private final AlcMain root;
    private AlcMenu fileMenu,  sessionMenu,  viewMenu,  intervalMenu,  switchMenu;
    private AlcMenuItem newItem,  printItem,  exportItem,  exitItem,  fullScreenItem,  directoryItem,  switchVectorItem,  switchBitmapItem,  switchVectorAppItem,  switchBitmapAppItem;
    private AlcCheckBoxMenuItem recordingItem,  defaultRecordingItem,  autoClearItem;
    private AlcRadioButtonMenuItem intervalItem;
    private File platformAppDir;
    //
    private PrinterJob printer = null;
    private PageFormat page = null;

    /** Creates a new instance of AlcMenuBar */
    public AlcMenuBar(AlcMain root) {

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

        //System.out.println(System.getProperty("user.dir"));

        //////////////////////////////////////////////////////////////
        // FILE MENU
        //////////////////////////////////////////////////////////////
        fileMenu = new AlcMenu("File");
        // New
        newItem = new AlcMenuItem("New...", KeyEvent.VK_N);
        newItem.addActionListener(this);
        fileMenu.add(newItem);

        fileMenu.add(new JSeparator());

        // Export
        exportItem = new AlcMenuItem("Export...", KeyEvent.VK_E);
        exportItem.addActionListener(this);
        fileMenu.add(exportItem);

        fileMenu.add(new JSeparator());

        // Print
        printItem = new AlcMenuItem("Print...", KeyEvent.VK_P);
        printItem.addActionListener(this);
        fileMenu.add(printItem);

        // Exit - not included on a MAC
        if (AlcMain.PLATFORM != MACOSX) {
            fileMenu.add(new JSeparator());

            exitItem = new AlcMenuItem("Exit", KeyEvent.VK_P);
            exitItem.addActionListener(this);
            fileMenu.add(exitItem);
        }

        this.add(fileMenu);

        //////////////////////////////////////////////////////////////
        // VIEW MENU
        //////////////////////////////////////////////////////////////
        viewMenu = new AlcMenu("View");
        // Fullscreen
        fullScreenItem = new AlcMenuItem("Fullscreen", KeyEvent.VK_F);
        fullScreenItem.addActionListener(this);
        viewMenu.add(fullScreenItem);
        this.add(viewMenu);

        //////////////////////////////////////////////////////////////
        // SESSION MENU
        //////////////////////////////////////////////////////////////
        sessionMenu = new AlcMenu("Session");
        // Toggle Recording
        recordingItem = new AlcCheckBoxMenuItem("Toggle Recording", KeyEvent.VK_R);
        recordingItem.setState(root.prefs.getRecordingState());
        if (root.prefs.getRecordingState()) {
            root.session.setRecording(true);
        }
        recordingItem.addActionListener(this);
        sessionMenu.add(recordingItem);
        sessionMenu.add(new JSeparator());
        // Default Recording
        defaultRecordingItem = new AlcCheckBoxMenuItem("Record on Startup", KeyEvent.VK_R);
        defaultRecordingItem.setState(root.prefs.getRecordingState());
        defaultRecordingItem.addActionListener(this);
        sessionMenu.add(defaultRecordingItem);
        // Interval submenu
        intervalMenu = new AlcMenu("Record Interval");
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
        switchVectorItem = new AlcMenuItem("Switch Vector", KeyEvent.VK_V);
        switchVectorItem.addActionListener(this);
        switchMenu.add(switchVectorItem);
        // Switch Bitmaps
        switchBitmapItem = new AlcMenuItem("Switch Bitmap", KeyEvent.VK_B);
        switchBitmapItem.addActionListener(this);
        switchMenu.add(switchBitmapItem);
        switchMenu.add(new JSeparator());
        // Switch Vector
        switchVectorAppItem = new AlcMenuItem("Set Vector Application...");
        switchVectorAppItem.addActionListener(this);
        switchMenu.add(switchVectorAppItem);
        // Switch Bitmaps
        switchBitmapAppItem = new AlcMenuItem("Set Bitmap Application...");
        switchBitmapAppItem.addActionListener(this);
        switchMenu.add(switchBitmapAppItem);
        this.add(switchMenu);
    }

    /*
    // Override the paint component to draw the gradient bg
    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    //int panelWidth = getWidth();
    //GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(215, 215, 215), 0, this.getHeight(), new Color(207, 207, 207), true);
    if (g instanceof Graphics2D) {
    Graphics2D g2 = (Graphics2D) g;
    // Turn on text antialias - windows does not use it by default
    //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    //g2.setPaint(gradientPaint);
    g2.setPaint(AlcToolBar.toolBarHighlightColour);
    g2.fillRect(0, 0, root.getWindowSize().width, this.getHeight());
    //g2.setPaint(AlcToolBar.toolBarHighlightColour);
    //g2.drawLine(0, 0, root.getWindowSize().width, 0);
    g2.setPaint(AlcToolBar.toolBarLineColour);
    g2.drawLine(0, this.getHeight() - 1, root.getWindowSize().width, this.getHeight() - 1);
    }
    }
     */
    /** Print */
    public void print() {
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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newItem) {
            root.canvas.clear();

        } else if (e.getSource() == exportItem) {
            askExportPath();

        } else if (e.getSource() == printItem) {
            this.print();

        } else if (e.getSource() == exitItem) {
            root.exitAlchemy();

        } else if (e.getSource() == fullScreenItem) {
            //System.out.println("Fullscreen to " + !root.isFullscreen());
            root.setFullscreen(!root.isFullscreen());

        } else if (e.getSource() == recordingItem) {
            root.session.setRecording(recordingItem.getState());

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

        } else if (e.getSource() == switchVectorItem) {
            switchVector();

        } else if (e.getSource() == switchBitmapItem) {
            switchBitmap();

        } else if (e.getSource() == switchVectorAppItem) {
            File file = askLocation("Select Vector Application", platformAppDir);
            if (file != null) {
                System.out.println(file.toString());
                root.prefs.setSwitchVectorApp(file.toString());
            }
        //

        } else if (e.getSource() == switchBitmapAppItem) {
            File file = askLocation("Select Bitmap Application", platformAppDir);
            if (file != null) {
                System.out.println(file.toString());
                root.prefs.setSwitchBitmapApp(file.toString());
            }

        }

    }
}
