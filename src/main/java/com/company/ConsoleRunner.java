package com.company;

import com.company.dto.Account;
import com.company.dto.PaymentRequest;
import com.company.dto.PaymentStatus;
import com.company.exceptions.AccountNotFoundException;
import com.company.exceptions.AccountRewriteException;
import com.company.implementations.IBankServiceImpl;
import com.company.network.BankNetworkManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Scanner;


public class ConsoleRunner {
    public ConsoleRunner() throws IOException{
        this.Run();
    }
    private static void ClearConsole() throws IOException {
        Runtime.getRuntime().exec("cls");
    }
    private void Run() throws IOException{
        //Semaphore semaphore = new Semaphore(1,true);
        //start server
        BankNetworkManager.getInstance().startServer();
        //BankNetworkManager network = new BankNetworkManager("Server",semaphore);
        //network.startServer();

        Scanner in = new Scanner(System.in);
        boolean isRunning = true;
        while(isRunning) {
            System.out.println("Enter command: ");
            System.out.println("SEND - send payment");
            System.out.println("PRINT - show bank accounts");
            System.out.println("CREATE - create new account");
            String command = in.nextLine();
            switch(command) {
                case "SEND":{//DONE
                    ConsoleRunner.ClearConsole();
                    System.out.println("Enter sender id:");
                    long senderId = in.nextLong();
                    System.out.println("Enter receiver id:");
                    long receiverId = in.nextLong();
                    System.out.println("Enter amount of money to send:");
                    long amount = in.nextLong();
                    long senderBankId;
                    try {
                        senderBankId = IBankServiceImpl.getInstance().getAccountBankId(senderId);
                    }catch(AccountNotFoundException exc){
                        System.out.println("Cannot find bank of sender account");
                        break;
                    }
                    long receiverBankId;
                    try {
                        receiverBankId = IBankServiceImpl.getInstance().getAccountBankId(receiverId);
                    }catch(AccountNotFoundException exc){
                        System.out.println("Cannot find bank of receiver account");
                        break;
                    }
                    PaymentStatus status = IBankServiceImpl.getInstance().makeTransaction(new PaymentRequest(senderId, receiverId, amount,senderBankId, receiverBankId),true);
                    if (status.ok()) {
                        System.out.println("Successful transaction");
                    } else {
                        System.out.println("Transaction failed. Reason: " + status.getMessage());
                    }
                    break;
                }
                case "PRINT":{//DONE
                    ConsoleRunner.ClearConsole();
                    System.out.println("Bank accounts:");
                    System.out.println(IBankServiceImpl.getInstance().showAccounts());
                    break;
                }
                case "CREATE":{//DONE
                    ConsoleRunner.ClearConsole();
                    System.out.println("Account creation");
                    System.out.println("Enter new account id:");
                    long id = in.nextLong();
                    System.out.println("Enter initial account balance:");
                    long balance = in.nextLong();
                    //semaphore.acquire();
                    try {
                        IBankServiceImpl.getInstance().createAccount(new Account(id, balance));
                    } catch (AccountRewriteException exc) {
                        System.out.println(exc.getMessage());
                    }
                    //semaphore.release();
                    break;
                }
                case "EXIT":{//DONE
                    ConsoleRunner.ClearConsole();
                    System.out.println("Exiting...");
                    isRunning = false;
                    break;
                }
                default:
                    System.out.print(String.format("Unknown command %s",command));
            }
        }
        BankNetworkManager.getInstance().interrupt();
    }
}
