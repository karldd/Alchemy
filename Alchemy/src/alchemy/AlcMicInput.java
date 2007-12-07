/**
 * AlcMicInput.java
 * Based on code by Richard G. Baldwin from: http://www.developer.com/java/other/print.php/1572251
 *
 * Created on December 6, 2007, 7:50 PM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AlcMicInput extends Thread {
    
    //An arbitrary-size temporary holding buffer
    private byte tempBuffer[];
    private boolean stopMicInput = false;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private SourceDataLine sourceDataLine;
    
    /** Creates a new instance of AlcMicInput */
    public AlcMicInput(int bufferSize) {
        
        tempBuffer = new byte[bufferSize];
        
    }
    
    public void startMicInput(){
        stopMicInput = false;
        try{
            //Get everything set up for capture
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            
            // Start the thread
            this.start();
            
        } catch (Exception e) {
            
            System.out.println(e);
            //System.exit(0);
            
        }
    }
    
    public void stopMicInput(){
        stopMicInput = true;
    }
    
    @Override
    public void run(){
        stopMicInput = false;
        
        try{
            //Loop until stopMicInput is turned off
            while(!stopMicInput){
                //Read data from the internal buffer of the data line.
                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                
                // When the buffer is full
                if(cnt > 0){
                    
                }
            }
            
        }catch (Exception e) {
            System.out.println(e);
            //System.exit(0);
        }
    }
    
    public double getMicLevel(){
        double sum = 0;
        for (int i = 0; i < tempBuffer.length; i++) {
            sum += Math.abs(tempBuffer[i]);
        }
        return sum / tempBuffer.length;
    }
    
    public byte[] getBuffer(){
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
    private AudioFormat getAudioFormat(){
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
