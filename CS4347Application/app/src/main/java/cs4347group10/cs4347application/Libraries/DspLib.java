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
import cs4347group10.cs4347application.pojo.Envelope;

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

    /*
    Computes the magnitude values of a complex FFT
     */
    static public double [] magnitude(double fft[]) {
        double[] values = new double[fft.length / 2];
        for (int i = 0; i < fft.length / 2; i++) {
            int idx = 2 * i;
            values[i] = Math.sqrt((fft[idx] * fft[idx]) + (fft[idx + 1] * fft[idx + 1]));
        }
        return values;
    }

    static public Envelope characterizeWithEnvelope(double[] buffer, int stepSize){

        //Characterize using a number of key anchor points, determined by stepSize
        double[] anchors = new double[(buffer.length / stepSize)+1];
        int startAnchorIdx = 0;
        int endAnchorIdx = 1;
        double smallestGradient = Double.MAX_VALUE;

        double[] window = new double[buffer.length];
        int firstElemAfterAnchor = (anchors.length-1) * stepSize;
        int numElemsLeft = buffer.length - firstElemAfterAnchor;

        //Assume 0, to enforce envelope characteristic of always starting and ending with 0 intensity
        anchors[0] = 0;
        double weight = (double)1/stepSize;
        for (int i = 1; i < anchors.length; i++){
            double runningAverage = 0;
            for (int j = (i-1)*stepSize; j < i*stepSize; j++){
                runningAverage += weight*Math.abs(anchors[j]);
            }
            anchors[i] = runningAverage;
            for (int start = 0; start <= i; start++){
                double avgGradient = 0;
                for (int offset = 1; offset <= i-start; offset++) {
                    double lambda = (double) 1 / offset;
                    avgGradient = (1 - lambda) * avgGradient + lambda * (anchors[start + offset] - anchors[start + offset - 1]);

                    if(Math.abs(avgGradient) <  smallestGradient ){
                        startAnchorIdx = i;
                        endAnchorIdx = start+offset;
                        smallestGradient = Math.abs(avgGradient);
                    }
                }
            }

            //Special case: there are no leftover samples
            if(i == anchors.length - 1){
                if(numElemsLeft < 1) {
                    anchors[i] = 0; //Then i is the last anchor, we force it to 0
                }
            }

            int startIdx = (i-1)*stepSize;
            for (int j = 0; j < stepSize; j++){
                //Linear interpolate values between anchors
                window[startIdx + j] = anchors[i-1] + ((double)j/stepSize)*(anchors[i] - anchors[i-1]);
            }
        }

        for (int j = 0; j < numElemsLeft; j++) {
            window[firstElemAfterAnchor + j] = ((double)1 - ((double) j / numElemsLeft)) * (anchors[anchors.length - 1]);
        }

        return new Envelope(window, startAnchorIdx*stepSize, endAnchorIdx*stepSize);
    }
}
