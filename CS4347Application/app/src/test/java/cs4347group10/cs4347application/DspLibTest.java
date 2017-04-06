package cs4347group10.cs4347application;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import cs4347group10.cs4347application.Libraries.DspLib;
import cs4347group10.cs4347application.pojo.Envelope;

/**
 * Created by Rodson on 03/31/2017.
 */
public class DspLibTest {

    public static String printArray(double[] array){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++){
            if(i!=0) sb.append(",");
            sb.append(Double.toString(array[i]));
        }

        return sb.toString();
    }

    @Test
    public void sineWave_minimalTest() throws Exception {
        int samples = 44100;
        double magnitude = 5.0;
        double frequency = 1.0;
        int samplingRate = 44100;
        double delta = 1e-15;

        double[] output = DspLib.sineWave(samples, magnitude, frequency, samplingRate);

        //Output is of expected length
        assertEquals(output.length, samples);

        //Output is of correct magnitude and within magnitude bounds
        double largestVal = 0;
        double smallestVal = 0;
        for(int i = 0; i < samples; i++){
            if (output[i] > largestVal){
                largestVal = output[i];
            }
            if (output[i] < smallestVal){
                smallestVal = output[i];
            }
        }

        assertTrue(largestVal <= magnitude);
        assertEquals(largestVal, magnitude, delta);
        assertTrue(Math.abs(smallestVal) <= magnitude);
        assertEquals(Math.abs(smallestVal), magnitude, delta);

    }

    @Test
    public void forwardFFT_inverseFFT_singleSineWaveTest() throws Exception {
        int samples = 1000;
        double magnitude = 1.0;
        double frequency = 100.0;
        int samplingRate = 1000;
        double delta = 1e-15;

        double[] timeDomainSamples = DspLib.sineWave(samples, magnitude, frequency, samplingRate);
        double[] complexFreqDomainSamples = DspLib.forwardFFT(timeDomainSamples);
        double[] freqDomainSamples = DspLib.magnitude(complexFreqDomainSamples);

        //FFT method includes symmetric other-half
        assertEquals(samples, freqDomainSamples.length);

        double freqStep = ((double) samplingRate / 2.0) / ((double) samples / 2.0);

        //We expect that
        int expectedBin = (int)(Math.round(frequency/freqStep));

        //Find the frequency bin with the largest magnitude value
        double largestMag = 0;
        int largestMagBin = -1;
        for (int i = 0; i <= samples/2; i++){
            if (Math.abs(freqDomainSamples[i]) > largestMag){
                largestMag = Math.abs(freqDomainSamples[i]);
                largestMagBin = i;
            }
        }

        assertEquals(
                "LargestMagnitude: "+ Double.toString(largestMag)
                +"\n Time Domain values: "
                + printArray(timeDomainSamples)
                +"\n Frequency Domain values: "
                + printArray(freqDomainSamples)
                +"\n"
                , expectedBin, largestMagBin);

        double[] regeneratedTimeDomainSamples = DspLib.inverseFFT(complexFreqDomainSamples);

        //Should return exactly the same number of samples
        assertEquals("Regenerated: " + printArray(regeneratedTimeDomainSamples)
                +"\n Original: " + printArray(timeDomainSamples)
                +"\n",
                timeDomainSamples.length, regeneratedTimeDomainSamples.length);

        //Error should be small (so more or less the same signal)
        for (int i = 0; i < timeDomainSamples.length; i++){
            assertFalse("Error, idx " + Integer.toString(i)
                    + " exceeded threshold, check inverseFFT?",
                    Math.abs(timeDomainSamples[i] - regeneratedTimeDomainSamples[i]) > delta);
        }
    }

    @Test
    public void envelopeTest() throws Exception {
        //Error margin
        double delta = 1e-15;

        //Produce sample buffer
        int steps = 10;
        double magnitude = 1.0;
        double[] sampleBuffer = new double[(2*steps)+1];
        sampleBuffer[steps] = magnitude;
        for (int i = 0; i < steps; i++){
            sampleBuffer[i] = magnitude * ((double) i / steps);
            sampleBuffer[(2*steps) - i] = sampleBuffer[i];
        }

        //Test with step size 1; every element is an anchor
        Envelope envelope = DspLib.characterizeWithEnvelope(sampleBuffer, 1);
        double[] window = envelope.window;

        //Envelope starts with 0.0 and ends with 0.0
        assertEquals(0.0, window[0], delta);
        assertEquals(0.0, window[window.length - 1], delta);

        for (int i = 0; i < window.length; i ++){
            assertNotNull(window[i]);
            assertTrue(0.0 <= window[i] && window[i] <= magnitude);
        }

        //TODO, need moar test
        assertTrue(0 <= envelope.sustainStart && envelope.sustainStart < window.length);
        assertTrue(0 <= envelope.sustainEnd && envelope.sustainEnd < window.length);
        //assertTrue(Integer.toString(envelope.sustainStart) + ">", envelope.sustainStart < envelope.sustainEnd);
    }
}