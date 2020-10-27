package com.company.network;

import com.company.BankManagerConfig;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerThread extends  Thread  {
    @Override
    public void run(){
        int port = BankManagerConfig.getInstance().getBankPort();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        }catch(IOException exc){
            System.out.println(exc.getMessage());
            exc.printStackTrace();
            return;
        }
        while (!isInterrupted()) {
            try {
                Socket clientSocket = serverSocket.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(clientSocket);
                connectionHandler.run();
                System.out.println("Thread started for new connection with port" + clientSocket.getPort());
            }catch(IOException exc){
                System.out.println(exc.getMessage());
                exc.printStackTrace();
                interrupt();
            }
        }
        try {
            serverSocket.close();
        }catch(IOException exc){
            System.out.println(exc.getMessage());
            exc.printStackTrace();
        }
    }
}
