/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007-2010 Karl D.D. Willis
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
    private boolean hasFolders,  hasRootShapes,  hasShapes;
    private int currentFolder;
    private String[] folderNames;
    private ArrayList[] shapeLists;
    private AlcSubComboBox folderSelector;
    //
    private boolean scale = true;
    private boolean rotate = true;
    private float size = 2F;

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


        // Only add if there are folders present
        if (hasFolders) {
            folderSelector = new AlcSubComboBox(null);
            folderSelector.setToolTipText("Select which folder of shapes to use");
            setupFolderSelector();
            subToolBarSection.add(folderSelector);
        }

        // Reload
        AlcSubButton reloadButton = new AlcSubButton("Reload", AlcUtil.getUrlPath("reload.png", getClassLoader()));
        reloadButton.setToolTipText("Reload shapes from the 'shapes' folder in the Alchemy directory");
        reloadButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        loadShapes();
                        folderSelector.removeAllItems();
                        // TODO - Bug test this
                        setupFolderSelector();
                    }
                });
        subToolBarSection.add(reloadButton);


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

        // Scale button
//        AlcSubToggleButton scaleButton = new AlcSubToggleButton("Scale", AlcUtil.getUrlPath("scale.png", getClassLoader()));
//        scaleButton.setSelected(scale);
//        scaleButton.setToolTipText("Toggle random scaling");
//        scaleButton.addActionListener(
//                new ActionListener() {
//
//                    public void actionPerformed(ActionEvent e) {
//                        scale = !scale;
//                    }
//                });
//        subToolBarSection.add(scaleButton);


        // Size Slider
        final AlcSubSlider sizeSlider = new AlcSubSlider("Size", 1, 50, (int) size * 10);
        sizeSlider.setToolTipText("Adjust the size of shapes created");
        sizeSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!sizeSlider.getValueIsAdjusting()) {
                            size = 0.1F + sizeSlider.getValue() / 10F;
                        }
                    }
                });
        subToolBarSection.add(sizeSlider);

        // Rotate button
        AlcSubToggleButton rotateButton = new AlcSubToggleButton("Rotate", AlcUtil.getUrlPath("rotate.png", getClassLoader()));
        rotateButton.setSelected(rotate);
        rotateButton.setToolTipText("Toggle random rotation");
        rotateButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        rotate = !rotate;
                    }
                });
        subToolBarSection.add(rotateButton);
    }

    private void setupFolderSelector() {
        for (int i = 0; i < folderNames.length; i++) {
            folderSelector.addItem(folderNames[i]);
        }
    }

    private void loadShapes() {
        // Initialise variables
        hasFolders = false;
        hasRootShapes = false;
        hasShapes = false;
        currentFolder = 0;
        shapeLists = null;
        folderNames = null;

        // Folder of the plugins
        File shapesDir = new File("shapes");

        // Filter for the folders not starting with '.'
        FileFilter folderFilter = new  

              FileFilter( ) {

                   public boolean accept(File pathname) {
                return (pathname.isDirectory() && !pathname.getName().startsWith(".")) ? true : false;
            }
        };

        // Filter to check for PDFs
        FilenameFilter pdfFilter = new  

              FilenameFilter(   ) {

                    
                     public boolean accept(File dir, String name) {
                // MIME types not working on Linux?
                if (Alchemy.OS == OS_LINUX) {
                    return name.endsWith(".pdf") || name.endsWith(".PDF");
                }
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
            hasRootShapes = true;
            // Initialise the array holding all shape lists
            // Equal to each folder plus the root folder
            shapeLists = new ArrayList[folders.length + 1];
            // Initialise the root shape list
            ArrayList<AlcShape> rootShapes = new ArrayList<AlcShape>();
            for (int i = 0; i < rootPdfs.length; i++) {
                // Add the shapes from each pdf to the root shape list
                rootShapes.addAll(AlcUtil.getPDFShapes(rootPdfs[i], true, 0));
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
                    folderShapes.addAll(AlcUtil.getPDFShapes(pdfs[j], true, 0));
                }
                // Store this folder of shapes in the main array
                shapeLists[count] = folderShapes;
                count++;
                //System.out.println(folders[i].getName() + " " + folderShapes.size());
                // Store the folder name
                folderNames[folderNameCount] = folders[i].getName() + " Folder";
                folderNameCount++;
                hasShapes = true;
            }
        }
        if (!hasShapes) {
            AlcUtil.showNoShapesDialog();
        }
    }

    private void addRandomShape(MouseEvent e) {

        // The folder to get a shape from
        int folder = currentFolder;
        if (!hasRootShapes) {
            folder = currentFolder - 1;
        }
        // If set to "All Shapes"
        if (currentFolder == 0 && hasFolders) {
            folder = (int) math.random(shapeLists.length);
        }
        int rand = (int) math.random(shapeLists[folder].size());
        if (shapeLists[folder].size() > 0) {
            AlcShape shape = (AlcShape) shapeLists[folder].get(rand);
            // Clone the shape
            AlcShape cloneShape = (AlcShape) shape.clone();
            if (scale) {
                // Scale it
                float scaleFactor = math.random(0.1F, size);
                cloneShape.scale(scaleFactor, scaleFactor);
            }
            if (rotate) {
                // Rotate it
                float rotation = math.random(0.1F, 57.2958F);
                cloneShape.rotate(rotation);
            }
            // Move it into place
            Rectangle bounds = cloneShape.getBounds();
            int x = e.getX() - (bounds.width >> 1);
            int y = e.getY() - (bounds.height >> 1);
            cloneShape.move(x, y);

            cloneShape.setup();
            canvas.createShapes.add(cloneShape);
            canvas.redraw();
        }
    }
    //expandedPath = (GeneralPath) currentPath.createTransformedShape(getScaleTransform(adjustedLevel, rect));
//    private GeneralPath scaleShape(GeneralPath gp){
//        
//    }
    /* Gets a scale transform based on the sound level */
//    private AffineTransform getScaleTransform(double level, Rectangle rect) {
//        AffineTransform scaleTransform = new AffineTransform();
//
//        double offsetX = rect.x + (rect.width / 2);
//        double offsetY = rect.y + (rect.height / 2);
//
//        scaleTransform.translate(offsetX, offsetY);
//        scaleTransform.scale(level, level);
//        scaleTransform.translate(-offsetX, -offsetY);
//
//        return scaleTransform;
//    }
    private int getFolder() {
        int folder = 0;
        if (hasFolders) {
            folder = folderSelector.getSelectedIndex();
        }
        return folder;
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (hasShapes) {
            currentFolder = getFolder();
            //System.out.println("Current Folder = " + currentFolder);
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
