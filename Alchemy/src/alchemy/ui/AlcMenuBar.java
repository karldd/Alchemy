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
import java.io.File;
import javax.swing.*;

public class AlcMenuBar extends JMenuBar implements ActionListener {

    AlcToolBar parent;
    AlcMain root;
    AlcMenu fileMenu, sessionMenu, viewMenu, intervalMenu;
    AlcMenuItem newItem, printItem, exportItem, fullScreenItem, directoryItem;
    AlcCheckBoxMenuItem recordingItem, defaultRecordingItem, autoClearItem;
    AlcRadioButtonMenuItem intervalItem;

    /** Creates a new instance of AlcMenuBar */
    public AlcMenuBar(AlcToolBar parent, AlcMain root) {

        this.parent = parent;
        this.root = root;

        this.setBackground(AlcToolBar.toolBarBgColour);
        //this.setOpaque(false);
        //this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        // FILE MENU
        fileMenu = new AlcMenu(parent, "File");
        // New
        newItem = new AlcMenuItem(parent, "New...", KeyEvent.VK_N);
        newItem.addActionListener(this);
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        // Export
        exportItem = new AlcMenuItem(parent, "Export...", KeyEvent.VK_E);
        exportItem.addActionListener(this);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        // Print
        printItem = new AlcMenuItem(parent, "Print...", KeyEvent.VK_P);
        printItem.addActionListener(this);
        fileMenu.add(printItem);
        //
        this.add(fileMenu);

        // VIEW MENU
        viewMenu = new AlcMenu(parent, "View");
        // Fullscreen
        fullScreenItem = new AlcMenuItem(parent, "Fullscreen", KeyEvent.VK_F);
        fullScreenItem.addActionListener(this);
        viewMenu.add(fullScreenItem);
        this.add(viewMenu);

        // SESSION MENU
        sessionMenu = new AlcMenu(parent, "Session");
        // Toggle Recording
        recordingItem = new AlcCheckBoxMenuItem(parent, "Toggle Recording", KeyEvent.VK_R);
        recordingItem.setState(root.prefs.getRecordingState());
        if (root.prefs.getRecordingState()) {
            root.session.setRecording(true);
        }
        recordingItem.addActionListener(this);
        sessionMenu.add(recordingItem);
        sessionMenu.addSeparator();
        // Default Recording
        defaultRecordingItem = new AlcCheckBoxMenuItem(parent, "Record on Startup", KeyEvent.VK_R);
        defaultRecordingItem.setState(root.prefs.getRecordingState());
        defaultRecordingItem.addActionListener(this);
        sessionMenu.add(defaultRecordingItem);
        // Interval submenu
        intervalMenu = new AlcMenu(parent, "Record Interval");
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < root.session.recordingIntervalString.length; i++) {
            intervalItem = new AlcRadioButtonMenuItem(parent, root.session.recordingInterval[i], root.session.recordingIntervalString[i]);
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
        sessionMenu.add(intervalMenu);
        // Default Directory
        directoryItem = new AlcMenuItem(parent, "Set Session Directory...");
        directoryItem.addActionListener(this);
        sessionMenu.add(directoryItem);
        // Auto Clear
        autoClearItem = new AlcCheckBoxMenuItem(parent, "Auto Clear Canvas");
        autoClearItem.setState(root.prefs.getAutoClear());
        autoClearItem.addActionListener(this);
        sessionMenu.add(autoClearItem);

        this.add(sessionMenu);


    }

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

    private void askExportPath() {
        // create a file chooser
        // TODO - find a way to center this
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export Pdf");
        // in response to a button click:
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String pdfPath = AlcUtil.addFileExtension(file.getPath(), "pdf");
            System.out.println(pdfPath);

            root.canvas.startPdf(pdfPath);
            root.canvas.savePdfFrame();
            root.canvas.endPdf();
        }
    }

    private void askDirectory() {
        // create a file chooser
        // TODO - find a way to center this
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle("Select Session Directory");

        // in response to a button click:
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String pdfPath = file.getPath();
            root.prefs.setSessionPath(pdfPath);
            System.out.println(pdfPath);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newItem) {
            root.canvas.clear();

        } else if (e.getSource() == exportItem) {
            askExportPath();

        } else if (e.getSource() == printItem) {
        // TODO - implement a print function

        } else if (e.getSource() == fullScreenItem) {
            root.setFullscreen(!root.isFullscreen());

        } else if (e.getSource() == recordingItem) {
            root.session.setRecording(recordingItem.getState());

        } else if (e.getSource() == defaultRecordingItem) {
            // Set the recording state reference
            root.prefs.setRecordingState(defaultRecordingItem.getState());

        } else if (e.getSource() == directoryItem) {
            askDirectory();

        } else if (e.getSource() == autoClearItem) {
            // Set the recording state reference
            root.prefs.setAutoClear(autoClearItem.getState());

        } else if (e.getActionCommand().equals("Interval")) {
            AlcRadioButtonMenuItem source = (AlcRadioButtonMenuItem) e.getSource();
            root.session.setTimerInterval(source.getIndex());
        }

    }
}
