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

import alchemy.AlcModule;
import java.awt.FlowLayout;
import javax.swing.JPanel;

/**
 * AlcSubToolBarSection
 * 
 */
public class AlcSubToolBarSection extends JPanel {

    private final AlcModule module;

    public AlcSubToolBarSection(AlcModule module) {

        this.module = module;
        this.setOpaque(false);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        this.add(new AlcSubLabel(module.getName()));


    }

    protected int getIndex() {
        return module.getIndex();
    }

    protected int getModuleType() {
        return module.getModuleType();
    }
}
