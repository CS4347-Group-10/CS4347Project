package cs4347group10.cs4347application.pojo;

/**
 * Created by Rodson on 6/4/2017.
 */

public class Envelope{
    public double[] window;
    public int sustainStart;
    public int sustainEnd;

    private Envelope(){};

    public Envelope(int windowSize){
        window = new double[windowSize];
        sustainStart = 0;
        sustainEnd = windowSize - 1;
    }

    public Envelope(double[] _window, int _sustainStart, int _sustainEnd){
        window = _window;
        sustainStart = _sustainStart;
        sustainEnd = _sustainEnd;
    }
}
