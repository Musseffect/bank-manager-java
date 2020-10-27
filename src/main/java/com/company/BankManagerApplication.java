package com.company;

import com.company.implementations.IAccountServiceImpl;
import com.company.implementations.IBankServiceImpl;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public class BankManagerApplication {
    private static void createConfig(String path){
        throw new NotImplementedException();
    }
    public static void main(String[] args) throws IOException,InterruptedException {
        String configPath = "./config.json";
        if(args.length>0){
            configPath = args[0];
        }
        createConfig(configPath);
        new ConsoleRunner();
    }
}
