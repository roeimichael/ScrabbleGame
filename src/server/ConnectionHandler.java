package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

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

    // game manager initailization
    private GameManager gameManager;

    // player related shit
    private int id;
    private int score;
    private ArrayList<Tile> hand;
    private boolean isTurn;


    public ConnectionHandler(Socket client, int id) {
        this.client = client;
        this.id = id + 1;

        gameManager = GameManager.getInstance();

        this.id = id;
        this.score = 0;
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

        gameManager.addPlayer(this);
        out = new PrintWriter(outToClient, true);
        in = new BufferedReader(new InputStreamReader(inFromClient));
        out.println(id);
        if (this.id == 1) {
            out.println("YOU ARE THE HOST");
        } else {
            out.println("YOU ARE NOT THE HOST");
        }
        out.println("Welcome to the server Player " + this.id + "!");
        System.out.println("Client " + this.id + " has connected");
    }

    @Override
    public void close() {}
    public int getId() {
        return id;
    }
    public int getScore() {
        return score;
    }
    public ArrayList<Tile> gethand() {
        return hand;
    }
    public void incrementScore(int score) {
        this.score += score;
    }
    public void removeTiles() {hand.clear();}
    public void resetScore() {
        score = 0;
    }
    public void refillBag(Tile.Bag tileBag) {
        while (hand.size() < 7) {
            Tile tile = tileBag.getRand();
            if (tile != null) {
                hand.add(tile);
            } else {
                break;
            }
        }
    }

    public void removeWord(Word w)
    {
        for(Tile t:w.getTiles())
        {
            hand.remove(t);
        }
    }
    public Word getWord() {
        Vector<Tile> wordTiles = new Vector<>(); // saves the tiles that are part of the word the user has selected
        Scanner Scanner = new Scanner(System.in);
        int input;
        while (true) {
            System.out.println(hand);
            System.out.println("Enter the index of the char (enter '0' to stop):");
            input = Scanner.nextInt();
            if (input == 0) {
                break;
            } else {
                wordTiles.add(hand.get(input-1));
            }
        }
        System.out.println("Enter row");
        int row = Scanner.nextInt();
        System.out.println("Enter col");
        int col = Scanner.nextInt();
        System.out.println("Enter vertical or horizontal (1 for vertical, 2 for horizontal)");
        int dir = Scanner.nextInt();
        boolean vert;
        if(dir==1)
        {
            vert=true;
        }
        else
        {
            vert=false;
        }
        Tile[] array = new Tile[wordTiles.size()];
        wordTiles.toArray(array);
        return new Word(array,row,col,vert);
    }
    public String toString() {
        return "Player " + id + " has a score of " + score;
    }
}
