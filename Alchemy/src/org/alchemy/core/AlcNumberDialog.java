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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 *
 * AlcNumberDialog.java
 */
class AlcNumberDialog extends JDialog implements AlcConstants {

    private JSpinner minField,  maxField;
    private SpinnerNumberModel minModel,  maxModel;
    private AlcNumberDialogInterface parent;
    private JButton okButton,  cancelButton;

    AlcNumberDialog(AlcNumberDialogInterface parent) {
        super(Alchemy.window, parent.getTitle() + " " + Alchemy.bundle.getString("sliderDialogTitle"));

        this.parent = parent;
        this.getContentPane().setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setPreferredSize(new Dimension(250, 90));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setBackground(COLOR_UI_HIGHLIGHT);

        minField = new JSpinner();
        maxField = new JSpinner();

        content.add(new JLabel(Alchemy.bundle.getString("minimum") + ":"));
        content.add(minField);
        content.add(new JLabel(Alchemy.bundle.getString("maximum") + ":"));
        content.add(maxField);

        this.getContentPane().add(content, BorderLayout.NORTH);
        this.getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
        this.setResizable(false);
        //this.setLocationRelativeTo(null);
        this.pack();

        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        AlcUtil.registerWindowCloseKeys(this.getRootPane(), new AbstractAction() {

            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        });
    }

    void show(int min, int max) {
        minModel = new SpinnerNumberModel(min, -10000, 10000, 1);
        maxModel = new SpinnerNumberModel(max, -10000, 10000, 1);
        minField.setModel(minModel);
        maxField.setModel(maxModel);
        Point p = AlcUtil.calculateCenter(this);
        this.setLocation(p);
        //okButton.requestFocus();
        this.setVisible(true);
    }

    private JPanel createButtonPanel() {

        // Cancel Button
        cancelButton = new JButton(Alchemy.bundle.getString("cancel"));
        cancelButton.setMnemonic(KeyEvent.VK_ESCAPE);
        cancelButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });

        // Ok Button
        okButton = new JButton(Alchemy.bundle.getString("ok"));
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        Integer newMin = (Integer) minModel.getValue();
                        Integer newMax = (Integer) maxModel.getValue();
                        parent.setup(newMin.intValue(), newMax.intValue());
                        setVisible(false);
                    }
                });

        JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        buttonPane.add(Box.createHorizontalGlue());
        if (Alchemy.OS == OS_MAC) {
            buttonPane.add(cancelButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(okButton);
        } else {
            buttonPane.add(okButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);
        }

        return buttonPane;
    }
}
