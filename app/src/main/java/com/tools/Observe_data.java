package com.tools;

/**
 * Created by win10 on 2017/5/4.
 */

public class Observe_data {

    private String Name;
    private Double Hz;
    private Double V;
    private Double S;

    public Observe_data() {

    }

    public Observe_data(String Name1, double Hz1, double V1, double S1) {
        Name = Name1;
        Hz = Hz1;
        V = V1;
        S = S1;
    }

    public Observe_data(String Name2, String Hz2, String V2, String S2) {
        Name = Name2;
        Hz = Double.valueOf(Hz2);
        V = Double.valueOf(V2);
        S = Double.valueOf(S2);
    }

    public String getName() {
        return Name;
    }

    public double getHz() {
        return Hz;
    }
    public String getHz_String() {
        return Hz.toString();
    }

    public double getV() {
        return V;
    }
    public String getV_String() {
        return V.toString();
    }

    public double getS() {
        return S;
    }
    public String getS_String() {
        return S.toString();
    }


    public void setName(String Name) {
        this.Name = Name;
    }
    public void setHz(double Hz) {
        this.Hz = Hz;
    }
    public void setV(double V) {
        this.V = V;
    }
    public void setS(double S) {
        this.S = S;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
