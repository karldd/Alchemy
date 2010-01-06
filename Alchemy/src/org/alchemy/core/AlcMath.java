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

import java.util.*;

/**
 * Utility class that handles math functions 
 * Random number generation, noise etc...
 * Based on Processing:
 * http://dev.processing.org/source/index.cgi/trunk/processing/core/src/processing/core/PApplet.java?view=markup
 */
public class AlcMath implements AlcConstants {

    /**
     * Creates a new instance of AlcMath
     */
    AlcMath() {
    }
    /** 
     *  Random and noise functions adapted from the Processing project
     *  http://processing.org
     *  http://dev.processing.org/source/index.cgi/trunk/processing/core/src/processing/core/PApplet.java?view=markup
     *
     */
    //////////////////////////////////////////////////////////////
    // RANDOM NUMBERS
    Random internalRandom;

    /**
     * Return a random number in the range [0, howbig).
     * <P>
     * The number returned will range from zero up to
     * (but not including) 'howbig'.
     * @param howbig
     * @return 
     */
    public final float random(float howbig) {
        // for some reason (rounding error?) Math.random() * 3
        // can sometimes return '3' (once in ~30 million tries)
        // so a check was added to avoid the inclusion of 'howbig'

        // avoid an infinite loop
        if (howbig == 0) {
            return 0;
        }

        // internal random number object
        if (internalRandom == null) {
            internalRandom = new Random();
        }

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
        if (howsmall >= howbig) {
            return howsmall;
        }
        float diff = howbig - howsmall;
        return random(diff) + howsmall;
    }

    public final void randomSeed(long what) {
        // internal random number object
        if (internalRandom == null) {
            internalRandom = new Random();
        }
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
    static final int PERLIN_YWRAP = 1 << PERLIN_YWRAPB;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 1 << PERLIN_ZWRAPB;
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
    public final float noise(float x, float y, float z) {

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
            sinLUT[i] = (float) Math.sin(i * MATH_DEG_TO_RAD * SINCOS_PRECISION);
            cosLUT[i] = (float) Math.cos(i * MATH_DEG_TO_RAD * SINCOS_PRECISION);

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

        if (x < 0) {
            x = -x;
        }
        if (y < 0) {
            y = -y;
        }
        if (z < 0) {
            z = -z;
        }

        int xi = (int) x, yi = (int) y, zi = (int) z;
        float xf = (x - xi);
        float yf = (y - yi);
        float zf = (z - zi);
        float rxf, ryf;

        float r = 0;
        float ampl = 0.5f;

        float n1, n2, n3;

        for (int i = 0; i < perlin_octaves; i++) {
            int of = xi + (yi << PERLIN_YWRAPB) + (zi << PERLIN_ZWRAPB);

            rxf = noise_fsc(xf);
            ryf = noise_fsc(yf);

            n1 = perlin[of & PERLIN_SIZE];
            n1 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n1);
            n2 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n2);
            n1 += ryf * (n2 - n1);

            of += PERLIN_ZWRAP;
            n2 = perlin[of & PERLIN_SIZE];
            n2 += rxf * (perlin[(of + 1) & PERLIN_SIZE] - n2);
            n3 = perlin[(of + PERLIN_YWRAP) & PERLIN_SIZE];
            n3 += rxf * (perlin[(of + PERLIN_YWRAP + 1) & PERLIN_SIZE] - n3);
            n2 += ryf * (n3 - n2);

            n1 += noise_fsc(zf) * (n2 - n1);

            r += n1 * ampl;
            ampl *= perlin_amp_falloff;
            xi <<= 1;
            xf *= 2;
            yi <<= 1;
            yf *= 2;
            zi <<= 1;
            zf *= 2;

            if (xf >= 1.0f) {
                xi++;
                xf--;
            }
            if (yf >= 1.0f) {
                yi++;
                yf--;
            }
            if (zf >= 1.0f) {
                zi++;
                zf--;
            }
        }
        return r;
    }

    /**
     *  now adjusts to the size of the cosLUT used via
     *  the new variables, defined above
     */
    private final float noise_fsc(float i) {
        // using bagel's cosine table instead
        return 0.5f * (1.0f - perlin_cosTable[(int) (i * perlin_PI) % perlin_TWOPI]);
    }

    /**
     *  make perlin noise quality user controlled to allow
     *  for different levels of detail. lower values will produce
     *  smoother results as higher octaves are surpressed
     */
    public final void noiseDetail(int lod) {
        if (lod > 0) {
            perlin_octaves = lod;
        }
    }

    public final void noiseDetail(int lod, float falloff) {
        if (lod > 0) {
            perlin_octaves = lod;
        }
        if (falloff > 0) {
            perlin_amp_falloff = falloff;
        }
    }

    public final void noiseSeed(long what) {
        if (perlinRandom == null) {
            perlinRandom = new Random();
        }
        perlinRandom.setSeed(what);
        // force table reset after changing the random number seed [0122]
        perlin = null;
    }

    /** Return the mean of an int array */
    public static final double mean(int[] p) {
        int sum = 0;  // sum of all the elements
        for (int i = 0; i < p.length; i++) {
            sum += p[i];
        }
        return sum / p.length;
    }

    /** Convenience function to map a variable 
     *  from one coordinate space to another. 
     *  Equivalent to unlerp() followed by lerp().
     * 
     * @param value     The incoming value to be converted
     * @param istart    Lower bound of the value's current range
     * @param istop     Upper bound of the value's current range
     * @param ostart    Lower bound of the value's target range
     * @param ostop     Upper bound of the value's target range
     * @return          The mapped number
     */
    public static final float map(float value, float istart, float istop, float ostart, float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    /**Constrains a value to not exceed a maximum and minimum value.
     * 
     * @param value     The value to constrain
     * @param min       Minimum limit
     * @param max       Maximum limit
     * @return
     */
    public static final int constrain(int value, int min, int max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    /**Constrains a value to not exceed a maximum and minimum value.
     * 
     * @param value     The value to constrain
     * @param min       Minimum limit
     * @param max       Maximum limit
     * @return
     */
    public static final float constrain(float value, float min, float max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }

    /** Calculates the distance between two points.
     * 
     * @param x1    x-coordinate of the first point
     * @param y1    y-coordinate of the first point
     * @param x2    x-coordinate of the second point
     * @param y2    y-coordinate of the second point
     * @return      The distance between the two points
     */
    public static final float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(sq(x2 - x1) + sq(y2 - y1));
    }

    /** Squares a number (multiplies a number by itself). 
     * 
     * @param num   The number to square
     * @return      The squared number
     */
    public static final float sq(float num) {
        return num * num;
    }
}
