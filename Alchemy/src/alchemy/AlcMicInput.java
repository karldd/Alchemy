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

import javax.sound.sampled.*;

/**
 * Base class used for microphone input <br />
 * With very limited functionality at the moment to return a buffer or the current sound level <br />
 * Based on code by Richard G. Baldwin from: http://www.developer.com/java/other/print.php/1572251
 */
public class AlcMicInput extends Thread {

    //An arbitrary-size temporary holding buffer
    private byte audioBytes[];
    private int[] audioSamples;
    private int lengthInSamples;
    private boolean stopMicInput = false;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    AlcMicInterface parent;

    /** Creates a new instance of AlcMicInput */
    public AlcMicInput(int bufferSize) {
        audioBytes = new byte[bufferSize];
        lengthInSamples = bufferSize / 2;
        audioSamples = new int[lengthInSamples];
    }

    /** Creates a new instance of AlcMicInput
     * 
     * @param parent        Reference to the parent class implementing the AlcMicInterface
     * @param bufferSize    Size of the required buffer
     */
    public AlcMicInput(AlcMicInterface parent, int bufferSize) {
        this.parent = parent;
        audioBytes = new byte[bufferSize];
        lengthInSamples = bufferSize / 2;
        audioSamples = new int[lengthInSamples];
    }

    /** Starts Microphone Input */
    public void startMicInput() {
        stopMicInput = false;
        try {
            //Get everything set up for capture
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            // Start the thread
            this.start();

        } catch (Exception e) {

            System.out.println("startMicInput: " + e);

        }
    }

//    private int getSixteenBitSample(int high, int low) {
//        return (high << 8) + (low & 0x00ff);
//    }
    /** Stops Microphone Input */
    public void stopMicInput() {
        stopMicInput = true;
    }

    public void run() {
        stopMicInput = false;

        try {
            //Loop until stopMicInput is turned off
            while (!stopMicInput) {
                //Read data from the internal buffer of the data line.
                int cnt = targetDataLine.read(audioBytes, 0, audioBytes.length);

                // When the buffer is full
                if (cnt > 0) {
                    // Call back to the parent if it implements the AlcMicInterface
                    if (parent != null) {

//                        int sampleIndex = 0;
//
//                        for (int t = 0; t < tempBuffer.length;) {
//                            int low = (int) tempBuffer[t];
//                            t++;
//                            int high = (int) tempBuffer[t];
//                            t++;
//                            int sample = getSixteenBitSample(high, low);
//                            sampleArray[sampleIndex] = sample;
//                            sampleIndex++;
//                        }

                        convertToSamples();
                        parent.bufferFull();
                    }

                }
            }

        } catch (Exception e) {
            System.out.println("run: " + e);
        //System.exit(0);
        }
    }

    private void convertToSamples() {
        //System.out.println(lengthInSamples);

        for (int i = 0; i < audioSamples.length; i++) {
            /* First byte is LSB (low order) */
            int LSB = (int) audioBytes[2 * i];
            /* Second byte is MSB (high order) */
            int MSB = (int) audioBytes[2 * i + 1];
            audioSamples[i] = MSB << 8 | (255 & LSB);
        }
    }

    /** Get the current Microphone Level
     *  Based on an average value of the buffer
     *  @return     Current Microphone Level
     */
    public double getMicLevel() {
        double sum = 0;
        for (int i = 0; i < audioBytes.length; i++) {
            sum += Math.abs(audioBytes[i]);
        }
        return sum / (double)audioBytes.length;
    }

    /** Get the raw buffer */
    public byte[] getBuffer() {
        return audioBytes;
    }

    /** Get the Samples */
    public int[] getSamples() {
        return audioSamples;
    }

    //This method creates and returns an
    // AudioFormat object for a given set
    // of format parameters.  If these
    // parameters don't work well for
    // you, try some of the other
    // allowable parameter values, which
    // are shown in comments following
    // the declarations.
    // TODO - Check the compatibility of getAudioFormat() across machines
    private AudioFormat getAudioFormat() {
        float sampleRate = 44100.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }
}
