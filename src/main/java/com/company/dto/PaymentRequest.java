package com.company.dto;

public class PaymentRequest {
    private long sender;
    private long receiver;
    private long amount;
    private long senderBank;
    private long receiverBank;
    public PaymentRequest(long sender,long receiver,long amount,long senderBank,long receiverBank){
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.senderBank = senderBank;
        this.receiverBank = receiverBank;
    }
    public long getSender(){
        return sender;
    }
    public long getReceiver(){
        return receiver;
    }
    public long getAmount(){
        return amount;
    }
    public long getSenderBank(){return senderBank;}
    public long getReceiverBank(){return receiverBank;}
}
