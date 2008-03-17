/*
 * This file is part of the Alchemy project - http://al.chemy.org
 * 
 * Copyright (c) 2007 Karl D.D. Willis
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * AlcShortcuts
 * @author Karl D.D. Willis
 */
class AlcShortcuts extends JDialog implements AlcConstants {

    private ArrayList mapper;
    private int index = 0;
    private JTable table;
    private TableModel model;

    AlcShortcuts(AlcWindow owner) {
        super(owner, Alchemy.bundle.getString("keyboardShortcutsWindowTitle"), true);
        mapper = new ArrayList(50);
//        try {
//            String[] prefKeys = AlcPreferences.prefs.keys();
//            AlcUtil.printStringArray(prefKeys);
//
//        } catch (BackingStoreException ex) {
//            ex.printStackTrace();
//        }
    }

    void setupWindow() {
        JPanel masterPanel = new JPanel();
        //masterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));
        masterPanel.setOpaque(true);
        masterPanel.setBackground(AlcToolBar.toolBarBgStartColour);
        masterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new AlcTableModel(mapper);
        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(350, 200));
        table.setFocusable(false);
        table.setShowHorizontalLines(true);
        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                // if (e.getClickCount() == 2) {
                //if (!e.isConsumed() && e.getButton() == 1 && e.getClickCount() > 1) {
                JTable target = (JTable) e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if (column >= 1) {
                    //table.setEditingColumn(column);
                    table.setEditingRow(row);
                    model.setValueAt(new String(""), row, column);
                }
            //e.consume();
            //}
            }
        });


        if (Alchemy.PLATFORM == MACOSX) {
            table.putClientProperty("Quaqua.Table.style", "striped");
        }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(400, 240));
        //Add the scroll pane to this panel.
        masterPanel.add(scrollPane);


        //Create and initialize the buttons.
        JButton cancelButton = new JButton(Alchemy.bundle.getString("cancel"));
        cancelButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
        JButton okButton = new JButton(Alchemy.bundle.getString("ok"));
        okButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });
        getRootPane().setDefaultButton(okButton);
        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(okButton);

        masterPanel.add(buttonPane);

        this.getContentPane().add(masterPanel);
        this.pack();
        this.setResizable(false);
    }

    void showWindow() {
        Point loc = AlcUtil.calculateCenter(this);
        this.setLocation(loc.x, loc.y);
        this.setVisible(true);
    }

    /** Set the keyboard shortcut to trigger an application wide action
     * 
     * @param key       The key to trigger the action
     * @param title     A unique title for the action
     * @param action    The name of the action to call
     * @return          The key actually used for this shortcut - user specified or default
     */
    int setShortcut(int key, String title, Action action) {
        return setShortcut(key, title, action, false);
    }

    /** Set the keyboard shortcut to trigger an application wide action
     *  with the default modifier key
     * 
     * @param key       The key to trigger the action
     * @param title     A unique title for the action
     * @param action    The name of the action to call
     * @param modifier  Use the system modifier key - Win=Ctrl or Mac=Command
     * @return          The key actually used for this shortcut - user specified or default
     */
    int setShortcut(int key, String title, Action action, boolean modifier) {
        // Look for the users key stored in the preferences
        // If not found go with the default
        int userKey = AlcPreferences.prefs.getInt(title, key);
        // Set the modifier key - 0 means no modifier, else use the system modifier
        int modifierKey = 0;
        if (modifier) {
            modifierKey = MODIFIER_KEY;
        }
        // Store the mappings
        mapper.add(new AlcShortcutMapper(index, userKey, title, action, modifierKey));
        index++;
        Alchemy.window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(userKey, modifierKey), title);
        Alchemy.palette.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(userKey, modifierKey), title);
        Alchemy.window.getRootPane().getActionMap().put(title, action);
        Alchemy.palette.getRootPane().getActionMap().put(title, action);
        // Return the key that will be used.
        return userKey;
    }

    /** Clear and reload all shortcuts */
    private void changeShortcuts() {
        // Remove all shortcut mappings
        Alchemy.window.getRootPane().resetKeyboardActions();
        Alchemy.palette.getRootPane().resetKeyboardActions();

        for (int i = 0; i < mapper.size(); i++) {
            AlcShortcutMapper shortcut = (AlcShortcutMapper) mapper.get(i);
            Alchemy.window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(shortcut.key, shortcut.modifier), shortcut.title);
            Alchemy.palette.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(shortcut.key, shortcut.modifier), shortcut.title);
            Alchemy.window.getRootPane().getActionMap().put(shortcut.title, shortcut.action);
            Alchemy.palette.getRootPane().getActionMap().put(shortcut.title, shortcut.action);
        }

    }
}

/** 
 * ShortcutMapper class
 * Stores a set of shortcut mappings
 * 
 * @author karldd
 */
class AlcShortcutMapper {

    int index, key, modifier;
    String title;
    Action action;

    AlcShortcutMapper(int index, int key, String title, Action action, int modifier) {
        this.index = index;
        this.key = key;
        this.title = title;
        this.action = action;
        this.modifier = modifier;
    }
}

class AlcTableModel extends AbstractTableModel {

    final ArrayList mapper;
    final String[] columnNames = {"Command", "Shortcut"};
    final Object[][] data;

    AlcTableModel(ArrayList mapper) {
        this.mapper = mapper;

        data = new Object[mapper.size()][2];

        for (int i = 0; i < mapper.size(); i++) {
            AlcShortcutMapper shortcut = (AlcShortcutMapper) mapper.get(i);
            data[i][0] = shortcut.title;
            String keyText = KeyEvent.getKeyText(shortcut.key);
            String keyModifier = KeyEvent.getKeyModifiersText(shortcut.modifier);
            if (keyModifier.equals("Command")) {
                keyModifier = Alchemy.MODIFIER_KEY_STRING;
            }
            data[i][1] = keyModifier + keyText;
        }
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col >= 1) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {

        //if (DEBUG) {
        System.out.println("Setting value at " + row + "," + col + " to " + value + " (an instance of " + value.getClass() + ")");
        //}

        data[row][col] = value;
        fireTableCellUpdated(row, col);
//
//        if (DEBUG) {
//            System.out.println("New value of data:");
//            printDebugData();
//        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
    }

class AlcCellEditor extends DefaultCellEditor {

    /**
     * Constructs a DefaultCellEditor that uses a text field.
     *
     * @param textField  a JTextField object
     */
    AlcCellEditor(JTextField textField) {
        super(textField);
        textField.setBorder(new LineBorder(Color.black));
    }

    /**
     * Constructs a DefaultCellEditor object that uses 
     * a check box.
     *
     * @param checkBox  a JCheckBox object
     */
    AlcCellEditor(JCheckBox checkBox) {
        super(checkBox);
        checkBox.setBorder(new LineBorder(Color.black));
    }

    /**
     * Constructs a DefaultCellEditor object that uses a
     * combo box.
     *
     * @param comboBox  a JComboBox object
     */
    AlcCellEditor(JComboBox comboBox) {
        super(comboBox);
        comboBox.setBorder(new LineBorder(Color.black));
    }
}

