package com.company.network;



public class BankNetworkManager{
    private static BankNetworkManager instance;
    private ServerThread server;
    private BankNetworkManager(){
        server = new ServerThread();
    }
    public void startServer(){
        server.start();
    }
    public void interrupt(){
        server.interrupt();
    }
    public static BankNetworkManager getInstance(){
        if(instance==null)
            instance = new BankNetworkManager();
        return instance;
    }
}
