package com.company.implementations;

import com.company.BankManagerConfig;
import com.company.dto.Account;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.company.exceptions.AccountNotFoundException;
import com.company.exceptions.AccountRewriteException;
import com.company.interfaces.IAccountService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class IAccountServiceImpl implements IAccountService {
    @Override
    public void fetch(){
    }
    @Override
    public void commit(){
    }
    private void save(List<Account> accounts) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(BankManagerConfig.getInstance().getDBPath()),accounts);
    }
    private List<Account> load() throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(BankManagerConfig.getInstance().getDBPath()), new TypeReference<List<Account>>(){});
    }
    @Override
    public List<Account> getAccounts() throws IOException {
        return load();
    }
    @Override
    public Account getAccountById(long id) throws IOException,AccountNotFoundException {
        return load().stream().filter(clientAccount -> clientAccount.getId()==id).findFirst().orElseThrow(AccountNotFoundException::new);
    }
    @Override
    public void createAccount(Account account) throws IOException,AccountRewriteException {
        List<Account> accounts = load();
        if(accounts.stream().filter(clientAccount -> clientAccount.getId()==account.getId()).findFirst().orElse(null)!=null)
            throw new AccountRewriteException();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(BankManagerConfig.getInstance().getDBPath()),accounts);
    }
    @Override
    public void updateAccount(Account account) throws IOException,AccountNotFoundException {
        List<Account> accounts = load();
        Account oldAccount = accounts.stream().filter(clientAccount -> clientAccount.getId()==account.getId()).findFirst().orElseThrow(AccountNotFoundException::new);
        oldAccount.setBalance(account.getBalance());
        save(accounts);
    }
    @Override
    public void deleteAccount(long id) throws IOException {
        List<Account> accounts = load();
        accounts.removeIf(acc -> acc.getId() == id);
        save(accounts);
    }
    @Override
    public Account getCorrespondentAccount(long id) throws IOException,AccountNotFoundException {
        return getAccounts().stream().filter(clientAccount -> clientAccount.getCorrespondentBankId() == id).findFirst().orElseThrow(AccountNotFoundException::new);
    }
}
