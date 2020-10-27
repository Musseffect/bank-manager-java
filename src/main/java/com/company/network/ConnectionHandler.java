package com.company.network;

import com.company.dto.PaymentRequest;
import com.company.dto.PaymentStatus;
import com.company.implementations.IBankServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler extends Thread{

    public ConnectionHandler(Socket clientSocket){
        try {
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            byte messageType = dis.readByte();
            switch (messageType) {
                case 0:{//Find account id
                    long accountId = dis.readLong();
                    try {
                        boolean hasAccount = IBankServiceImpl.getInstance().hasAccount(accountId);
                        if(hasAccount){
                            dos.writeByte(1);
                        }else{
                            dos.writeByte(0);
                        }
                    }catch(IOException exc){
                        dos.writeByte(0);
                    }
                    break;
                }
                case 1:{//Transaction
                    String message = dis.readUTF();
                    ObjectMapper objectMapper = new ObjectMapper();
                    PaymentRequest request = objectMapper.readValue(message,PaymentRequest.class);
                    PaymentStatus status = IBankServiceImpl.getInstance().makeTransaction(request,false);
                    dos.writeUTF(objectMapper.writeValueAsString(status));
                    break;
                }
            }
            clientSocket.close();
        }catch(IOException exc){
            interrupt();
        }
    }

}
