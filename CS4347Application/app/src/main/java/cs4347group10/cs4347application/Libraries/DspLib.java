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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        int[] bounds = new int[buffer.length / stepSize];
        //Generate bounds
        for (int i = 0; i < buffer.length / stepSize; i++){
            bounds[i] = i*stepSize;
        }

        double[] anchors = new double[(buffer.length / stepSize) - 2];
        double largestIntensity = 0;
        int anchorWithLargestIntensity = 0;

        double[] window = new double[buffer.length];
        int firstElemAfterBound = bounds[bounds.length-1] + 1;
        int numElemsLeft = buffer.length - firstElemAfterBound;

        //Assume 0, to enforce envelope characteristic of always starting and ending with 0 intensity
        for (int i = 0; i < anchors.length; i++){
            double weight = (double) 1 / (bounds[i+2]-bounds[i]+1);
            double runningAverage = 0;

            for (int j = bounds[i]; j <= bounds[i+2]; j++){
                runningAverage += weight*Math.abs(buffer[j]);
            }
            anchors[i] = runningAverage;

            //Update largest intensity anchor
            if(anchors[i] > largestIntensity){
                anchorWithLargestIntensity = i;
                largestIntensity = anchors[i];
            }
        }

        //Handle from 0 to 1st anchor (exclusive)
        for (int j = 0; j < bounds[1]; j++){
            window[j] = (double) 0 + (j/(bounds[1]))*anchors[0];
        }

        //Handle 1st anchor to last anchor
        for (int i = 0; i < anchors.length-1; i++){
            double weight = (double) 1 / (bounds[i+2] - bounds[i+1]);
            int count = 0;
            for (int j = bounds[i+1]; j < bounds[i+2]; j++){
                window[j] = anchors[i] + count*weight*(anchors[i+1]-anchors[i]);
                count++;
            }
        }

        //Handle last anchor to last element
        double weight = (double) 1 / (window.length - bounds[bounds.length-2]-1);
        int count = 0;
        for (int j = bounds[bounds.length-2]; j < window.length; j++){
            window[j] = (1-(count*weight))*anchors[anchors.length-1];
            count++;
        }

        //Compute start and end sustain
        int startAnchorIdx = anchorWithLargestIntensity;
        int endAnchorIdx = anchors.length - 1;
        double smallestGradient = Double.MAX_VALUE;
        for (int anchorStartId = anchorWithLargestIntensity; anchorStartId < anchors.length; anchorStartId++){
            double avgGradient = 0;
            for (int offset = 1; offset < anchors.length - anchorStartId; offset++) {
                double lambda = (double) 1 / offset;
                avgGradient = (1 - lambda) * avgGradient
                        + lambda * (anchors[anchorStartId + offset] - anchors[anchorStartId + offset - 1]);

                if(Math.abs(avgGradient) <  smallestGradient ){
                    startAnchorIdx = anchorStartId;
                    endAnchorIdx = anchorStartId+offset;
                    smallestGradient = Math.abs(avgGradient);
                }

                //If the average gradient is somehow the same, but anchor distance is greater, it is preferred
                if(Math.abs(avgGradient) ==  smallestGradient && (endAnchorIdx - startAnchorIdx) < offset){
                    startAnchorIdx = anchorStartId;
                    endAnchorIdx = anchorStartId+offset;
                }
            }
        }

        //Normalize window
        double largestValue = 0;
        for (int i = 0; i < window.length; i++){
            if(window[i] > largestValue) largestValue = window[i];
        }
        for (int i = 0; i < window.length; i++){
            window[i] /= largestValue;
        }


        return new Envelope(window,                                 //Envelope filter
                (startAnchorIdx+1)*stepSize,                            //id of where sustain starts
                Math.min((endAnchorIdx+1)*stepSize, buffer.length-1),   //id of where sustain ends (inclusive)
                anchorWithLargestIntensity);                        //Peak of the signal (attack)
    }

    static public float[] doubleToFloat(double[] data){
        float[] fdata = new float[data.length];
        for (int i = 0 ; i < data.length; i++)
        {
            fdata[i] = (float) data[i];
        }
        return fdata;
    }

    static public double[] floatToDouble(float[] data){
        double[] ddata = new double[data.length];
        for (int i = 0 ; i < data.length; i++)
        {
            ddata[i] = (double) data[i];
        }
        return ddata;
    }
    static public short[] floatToShort(float[] data){
        short[] ss = new short[data.length];
        for(int i =0;i<data.length;i++){
            ss[i]=(short) (Math.floor(data[i]*3276));
        }
        return ss;
    }
    static public short[] byteToShort(byte[] data){
        short[] sdata = new short[data.length/2];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sdata);
        return sdata;
    }

    static public float[] shortToFloat(short[] data){
        float[] fdata = new float[data.length];
        for(int i =0;i<data.length;i++){
            fdata[i]=(float)(data[i]/32767.0);
        }
        return fdata;
    }
    static public float[] testSound(double factor){
        double[] data=sineWave(44100,1,440,44100);
        float[] indata = doubleToFloat(data);
        float[] outdata = new float[data.length];
        PitchShifter ps = new PitchShifter(1024);
        ps.setFftFrameSize(1024);
        ps.setOversampling(32);
        ps.setSampleRate(44100);
        ps.setPitchShift(factor);
        ps.smbPitchShift(indata, outdata, 0, 44100);
        return outdata;
    }
    static public float[] shiftSound(double[] data, double factor){
        float[] indata = doubleToFloat(data);
        float[] outdata = new float[data.length];
        PitchShifter ps = new PitchShifter(1024);
        ps.setFftFrameSize(1024);
        ps.setOversampling(32);
        ps.setSampleRate(44100);
        ps.setPitchShift(factor);
        ps.smbPitchShift(indata, outdata, 0, data.length);
        return outdata;
    }

    static public double pitchDetection(double[] data){

        double[] fftData=forwardFFT(data);
        int maxIndex = 0;
        for (int i = 1; i < fftData.length/2-1; i++) {
            i++;
            double newnumber = fftData[i];
            if ((newnumber > fftData[maxIndex])) {
                maxIndex = i;
            }
        }
        return (double) maxIndex/2;
    }
}
