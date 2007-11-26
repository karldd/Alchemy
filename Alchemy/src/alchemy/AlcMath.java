/**
 * AlcRandom.java
 *
 * Created on November 26, 2007, 11:07 AM
 *
 * @author  Karl D.D. Willis
 * @version 1.0
 */

package alchemy;

import java.util.*;

public class AlcMath implements AlcConstants{
    
    /**
     * Creates a new instance of AlcMath
     */
    public AlcMath() {
    }
    
    //////////////////////////////////////////////////////////////
    
    // RANDOM NUMBERS
    
    
    Random internalRandom;
    
    /**
     * Return a random number in the range [0, howbig).
     * <P>
     * The number returned will range from zero up to
     * (but not including) 'howbig'.
     */
    public final float random(float howbig) {
        // for some reason (rounding error?) Math.random() * 3
        // can sometimes return '3' (once in ~30 million tries)
        // so a check was added to avoid the inclusion of 'howbig'
        
        // avoid an infinite loop
        if (howbig == 0) return 0;
        
        // internal random number object
        if (internalRandom == null) internalRandom = new Random();
        
        float value = 0;
        do {
            //value = (float)Math.random() * howbig;
            value = internalRandom.nextFloat() * howbig;
        } while (value == howbig);
        return value;
    }
    
    
    /**
     * Return a random number in the range [howsmall, howbig).
     * <P>
     * The number returned will range from 'howsmall' up to
     * (but not including 'howbig'.
     * <P>
     * If howsmall is >= howbig, howsmall will be returned,
     * meaning that random(5, 5) will return 5 (useful)
     * and random(7, 4) will return 7 (not useful.. better idea?)
     */
    public final float random(float howsmall, float howbig) {
        if (howsmall >= howbig) return howsmall;
        float diff = howbig - howsmall;
        return random(diff) + howsmall;
    }
    
    
    public final void randomSeed(long what) {
        // internal random number object
        if (internalRandom == null) internalRandom = new Random();
        internalRandom.setSeed(what);
    }
    
    
    
    //////////////////////////////////////////////////////////////
    
    // PERLIN NOISE
    
    // [toxi 040903]
    // octaves and amplitude amount per octave are now user controlled
    // via the noiseDetail() function.
    
    // [toxi 030902]
    // cleaned up code and now using bagel's cosine table to speed up
    
    // [toxi 030901]
    // implementation by the german demo group farbrausch
    // as used in their demo "art": http://www.farb-rausch.de/fr010src.zip
    
    static final int PERLIN_YWRAPB = 4;
    static final int PERLIN_YWRAP = 1<<PERLIN_YWRAPB;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 1<<PERLIN_ZWRAPB;
    static final int PERLIN_SIZE = 4095;
    
    int perlin_octaves = 4; // default to medium smooth
    float perlin_amp_falloff = 0.5f; // 50% reduction/octave
    
    // [toxi 031112]
    // new vars needed due to recent change of cos table in PGraphics
    int perlin_TWOPI, perlin_PI;
    float[] perlin_cosTable;
    float[] perlin;
    
    Random perlinRandom;
    
    
    /**
     * Computes the Perlin noise function value at point x.
     */
    public float noise(float x) {
        // is this legit? it's a dumb way to do it (but repair it later)
        return noise(x, 0f, 0f);
    }
    
    /**
     * Computes the Perlin noise function value at the point x, y.
     */
    public float noise(float x, float y) {
        return noise(x, y, 0f);
    }
    
    /**
     * Computes the Perlin noise function value at x, y, z.
     */
    public float noise(float x, float y, float z) {
        
        // precalculate sin/cos lookup tables [toxi]
        // circle resolution is determined from the actual used radii
        // passed to ellipse() method. this will automatically take any
        // scale transformations into account too
        
        // [toxi 031031]
        // changed table's precision to 0.5 degree steps
        // introduced new vars for more flexible code
        float sinLUT[];
        float cosLUT[];
        float SINCOS_PRECISION = 0.5f;
        int SINCOS_LENGTH = (int) (360f / SINCOS_PRECISION);
        sinLUT = new float[SINCOS_LENGTH];
        cosLUT = new float[SINCOS_LENGTH];
        for (int i = 0; i < SINCOS_LENGTH; i++) {
            sinLUT[i] = (float) Math.sin(i * DEG_TO_RAD * SINCOS_PRECISION);
            cosLUT[i] = (float) Math.cos(i * DEG_TO_RAD * SINCOS_PRECISION);
            
        }
        
        if (perlin == null) {
            if (perlinRandom == null) {
                perlinRandom = new Random();
            }
            perlin = new float[PERLIN_SIZE + 1];
            for (int i = 0; i < PERLIN_SIZE + 1; i++) {
                perlin[i] = perlinRandom.nextFloat(); //(float)Math.random();
            }
            // [toxi 031112]
            // noise broke due to recent change of cos table in PGraphics
            // this will take care of it
            perlin_cosTable = cosLUT;
            perlin_TWOPI = perlin_PI = SINCOS_LENGTH;
            perlin_PI >>= 1;
        }
        
        if (x<0) x=-x;
        if (y<0) y=-y;
        if (z<0) z=-z;
        
        int xi=(int)x, yi=(int)y, zi=(int)z;
        float xf = (float)(x-xi);
        float yf = (float)(y-yi);
        float zf = (float)(z-zi);
        float rxf, ryf;
        
        float r=0;
        float ampl=0.5f;
        
        float n1,n2,n3;
        
        for (int i=0; i<perlin_octaves; i++) {
            int of=xi+(yi<<PERLIN_YWRAPB)+(zi<<PERLIN_ZWRAPB);
            
            rxf=noise_fsc(xf);
            ryf=noise_fsc(yf);
            
            n1  = perlin[of&PERLIN_SIZE];
            n1 += rxf*(perlin[(of+1)&PERLIN_SIZE]-n1);
            n2  = perlin[(of+PERLIN_YWRAP)&PERLIN_SIZE];
            n2 += rxf*(perlin[(of+PERLIN_YWRAP+1)&PERLIN_SIZE]-n2);
            n1 += ryf*(n2-n1);
            
            of += PERLIN_ZWRAP;
            n2  = perlin[of&PERLIN_SIZE];
            n2 += rxf*(perlin[(of+1)&PERLIN_SIZE]-n2);
            n3  = perlin[(of+PERLIN_YWRAP)&PERLIN_SIZE];
            n3 += rxf*(perlin[(of+PERLIN_YWRAP+1)&PERLIN_SIZE]-n3);
            n2 += ryf*(n3-n2);
            
            n1 += noise_fsc(zf)*(n2-n1);
            
            r += n1*ampl;
            ampl *= perlin_amp_falloff;
            xi<<=1; xf*=2;
            yi<<=1; yf*=2;
            zi<<=1; zf*=2;
            
            if (xf>=1.0f) { xi++; xf--; }
            if (yf>=1.0f) { yi++; yf--; }
            if (zf>=1.0f) { zi++; zf--; }
        }
        return r;
    }
    
    // [toxi 031112]
    // now adjusts to the size of the cosLUT used via
    // the new variables, defined above
    private float noise_fsc(float i) {
        // using bagel's cosine table instead
        return 0.5f*(1.0f-perlin_cosTable[(int)(i*perlin_PI)%perlin_TWOPI]);
    }
    
    // [toxi 040903]
    // make perlin noise quality user controlled to allow
    // for different levels of detail. lower values will produce
    // smoother results as higher octaves are surpressed
    
    public void noiseDetail(int lod) {
        if (lod>0) perlin_octaves=lod;
    }
    
    public void noiseDetail(int lod, float falloff) {
        if (lod>0) perlin_octaves=lod;
        if (falloff>0) perlin_amp_falloff=falloff;
    }
    
    public void noiseSeed(long what) {
        if (perlinRandom == null) perlinRandom = new Random();
        perlinRandom.setSeed(what);
        // force table reset after changing the random number seed [0122]
        perlin = null;
    }
    
}
