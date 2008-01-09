/*
 *  This file is part of the Alchemy project - http://al.chemy.org
 * 
 *  Copyright (c) 2007 Karl D.D. Willis
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
package alchemy.ui;

import alchemy.AlcUtil;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Point;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class AlcFileChooser extends JFileChooser {

    public AlcFileChooser() {
        super();
    }

    public AlcFileChooser(File currentDirectory) {
        super(currentDirectory);
    }

    
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        Point p = AlcUtil.calculateCenter(dialog);
        dialog.setLocation(p.x, p.y);
        dialog.setResizable(false);
        return dialog;
    }
}
