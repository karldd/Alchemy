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

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * AlcSeparator
 * 
 * 
 */
public class AlcSeparator extends JLabel {

    public AlcSeparator(AlcToolBar parent) {
        
        this.setIcon(parent.createImageIcon("data/separator.png"));
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        
        // Top Left Bottom Right
        this.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 4));
        //this.setLocation(this.getX(), this.getY()-10);
    }
}

