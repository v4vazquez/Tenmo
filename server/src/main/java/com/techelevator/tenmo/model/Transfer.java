package com.techelevator.tenmo.model;

public class Transfer {

    private int transferId;
    private int senderId;
    private int receiverId;
    private String transferType;
    private double transferAmount;
    private String transferStatus;

    public Transfer(){}

    public Transfer(int transferId, int senderId, int receiverId, String transferType, double transferAmount, String transferStatus) {
        this.transferId = transferId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.transferType = transferType;
        this.transferAmount = transferAmount;
        this.transferStatus = transferStatus;
    }

    public int getTransferId() {
        return transferId;
    }
    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getSenderId() {
        return senderId;
    }
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getTransferType() {
        return transferType;
    }
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public double getTransferAmount() {
        return transferAmount;
    }
    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public String getTransferStatus() {
        return transferStatus;
    }
    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", transferType='" + transferType + '\'' +
                ", transferAmount=" + transferAmount +
                ", transferStatus=" + transferStatus +
                '}';
    }
}
