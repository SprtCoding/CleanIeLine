package com.example.myapplication10101010.Admin.Fragments.Model;

import java.util.Date;

public class EarningsData {
    private Date date;
    private double earnings;

    public EarningsData() {
    }

    public EarningsData(Date date, double earnings) {
        this.date = date;
        this.earnings = earnings;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }
}
