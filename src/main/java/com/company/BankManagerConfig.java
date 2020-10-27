package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankManagerConfig {
    private String dbPath;
    private int bankPort;
    private HashMap<Long,Integer> networkPorts;
    private static BankManagerConfig instance;
    private BankManagerConfig(){
        this.dbPath = "./accounts.json";
        this.bankPort = 8000;
        this.networkPorts = new HashMap<>();
    }
    public static BankManagerConfig getInstance(){
        if(instance == null)
            instance = new BankManagerConfig();
        return instance;
    }
    public String getDBPath(){
        return dbPath;
    }
    public int getBankPort(){
        return bankPort;
    }
    public List<Map.Entry<Long,Integer>> getBankList(){
        return new ArrayList<>(networkPorts.entrySet());
    }
    public int getNetworkPortByBankIndex(long bankIndex){
        return networkPorts.get(bankIndex);
    }
    public void setDBPath(String dbPath){this.dbPath = dbPath; }
    public void setBankPort(int port){
        this.bankPort = port;
    }
    public void setNetworkPorts(HashMap<Long,Integer> networkPorts){
        this.networkPorts = networkPorts;
    }
}
