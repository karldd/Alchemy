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
package alchemy;

import alchemy.ui.AlcToolBar;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.net.URL;

/**
 * Alchemy module <br />
 * This is an 'abstract class' which must be extended to make an Alchemy plugin
 * 
 */
public abstract class AlcModule {

    /** Access to the root */
    protected AlcMain root;
    /** Access to the canvas */
    protected AlcCanvas canvas;
    /** Access to the toolBar */
    protected AlcToolBar toolBar;
    /** ClassLoaded used to load other resources from the plugin */
    private ClassLoader classLoader;
    /** The name of this module */
    private String moduleName;
    /** The type of module  - either CREATE (0) or AFFECT (1) */
    private int moduleType;
    /** The description of this module as documented in the modules plugin.xml file */
    private String description;
    /** The name of the icon used for this modules button as documented in the modules plugin.xml file */
    private String iconName;
    /** The full icon url for this modules button */
    private URL iconUrl;
    /** The index of this module in the "root.creates" or "root.affects" array */
    private int index;
    /** The loaded state of this module - set to true when the module is first selected */
    private boolean loaded = false;
    /** Sort order variable determines the order of display in the popup menu */
    private int sortIndex = -1;

    public AlcModule() {
    }

    //////////////////////////////////////////////////////////////
    // STRUCTURE
    //////////////////////////////////////////////////////////////
    /** Sets global references to the root, canvas, and toolbar */
    void setGlobals(AlcMain root, AlcCanvas canvas, AlcToolBar toolBar) {
        this.root = root;
        this.canvas = canvas;
        this.toolBar = toolBar;
    }

    /** Called to load the module when first run */
    protected void setup() {
    }

    /** Called when the module is reselected */
    protected void reselect() {
    }

    /** Called when the module is deselected */
    protected void deselect() {
    }

    /** Called after the canvas is cleared */
    protected void cleared() {
    }

    /** Called after all shapes are commited */
    protected void commited() {
    }

    /**
     *  Apply affect
     *  Called for every active affect module, before the canvas is redrawn.
     *  This is used by affect modules to 'affect' a shape, apply some sort of change to it
     *  Typically the affect module will work with all shapes in the canvas.createShapes array
     *  then either replace them or add new shapes to the canvas.affectShapes array
     */
    protected void affect() {
    }
    
    /**
     * Affect Shapes
     * Passes the location of the cursor and a list of 'active' shapes which the cursor is 'inside' of.
     * 
     * If there are no active shapes, the activeShapes array will be null.
     * This is useful for determining when to stop processing other stuff.
     * 
     * Called for every active affect module after mouse events have been passed.
     * 
     * @param activeShapes      Array of 'active' shapes
     * @param cursorLocation    The location of the cursor
     */
     protected void affectShapes(int[] activeShapes, Point cursorLocation) {
     }

    //////////////////////////////////////////////////////////////
    // MODULE DATA
    //////////////////////////////////////////////////////////////
    /** 
     *  Get the name of this module
     *  @return The modules name
     */
    public String getName() {
        return this.moduleName;
    }

    void setName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Get the index of this module in either the 'creates' or 'affects' arraylist
     * @return The index of the module
     */
    public int getIndex() {
        return this.index;
    }

    void setIndex(int i) {
        this.index = i;
    }

    /** 
     * Get the type of module
     * @return  The type of module - either "CREATE" (0) or "AFFECT" (1)
     */
    public int getModuleType() {
        return this.moduleType;
    }

    void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    /** 
     * Loaded state of this module
     * @return  If the module  has been loaded or not
     */
    public boolean getLoaded() {
        return this.loaded;
    }

    void setLoaded(boolean l) {
        this.loaded = l;
    }

    /** 
     * Returns the classloader to load resources from the plugin .zip
     * @return  ClassLoader reference to this modules .zip
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Get the icon name
     * @return  This modules icon name
     */
    public String getIconName() {
        return this.iconName;
    }

    void setIconName(String n) {
        this.iconName = n;
    }

    /** 
     * Get the Icon URL from within the modules .zip file
     * @return URL linking to this modules icon file
     */
    public URL getIconUrl() {
        return this.iconUrl;
    }

    void setIconUrl(URL url) {
        this.iconUrl = url;
    }

    /**
     * Get the description of this module as defined in it's plugin.xml file
     * @return Text description of this module
     */
    public String getDescription() {
        return this.description;
    }

    void setDescription(String n) {
        this.description = n;
    }

    /**
     * Get the sort index to determine the display order of the module
     * @retun The sort index
     */
    int getSortOrderIndex() {
        return this.sortIndex;
    }

    void setSortOrderIndex(int i) {
        this.sortIndex = i;
    }


    //////////////////////////////////////////////////////////////
    // MOUSE EVENTS
    //////////////////////////////////////////////////////////////
    /**
     * The below mouse events are called when the modules is active (selected by the user)
     * The full MouseEvent is passed in, as described here:
     * <p/>
     * http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/MouseEvent.html
     * <p/>
     * Useful things you can do with this MouseEvent:
     * <pre>
     * int x = e.getX();
     * int y = e.getY();
     * Point p = e.getPoint();
     * </pre>
     */
    public void mousePressed(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    //////////////////////////////////////////////////////////////
    // KEY EVENTS
    //////////////////////////////////////////////////////////////
    /**
     * The below key events are called when the modules is active (selected by the user)
     * The full KeyEvent is passed in, as described here:
     * <p/>
     * http://java.sun.com/j2se/1.4.2/docs/api/java/awt/event/KeyEvent.html
     * <p/>
     * Useful things you can do with this KeyEvent:
     * <pre>
     *  char keyChar = e.getKeyChar();
     *  int keyCode = e.getKeyCode();
     *  String keyText = e.getKeyText(keyCode);
     * </pre>
     */
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }
}
