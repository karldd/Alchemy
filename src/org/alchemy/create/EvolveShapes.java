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
package org.alchemy.create;

import java.awt.geom.*;
import javax.swing.event.ChangeEvent;
import org.alchemy.core.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * EvolveShapes
 * @author Karl D.D. Willis
 */
public class EvolveShapes extends AlcModule {

    private AlcShape[] shapes;
    private AlcToolBarSubSection subToolBarSection;
    private float mutation = 0.5F;
    // Interface
    JPanel shapePanel = null;
    JScrollPane scrollPane = null;
    //
    // Timing
    private long mouseDelayGap = 51;
    private long mouseDelayTime;
    //
    Point2D.Float pen = new Point2D.Float();
    Point2D.Float oldPen = new Point2D.Float();


    @Override
    protected void setup() {

        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

        ArrayList<AlcShape> canvasShapes = canvas.normailzeShapes(canvas.shapes, 100);
        if (canvasShapes.size() > 0) {

            for (int i = 0; i < canvasShapes.size(); i++) {
                AlcShape shape = canvasShapes.get(i);
                Rectangle bounds = shape.getBounds();
                if (bounds.width == 0 && bounds.height == 0) {
                    canvasShapes.remove(i);
                }
            }

            shapes = new AlcShape[canvasShapes.size()];
            canvasShapes.toArray(shapes);
        }

    }

    @Override
    protected void reselect() {
        toolBar.addSubToolBarSection(subToolBarSection);
    }

    @Override
    protected void cleared() {
    }

    public void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        // Evolve Button
        AlcSubButton evolveButton = new AlcSubButton("Evolver", AlcUtil.getUrlPath("evolver.png", getClassLoader()));
        evolveButton.setToolTipText("Evolve shapes using a genetic algorithm");
        evolveButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        launchEvolver();
                    }
                });
        subToolBarSection.add(evolveButton);

        // Spacing Slider
        int initialSliderValue = 5;
        final AlcSubSlider spacingSlider = new AlcSubSlider("Spacing", 0, 100, initialSliderValue);
        spacingSlider.setToolTipText("Adjust the spacing interval");
        spacingSlider.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!spacingSlider.getValueIsAdjusting()) {
                            int value = spacingSlider.getValue();
                            mouseDelayGap = 1 + value * 2;
                        }
                    }
                });
        subToolBarSection.add(spacingSlider);


    }

    private void launchEvolver() {
        final JDialog evolver = new JDialog(window, "Evolver", true);

        final JPanel masterPanel = new JPanel();
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.PAGE_AXIS));
        masterPanel.setOpaque(true);
        masterPanel.setBackground(AlcToolBar.COLOR_UI_HIGHLIGHT);
        masterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //////////////////////////////////////////////////////////////
        // EVOLVE PANEL
        //////////////////////////////////////////////////////////////
        // Load from Canvas
        JButton loadCanvasButton = new JButton("Canvas");
        loadCanvasButton.setToolTipText("Load shapes from the canvas");
        loadCanvasButton.setFont(FONT_MEDIUM);
        loadCanvasButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        shapePanel = layoutShapePanelFromCanvas();
                        scrollPane.setViewportView(shapePanel);
                        evolver.repaint();
                    }
                });

        // Load from PDF
        JButton loadPDFButton = new JButton("PDF...");
        loadPDFButton.setToolTipText("Load shapes from a PDF file");
        loadPDFButton.setFont(FONT_MEDIUM);
        loadPDFButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        File file = AlcUtil.showFileChooser();
                        if(file != null && file.exists()){
                            shapePanel = layoutShapePanelFromPDF(file);
                            scrollPane.setViewportView(shapePanel);
                            evolver.repaint();
                        }
                    }
                });


        // Mutation Spinner
        final JSlider mutationSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
        mutationSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                mutation = mutationSlider.getValue() / 100f;
            }
        });


        String mutationToolTip = "Set the mutation rate";
        mutationSlider.setPreferredSize(new Dimension(100, 25));
//        mutationSlider.setMaximumSize(new Dimension(100, 25));
        mutationSlider.setToolTipText(mutationToolTip);
        JLabel mutationLabel = new JLabel("Mutation");
        mutationLabel.setFont(FONT_MEDIUM);
        mutationLabel.setToolTipText(mutationToolTip);

        // Evolve
        JButton evolveButton = new JButton("Evolve");
        evolveButton.setToolTipText("Evolve the next generation of shapes");
        evolveButton.setFont(FONT_MEDIUM);
        evolveButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        evolveNextGeneration();
                        shapePanel = layoutShapePanel();
                        scrollPane.setViewportView(shapePanel);
                        evolver.repaint();
                    }
                });


        JPanel evolvePane = new JPanel();
        evolvePane.setOpaque(false);
        evolvePane.setLayout(new BoxLayout(evolvePane, BoxLayout.LINE_AXIS));
        evolvePane.setBorder(BorderFactory.createEmptyBorder(2, 0, 10, 0));
        evolvePane.add(loadCanvasButton);
        evolvePane.add(Box.createRigidArea(new Dimension(10, 0)));
        evolvePane.add(loadPDFButton);
        evolvePane.add(Box.createHorizontalGlue());
        evolvePane.add(mutationLabel);
        evolvePane.add(mutationSlider);
        evolvePane.add(Box.createHorizontalGlue());
        evolvePane.add(evolveButton);
        masterPanel.add(evolvePane);


        //////////////////////////////////////////////////////////////
        // SCROLL PANE
        //////////////////////////////////////////////////////////////
        if(shapes == null || shapes.length == 0){
            shapePanel = layoutShapePanelFromCanvas();
        } else {
            shapePanel = layoutShapePanel();
        }
        //Create the scroll pane and add the panel to it.
        scrollPane = new JScrollPane(shapePanel);
        //scrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        //Add the scroll pane to this panel.
        masterPanel.add(scrollPane);

        // Cancel
        JButton cancelButton = new JButton(bundle.getString("cancel"));
        cancelButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        evolver.setVisible(false);
                    }
                });

        // OK
        JButton okButton = new JButton(bundle.getString("ok"));
        okButton.setMnemonic(KeyEvent.VK_ENTER);
        okButton.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        evolver.setVisible(false);
                    }
                });

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPane.add(Box.createHorizontalGlue());
        if (Alchemy.OS == OS_MAC) {
            buttonPane.add(cancelButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(okButton);
        } else {
            buttonPane.add(okButton);
            buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
            buttonPane.add(cancelButton);
        }


        masterPanel.add(buttonPane);
        evolver.getContentPane().add(masterPanel);

        evolver.setResizable(false);
        evolver.pack();
        Point loc = AlcUtil.calculateCenter(evolver);
        evolver.setLocation(loc.x, loc.y);
        okButton.requestFocusInWindow();
        evolver.setVisible(true);
        

        AlcUtil.registerWindowCloseKeys(evolver.getRootPane(), new AbstractAction() {

            public void actionPerformed(ActionEvent actionEvent) {
                evolver.setVisible(false);
            }
        });
    }


    //////////////////////////////////////////////////////////////
    // LAYOUT SHAPE PANEL
    //////////////////////////////////////////////////////////////
    private JPanel layoutShapePanelFromCanvas() {
        ArrayList<AlcShape> canvasShapes = canvas.normailzeShapes(canvas.shapes, 100);
        if (canvasShapes.size() > 0) {

            for (int i = 0; i < canvasShapes.size(); i++) {
                AlcShape shape = canvasShapes.get(i);
                Rectangle bounds = shape.getBounds();
                if(bounds.width == 0 && bounds.height == 0){
                    canvasShapes.remove(i);
                }
            }

            shapes = new AlcShape[canvasShapes.size()];
            canvasShapes.toArray(shapes);
            return layoutShapePanel();
        } else {
            JPanel panel = new JPanel();
            JLabel message = new JLabel("Please load shapes from the canvas or a PDF file to begin");
            message.setFont(FONT_MEDIUM);
            panel.add(message, BorderLayout.CENTER);
            return panel;
        }
    }
    
    private JPanel layoutShapePanelFromPDF(File file) {
        ArrayList<AlcShape> canvasShapes = (ArrayList<AlcShape>) AlcUtil.getPDFShapes(file, true, 100);
        if (canvasShapes.size() > 0) {

            for (int i = 0; i < canvasShapes.size(); i++) {
                AlcShape shape = canvasShapes.get(i);
                Rectangle bounds = shape.getBounds();
                if(bounds.width == 0 && bounds.height == 0){
                    canvasShapes.remove(i);
                }
            }

            shapes = new AlcShape[canvasShapes.size()];
            canvasShapes.toArray(shapes);
            return layoutShapePanel();
        }
        return null;
    }

    private JPanel layoutShapePanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        for (int i = 0; i < shapes.length; i++) {

            JPanel shapeSpinner = new JPanel();
            //shapeSpinner.setBorder(BorderFactory.createLineBorder(COLOR_UI_LINE, 1));
            shapeSpinner.setPreferredSize(new Dimension(100, 150));


            final int index = i;
            JPanel singleShape = new JPanel() {

                @Override
                public void paintComponent(Graphics g) {

                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(Color.BLACK);
                    g2.fill(shapes[index].getPath());

                }
            };
            singleShape.setPreferredSize(new Dimension(100, 100));
            shapeSpinner.add(singleShape, BorderLayout.CENTER);

            final AlcSubSpinner spinner = new AlcSubSpinner(null, 0, 100, 0, 1);
            spinner.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    int value = spinner.getValue();
                    shapes[index].setSortIndex(value);
                }
            });
            shapeSpinner.add(spinner, BorderLayout.PAGE_END);
            panel.add(shapeSpinner);
        }
        int height = (int) (Math.ceil(shapes.length / (float) 5) * 160);
        panel.setPreferredSize(new Dimension(600, height));
        return panel;
    }

    //////////////////////////////////////////////////////////////
    // EVOLVE NEXT GENERATION
    //////////////////////////////////////////////////////////////
    private void evolveNextGeneration() {
        // Sort the shapes according to inputed values
        Arrays.sort(shapes, new Comparator<AlcShape>() {

            public int compare(AlcShape s1, AlcShape s2) {
                if (s1.getSortIndex() > s2.getSortIndex()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        ArrayList<AlcShape> crossBreedShapes = new ArrayList<AlcShape>(shapes.length);
        int rankingCount = 0;

        for (int i = 0; i < shapes.length - 1; i++) {
            AlcShape thisShape = shapes[i];
            AlcShape nextShape = shapes[i + 1];
            if (thisShape.getSortIndex() > 0 || nextShape.getSortIndex() > 0) {

                int total = thisShape.getSortIndex() + nextShape.getSortIndex();
                // Weight the second shape as dominant
                float ratio = thisShape.getSortIndex() / (float) total;

                // Pass in the dominant shape first
                AlcShape crossBreed = breedShapes(thisShape, nextShape, ratio);
                crossBreed.setSortIndex(total);
                // Assign the new shape to the next
                crossBreedShapes.add(crossBreed);

                rankingCount++;
            }
        }


        // If no shapes have been ranked
        // Generate random mutations
        if (rankingCount == 0) {
            for (int i = 0; i < shapes.length; i++) {
                AlcShape mutant = mutateShape(shapes[i]);
                crossBreedShapes.add(mutant);
            }

        } else {

            if (crossBreedShapes.size() > 0) {
                // The master shape
                AlcShape masterShape = crossBreedShapes.get(crossBreedShapes.size() - 1);
                // Need to generate this many mutations
                int extras = shapes.length - crossBreedShapes.size();

                // Blend the master shape with some other shape
                for (int i = 0; i < extras; i++) {
                    int num = (int) math.random(shapes.length);
                    float mix = math.random(1);
                    AlcShape extraShape = breedShapes(masterShape, shapes[num], mix);
                    extraShape = mutateShape(extraShape);
                    crossBreedShapes.add(extraShape);
                }

            }

        }

        for(AlcShape shape : crossBreedShapes){
            shape.setSortIndex(0);
        }

        crossBreedShapes = canvas.normailzeShapes(crossBreedShapes, 100);
        Collections.shuffle(crossBreedShapes);
        crossBreedShapes.toArray(shapes);
        

    }

    /** Breed two shapes together at a given mix rate
     *
     * @param s1    The dominant shape
     * @param s2    The dominated shape
     * @param mix   The rate at which to mix, scale of 0f - 1f
     * @return      The cross-bred shape
     */
    private AlcShape breedShapes(AlcShape s1, AlcShape s2, float mix) {

        ArrayList<Point2D.Float> pts1 = s1.getPoints();
        ArrayList<Point2D.Float> pts2 = s2.getPoints();

        // Check if these shapes are winding in the same clockwise/anticlockwise direction
        boolean pts1Wind = getWinding(pts1);
        boolean pts2Wind = getWinding(pts2);
        // If not, reverse the point order of the second shape
        if (pts1Wind != pts2Wind) {
            Collections.reverse(pts2);
        }

        // The number of points for the crossbred shape
        int newPointTotal = Math.round((pts1.size() * mix) + (pts2.size() * (1f - mix)));
        GeneralPath newPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, newPointTotal);

        // For the number of points in the new shape
        for (int i = 0; i < newPointTotal; i++) {
            // Current points from shape 1 & 2
            int current1 = (int) Math.floor(AlcMath.map(i, 0, newPointTotal - 1, 0, pts1.size() - 1));
            int current2 = (int) Math.floor(AlcMath.map(i, 0, newPointTotal - 1, 0, pts2.size() - 1));

            Point2D.Float newPoint = breedPoints(pts1.get(current1), pts2.get(current2), mix);

            if (i == 0) {
                newPath.moveTo(newPoint.x, newPoint.y);
            } else {
                newPath.lineTo(newPoint.x, newPoint.y);
            }

        }

        AlcShape newShape = s1.customClone(newPath);

        return newShape;
    }

    /** Get the winding direction of a set of points
     *
     * @param points    An array list of points on the perimeter
     * @return          True if the points are in a clockwise direction, else false
     */
    private boolean getWinding(ArrayList<Point2D.Float> points) {
        float total = 0;
        for (int i = 0; i < points.size(); i++) {
            Point2D.Float thisPoint = points.get(i);
            Point2D.Float prevPoint, nextPoint;
            int sizeMinusOne = points.size() - 1;
            if (i == 0) {
                prevPoint = points.get(sizeMinusOne);
            } else {
                prevPoint = points.get(i - 1);
            }
            if (i == sizeMinusOne) {
                nextPoint = points.get(0);
            } else {
                nextPoint = points.get(i + 1);
            }
            total += thisPoint.x * (nextPoint.y - prevPoint.y);
        }
        total /= 2;
        return (total > 0) ? true : false;

    }

    private Point2D.Float breedPoints(Point2D.Float pt1, Point2D.Float pt2, float mix) {
        Point2D.Float newPoint = new Point2D.Float(pt1.x, pt1.y);
        float xDiff = pt2.x - pt1.x;
        float yDiff = pt2.y - pt1.y;
        float imix = 1f - mix;
        newPoint.x += xDiff * imix;
        newPoint.y += yDiff * imix;
        return newPoint;
    }

    private AlcShape mutateShape(AlcShape shape) {

        Rectangle bounds = shape.getBounds();
        bounds.width /= 2;
        bounds.height /= 2;
        ArrayList<Point2D.Float> points = shape.getPoints();

        int mutationRate = (int) AlcMath.map(mutation, 0, 1, 50, 0);
        mutationRate *= mutationRate;
        

        for (int i = 0; i < points.size(); i++) {
            Point2D.Float thisPoint = points.get(i);

            // Mutate this point
            if ((int) math.random(mutationRate) == 0) {
                thisPoint.x += math.random(-bounds.width, bounds.width);
                thisPoint.y += math.random(-bounds.height, bounds.height);
            }

            points.set(i, thisPoint);
        }

        int smoothRate = (int) AlcMath.map(mutation, 0, 1, 1, 35);

        for (int i = 0; i < (int) math.random(smoothRate); i++) {
            points = smooth(points);
        }

        GeneralPath newPath = new GeneralPath(GeneralPath.WIND_NON_ZERO, points.size());
        for (int i = 0; i < points.size(); i++) {
            Point2D.Float thisPoint = points.get(i);
            if (i == 0) {
                newPath.moveTo(thisPoint.x, thisPoint.y);
            } else {
                newPath.lineTo(thisPoint.x, thisPoint.y);
            }
        }

        return shape.customClone(newPath);
    }

    /** Return an AlcShape with the given text at the given location */
    private AlcShape getText(String string, Point location) {
        AffineTransform transform = new AffineTransform();
        transform.translate(location.x, location.y);
        FontRenderContext fontRenderContext = new FontRenderContext(transform, false, false);
        GlyphVector gv = AlcUtil.FONT_LARGE.createGlyphVector(fontRenderContext, string);
        Shape shape = gv.getOutline();
        GeneralPath gp = new GeneralPath(shape);
        gp = (GeneralPath) gp.createTransformedShape(transform);
        return new AlcShape(gp, Color.BLACK, 255, STYLE_FILL, 1);
    }

    /** Smooth the points  */
    private ArrayList<Point2D.Float> smooth(ArrayList<Point2D.Float> points) {
        for (int i = 0; i < points.size(); i++) {

            if (i != 0 && i != points.size() - 1) {
                Point2D.Float p0 = points.get(i - 1);
                Point2D.Float p1 = points.get(i);
                Point2D.Float p2 = points.get(i + 1);
                // Average the 3 points
                float x = p0.x * 0.25F + p1.x * 0.5F + p2.x * 0.25F;
                float y = p0.y * 0.25F + p1.y * 0.5F + p2.y * 0.25F;
                points.set(i, new Point2D.Float(x, y));
            }
        }
        return points;
    }

    private void addRandomShape(MouseEvent e) {

        int rand = (int) math.random(shapes.length);

        // Clone the shape
        AlcShape cloneShape = (AlcShape) shapes[rand].clone();
        // Scale it
        float scaleFactor = canvas.getPenPressure() * 2;
        if(canvas.getPenType() == PEN_CURSOR){
            scaleFactor = 1;
        }
        cloneShape.scale(scaleFactor, scaleFactor);

        // Rotate it
        cloneShape.rotate(getDirection());
        // Move it into place
        Rectangle bounds = cloneShape.getBounds();
        int x = e.getX() - (bounds.width >> 1);
        int y = e.getY() - (bounds.height >> 1);
        cloneShape.move(x, y);

        cloneShape.setup();
        canvas.createShapes.add(cloneShape);
        canvas.redraw();
    }

    private double getDirection(){
        return Math.atan2(oldPen.y - pen.y, oldPen.x - pen.x);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (shapes != null && shapes.length > 0) {
            
            //System.out.println("Current Folder = " + currentFolder);
            mouseDelayTime = System.currentTimeMillis();
            addRandomShape(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
         if (shapes != null && shapes.length > 0) {
            pen = canvas.getPenLocation();
            
            if (System.currentTimeMillis() - mouseDelayTime >= mouseDelayGap) {
                
                mouseDelayTime = System.currentTimeMillis();
                //System.out.println(e.getPoint());
                addRandomShape(e);
                oldPen = pen;
            }
            
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (shapes != null && shapes.length > 0) {
            canvas.commitShapes();
        }
    }
}
