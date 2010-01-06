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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.JTextField;

/**
 * AlcShortcuts
 * @author Karl D.D. Willis
 */
class AlcShortcuts extends JDialog implements AlcConstants {

    private ArrayList<AlcShortcutMapper> userShortcuts;
    private ArrayList<AlcShortcutMapper> defaultShortcuts;
    private JTextField[] textfields;
    private int index = 0;
    private boolean listenerActive = false;
    private JButton okButton;
    private boolean reloadShortcuts = false;
    private static final Font shortcutFont = new Font("sansserif", Font.PLAIN, 12);
    private JPanel shortcutPanel;

    AlcShortcuts(AlcWindow owner) {
        super(owner, Alchemy.bundle.getString("keyboardShortcutsWindowTitle"), true);
        userShortcuts = new ArrayList<AlcShortcutMapper>(50);
        defaultShortcuts = new ArrayList<AlcShortcutMapper>(50);
        this.setPreferredSize(new Dimension(400, 300));
    }

    void setupWindow() {

        // Update the tooltips with the user defined shortcuts
        refreshInterfaceElements();

        final JPanel masterPanel = new JPanel();
        //masterPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));
        masterPanel.setOpaque(true);
        masterPanel.setBackground(AlcToolBar.COLOR_UI_HIGHLIGHT);
        masterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        //////////////////////////////////////////////////////////////
        // SCROLL PANE
        //////////////////////////////////////////////////////////////
        shortcutPanel = setupShortcutPanel();
        //Create the scroll pane and add the panel to it.
        final JScrollPane scrollPane = new JScrollPane(shortcutPanel);
        //scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        //Add the scroll pane to this panel.
        masterPanel.add(scrollPane);

        //////////////////////////////////////////////////////////////
        // RESTORE DEFAULT BUTTON
        //////////////////////////////////////////////////////////////
        JButton defaultButton = new JButton(Alchemy.bundle.getString("restoreDefaults"));
        defaultButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        userShortcuts = new ArrayList<AlcShortcutMapper>(defaultShortcuts);
                        reloadShortcuts();
                        refreshTextfields();
                        okButton.requestFocus();
                        reloadShortcuts = true;
                    }
                });

        //////////////////////////////////////////////////////////////
        // CANCEL BUTTON
        //////////////////////////////////////////////////////////////
        JButton cancelButton = new JButton(Alchemy.bundle.getString("cancel"));
        cancelButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                });

        //////////////////////////////////////////////////////////////
        // OK BUTTON
        //////////////////////////////////////////////////////////////
        okButton = new JButton(Alchemy.bundle.getString("ok"));
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        // If there have been changes
                        if (reloadShortcuts) {
                            reloadShortcuts();
                            saveShortcuts();
                            refreshInterfaceElements();
                        }
                        reloadShortcuts = false;
                        setVisible(false);
                    }
                });

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPane.add(defaultButton);
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


        masterPanel.add(buttonPane);
        //getRootPane().setDefaultButton(okButton);

        AlcUtil.registerWindowCloseKeys(this.getRootPane(), new AbstractAction() {

            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        });

        this.getContentPane().add(masterPanel);
        this.pack();
    //this.setResizable(false);
    //okButton.requestFocus();
    }

    /** Show and centre the shorcut window */
    void showWindow() {
        Point loc = AlcUtil.calculateCenter(this);
        this.setLocation(loc.x, loc.y);
        this.setVisible(true);
    }

    /** Create and return the shortcut panel*/
    JPanel setupShortcutPanel() {

        textfields = new JTextField[userShortcuts.size()];

        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setBackground(AlcToolBar.COLOR_UI_START);
        panel.setLayout(new GridLayout(userShortcuts.size(), 2, 5, 5));

        for (int i = 0; i < userShortcuts.size(); i++) {
            final AlcShortcutMapper shortcut = userShortcuts.get(i);
            JLabel label = new JLabel(shortcut.title);
            label.setFont(shortcutFont);
            panel.add(label);

            final String originalShortcut = getShortcutString(shortcut.key, shortcut.modifier);
            final JTextField textfield = new JTextField(originalShortcut);
            textfield.setBackground(Color.WHITE);
            textfield.setFont(shortcutFont);

            //Print out a list of the shortcuts
            //System.out.println(shortcut.title +": " + originalShortcut);

            textfield.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    listenerActive = true;
                    textfield.setText("");
                    textfield.setBackground(AlcToolBar.COLOR_UI_BG);
                }
            });

            textfield.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {

                    if (listenerActive) {
                        int key = e.getKeyCode();
                        int modifier = e.getModifiers();
                        // Make a text string of the shortcut
                        String shortcutString = getShortcutString(key, modifier);
                        // Check the shortcut is valid
                        int changeShortcut = validateShortcut(key, modifier, shortcut.index);

                        // Shortcut is valid
                        if (changeShortcut == 0) {
                            textfield.setText(shortcutString);
                            shortcut.key = key;
                            shortcut.modifier = modifier;
                            reloadShortcuts = true;
                            textfield.setBackground(Color.WHITE);
                        } else {
                            textfield.setBackground(Color.PINK);

                            if (changeShortcut == 1) {
                                textfield.setText(Alchemy.bundle.getString("invalidKeyError"));
                            } else if (changeShortcut == 2) {
                                textfield.setText(Alchemy.bundle.getString("keyAlreadyAssignedError"));
                            }
                            javax.swing.Timer timer = new javax.swing.Timer(1500, new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    textfield.setBackground(Color.WHITE);
                                    textfield.setText(originalShortcut);
                                }
                            });

                            timer.start();
                            timer.setRepeats(false);
                        }

                        //System.out.println(key + " " + modifier + " " + MODIFIER_KEY);

                        listenerActive = false;
                        okButton.requestFocus();

                    }

                }
            });

            textfield.addFocusListener(new FocusAdapter() {

                @Override
                public void focusLost(FocusEvent e) {
                    if (textfield.getText().equals("")) {
                        textfield.setText(originalShortcut);
                        textfield.setBackground(Color.WHITE);
                    }
                }
            });


            textfields[i] = textfield;
            panel.add(textfield);
        }
        return panel;
    }

    private int validateShortcut(int key, int modifier, int index) {
        if (modifier == 0) {
            // Make sure these keys are not used alone
            if (key == KeyEvent.VK_META ||
                    key == KeyEvent.VK_ALT ||
                    key == KeyEvent.VK_CONTROL ||
                    key == KeyEvent.VK_SHIFT) {
                return 1;
            }
        }

        // Check the shortcut is not the same as any other
        for (int i = 0; i < userShortcuts.size(); i++) {
            final AlcShortcutMapper shortcut = userShortcuts.get(i);
            if (i != index) {
                if (shortcut.modifier == modifier && shortcut.key == key) {
                    return 2;
                }
            }
        }

        return 0;
    }

    /** Reload the shortcuts into the textfields */
    private void refreshTextfields() {
        for (int i = 0; i < textfields.length; i++) {
            final AlcShortcutMapper shortcut = userShortcuts.get(i);
            // Make a text string of the shortcut
            String shortcutString = getShortcutString(shortcut.key, shortcut.modifier);
            textfields[i].setText(shortcutString);
            textfields[i].setBackground(Color.WHITE);
        }
    }

    /** Set the keyboard shortcut to trigger an application wide action
     * 
     * @param component The component linked to the shortcut
     * @param key       The key to trigger the action
     * @param title     A unique title for the action
     * @param action    The name of the action to call
     * @return          The key actually used for this shortcut - user specified or default
     */
    int setShortcut(JComponent component, int key, String title, Action action) {
        return setShortcut(component, key, title, action, 0);
    }

    /** Set the keyboard shortcut to trigger an application wide action
     *  with the default modifier key
     * 
     * @param component The component linked to the shortcut
     * @param key       The key to trigger the action
     * @param title     A unique title for the action
     * @param action    The name of the action to call
     * @param modifier  Use the system modifier key - Win=Ctrl or Mac=Command
     * @return          The key actually used for this shortcut - user specified or default
     */
    int setShortcut(JComponent component, int key, String title, Action action, int modifier) {

        String bundleTitle, bundleTitleEn;
        try {
            // Get the localised string to display
            bundleTitle = Alchemy.bundle.getString(title);
            // Get the english string to store as a reference - the two may or may not be the same
            bundleTitleEn = Alchemy.bundleEn.getString(title);
        } catch (Exception e) {
            bundleTitle = title;
            bundleTitleEn = title;
        }

        // Keep track of the default shortcuts
        defaultShortcuts.add(new AlcShortcutMapper(component, index, key, bundleTitle, bundleTitleEn, action, modifier));

        // Look for the users key stored in the preferences
        // If not found go with the default
        int userKey = AlcPreferences.prefs.getInt(bundleTitleEn, key);
        // Look for the modifier key
        int modifierKey = AlcPreferences.prefs.getInt(bundleTitleEn + " Modifier", modifier);
        // Store the mappings into the user set of shortcuts
        userShortcuts.add(new AlcShortcutMapper(component, index, userKey, bundleTitle, bundleTitleEn, action, modifierKey));
        index++;

        Alchemy.window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(userKey, modifierKey), bundleTitleEn);
        Alchemy.palette.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(userKey, modifierKey), bundleTitleEn);
        Alchemy.window.getRootPane().getActionMap().put(bundleTitleEn, action);
        Alchemy.palette.getRootPane().getActionMap().put(bundleTitleEn, action);
        // Return the key that will be used.
        return userKey;
    }

    /** Clear and reload all shortcuts */
    private void reloadShortcuts() {

        // Remove all shortcut mappings
        Alchemy.window.getRootPane().resetKeyboardActions();
        Alchemy.palette.getRootPane().resetKeyboardActions();

        for (int i = 0; i < userShortcuts.size(); i++) {
            AlcShortcutMapper shortcut = userShortcuts.get(i);
            Alchemy.window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(shortcut.key, shortcut.modifier), shortcut.titleEn);
            Alchemy.palette.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(shortcut.key, shortcut.modifier), shortcut.titleEn);
            Alchemy.window.getRootPane().getActionMap().put(shortcut.titleEn, shortcut.action);
            Alchemy.palette.getRootPane().getActionMap().put(shortcut.titleEn, shortcut.action);
        }
    }

    /** Store the shortcuts in the preferences persistant storage */
    private void saveShortcuts() {
        for (int i = 0; i < userShortcuts.size(); i++) {
            AlcShortcutMapper shortcut = userShortcuts.get(i);
            AlcPreferences.prefs.putInt(shortcut.titleEn, shortcut.key);
            AlcPreferences.prefs.putInt(shortcut.titleEn + " Modifier", shortcut.modifier);
        }
    }

    /** Refresh the tooltip and accelerator key information */
    private void refreshInterfaceElements() {
        for (int i = 0; i < userShortcuts.size(); i++) {
            AlcShortcutMapper shortcut = userShortcuts.get(i);
            // Check the component is not null and that it implements the shortcut interface
            if (shortcut.component != null && shortcut.component instanceof AlcShortcutInterface) {
                AlcShortcutInterface shortcutComponent = (AlcShortcutInterface) shortcut.component;
                shortcutComponent.refreshShortcut(shortcut.key, shortcut.modifier);
            }
        }
    }

    /** Make a shortcut string from the shortcut key and modifier
     * 
     * @param key       The main key
     * @param modifier  Modifier key
     * @return          String in the format MODIFIER+KEY
     */
    static String getShortcutString(int key, int modifier) {
        String keyText = KeyEvent.getKeyText(key).toUpperCase();
        String keyModifier = KeyEvent.getKeyModifiersText(modifier);

        if (Alchemy.OS == OS_MAC) {
            switch (modifier) {
                case KeyEvent.META_MASK:
                    keyModifier = Alchemy.KEY_MODIFIER_STRING;
                    break;
                case KeyEvent.SHIFT_MASK:
                    keyModifier = Alchemy.KEY_SHIFT_STRING;
                    break;
                case KeyEvent.ALT_MASK:
                    keyModifier = Alchemy.KEY_ALT_STRING;
                    break;
            }

            // Space bar is not showing up correctly on mac (and pc?)
            if (key == KeyEvent.VK_SPACE) {
                keyText = "SPACE";
            }

        }

        if (modifier > 0) {
            keyModifier += "+";
        }
        return keyModifier + keyText;
    }

    static String getShortcutString(int key, int modifier, String title) {
        return title + " (" + getShortcutString(key, modifier) + ")";
    }
}

/** 
 * ShortcutMapper class
 * Stores a set of shortcut mappings
 * 
 * @author karldd
 */
class AlcShortcutMapper {

    JComponent component;
    int index, key, modifier;
    String title, titleEn;
    Action action;

    AlcShortcutMapper(JComponent component, int index, int key, String title, String titleEn, Action action, int modifier) {
        this.component = component;
        this.index = index;
        this.key = key;
        this.title = title;
        this.titleEn = titleEn;
        this.action = action;
        this.modifier = modifier;
    }
}
