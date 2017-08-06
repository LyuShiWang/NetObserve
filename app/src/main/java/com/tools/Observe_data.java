package com.tools;

/**
 * Created by win10 on 2017/5/4.
 */

public class Observe_data {

    private String Station_Name;
    private int Cehuishu;
    private String Face;
    private String Focus_Name;
    private Double Hz;
    private Double V;
    private Double S;

    private My_Func my_func = new My_Func();

    public Observe_data() {

    }

    public Observe_data(String Station_Name1,
                        int Cehuishu1, String Face1, String Focus_Name1,
                        double Hz1, double V1, double S1) {
        Station_Name = Station_Name1;
        Cehuishu = Cehuishu1;
        Face = Face1;
        Focus_Name = Focus_Name1;
        Hz = Hz1;
        V = V1;
        S = S1;
    }

    public Observe_data(String Station_Name2,
                        int Cehuishu2, String Face2, String Focus_Name2,
                        String Hz2, String V2, String S2) {
        Cehuishu = Cehuishu2;
        Face = Face2;
        Station_Name = Station_Name2;
        Focus_Name = Focus_Name2;
        Hz = Double.valueOf(Hz2);
        V = Double.valueOf(V2);
        S = Double.valueOf(S2);
    }

    public String getStationName() {
        return Station_Name;
    }

    public int getCehuishu() {
        return Cehuishu;
    }

    public String getFace() {
        return Face;
    }

    public String getFocusName() {
        return Focus_Name;
    }

    public double getHz() {
        return Hz;
    }

    public double getV() {
        return V;
    }

    public double getS() {
        return S;
    }

    public String getHz_String() {
        return Hz.toString();
    }

    public String getV_String() {
        return V.toString();
    }

    public String getS_String() {
        return S.toString();
    }

    public void setCehuishu(int Cehuishu) {
        this.Cehuishu = Cehuishu;
    }

    public void setFace(String Face) {
        this.Face = Face;
    }

    public void setStationName(String Station_Name) {
        this.Station_Name = Station_Name;
    }

    public void setFocusName(String Focus_Name) {
        this.Focus_Name = Focus_Name;
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

    public String toFileString() {
        String file_string = "";
        file_string += String.valueOf(Cehuishu) + "," + Face + ",";
        file_string += Station_Name + "," + Focus_Name + ",";
        file_string += String.valueOf(Hz) + "," + String.valueOf(V) + ","
                + String.valueOf(my_func.baoliu_weishu(S, 5));

        return file_string;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
