package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import server.ClientHandler;
import server.Server;
import server.protocols;
import test.GameManager;
import test.Tile;
import test.Word;

public class ConnectionHandler implements ClientHandler {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private int id;
    private Server server;

    private boolean isTurn;
    private int score;
    private ArrayList<Tile> hand;

    private GameManager gameManager;

    public ConnectionHandler(Socket client, int id) {
        this.client = client;
        this.id = id + 1;
        isTurn = false;
        server = Server.getInstance();
        gameManager = GameManager.getInstance();
        this.hand = new ArrayList<>();

    }

    public void sendMessage(String mes) {
        out.println(mes);
    }

    public void shutdown() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (client != null && !client.isClosed())
                client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        try {
            server = Server.getInstance();
            gameManager = GameManager.getInstance();
            gameManager.addPlayer(this);
            out = new PrintWriter(outToClient, true);
            in = new BufferedReader(new InputStreamReader(inFromClient));
            isTurn = false;

            if (this.id == 1) {
                out.println("YOU ARE THE HOST");
                //out.println("type 'start' to start the game");
            } else {
                out.println("YOU ARE NOT THE HOST");
            }

            out.println("Welcome to the server Player " + this.id + "!");
            System.out.println("Client " + this.id + " has connected");
            //server.broadcast("Client " + this.id + " has connected");

            String message;
            while ((message = in.readLine()) != null) {
                switch(message){
                    case protocols.NEW_GAME_AS_HOST:
                        out.println("Creating game as host");
                        server.broadcast("Game has been created");
                        gameManager.restartGame();
                        break;
                    case protocols.JOIN_GAME_AS_CLIENT:
                        gameManager.joinGame(this);
                        out.println("Joining game as client");
                        server.broadcast("Game has started");

                        break;
                    case protocols.START_GAME:
                        if (id == 1) {
                            server.broadcast("PLAYER 1 wants to start the game");

                            server.setGameStarted();
                            server.getConnections().get(server.getTurn()).isTurn = true;
                            gameManager.restartGame();
                        } else {
                            out.println("You are not the host so be quiet");
                        }
                        break;
                }
//                if(message.equals(protocols.START_GAME_AS_HOST))
//                {
//                    out.println("Starting game as host");
//                    server.broadcast("Game has started");
//                }
//                 else if (message.equals("start")) {
//                    if (id == 1) {
//                        server.broadcast("Game has started");
//                        server.setGameStarted();
//                        server.getConnections().get(server.getTurn()).isTurn = true;
//                    } else {
//                        out.println("You are not the host so be quiet");
//                    }
//                } else {
//                    if (this.isTurn) {
//                        System.out.println("Client " + id + " has sent: " + message);
//                        server.broadcast("Client " + id + " has sent: " + message);
//                        this.isTurn = false;
//                        server.nextTurn();
//                        server.getConnections().get(server.getTurn()).isTurn = true;
//                    } else {
//                        out.println("It is not your turn");
//                    }

            }
        } catch (IOException e) {
            shutdown();
        }
    }
    public int getId()
    {
        return this.id;
    }
    public boolean getisTurn()
    {
        return this.isTurn;
    }
    public int getScore()
    {
        return this.score;
    }
    public void removeTiles()
    {
        if (hand!=null && hand.size() > 0)
            hand.clear();
    }
    public void refillBag(Tile.Bag tileBag) {
        // addding tiles to the hand until it reaches 7 tiles
        while (hand.size() < 7) {
            Tile tile = tileBag.getRand();
            if (tile != null) {
                hand.add(tile);
            } else {
                break; // If no more tiles left in the bag
            }
        }
    }

    public void resetScore() {
        score = 0;
    }

    public void removeWord(Word w)
    {
        for(Tile t:w.getTiles())
        {
            hand.remove(t);
        }
    }
    public void incrementScore(int score) {
        this.score += score;
    }

    public ArrayList<Tile> gethand() {
        return hand;
    }
    @Override
    public void close() {

    }

    public String toString() {
        return "Client " + id;
    }
}
