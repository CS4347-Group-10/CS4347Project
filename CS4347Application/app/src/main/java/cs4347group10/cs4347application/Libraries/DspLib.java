package cs4347group10.cs4347application.Libraries;

/**
 * Created by Rodson on 03/26/2017.
 *
 * Class contains static methods related to Digital Signal Processing (DSP)
 *
 * Examples include Forward/Inverse Fourier Transforms.
 */

import java.lang.Math.*;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class DspLib {
    static public double[] sineWave(int numOfSamples, double magnitude, double frequency, int samplingRate) {
        double[] samples = new double[numOfSamples];
        double k = Math.PI * 2. * frequency / samplingRate;

        for (int i = 0; i < numOfSamples; i++){
            samples[i] = magnitude * Math.sin(k * i);
        }
        return samples;
    }

    /*
    Computes forward FFT (time-domain to frequency-domain)
    @return complex FFT values.
     */
    static public double[] forwardFFT(double[] input) {
        DoubleFFT_1D inputSeq = new DoubleFFT_1D(input.length);
        double output[] = new double[input.length * 2];
        System.arraycopy(input, 0, output, 0, input.length);
        inputSeq.realForwardFull(output);
        return output;
    }

    static public double[] inverseFFT(double[] input) {
        DoubleFFT_1D inputSeq = new DoubleFFT_1D(input.length/2);
        double output[] = new double[input.length];
        System.arraycopy(input, 0, output, 0, input.length);
        inputSeq.complexInverse(output, true);

        double realOutput[] = new double[input.length/2];
        //Ignore complex terms, only take real terms
        for (int i = 0; i < input.length/2; i++){
            realOutput[i] = output[i * 2];
        }
        return realOutput;
    }

    static public double [] magnitude(double fft[]) {
        double[] values = new double[fft.length / 2];
        for (int i = 0; i < fft.length / 2; i++) {
            int idx = 2 * i;
            values[i] = Math.sqrt((fft[idx] * fft[idx]) + (fft[idx + 1] * fft[idx + 1]));
        }
        return values;
    }
}
