package com.company.dto;

public class Account {
    private long clientId;
    private long balance;
    private long correspondentBankId;
    public Account(long clientId,long balance,long correspondentBankId){
        this.clientId = clientId;
        this.balance = balance;
        this.correspondentBankId = correspondentBankId;
    }
    public Account(long clientId,long balance){
        this.clientId = clientId;
        this.balance = balance;
        this.correspondentBankId = -1;
    }
    public long getCorrespondentBankId(){
        return correspondentBankId;
    }
    public long getId(){
        return clientId;
    }
    public long getBalance(){
        return balance;
    }
    public void setBalance(long balance){
        this.balance = balance;
    }
    public boolean isCorrespondent(){
        return correspondentBankId!=-1;
    }
    @Override
    public String toString(){
        if(correspondentBankId!=-1)
            return String.format("Client %d balance: %d",clientId,balance);
        return String.format("Correspondent account %d from bank %d balance: %d",clientId,correspondentBankId,balance);
    }
}
