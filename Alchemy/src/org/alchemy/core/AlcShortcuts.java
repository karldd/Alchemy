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

import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * AlcShortcuts
 * @author Karl D.D. Willis
 */
class AlcShortcuts extends JDialog implements AlcConstants {

    /** Persistant storage for the shortcuts */
    private final Preferences sc;

    AlcShortcuts(AlcWindow owner) {
        super(owner);
        sc = Preferences.userNodeForPackage(getClass());
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
        int userKey = sc.getInt(title, key);
        // Set the modifier key - 0 means no modifier, else use the system modifier
        int modifierKey = 0;
        if (modifier) {
            modifierKey = MODIFIER_KEY;
        }
        Alchemy.window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(userKey, modifierKey), title);
        Alchemy.palette.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(userKey, modifierKey), title);
        Alchemy.window.getRootPane().getActionMap().put(title, action);
        Alchemy.palette.getRootPane().getActionMap().put(title, action);
        return userKey;
    }
}
