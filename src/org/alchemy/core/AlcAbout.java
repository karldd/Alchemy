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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The Alchemy about window
 */
class AlcAbout extends Window implements AlcConstants {

    private final Image image;
    private final Dimension size;

    AlcAbout(Window owner) {
        super(owner);

        image = AlcUtil.getImage("about.png");
        size = new Dimension(image.getWidth(null), image.getHeight(null));

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                setVisible(false);
                dispose();
            }
        });

        this.setSize(size);
        Point loc = AlcUtil.calculateCenter(this);
        this.setBounds(loc.x, loc.y, size.width, size.height);
        this.setVisible(true);

    }

    @Override
    public void paint(Graphics g) {
        int x = size.width / 2;

        g.drawImage(image, 0, 0, null);

        //Graphics2D g2 = (Graphics2D) g;
        //g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        g.setFont(FONT_SMALL);
        g.setColor(Color.white);
        g.drawString(Alchemy.bundle.getString("version.string"), 360, 277);

    }
}
