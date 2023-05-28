package server;

import test.ClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    int port;
    ClientHandler ch;
    int numOfThreads;
    boolean stop;
    public static int numOfClients;
    private static List<Socket> clients; // Maintain a list of connected clients
    private int currentPlayerIndex;

    ExecutorService threadPool;

    public MyServer(int port, ClientHandler ch, int numOfThreads) {
        clients = new ArrayList<>();
        currentPlayerIndex = 0;


        this.port = port;
        this.numOfThreads = numOfThreads;
        this.ch = ch;
        this.threadPool = Executors.newFixedThreadPool(numOfThreads);
        numOfClients = 0;
    }

    public void start() {
        stop = false;
        new Thread(this::startsServer).start();
    }

    private void clientMethod(Socket client) {//creates new clienthandler for every client, closes socket and stream when finished.
    	try {
        	Class<? extends ClientHandler> chClass = this.ch.getClass();
        	ClientHandler chNew = chClass.getDeclaredConstructor().newInstance();
        	chNew.handleClient(client.getInputStream(), client.getOutputStream());
        	chNew.close();
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void startsServer() {//takes care of every client in a different thread with threadpool
        try {
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(1000);//waiting restarts every second
            while (!stop) {
                try {
                    Socket client = server.accept();
                    clients.add(client); // Add the connected client to the list
                    numOfClients=clients.size();
                    threadPool.execute(()->clientMethod(client));
                } catch (SocketTimeoutException e) {
                }
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        stop = true;
        threadPool.shutdown();
    }
    public static void sendToAllClients(String message) {
        for (Socket client : clients) {
            sendToClient(client, message);
        }
    }

    public void sendToClient(int clientIndex, String message) {
        Socket client = clients.get(clientIndex);
        sendToClient(client, message);
    }
    private static void sendToClient(Socket client, String message) {
        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumOfClients() {
        return this.numOfClients;
    }

    public void startGame() {
        sendToClient(currentPlayerIndex, "Your turn! Enter 1 or 2:");

        while (!stop) {
            try {
                Socket client = clients.get(currentPlayerIndex);

                // Wait for the client's response
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String clientResponse = in.readLine();
                System.out.println("Client " + currentPlayerIndex + " response: " + clientResponse);

                // Send the response to all clients
                sendToAllClients("Client " + currentPlayerIndex + " chose: " + clientResponse);

                // Move to the next player
                currentPlayerIndex = (currentPlayerIndex + 1) % clients.size();
                sendToClient(currentPlayerIndex, "Your turn! Enter 1 or 2:");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}