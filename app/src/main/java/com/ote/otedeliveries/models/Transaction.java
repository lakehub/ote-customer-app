package com.ote.otedeliveries.models;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Transaction implements RealmModel {
    @PrimaryKey
    private String transactionID;

    private long transactionDateTime;
    private String transcationAmount;
    private String transactionType;

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public long getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(long transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public String getTranscationAmount() {
        return transcationAmount;
    }

    public void setTranscationAmount(String transcationAmount) {
        this.transcationAmount = transcationAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
