package com.company.interfaces;

import com.company.dto.Account;
import com.company.dto.PaymentRequest;
import com.company.dto.PaymentStatus;
import com.company.exceptions.AccountNotFoundException;
import com.company.exceptions.AccountRewriteException;

import java.io.IOException;

public interface IBankService {
    String showAccounts()  throws IOException;
    void createAccount(Account account) throws IOException,AccountRewriteException;
    PaymentStatus makeTransaction(PaymentRequest request, boolean local) throws IOException ;
    boolean hasAccount(long accountId)throws IOException;
    long getAccountBankId(long accountId)throws IOException,AccountNotFoundException;
}
