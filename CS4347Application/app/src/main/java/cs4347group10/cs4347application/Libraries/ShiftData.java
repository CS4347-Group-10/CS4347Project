package cs4347group10.cs4347application.Libraries;

import java.util.ArrayList;

import cs4347group10.cs4347application.pojo.Envelope;

/**
 * Created by bleac on 2017/4/7.
 */

public class ShiftData {
    private ArrayList<short[]> shifted=new ArrayList<>();
    private ArrayList<short[]> sustains =new ArrayList<>();
    private ArrayList<short[]> distorted=new ArrayList<>();
    private int sustainStart = 0;
    private int sustainEnd = 0;
    final private double[] factors = {0.5,0.5625,0.625,0.6667,0.75,0.8333,0.9375,1,1.125,1.25,1.333,1.5,1.6667,1.8750,2.0};
    public ShiftData(double[] data,Envelope en){
        sustainStart=en.sustainStart;
        sustainEnd=en.sustainEnd;
        double[] newData = new double[en.clipEnd-en.clipStart];
        System.arraycopy(data,en.clipStart,newData,0,en.clipEnd-en.clipStart);
        float[] tempsustain = new float[sustainStart-newData.length/4];
        float[] tempsound;
        float[] tempdistort;
        for(int i =0;i<factors.length;i++){
            tempsound=DspLib.shiftSound(data,factors[i]);
            shifted.add(i,DspLib.floatToShort(tempsound));
            System.arraycopy(tempsound,newData.length/4,tempsustain,0,sustainStart-newData.length/4);
            sustains.add(i,DspLib.floatToShort(tempsustain));
            tempdistort = DspLib.distortSound(DspLib.floatToDouble(tempsustain),4);
            distorted.add(i,DspLib.floatToShort(tempdistort));
        }
    }
    public int getSusStart() { return sustainStart; }
    public int getSusEnd() { return sustainEnd; }
    public short[] getFullNote(int index){
        return shifted.get(index+7);
    }
    public short[] getSustain (int index) {return sustains.get(index+7);}
    public short[] getDistort (int index) {return distorted.get(index+7);}
}

