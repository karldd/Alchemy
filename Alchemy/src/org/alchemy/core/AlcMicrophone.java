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

import javax.sound.sampled.*;

/**
 * Base class used for microphone input <br />
 * With very limited functionality at the moment to return a buffer or the current sound level <br />
 * Based on code by Richard G. Baldwin from: http://www.developer.com/java/other/print.php/1572251
 */
public class AlcMicrophone {

    private Thread micThread;
    //An arbitrary-size temporary holding buffer
    private byte audioBytes[];
    private int[] audioSamples;
    private int lengthInSamples;
    private boolean running = false;
    private boolean firstRun = true;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    AlcMicInterface parent;

    /** Creates a new instance of AlcMicrophone
     * @param bufferSize    Size of the audio buffer - affects latency
     */
    public AlcMicrophone(int bufferSize) {
        setup(null, bufferSize);
    }

    /** Creates a new instance of AlcMicrophone
     * 
     * @param parent        Reference to the parent class implementing the AlcMicInterface
     * @param bufferSize    Size of the required buffer
     */
    public AlcMicrophone(AlcMicInterface parent, int bufferSize) {
        setup(parent, bufferSize);
    }

    public AlcMicrophone(AlcMicInterface parent) {
        setup(parent, -1);
    }

    private void setup(AlcMicInterface parent, int bufferSize) {
        if (parent != null) {
            this.parent = parent;
        }
        audioFormat = getAudioFormat();
        System.out.println("Selected Format: " + audioFormat);

        if (bufferSize > 0) {
            setBuffer(bufferSize);
        }
    }

    /** Set the buffer to a certain size 
     * 
     * @param bufferSize Size for the buffer
     */
    public void setBuffer(int bufferSize) {
        audioBytes = new byte[bufferSize];
        lengthInSamples = bufferSize / 2;
        audioSamples = new int[lengthInSamples];
        if (firstRun) {
            openLine();
        }
    }

    /** Open the mic line */
    public void openLine() {
        try {
            //Get everything set up for capture
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            firstRun = false;

        } catch (LineUnavailableException ex) {
            System.err.println("ERROR opening the audio line: " + ex);
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Close the mic line
     *  The line can be opened again by calling setBufer() or openLine() directly
     */
    public void closeLine() {
        stop();
        targetDataLine.stop();
        targetDataLine = null;
        firstRun = true;
    }

    /** Starts Microphone Input */
    public void start() {
        running = true;
        micThread = new Thread() {

            @Override
            public void run() {
                try {
                    //Loop until running is turned off
                    while (running) {
                        //Read data from the internal buffer of the data line.
                        int cnt = targetDataLine.read(audioBytes, 0, audioBytes.length);

                        // When the buffer is full
                        if (cnt > 0) {
                            convertToSamples();
                            // Call back to the parent if it implements the AlcMicInterface
                            if (parent != null) {
                                parent.microphoneEvent();
                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        micThread.start();
    }

    /** Stops Microphone Input */
    public void stop() {
        running = false;
        micThread = null;
    }

    private void convertToSamples() {
        //System.out.println(lengthInSamples);
        if (audioFormat.isBigEndian()) {
            for (int i = 0; i < audioSamples.length; i++) {
                /* First byte is MSB (high order) */
                int MSB = (int) audioBytes[2 * i];
                /* Second byte is LSB (low order) */
                int LSB = (int) audioBytes[2 * i + 1];
                audioSamples[i] = MSB << 8 | (255 & LSB);
            }
        } else {
            for (int i = 0; i < audioSamples.length; i++) {
                /* First byte is LSB (low order) */
                int LSB = (int) audioBytes[2 * i];
                /* Second byte is MSB (high order) */
                int MSB = (int) audioBytes[2 * i + 1];
                audioSamples[i] = MSB << 8 | (255 & LSB);
            }
        }
    }

    /** Get the current Microphone Level
     *  Based on an average value of the buffer
     *  @return     Current Microphone Level
     */
    public double getMicLevel() {
        // TODO - Figure out how to manage the sound level intelligently
        double sum = 0;
        for (int i = 0; i < audioSamples.length; i++) {
            sum += Math.abs(audioSamples[i]);
        }
        double average = (sum / (double) audioSamples.length) / 100;
        return average;
    }

    /** Get the raw buffer */
    public byte[] getBuffer() {
        return audioBytes;
    }

    /** Get the Samples */
    public int[] getSamples() {
        return audioSamples;
    }

    /** Creates and returns an AudioFormat object by walking through the list
     *  of supported formats and returning the first one that matches the criteria.
     *  Failing to find a suitable format it returns a default object
     * 
     * @return AudioFormat object
     */
    private AudioFormat getAudioFormat() {
        Mixer.Info[] mi = AudioSystem.getMixerInfo();
        // Top layer to break out to when we find the correct format
        search:
        for (int i = 0; i < mi.length; i++) {
            //System.out.println(mi[i]);
            Mixer m = AudioSystem.getMixer(mi[i]);

            Line.Info[] tli = m.getTargetLineInfo();

            for (int j = 0; j < tli.length; j++) {
                //System.out.println("target: " + tli[j]);

                try {
                    AudioFormat[] formats = ((DataLine.Info) tli[j]).getFormats();
                    for (int k = 0; k < formats.length; k++) {
                        AudioFormat thisFormat = formats[k];
                        //System.out.println("    " + thisFormat);
                        // Get the first mono / 2frame / 16 bit format from the list
                        if (thisFormat.getChannels() == 1 &&
                                thisFormat.getFrameSize() == 2 &&
                                thisFormat.getSampleSizeInBits() == 16) {

                            audioFormat = thisFormat;
                            // If a match is found break out to the top
                            break search;
                        }
                    }
                } catch (ClassCastException e) {
                //e.printStackTrace();
                }
            }
        //System.out.println();
        }

        if (audioFormat.getSampleRate() == AudioSystem.NOT_SPECIFIED) {
            System.out.println("Sample Rate not specified, assigning 441000hz");
            audioFormat = new AudioFormat(
                    //audioFormat.getEncoding(),
                    //32000F,
                    41000F,
                    //audioFormat.getSampleRate(),
                    audioFormat.getSampleSizeInBits(),
                    audioFormat.getChannels(),
                    true,
                    //audioFormat.getFrameSize(),
                    //audioFormat.getFrameRate(),
                    //false);
                    audioFormat.isBigEndian());
        }

        if (audioFormat == null) {
            audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);
            System.err.println("No audio format found, assigning the default format");
        }
        return audioFormat;
    }
}
