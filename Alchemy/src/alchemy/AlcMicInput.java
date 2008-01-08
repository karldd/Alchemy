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
 * Base class used for microphone input
 * With very limited functionality at the moment to return a buffer or the current sound level
 * Based on code by Richard G. Baldwin from: http://www.developer.com/java/other/print.php/1572251
 */
public class AlcMicInput extends Thread {

    //An arbitrary-size temporary holding buffer
    private byte tempBuffer[];
    private boolean stopMicInput = false;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    AlcMicInterface parent;

    /** Creates a new instance of AlcMicInput */
    public AlcMicInput(int bufferSize) {
        tempBuffer = new byte[bufferSize];
    }

    /** Creates a new instance of AlcMicInput
     * 
     * @param parent        Reference to the parent class implementing the AlcMicInterface
     * @param bufferSize    Size of the required buffer
     */
    public AlcMicInput(AlcMicInterface parent, int bufferSize) {
        this.parent = parent;
        tempBuffer = new byte[bufferSize];
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

    /** Stops Microphone Input */
    public void stopMicInput() {
        stopMicInput = true;
    }

    @Override
    public void run() {
        stopMicInput = false;

        try {
            //Loop until stopMicInput is turned off
            while (!stopMicInput) {
                //Read data from the internal buffer of the data line.
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);

                // When the buffer is full
                if (cnt > 0) {
                    // Call back to the parent if it implements the AlcMicInterface
                    if (parent != null) {
                        parent.bufferFull();
                    }

                }
            }

        } catch (Exception e) {
            System.out.println("run: " + e);
        //System.exit(0);
        }
    }

    /** Get the current Microphone Level
     *  Based on an average value of the buffer
     *  @return     Current Microphone Level
     */
    public double getMicLevel() {
        double sum = 0;
        for (int i = 0; i < tempBuffer.length; i++) {
            sum += Math.abs(tempBuffer[i]);
        }
        return sum / tempBuffer.length;
    }

    /** Get the raw buffer */
    public byte[] getBuffer() {
        return tempBuffer;
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
