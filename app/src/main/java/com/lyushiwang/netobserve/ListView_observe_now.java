package com.lyushiwang.netobserve;

/**
 * Created by win10 on 2017/4/24.
 */

public class ListView_observe_now {
    private String id;
    private String order_number;
    private double Hz;
    private double V;
    private double S;

    public ListView_observe_now() {
        super();
    }

    public ListView_observe_now(String id, String order_number, double Hz, double V, double S) {
        super();
        this.id = id;
        this.order_number = order_number;
        this.Hz = Hz;
        this.V = V;
        this.S = S;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return order_number;
    }

    public void setOrderNumber(String order_number) {
        this.order_number = order_number;
    }

    public double getHz() {
        return Hz;
    }

    public void setHz(double Hz) {
        this.Hz = Hz;
    }

    public double getV() {
        return V;
    }

    public void setV(double V) {
        this.V = V;
    }

    public double getS() {
        return S;
    }

    public void setS(double S) {
        this.S = S;
    }
}

