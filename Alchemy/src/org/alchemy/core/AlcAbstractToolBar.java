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

import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * Abstract class to allow multiple toolbar implementations <br>
 * Eg, the regular toolbar as well as a simplified kids one
 * 
 */
public abstract class AlcAbstractToolBar extends JPanel implements AlcConstants {

    /** Keep track of the windowSize */
    Dimension windowSize;
    /** Width of the toolbar */
    int toolBarWidth;
    
    boolean isToolBarVisible() {
        return this.isVisible();
    }

    int getTotalHeight() {
        return this.getHeight();
    }

    void toggleToolBar(int y) {
    }

    void toggleToolBar(int y, boolean startTimer) {
    }

    void setToolBarVisible(boolean visible) {
    }

    void removeSubToolBarSection(int index) {
    }

    void refreshColorButton() {
    }

    void refreshTransparencySlider() {
    }

    void resizeToolBar() {
    }

    void resizeToolBar(Dimension windowSize) {
    }
    
    void toggleSubSection(AlcToolBarSubSection subSection){
    }

    void calculateTotalHeight() {
    }

    void detachToolBar() {
    }

    void attachToolBar() {
    }

    void toggleDetachButton(boolean visible) {
    }

    void addPaletteContent() {
    }
    
    void setSwatchLRButtons() {   
    }
    void disableUndo() {   
    }
    void enableUndo() {   
    }
    void setZoomButtonSelected(){
    }
    void refreshRClickPicker(){
    }
    void flipToolBar(){
    }
    void refreshSwatch(){
        
    }

    public void addSubToolBarSection(AlcToolBarSubSection subSection) {
    }

    /** Get a string from the resource bundle */
    String getS(String stringName) {
        return Alchemy.bundle.getString(stringName);
    }

    /** Check if the module should be loaded or not */
    boolean loadModule(AlcModule module) {
        String moduleName = module.getName();
        String moduleNodeName = Alchemy.preferences.modulePrefix + moduleName;
        return AlcPreferences.prefs.getBoolean(moduleNodeName, true);
    }
}
