package com.company.implementations;

import com.company.BankManagerConfig;
import com.company.dto.Account;
import com.company.exceptions.AccountNotFoundException;
import com.company.exceptions.AccountRewriteException;
import com.company.interfaces.IAccountService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IAccountServiceCachedImpl implements IAccountService {
    private List<Account> accountCache;
    @Override
    public void fetch() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
            this.accountCache = objectMapper.readValue(
                    new File(BankManagerConfig.getInstance().getDBPath()),
                    new TypeReference<List<Account>>() {
        });
    }

    @Override
    public void commit() throws IOException {ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(BankManagerConfig.getInstance().getDBPath()),accountCache);
    }

    @Override
    public List<Account> getAccounts(){
        return accountCache;
    }

    @Override
    public Account getAccountById(long id) throws AccountNotFoundException {
        return getAccounts().stream().filter(clientAccount -> clientAccount.getId() == id).findFirst().orElseThrow(AccountNotFoundException::new);
    }

    @Override
    public void createAccount(Account account) throws AccountRewriteException {
        if(getAccounts().stream().filter(clientAccount -> clientAccount.getId() == account.getId()).findFirst().orElse(null)!=null)
            throw new AccountRewriteException();
        accountCache.add(account);
    }

    @Override
    public void updateAccount(Account account) throws AccountNotFoundException {
        Account targetAccount = getAccountById(account.getId());
        targetAccount.setBalance(account.getBalance());
    }

    @Override
    public void deleteAccount(long id){
        accountCache.removeIf(acc -> acc.getId() == id);
    }
    @Override
    public Account getCorrespondentAccount(long id) throws IOException,AccountNotFoundException {
        return getAccounts().stream().filter(clientAccount -> clientAccount.getCorrespondentBankId() == id).findFirst().orElseThrow(AccountNotFoundException::new);
    }
}

