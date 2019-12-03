package com.ote.otedeliveries.models;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Order implements RealmModel {
    @PrimaryKey
    private String orderID;

    private String pickupAddress, pickupContact, pickupContactNumber;
    private String dropOffAddress, dropOffContact, dropOffContactNumber;
    private String itemValueCost;
    private String notes;

    private String orderCost;
    private String invoiceID;
    private String mpesaPaymentID;
    private long dateCreated;
    private String userID;
    private String orderStatus;
    private String riderID;

    private boolean orderSynced, isBeingEdited;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getPickupContact() {
        return pickupContact;
    }

    public void setPickupContact(String pickupContact) {
        this.pickupContact = pickupContact;
    }

    public String getPickupContactNumber() {
        return pickupContactNumber;
    }

    public void setPickupContactNumber(String pickupContactNumber) {
        this.pickupContactNumber = pickupContactNumber;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public String getDropOffContact() {
        return dropOffContact;
    }

    public void setDropOffContact(String dropOffContact) {
        this.dropOffContact = dropOffContact;
    }

    public String getDropOffContactNumber() {
        return dropOffContactNumber;
    }

    public void setDropOffContactNumber(String dropOffContactNumber) {
        this.dropOffContactNumber = dropOffContactNumber;
    }

    public String getItemValueCost() {
        return itemValueCost;
    }

    public void setItemValueCost(String itemValueCost) {
        this.itemValueCost = itemValueCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getMpesaPaymentID() {
        return mpesaPaymentID;
    }

    public void setMpesaPaymentID(String mpesaPaymentID) {
        this.mpesaPaymentID = mpesaPaymentID;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRiderID() {
        return riderID;
    }

    public void setRiderID(String riderID) {
        this.riderID = riderID;
    }

    public boolean isOrderSynced() {
        return orderSynced;
    }

    public void setOrderSynced(boolean orderSynced) {
        this.orderSynced = orderSynced;
    }

    public boolean isBeingEdited() {
        return isBeingEdited;
    }

    public void setBeingEdited(boolean beingEdited) {
        isBeingEdited = beingEdited;
    }
}
