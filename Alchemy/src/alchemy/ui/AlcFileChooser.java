/*
 *   Part of the Alchemy project - http://al.chemy.org
 * 
 *   Copyright (c) 2007 Karl D.D. Willis
 * 
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 * 
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 * 
 *   You should have received a copy of the GNU Lesser General
 *   Public License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA  02111-1307  USA
 */
package alchemy.ui;

import alchemy.AlcUtil;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Point;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 * AlcFileChooser
 * 
 * 
 */
public class AlcFileChooser extends JFileChooser {

    public AlcFileChooser() {
        super();
    }

    public AlcFileChooser(File currentDirectory) {
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
}
