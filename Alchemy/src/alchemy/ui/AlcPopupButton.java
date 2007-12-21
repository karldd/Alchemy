/**
 * AlcPopupButton.java
 *
 * Created on December 2, 2007, 10:27 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */
package alchemy.ui;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class AlcPopupButton extends AlcButton {

    private final int uiPopupMenuY = parent.getToolBarHeight() - 10;
    private AlcPopupMenu popup;

    /** Creates a new instance of AlcPopupButton */
    public AlcPopupButton(AlcToolBar parent, String text, String toolTip, URL iconUrl) {
        super(parent, text, toolTip, iconUrl);
        popup = new AlcPopupMenu(parent);

        // Add a mouse listener to detect when the button is pressed and display the popup menu
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                popup.show(e.getComponent(), 0, uiPopupMenuY);
            }
            });

    }

    /** Add an interface element to the popup menu */
    public void addItem(Component item) {
        popup.add(item);
    }

    /** Hide the popup menu */
    public void hidePopup() {
        popup.setVisible(false);
    }
}
