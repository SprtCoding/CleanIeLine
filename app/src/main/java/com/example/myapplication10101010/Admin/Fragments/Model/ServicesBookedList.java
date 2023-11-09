package com.example.myapplication10101010.Admin.Fragments.Model;

import java.util.Date;

public class ServicesBookedList {
    String ServicesType, UserID, OrderStatus, BookedID, PickUpAddress, PickUpTime, DeliveryInstruction,
            DeliveryAddress, PaymentMethod, ContactNo;
    double TotalCost, Kilogram, SubTotal, DeliveryFee;
    Date DateTimePlaced;

    public ServicesBookedList() {
    }

    public ServicesBookedList(String servicesType, String userID, String orderStatus, String bookedID, String pickUpAddress, String pickUpTime, String deliveryInstruction, String deliveryAddress, String paymentMethod, String contactNo, double totalCost, double kilogram, double subTotal, double deliveryFee, Date dateTimePlaced) {
        ServicesType = servicesType;
        UserID = userID;
        OrderStatus = orderStatus;
        BookedID = bookedID;
        PickUpAddress = pickUpAddress;
        PickUpTime = pickUpTime;
        DeliveryInstruction = deliveryInstruction;
        DeliveryAddress = deliveryAddress;
        PaymentMethod = paymentMethod;
        ContactNo = contactNo;
        TotalCost = totalCost;
        Kilogram = kilogram;
        SubTotal = subTotal;
        DeliveryFee = deliveryFee;
        DateTimePlaced = dateTimePlaced;
    }

    public String getServicesType() {
        return ServicesType;
    }

    public void setServicesType(String servicesType) {
        ServicesType = servicesType;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getBookedID() {
        return BookedID;
    }

    public void setBookedID(String bookedID) {
        BookedID = bookedID;
    }

    public String getPickUpAddress() {
        return PickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        PickUpAddress = pickUpAddress;
    }

    public String getPickUpTime() {
        return PickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        PickUpTime = pickUpTime;
    }

    public String getDeliveryInstruction() {
        return DeliveryInstruction;
    }

    public void setDeliveryInstruction(String deliveryInstruction) {
        DeliveryInstruction = deliveryInstruction;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        DeliveryAddress = deliveryAddress;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public double getTotalCost() {
        return TotalCost;
    }

    public void setTotalCost(double totalCost) {
        TotalCost = totalCost;
    }

    public double getKilogram() {
        return Kilogram;
    }

    public void setKilogram(double kilogram) {
        Kilogram = kilogram;
    }

    public double getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(double subTotal) {
        SubTotal = subTotal;
    }

    public double getDeliveryFee() {
        return DeliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        DeliveryFee = deliveryFee;
    }

    public Date getDateTimePlaced() {
        return DateTimePlaced;
    }

    public void setDateTimePlaced(Date dateTimePlaced) {
        DateTimePlaced = dateTimePlaced;
    }
}
