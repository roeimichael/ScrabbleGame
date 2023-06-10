package model;
import test.ClientHandler;

import java.io.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import test.GameManager;

public class PlayerHandler implements ClientHandler{
    //private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean exit=false;
    private int playerId;
    private GameManager gm;
    private static AtomicInteger connectedClients = new AtomicInteger(0);
    private static AtomicInteger turn = new AtomicInteger(0);
    private static AtomicBoolean gameStarted = new AtomicBoolean(false);

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {

        in=new BufferedReader(new InputStreamReader(inFromclient));
        out=new PrintWriter(outToClient,true);
        int totalConnectedClients = connectedClients.incrementAndGet();
        System.out.println("[Server]Total connected clients: " + totalConnectedClients);

        // first the server sends the new player his id and adds him to the game
        String msgFromPlayer= null;
        try {
            gm=GameManager.get();
            msgFromPlayer=in.readLine();//gets the id from the client
            playerId= Integer.parseInt(msgFromPlayer);
            System.out.println("[Server]client "+msgFromPlayer);
            gm.addPlayer(playerId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO - need to rewrite this part
        // game loop
        // server runs infinitely
        // server waits for a message from the client

            // listen to the server, and acts accordingly
        while(true)
        {	//communication protocol: newServer -> model -> playerHandler
            // 1. server sends message type
            // 2. model sends message to playehandler requesting the content
            // 3. playerhandler sends content to player
            try {
                String msg = in.readLine();
                switch (msg) {
                    case protocols.NEW_GAME -> this.startGame();
                    case protocols.GET_BOARD -> this.sendBoard();
                    case protocols.GET_TURN -> this.sendTurn();
                    case protocols.GET_HAND -> this.sendHand();
                    case protocols.GET_SCORE -> this.sendScore();

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



    }

    private void startGame() {
        // model sends the message to start the game
        gameStarted.set(true);
        if(this.playerId == 0) {
            // if this is the host, he needs to start the game
            gm.startGame();
        }
        else {
            System.out.println("waiting for host to start the game");
        }
        out.println(protocols.NEW_GAME);
        // when starting the game all the players need to get their hand
        out.println(gm.getPlayer(this.playerId).getHand());

    }

    // send methods to send information to the model
    private void sendScore() {
        out.println(protocols.GET_SCORE);
        out.println(gm.getPlayer(playerId).getScore());
    }

    private void sendHand() {
        out.println(protocols.GET_HAND);
        out.println(gm.getPlayer(playerId).getHand());
    }

    private void sendTurn() {

    }

    private void sendBoard() {// TODO - need to check if true
        out.println(protocols.GET_BOARD);
        out.println(gm.getBoard());
    }

    @Override
    public void close() {

    }

}
