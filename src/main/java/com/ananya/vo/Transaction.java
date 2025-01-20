package com.ananya.vo;

import java.util.List;

public class Transaction {
    private String payer;
    private List<String> payee;
    private Double amount;
    private String transactionDescription;

    public Transaction(String payer, List<String> payee, Double amount, String transactionDescription) {
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.transactionDescription = transactionDescription;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public List<String> getPayee() {
        return payee;
    }

    public void setPayee(List<String> payee) {
        this.payee = payee;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    @Override
    public String toString() {
        return String.format("%s paid Rs. %.2f to %s for %s", payer, amount, payee.toString(), transactionDescription);
    }
}
