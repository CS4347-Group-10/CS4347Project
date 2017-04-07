package cs4347group10.cs4347application.Libraries;

import java.util.ArrayList;

import cs4347group10.cs4347application.pojo.Envelope;

/**
 * Created by bleac on 2017/4/7.
 */

public class ShiftData {
    private ArrayList<short[]> shifted=new ArrayList<>();
    private ArrayList<short[]> distorted=new ArrayList<>();
    final private double[] factors = {0.5,0.5625,0.625,0.6667,0.75,0.8333,0.9375,1,1.125,1.25,1.333,1.5,1.6667,1.8750,2.0};
    public ShiftData(double[] data,Envelope en){
        for(int i =0;i<factors.length;i++){
            shifted.add(i,DspLib.floatToShort(DspLib.shiftSound(data,factors[i])));
        }
    }
    public short[] getFullNote(int index){
        if(shifted.isEmpty()){
            return null;
        }
        return shifted.get(index+7);
    }
}
