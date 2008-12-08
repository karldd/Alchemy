/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2008 Karl D.D. Willis
 * 
 * Alchemy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alchemy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.alchemy.create;

import eu.medsea.util.MimeUtil;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.alchemy.core.*;

/**
 *
 * PullShapes.java
 */
public class PullShapes extends AlcModule implements AlcConstants {

    private AlcToolBarSubSection subToolBarSection;
    //
    // Timing
    private long mouseDelayGap = 51;
    private long mouseDelayTime;
    //
    private boolean hasFolders = false;
    private boolean hasShapes = false;
    private int currentFolder = 0;
    private String[] folderNames;
    private ArrayList[] shapeLists;
    private AlcSubComboBox folderSelector;

    public PullShapes() {
    }

    @Override
    protected void setup() {
        loadShapes();
        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    @Override
    protected void reselect() {
        loadShapes();
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);


        AlcSubButton directoryButton = new AlcSubButton("Reload", AlcUtil.getUrlPath("reload.png", getClassLoader()));

        directoryButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        loadShapes();
                    }
                });
        subToolBarSection.add(directoryButton);


        // Spacing Slider
        int initialSliderValue = 25;
        final AlcSubSlider spacingSlider = new AlcSubSlider("Spacing", 0, 100, initialSliderValue);
        spacingSlider.setToolTipText("Adjust the spacing interval");
        spacingSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!spacingSlider.getValueIsAdjusting()) {
                            int value = spacingSlider.getValue();
                            mouseDelayGap = 1 + value * 2;
                        }
                    }
                });
        subToolBarSection.add(spacingSlider);

        // Only add if there are folders present
        if (hasFolders) {
            folderSelector = new AlcSubComboBox("Folder");
            folderSelector.setToolTipText("Select which folder of shapes to use");
            for (int i = 0; i < folderNames.length; i++) {
                folderSelector.addItem(folderNames[i]);
            }
            subToolBarSection.add(folderSelector);
        }
    }

    private void loadShapes() {
        // Folder of the plugins
        File shapesDir = new File("shapes");

        // Filter for the folders not starting with '.'
        FileFilter folderFilter = new FileFilter() {

            public boolean accept(File pathname) {
                return (pathname.isDirectory() && !pathname.getName().startsWith(".")) ? true : false;
            }
        };

        // Filter to check for PDFs
        FilenameFilter pdfFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {

                File file = new File(dir, name);
                String mime = MimeUtil.getMimeType(file.getAbsoluteFile());
                return (mime.equals("application/pdf")) ? true : false;
            }
        };

        // Folders in the root shapes folder
        File[] folders = shapesDir.listFiles(folderFilter);
        // Pdf files in the root shapes folder
        File[] rootPdfs = shapesDir.listFiles(pdfFilter);

        if (rootPdfs != null && rootPdfs.length > 0) {
            // Initialise the array holding all shape lists
            shapeLists = new ArrayList[rootPdfs.length + folders.length];
            // Initialise the root shape list
            ArrayList<AlcShape> rootShapes = new ArrayList<AlcShape>();
            for (int i = 0; i < rootPdfs.length; i++) {
                // Add the shapes from each pdf to the root shape list
                rootShapes.addAll(AlcUtil.getPDFShapes(rootPdfs[i], true));
            }
            // Add the rootShapes to the main array
            shapeLists[0] = rootShapes;
            hasShapes = true;
        }

        if (folders.length > 0) {
            hasFolders = true;
            int count = 1;
            // If there were not root pdfs
            if (shapeLists == null) {
                shapeLists = new ArrayList[folders.length];
                count = 0;
            }

            // Add an extra slot for 'ALL' shapes
            folderNames = new String[folders.length + 1];
            folderNames[0] = "All Shapes";
            int folderNameCount = 1;

            // For every folder
            for (int i = 0; i < folders.length; i++) {
                // Get every pdf in each folder
                File[] pdfs = AlcUtil.listFilesAsArray(folders[i], pdfFilter, true);
                ArrayList<AlcShape> folderShapes = new ArrayList<AlcShape>();
                for (int j = 0; j < pdfs.length; j++) {
                    // Add the shapes from each pdf to the folder shape list
                    folderShapes.addAll(AlcUtil.getPDFShapes(pdfs[j], true));
                }
                // Store this folder of shapes in the main array
                shapeLists[count] = folderShapes;
                count++;
                //System.out.println(folders[i].getName());
                // Store the folder name
                folderNames[folderNameCount] = folders[i].getName();
                folderNameCount++;
                hasShapes = true;
            }
        }
    }

    private void addRandomShape(MouseEvent e) {

        // The folder to get a shape from
        int folder = currentFolder;
        // If set to "All Shapes"
        if (currentFolder == 0 && hasFolders) {
            folder = (int) math.random(shapeLists.length);
        }
        int rand = (int) math.random(shapeLists[folder].size());
        AlcShape shape = (AlcShape) shapeLists[folder].get(rand);
        AlcShape movedShape = (AlcShape) shape.clone();
        Rectangle bounds = movedShape.getBounds();
        int x = e.getX() - (bounds.width >> 1);
        int y = e.getY() - (bounds.height >> 1);
        movedShape.move(x, y);
        movedShape.setupDefaultAttributes();
        canvas.createShapes.add(movedShape);
        canvas.redraw();
    }

    private int getFolder() {
        if (hasFolders) {
            return folderSelector.getSelectedIndex();
        } else {
            return 0;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (hasShapes) {
            currentFolder = getFolder();
            mouseDelayTime = System.currentTimeMillis();
            addRandomShape(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (hasShapes) {

            if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                mouseDelayTime = System.currentTimeMillis();
                //System.out.println(e.getPoint());
                addRandomShape(e);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (hasShapes) {
            canvas.commitShapes();
        }
    }
}
