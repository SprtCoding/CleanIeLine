package com.example.myapplication10101010.Admin.Fragments.Model;

import java.util.Date;

public class NotificationList {
    String from, to, Details;
    Date Date;

    public NotificationList() {
    }

    public NotificationList(String from, String to, String details, Date date) {
        this.from = from;
        this.to = to;
        this.Details = details;
        this.Date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }
}
