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

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The Alchemy Module Class
 * <p>
 * This is an 'abstract class' that must be extended to create an Alchemy module. 
 * <p>
 * This class takes care of basic functionality such as module loading and passing on mouse and other events.
 * It also has access to a bunch of other useful classes such as {@link AlcCanvas}, {@link AlcMath} etc...
 * 
 */
public abstract class AlcModule implements AlcConstants {

    //////////////////////////////////////////////////////////////
    // STATIC MODULE REFERENCES
    //////////////////////////////////////////////////////////////
    /** Access to the Alchemy canvas */
    protected static AlcCanvas canvas;
    /** Access to the Alchemy toolBar */
    protected static AlcAbstractToolBar toolBar;
    /** Access to the Alchemy math class */
    protected static AlcMath math;
    /** Access to the Alchemy color selector */
    protected static AlcColorSelector colorSelector;
    /** Access to the Alchemy window */
    protected static AlcWindow window;
    /** Access to the Alchemy language bundle */
    protected static ResourceBundle bundle;
    //
    //////////////////////////////////////////////////////////////
    // MODULE INSTANCE REFERENCES
    //////////////////////////////////////////////////////////////
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

    //////////////////////////////////////////////////////////////
    // STRUCTURE
    //////////////////////////////////////////////////////////////
    /** Sets global references to the root, canvas, and toolbar */
    void setGlobals(AlcCanvas c, AlcAbstractToolBar t, AlcMath m, AlcColorSelector cs, AlcWindow w, ResourceBundle b) {
        canvas = c;
        toolBar = t;
        math = m;
        colorSelector = cs;
        window = w;
        bundle = b;
    }

    /** Called when the module is first selected in the menu.
     * <p>
     *  It will only be called once, so is useful for doing stuff like
     *  loading interface elements into the menu bar etc....
     * <p>
     * This function should be used instead of a constructor.
     */
    protected void setup() {
    }

    /** Called when the module is reselected in the menu. 
     *  i.e. the module is turned off then on again.
     */
    protected void reselect() {
    }

    /** Called when the module is deselected. */
    protected void deselect() {
    }

    /** Called when the canvas is cleared.
     * <p>
     *  You might sometimes need to use this function if for example you are
     *  counting the number of shapes and you want to know when 
     *  to reset the count to zero.
     */
    protected void cleared() {
    }

    /** Called after all shapes are commited to the buffer.
     * <p>
     * To keep things speedy, shapes are committed to an image buffer when possible
     * so they do not need to be redraw each and every time. When this happens, 
     * the shape will be moved to the shapes array and seemingly dissappear.
     * This function is used to warn each module of dissappearing shapes.
     */
    protected void commited() {
    }

    /**
     *  Apply affect.
     * <p>
     *  Called for every active affect module, before the canvas is redrawn.
     *  This is used by affect modules to 'affect' a shape i.e. apply some sort of change to it.
     *  Typically the affect module will work with all shapes in the canvas.createShapes array
     *  then either replace them or add new shapes to the canvas.affectShapes array.
     */
    protected void affect() {
    }

    //////////////////////////////////////////////////////////////
    // MODULE DATA
    //////////////////////////////////////////////////////////////

    /**
     * Get the name of this module as defined in it's plugin.xml file.
     * <pre>
     * {@code
     * // Will return the text from the XML node below:
     * <parameter id="name" value="This is my name"/> 
     * }
     * </pre>
     * @return The module's name
     */
    protected String getName() {
        return this.moduleName;
    }

    void setName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Get the index of this module in either the 'creates' or 'affects' arraylist
     * @return The index of the module
     */
    protected int getIndex() {
        return this.index;
    }

    void setIndex(int i) {
        this.index = i;
    }

    /** 
     * Get the type of module
     * @return  The type of module - either {@link AlcConstants#MODULE_CREATE} or {@link AlcConstants#MODULE_AFFECT}
     */
    protected int getModuleType() {
        return this.moduleType;
    }

    void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    /** 
     * Loaded state of this module
     * @return  If the module  has been loaded or not
     */
    protected boolean getLoaded() {
        return this.loaded;
    }

    void setLoaded(boolean l) {
        this.loaded = l;
    }

    /** 
     * Returns the classloader to load resources from the plugin .zip
     * @return  ClassLoader reference to this modules .zip
     */
    protected ClassLoader getClassLoader() {
        return this.classLoader;
    }

    void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Get the icon name as defined in it's plugin.xml file.
     * <pre>
     * {@code
     * // Will return the name of the icon file below:
     * <parameter id="icon" value="icon.png" />
     * }
     * </pre>
     * @return  This modules icon name
     */
    protected String getIconName() {
        return this.iconName;
    }

    void setIconName(String n) {
        this.iconName = n;
    }

    /** 
     * Get the Icon URL from within the module's .zip file.
     * <pre>
     * {@code
     * // Will return a URL object to the icon file below:
     * <parameter id="icon" value="icon.png" />
     * }
     * </pre>
     * @return URL linking to this modules icon file
     */
    protected URL getIconUrl() {
        return this.iconUrl;
    }

    void setIconUrl(URL url) {
        this.iconUrl = url;
    }

    /**
     * Get the description of this module as defined in it's plugin.xml file.
     * <pre>
     * {@code
     * // Will return the text from the XML node below:
     * <parameter id="description" value="This is where the description lives!" />
     * }
     * </pre>
     * @return Text description of this module
     */
    protected String getDescription() {
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
     * Called when the mouse is pressed, only if the module is active.
     * @param e The {@link MouseEvent} containing location data.   
     * <p>
     * Useful things you can do with the MouseEvent passed in:
     * <pre>
     * // Get the x location
     * int x = e.getX();
     * // Get the y location
     * int y = e.getY();
     * // Get the location as a point
     * Point p = e.getPoint();
     * </pre>
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Called when the mouse is moved, only if the module is active.
     * @param e The {@link MouseEvent} containing location data.   
     * <p>
     * Useful things you can do with the MouseEvent passed in:
     * <pre>
     * // Get the x location
     * int x = e.getX();
     * // Get the y location
     * int y = e.getY();
     * // Get the location as a point
     * Point p = e.getPoint();
     * </pre>
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Called when the mouse is clicked, only if the module is active.
     * @param e The {@link MouseEvent} containing location data.   
     * <p>
     * Useful things you can do with the MouseEvent passed in:
     * <pre>
     * // Get the x location
     * int x = e.getX();
     * // Get the y location
     * int y = e.getY();
     * // Get the location as a point
     * Point p = e.getPoint();
     * </pre>
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Called when the mouse is Dragged, only if the module is active.
     * @param e The {@link MouseEvent} containing location data.   
     * <p>
     * Useful things you can do with the MouseEvent passed in:
     * <pre>
     * // Get the x location
     * int x = e.getX();
     * // Get the y location
     * int y = e.getY();
     * // Get the location as a point
     * Point p = e.getPoint();
     * </pre>
     */
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Called when the mouse is released, only if the module is active.
     * @param e The {@link MouseEvent} containing location data.   
     * <p>
     * Useful things you can do with the MouseEvent passed in:
     * <pre>
     * // Get the x location
     * int x = e.getX();
     * // Get the y location
     * int y = e.getY();
     * // Get the location as a point
     * Point p = e.getPoint();
     * </pre>
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Called when the mouse enters the canvas, only if the module is active.
     * @param e The {@link MouseEvent} containing location data.   
     * <p>
     * Useful things you can do with the MouseEvent passed in:
     * <pre>
     * // Get the x location
     * int x = e.getX();
     * // Get the y location
     * int y = e.getY();
     * // Get the location as a point
     * Point p = e.getPoint();
     * </pre>
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Called when the mouse exits the canvas, only if the module is active.
     * @param e The {@link MouseEvent} containing location data.   
     * <p>
     * Useful things you can do with the MouseEvent passed in:
     * <pre>
     * // Get the x location
     * int x = e.getX();
     * // Get the y location
     * int y = e.getY();
     * // Get the location as a point
     * Point p = e.getPoint();
     * </pre>
     */
    public void mouseExited(MouseEvent e) {
    }

    //////////////////////////////////////////////////////////////
    // KEY EVENTS
    //////////////////////////////////////////////////////////////
    /**
     * Called when a key is pressed, only when the module is active.
     * @param e The {@link KeyEvent} containing key data.      
     * <p>
     *  Useful things you can do with the KeyEvent passed in:
     * <pre>
     * // Get the character of the key pressed eg "A" 
     * char keyChar = e.getKeyChar();
     * // Get the key code of the key pressed eg 68
     * int keyCode = e.getKeyCode();
     * // Get the text of the key pressed eg "F1"
     * String keyText = e.getKeyText(keyCode);
     * </pre>
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Called when a key is released, only when the module is active.
     * @param e The {@link KeyEvent} containing key data.     
     * <p>
     *  Useful things you can do with the KeyEvent passed in:
     * <pre>
     * // Get the character of the key pressed eg "A" 
     * char keyChar = e.getKeyChar();
     * // Get the key code of the key pressed eg 68
     * int keyCode = e.getKeyCode();
     * // Get the text of the key pressed eg "F1"
     * String keyText = e.getKeyText(keyCode);
     * </pre>
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Called when a key is typed, only when the module is active.
     * @param e The {@link KeyEvent} containing key data.   
     * <p>
     *  Useful things you can do with the KeyEvent passed in:
     * <pre>
     * // Get the character of the key pressed eg "A" 
     * char keyChar = e.getKeyChar();
     * // Get the key code of the key pressed eg 68
     * int keyCode = e.getKeyCode();
     * // Get the text of the key pressed eg "F1"
     * String keyText = e.getKeyText(keyCode);
     * </pre>
     */
    public void keyTyped(KeyEvent e) {
    }
}
