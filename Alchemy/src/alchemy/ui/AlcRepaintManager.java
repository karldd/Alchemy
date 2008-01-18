package alchemy.ui;

import java.awt.Container;
import javax.swing.RepaintManager;
import javax.swing.JComponent;

public class AlcRepaintManager extends RepaintManager {

    public void addDirtyRegion(JComponent comp, int x, int y, int w, int h) {
        super.addDirtyRegion(comp, x, y, w, h);
        // Make sure that the semi transparent AlcMenu is redrawn properly
        if (comp.getClass().getName().endsWith("AlcMenu")) {
            Container parentContainer = comp.getParent();
            if (parentContainer instanceof JComponent) {
                JComponent parent = (JComponent) parentContainer;
                super.addDirtyRegion(parent, 0, 0, parent.getWidth(), parent.getHeight());
            }
        }
    //JComponent root = getRootJComponent(comp);
    // to avoid a recursive infinite loop

    //System.out.println(comp);
//        if (comp != root) {
//            //System.out.println(root.getClass());
//            super.addDirtyRegion(root, 0, 0, root.getWidth(), root.getHeight());
//
//        }
    }

//    public JComponent getRootJComponent(JComponent comp) {
//        Container parent = comp.getParent();
//        if (parent instanceof JComponent) {
//            //return getRootJComponent((JComponent) parent);
//            return (JComponent) parent;
//        }
//        return comp;
//    }
}

