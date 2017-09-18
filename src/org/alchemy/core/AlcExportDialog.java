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

package org.alchemy.core;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.*;
import java.util.Iterator;
import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

class AlcExportDialog extends JDialog implements AlcConstants {

    private JFormattedTextField widthField, heightField, percentField;
    private JCheckBox transparencyCheckBox;
    private int startWidth, startHeight;
    private static final Font font = new Font("sansserif", Font.PLAIN, 12);
    /** Format to save the image */
    private String imageFormat = "PNG";
    /** File to save the image as */
    private File file;

    AlcExportDialog(){
        super(Alchemy.window, Alchemy.bundle.getString("exportFileTitle"), true);
        Dimension size = new Dimension(250, 250);
        this.setPreferredSize(size);

        JPanel contentPanel = new JPanel(new MigLayout("fill, insets 15, gap 2"));
//        contentPanel.setPreferredSize(size);

        addSeparator(contentPanel, Alchemy.bundle.getString("exportOptions"));

        java.awt.Rectangle canvasSize = Alchemy.canvas.getVisibleRect();

        contentPanel.add(getLabel("exportWidth"), "gap para");
        widthField = getNumberField(canvasSize.width);
        contentPanel.add(widthField, "growx, wrap");

        contentPanel.add(getLabel("exportHeight"), "gap para");
        heightField = getNumberField(canvasSize.height);
        contentPanel.add(heightField, "growx, wrap");

        contentPanel.add(getLabel("exportPercent"), "gap para");
        percentField = getNumberField(100);
        contentPanel.add(percentField, "growx, wrap 10px");
        
        transparencyCheckBox = new JCheckBox(Alchemy.bundle.getString("exportTransparency"));
        transparencyCheckBox.setFont(font);
        contentPanel.add(transparencyCheckBox, "gap para, span, wrap 10px");



        AbstractAction closeAction = new AbstractAction() {

            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };

        AlcUtil.registerWindowCloseKeys(this.getRootPane(), closeAction);


        JButton okButton = new JButton(Alchemy.bundle.getString("ok"));
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    int width = new Integer(widthField.getText());
                    int height = new Integer(heightField.getText());
                    float scale = new Float(percentField.getText()) /  100f;
                    boolean transparency = transparencyCheckBox.isSelected();

//                    System.out.println("Width: " + width + " Height: " + height);
//                    System.out.println("Transparency: " + transparency + " Scale: " + scale);
//                    System.out.println("Format: " + imageFormat);

                    Alchemy.canvas.setGuide(false);
                    BufferedImage bitmapImage = (BufferedImage) Alchemy.canvas.renderCanvas(true, transparency, scale, width, height);
                    Alchemy.canvas.setGuide(true);

                    // Use the slightly more complex ImageWriter
                    // So the JPEG images are full quality
                    Iterator iter = ImageIO.getImageWritersByFormatName(imageFormat);
                    ImageWriter writer = (ImageWriter)iter.next();
                    ImageWriteParam iwp = writer.getDefaultWriteParam();
                    if(imageFormat.equals("JPG")){
                        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        iwp.setCompressionQuality(1);
                    }
                    FileImageOutputStream output = new FileImageOutputStream(file);
                    writer.setOutput(output);
                    IIOImage image = new IIOImage(bitmapImage, null, null);
                    writer.write(null, image, iwp);
                    writer.dispose();

//                    ImageIO.write((BufferedImage) bitmapImage, imageFormat, file);


                } catch (Exception ex) {
                    AlcUtil.showConfirmDialogFromBundle("imageExportErrorDialogTitle", "imageExportErrorDialogMessage");
                    ex.printStackTrace();
                }
                setVisible(false);
            }
        });
        contentPanel.add(okButton,  "skip 1, split, alignx right, tag ok");

        // Cancel button
        JButton cancelButton = new JButton(Alchemy.bundle.getString("cancel"));
        cancelButton.addActionListener(closeAction);
        contentPanel.add(cancelButton, "tag cancel");


        this.getContentPane().add(contentPanel);
        this.pack();
        
    }


    /** Show and centre the shorcut window */
    void showWindow(File file, String imageFormat) {
        this.imageFormat = imageFormat;
        this.file = file;
        java.awt.Rectangle canvasSize = Alchemy.canvas.getVisibleRect();
        startWidth = canvasSize.width;
        widthField.setValue(new Integer(startWidth));
        startHeight = canvasSize.height;
        heightField.setValue(new Integer(startHeight));
        percentField.setValue(new Integer(100));

        if(imageFormat.equals("PNG")){
            transparencyCheckBox.setEnabled(true);
        } else {
            transparencyCheckBox.setSelected(false);
            transparencyCheckBox.setEnabled(false);
        }

        Point loc = AlcUtil.calculateCenter(this);
        this.setLocation(loc.x, loc.y);
        this.setVisible(true);
    }

    private void addSeparator(JPanel panel, String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(0, 0, 0));
        panel.add(l, "gapbottom 1, span, split 2, aligny center");
        panel.add(new JSeparator(), "gapleft rel, growx");
    }

    /** Get a label for this dialog */
    private JLabel getLabel(String text){
        JLabel label = new JLabel(Alchemy.bundle.getString(text));
        label.setFont(font);
        return label;
    }

    /** Create a formatted text field only for numbers */
    private JFormattedTextField getNumberField(int value){
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        DecimalFormat format = (DecimalFormat)numberFormat;
        format.applyPattern("#############");
        JFormattedTextField field = new JFormattedTextField(format);
        field.setValue(new Integer(value));
        field.setFont(font);
//        field.setColumns(7);

        field.addKeyListener(new KeyAdapter(){

            @Override
            public void keyReleased(KeyEvent evt) {

                try {
                    float width = new Float(widthField.getText());
                    float height = new Float(heightField.getText());
                    float percent = new Float(percentField.getText());

                    JFormattedTextField source = (JFormattedTextField) evt.getSource();

                    if (source == widthField) {
                        float change = width / startWidth;
                        heightField.setValue(new Integer(Math.round(startHeight * change)));
                        percentField.setValue(new Integer(Math.round(change * 100f)));

                    } else if (source == heightField) {
                        float change = height / startHeight;
                        widthField.setValue(new Integer(Math.round(startWidth * change)));
                        percentField.setValue(new Integer(Math.round(change * 100f)));

                    } else if (source == percentField) {
                        widthField.setValue(new Integer(Math.round(startWidth * (percent / 100f))));
                        heightField.setValue(new Integer(Math.round(startHeight * (percent / 100f))));
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Number Format Exception");
                }

            }
        });


        return field;
    }
}
