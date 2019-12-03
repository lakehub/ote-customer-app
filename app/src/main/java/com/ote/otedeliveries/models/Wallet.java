package com.ote.otedeliveries.models;

import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Wallet implements RealmModel {
    @PrimaryKey
    private String walletID;

    private RealmList<Transaction> walletTransactions;

    private String walletBalance;

    public String getWalletID() {
        return walletID;
    }

    public void setWalletID(String walletID) {
        this.walletID = walletID;
    }

    public RealmList<Transaction> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(RealmList<Transaction> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }
}