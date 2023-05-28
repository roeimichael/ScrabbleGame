package server;
import test.ClientHandler;

import java.io.*;
import java.net.Socket;

public class PlayerHandler implements ClientHandler {
    BufferedReader in;
    PrintWriter out;
    private int clientNumber;
    private Socket client;


    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient)
    {
        // the server will handle the host as he is the first player to connect to the server
        // the server will send the host the game id and the host will send the game id to the client
        /*
        server send messages by using outToServer.println("message");
        server receive messages by using String serverMessage = inFromServer.readLine();
         */
        //client = clientSocket;

        this.clientNumber = MyServer.numOfClients - 1;
        in=new BufferedReader(new InputStreamReader(inFromclient)); // in to the server
        out=new PrintWriter(outToClient,true); // out to the player
        out.println("Welcome to the game");
        out.println("0-Host, 1-Guest");
        String clientMessage = null;
        try {
            while ((clientMessage) != null) {
                clientMessage = in.readLine();
                System.out.println("Client " + clientNumber + ": " + clientMessage);

                // Send the client's response to the server
                // You can implement your game logic here
                clientMessage="0";
                MyServer.sendToAllClients("Client " + clientNumber + " chose: " + clientMessage);

                clientMessage = null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public void close() {

    }
}
