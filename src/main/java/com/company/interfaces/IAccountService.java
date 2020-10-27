package com.company.interfaces;

import com.company.BankManagerConfig;
import com.company.dto.Account;
import com.company.exceptions.AccountNotFoundException;
import com.company.exceptions.AccountRewriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IAccountService {
    void fetch()throws IOException ;
    void commit()throws IOException;
    List<Account> getAccounts()  throws IOException;
    Account getAccountById(long id)  throws IOException,AccountNotFoundException;
    Account getCorrespondentAccount(long id) throws IOException,AccountNotFoundException;
    void createAccount(Account account) throws IOException,AccountRewriteException;
    void updateAccount(Account account) throws IOException,AccountNotFoundException ;
    void deleteAccount(long id) throws IOException ;
}
