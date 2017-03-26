package com.example.jefferson.myfirstapp.libraries;

/**
 * Created by Rodson on 03/26/2017.
 *
 * Class contains static methods related to Digital Signal Processing (DSP)
 *
 * Examples include Forward/Inverse Fourier Transforms.
 */

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class DspLib {
    static public double[] forwardFFT(double[] input) {
        DoubleFFT_1D inputSeq = new DoubleFFT_1D(input.length);
        double output[] = new double[input.length];
        inputSeq.realForward(output);
        return output;
    }

    static public double[] inverseFFT(double[] input) {
        DoubleFFT_1D inputSeq = new DoubleFFT_1D(input.length);
        double output[] = new double[input.length];
        inputSeq.realInverse(output, false);
        return output;
    }
}
