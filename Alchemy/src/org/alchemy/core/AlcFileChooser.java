/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007-2010 Karl D.D. Willis
 * 
 *  Alchemy is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Alchemy is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Alchemy.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.alchemy.core;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Point;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

class AlcFileChooser extends JFileChooser implements AlcConstants {

    AlcFileChooser() {
        super();
    }

    AlcFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    AlcFileChooser(File currentDirectory) {
        super(currentDirectory);
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        Point p = AlcUtil.calculateCenter(dialog);
        dialog.setLocation(p.x, p.y);
        dialog.setResizable(false);
        return dialog;
    }

    /** Display a warning dialog when overwriting a file */
    @Override
    public void approveSelection() {

        if (this.getDialogType() == SAVE_DIALOG) {

            // First get the selected format
            String format = this.getFileFilter().getDescription();
            // Use the first three letters as the extension
            String ext = format.substring(0, 3).toLowerCase();
            // Check the extension is properly added
            File fileWithExtension = AlcUtil.addFileExtension(this.getSelectedFile(), ext);

            if (fileWithExtension.exists()) {

                String title, message;
                Object[] options = new Object[2];
                if (Alchemy.OS == OS_MAC) {
                    title = "";
                    message =
                            "<html>" + UIManager.get("OptionPane.css") +
                            "<b>\"" + fileWithExtension.getName() + "\" " + Alchemy.bundle.getString("existsMacDialogTitle") + "</b>" +
                            "<p>" + Alchemy.bundle.getString("existsMacDialogMessage");
                    options[0] = Alchemy.bundle.getString("replace");
                    options[1] = Alchemy.bundle.getString("cancel");
                } else {
                    title = Alchemy.bundle.getString("existsWinDialogTitle");
                    message = "\"" + fileWithExtension.getName() + "\" " + Alchemy.bundle.getString("existsWinDialogMessage");
                    options[0] = Alchemy.bundle.getString("yes");
                    options[1] = Alchemy.bundle.getString("no");
                }


                int result = JOptionPane.showOptionDialog(
                        Alchemy.window,
                        message,
                        title,
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (result != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            this.setSelectedFile(fileWithExtension);
        }
        super.approveSelection();
    }
}
