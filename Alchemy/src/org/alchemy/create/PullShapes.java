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
    private AlcShape[] shapes;
    private boolean pathsLoaded = false;    // Timing
    private long mouseDelayGap = 51;
    private long mouseDelayTime;
    //
    private boolean hasFolders = false;
    private String[] folderNames;
    private ArrayList[] shapeLists;

    public PullShapes() {
    }

    @Override
    protected void setup() {
        createSubToolBarSection();
        loadShapes();
        toolBar.addSubToolBarSection(subToolBarSection);

    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
        loadShapes();
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
            AlcSubComboBox folderSelector = new AlcSubComboBox("Folder");
            folderSelector.addItem("Hello");
            folderSelector.addItem("Again");
            subToolBarSection.add(folderSelector);
        }
    }

    private void loadShapes() {
//        shapes = AlcUtil.getShapes();
//        if (shapes != null) {
//            pathsLoaded = true;
//        }

        ArrayList<AlcShape> shapeArrayList = new ArrayList<AlcShape>();
        // Folder of the plugins
        File shapesDir = new File("shapes");

        // Filter for the folders
        FileFilter folderFilter = new FileFilter() {

            public boolean accept(File pathname) {
                return (pathname.isDirectory()) ? true : false;
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


        File[] folders = shapesDir.listFiles(folderFilter);
        File[] rootPdfs = shapesDir.listFiles(pdfFilter);
        
        if(rootPdfs.length > 0){
            shapeLists = new ArrayList[rootPdfs.length + folders.length];
        }

        if (folders.length > 0) {
            hasFolders = true;
            
            folderNames = new String[folders.length];
            shapeLists = new ArrayList[folders.length];
            for (int i = 0; i < folders.length; i++) {
                // Get every pdf in each folder
                File[] pdfs = AlcUtil.listFilesAsArray(folders[i], pdfFilter, true);
                
            }

        } else {
        }














    //Filter to check the MIME type, not just the file extension


    // Get the list of PDF files
//        File[] pdfs = AlcUtil.listFilesAsArray(shapesDir, pdfFilter, true);
    // For each pdf add the shapes to the array list
//        for (int i = 0; i < pdfs.length; i++) {
//            shapeArrayList.addAll(AlcUtil.getPDFShapes(pdfs[i], true));
//        }
//        if (shapeArrayList.size() > 0) {
//            AlcShape[] arr = new AlcShape[shapeArrayList.size()];
////            return shapeArrayList.toArray(arr);
//        }
//        String message = Alchemy.bundle.getString("noShapesMessage1") + "<br>" +
//                Alchemy.preferences.shapesPath + "<br>" +
//                Alchemy.bundle.getString("noShapesMessage2");
//        showConfirmDialog(Alchemy.bundle.getString("noShapesTitle"), message);
//        

    }

    private void addRandomShape(MouseEvent e) {
        int rand = (int) math.random(shapes.length);
        AlcShape movedShape = (AlcShape) shapes[rand].clone();
        Rectangle bounds = movedShape.getBounds();
        int x = e.getX() - (bounds.width >> 1);
        int y = e.getY() - (bounds.height >> 1);
        movedShape.move(x, y);
        movedShape.setupDefaultAttributes();
        canvas.createShapes.add(movedShape);
        canvas.redraw();
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (pathsLoaded) {

            mouseDelayTime = System.currentTimeMillis();
            addRandomShape(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pathsLoaded) {

            if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                mouseDelayTime = System.currentTimeMillis();
                //System.out.println(e.getPoint());
                addRandomShape(e);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pathsLoaded) {
            canvas.commitShapes();
        }
    }
}
