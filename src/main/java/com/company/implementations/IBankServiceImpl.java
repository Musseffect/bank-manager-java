package com.company.implementations;

import com.company.BankManagerConfig;
import com.company.dto.Account;
import com.company.dto.PaymentRequest;
import com.company.dto.PaymentStatus;
import com.company.exceptions.AccountNotFoundException;
import com.company.exceptions.AccountRewriteException;
import com.company.interfaces.IAccountService;
import com.company.interfaces.IBankService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;

public class IBankServiceImpl implements IBankService {
    private IAccountService accountService;
    private static IBankServiceImpl instance;
    private IBankServiceImpl(){
        this.accountService = new IAccountServiceCachedImpl();
    }
    public static IBankServiceImpl getInstance(){
        if(instance == null){
            instance = new IBankServiceImpl();
        }
        return instance;
    }
    @Override
    public boolean hasAccount(long accountId)throws IOException{
        accountService.fetch();
        try {
            accountService.getAccountById(accountId);
            return true;
        }catch(AccountNotFoundException exc){
            return false;
        }
    }
    @Override
    public void createAccount(Account account) throws IOException,AccountRewriteException{
        accountService.fetch();
        accountService.createAccount(account);
        accountService.commit();
    }
    @Override
    public String showAccounts() throws IOException {
        accountService.fetch();
        List<Account> accounts = accountService.getAccounts();
        StringBuilder result = new StringBuilder();
        for(Account account: accounts){
            result.append(account.toString()).append('\n');
        }
        return result.toString();
    }
    private boolean isCurrentBank(long bankId){
        return bankId == BankManagerConfig.getInstance().getBankPort();
    }
    public long getAccountBankId(long accountId) throws IOException,AccountNotFoundException{
        try {
            Account account = accountService.getAccountById(accountId);
            return BankManagerConfig.getInstance().getBankPort();
        }catch(AccountNotFoundException exc){
            //find sender bank
            for (Map.Entry<Long, Integer> bank : BankManagerConfig.getInstance().getBankList()) {
                Socket socket = new Socket("127.0.0.1", bank.getValue());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                dos.writeByte(0);
                ObjectMapper objectMapper = new ObjectMapper();
                dos.writeUTF(objectMapper.writeValueAsString(accountId));
                byte answer = dis.readByte();
                if (answer != 1)
                    continue;
                socket.close();
                return bank.getKey();
            }
        }
        throw new AccountNotFoundException();
    }
    private PaymentStatus localTransaction(PaymentRequest request) throws IOException{
        try {
            accountService.fetch();
            if (accountService.getAccountById(request.getSender()).getBalance() >= request.getAmount()) {
                Account sender = accountService.getAccountById(request.getReceiver());
                Account receiver = accountService.getAccountById(request.getReceiver());
                sender.setBalance(sender.getBalance() - request.getAmount());
                receiver.setBalance(receiver.getBalance() - request.getAmount());
                accountService.updateAccount(sender);
                accountService.updateAccount(receiver);
                accountService.commit();
                return new PaymentStatus();
            }
        }catch(AccountNotFoundException exc){
            return new PaymentStatus("Cannot found account");
        }
        return new PaymentStatus("Sender's balance doesn't have enough money to make a transaction");
    }
    //only if receiver in this bank
    private void rollbackTransaction(long accountId,long balanceChange) throws IOException{
        try {
            Account account = accountService.getAccountById(accountId);
            account.setBalance(account.getBalance() + balanceChange);
            accountService.updateAccount(account);
        }catch(AccountNotFoundException exc){
            System.out.println("Critical error: cant found account to rollback transaction");
            System.exit(1);
        }
    }
    private PaymentStatus foreignReceiver(PaymentRequest request){
        try {
            accountService.fetch();
            Account account = accountService.getAccountById(request.getReceiver());
            Account correspondentAccount = accountService.getCorrespondentAccount(request.getReceiverBank());
            account.setBalance(account.getBalance() - request.getAmount());
            correspondentAccount.setBalance(account.getBalance() + request.getAmount());
            try {
                Socket socket = new Socket("127.0.0.1", BankManagerConfig.getInstance().getNetworkPortByBankIndex(request.getReceiverBank()));
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                ObjectMapper objectMapper = new ObjectMapper();
                dos.writeByte(1);
                dos.writeUTF(objectMapper.writeValueAsString(request));
                PaymentStatus status = objectMapper.readValue(dis.readUTF(),PaymentStatus.class);
                socket.close();
                if(status.ok())
                    accountService.commit();
                return status;
            }catch(IOException exc){
                rollbackTransaction(request.getSender(),request.getAmount());
                rollbackTransaction(correspondentAccount.getId(),-request.getAmount());
                return new PaymentStatus("Cannot complete requested transaction");
            }
        }catch(AccountNotFoundException exc){
            //TODO: print id of account
            return new PaymentStatus("Cannot find account");
        }catch(IOException exc){
            return new PaymentStatus("Cannot complete requested transaction");
        }
    }
    private PaymentStatus foreignSender(PaymentRequest request, boolean local){
        if(!local){
            try {
                accountService.fetch();
                Account account = accountService.getAccountById(request.getReceiver());
                Account correspondentAccount = accountService.getCorrespondentAccount(request.getSenderBank());
                account.setBalance(account.getBalance() + request.getAmount());
                correspondentAccount.setBalance(account.getBalance() - request.getAmount());
                accountService.commit();
                return new PaymentStatus();
            }catch(AccountNotFoundException exc){
                //TODO: print id of account
                return new PaymentStatus("Cannot find account");
            }catch(IOException exc){
                return new PaymentStatus("Cannot complete requested transaction");
            }
        }else {
            try {
                Socket socket = new Socket("127.0.0.1", BankManagerConfig.getInstance().getNetworkPortByBankIndex(request.getSenderBank()));
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                //Just send request and return status, it will invoke foreign receiver in sender bank
                ObjectMapper objectMapper = new ObjectMapper();
                dos.writeByte(1);
                dos.writeUTF(objectMapper.writeValueAsString(request));
                PaymentStatus status = objectMapper.readValue(dis.readUTF(),PaymentStatus.class);
                socket.close();
                return status;
            } catch (IOException exc) {
                return new PaymentStatus("Cannot complete requested transaction");
            }
        }
    }
    private PaymentStatus foreignTransaction(PaymentRequest request){
        try{
            Socket socket = new Socket("127.0.0.1",BankManagerConfig.getInstance().getNetworkPortByBankIndex(request.getSenderBank()));
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            ObjectMapper objectMapper = new ObjectMapper();
            //send transaction
            dos.writeByte(1);
            dos.writeUTF(objectMapper.writeValueAsString(request));
            //receive payment status
            PaymentStatus status = objectMapper.readValue(dis.readUTF(),PaymentStatus.class);
            socket.close();
            return status;
        }catch(IOException exc){
            return new PaymentStatus("Cannot complete requested transaction");
        }
    }
    @Override
    public PaymentStatus makeTransaction(PaymentRequest request, boolean local) throws IOException {
        if(request.getAmount()<=0){
            return new PaymentStatus("Invalid value of amount");
        }
        if(isCurrentBank(request.getSenderBank())){
            if(isCurrentBank(request.getReceiverBank())){
                return localTransaction(request);
            }else{
                return foreignReceiver(request);
            }
        }
        if(isCurrentBank(request.getReceiverBank())){
            return foreignSender(request, local);
        }
        return foreignTransaction(request);
    }
}
