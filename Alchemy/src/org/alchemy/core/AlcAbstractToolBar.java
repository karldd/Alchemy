/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.alchemy.core;

import java.awt.Color;
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
    //////////////////////////////////////////////////////////////
    // INTERFACE COLOURS
    //////////////////////////////////////////////////////////////
    static final Color toolBarBgColour = new Color(225, 225, 225);
    static final Color toolBarBgStartColour = new Color(235, 235, 235, 240);
    static final Color toolBarBgEndColour = new Color(215, 215, 215, 240);
    static final Color toolBarLineColour = new Color(140, 140, 140);
    static final Color toolBarSubLineColour = new Color(160, 160, 160);
    static final Color toolBarHighlightColour = new Color(231, 231, 231);
    static final Color toolBarAlphaHighlightColour = new Color(231, 231, 231, 240);
    static final Color toolBarBoxColour = new Color(190, 190, 190);

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

    void refreshColourButton() {
    }

    void queueColourButtonRefresh() {
    }

    void toggleColourButton() {
    }

    void refreshTransparencySlider() {
    }

    void resizeToolBar() {
    }

    void resizeToolBar(Dimension windowSize) {
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

    public void addSubToolBarSection(AlcToolBarSubSection subToolBarSection) {
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
