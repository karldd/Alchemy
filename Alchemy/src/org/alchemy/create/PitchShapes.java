package org.alchemy.create;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.beadsproject.beads.analysis.FeatureExtractor;
import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.analysis.featureextractors.PowerSpectrum;
import net.beadsproject.beads.analysis.featureextractors.SpectralPeaks;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.TimeStamp;
import net.beadsproject.beads.ugens.RTInput;

import org.alchemy.core.*;

public class PitchShapes extends AlcModule implements AlcConstants {

    private AudioContext ac;
    private float weight = 1;
    private float targetWeight = 1;
    private Point2D.Float lastPt;
    float maxWeight = 50;
    float minWeight = 1;
    float minPitch = 50;
    float maxPitch = 1800;
    private AlcSubSlider currentPitchSlider;
    private AlcToolBarSubSection subToolBarSection;

    static class PitchListener extends FeatureExtractor<Float, float[][]> {

        PitchShapes ps;

        public PitchListener(PitchShapes ps) {
            this.ps = ps;
        }

        public void process(TimeStamp a, TimeStamp b, float[][] f) {
            // take the weighted average of all pitch parts
            float weightedSum = 0;
            float energySum = 0;
            for (int i = 0; i < f.length; i++) {
                weightedSum += f[i][0] * f[i][1];
                energySum += f[i][1];

                //if (f[i][1] > 1000)
                //	System.out.printf("%.0f %.2f, ", f[i][0], f[i][1]);
            }

            if (f[0][1] > 1000) {
                weightedSum /= energySum;
                energySum /= f.length;
                System.out.printf("\nWS: %f, E: %f\n", weightedSum, energySum);
                ps.affectDrawingShapeSomehow(weightedSum);
            }
        }
    };

    @Override
    public void setup() {

        createSubToolBarSection();
        toolBar.addSubToolBarSection(subToolBarSection);

        // This function is called when the module is first selected in the menu
        // It will only be called once, so is useful for doing stuff like
        // loading interface elements into the menu bar etc...
        ac = new AudioContext();

        RTInput input = new RTInput(ac);
        ShortFrameSegmenter sfs = new ShortFrameSegmenter(ac);
        sfs.setChunkSize(2048);
        sfs.setHopSize(777);

        sfs.addInput(input);
        ac.out.addDependent(sfs);

        // set up the analysis chain to extract the largest spectral peak (i.e., the primary pitch)
        // 1. build analysis objects
        FFT fft = new FFT();
        PowerSpectrum ps = new PowerSpectrum();
        SpectralPeaks f = new SpectralPeaks(ac, 10);

        //SpectralCentroid sc = new SpectralCentroid(ac.getSampleRate());
        PitchListener pl = new PitchListener(this);

        // 2. connect chain of objects (sfs-->fft-->ps-->f)
        sfs.addListener(fft);
        fft.addListener(ps);
        ps.addListener(f);
        f.addListener(pl);

        // finally start the system
        ac.start();
    }

    private void createSubToolBarSection() {
        subToolBarSection = new AlcToolBarSubSection(this);

        currentPitchSlider = new AlcSubSlider("Current Pitch", (int) minPitch, (int) maxPitch, (int) minPitch);
        subToolBarSection.add(currentPitchSlider);

        subToolBarSection.add(new AlcSubSeparator());

        final AlcSubSlider minP = new AlcSubSlider("Lower Pitch", (int) minPitch, (int) maxPitch, (int) minPitch);
        minP.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!minP.getValueIsAdjusting()) {
                            int value = minP.getValue();
                            minPitch = value;
                        }
                    }
                });
        subToolBarSection.add(minP);

        final AlcSubSlider maxP = new AlcSubSlider("Upper Pitch", (int) minPitch, (int) maxPitch, (int) maxPitch);
        minP.addChangeListener(
                new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        if (!maxP.getValueIsAdjusting()) {
                            int value = maxP.getValue();
                            maxPitch = value;
                        }
                    }
                });
        subToolBarSection.add(maxP);
    }

    protected void affectDrawingShapeSomehow(float pitch) {
        currentPitchSlider.setValue((int) pitch);

        if (pitch < maxPitch && pitch > minPitch) {
            // 200-->maxWeight
            // 600-->minWeight

            float pn = 1 - (pitch - minPitch) / (maxPitch - minPitch);
            setWeight((maxWeight - minWeight) * pn + minWeight);
        } else if (pitch >= maxPitch) {
            setWeight(minWeight);
        } else if (pitch <= minPitch) {
            setWeight(maxWeight);
        }
        //System.out.printf("Freq: %fHz\n", pitch);
    }

    protected void setWeight(float newweight) {
        targetWeight = newweight;
    }

    @Override
    protected void cleared() {
    }

    @Override
    protected void deselect() {
        ac.stop();
        ac = null;
    }

    @Override
    protected void reselect() {
        setup();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // This function is called when the mouse/pen moves
        // It is called A LOT
    }

    @Override
    public void mousePressed(MouseEvent e) {
        AlcShape shape = new AlcShape();
        canvas.createShapes.add(shape);
        shape.spineTo(canvas.getPenLocation(), weight);
        canvas.redraw();
        lastPt = canvas.getPenLocation();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // This function is called when the mouse/pen is dragged
        float dist = targetWeight - weight;
        float dir = dist < 0 ? -1 : 1;
        float amount = 2;
        if (Math.abs(dist) < amount) {
            amount = Math.abs(dist);
        }
        weight += amount * dir;

        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {
            Point2D.Float p = canvas.getPenLocation();
            currentShape.spineTo(p, weight);
            canvas.redraw();
            lastPt = new Point2D.Float(p.x, p.y);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        AlcShape currentShape = canvas.getCurrentCreateShape();
        // Need to test if it is null incase the shape has been auto-cleared
        if (currentShape != null) {
            Point2D.Float p = canvas.getPenLocation();
            canvas.redraw();
            canvas.commitShapes();
        }
    }
}
