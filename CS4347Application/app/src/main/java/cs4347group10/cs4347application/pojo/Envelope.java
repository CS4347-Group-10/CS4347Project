package cs4347group10.cs4347application.pojo;

/**
 * Created by Rodson on 6/4/2017.
 */

public class Envelope{
    public double[] window;
    public int sustainStart;
    public int sustainEnd;
    public int peak;

    private Envelope(){};

    public Envelope(int windowSize){
        this.window = new double[windowSize];
        this.sustainStart = 0;
        this.sustainEnd = windowSize - 1;
        this.peak = sustainStart;
    }

    public Envelope(double[] window, int sustainStart, int sustainEnd, int peak){
        this.window = window;
        this.sustainStart = sustainStart;
        this.sustainEnd = sustainEnd;
        this.peak = peak;
    }
}
