package model;

import server.Client;
import server.Server;
import test.GameManager;
import test.Tile;
import test.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class ScrabblePlayer {
    private Server server; // New server instance for hosting

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private int id;
    private int score;
    private ArrayList<Tile> hand;
    private boolean isTurn;
    public ScrabblePlayer() {
        this.hand = new ArrayList<>();
        this.isTurn = false;
        //GameManager.getInstance().addPlayer(this);
    }
    public void startServer()
    {
        if (Server.isGameInProgress) {
            // Show error message
            System.out.println("A game is already in progress. Cannot start a new game as host.");
            // Return to the main menu
        }
        else {
            server = Server.getInstance();
            new Thread(() -> server.run()).start();
            try {
                Thread.sleep(1000); // need a sleep to wait for server to start
                connectToServer();
            } catch (InterruptedException e) {
                System.out.println("Error starting server");
                e.printStackTrace();
            }
        }

    }

    public void connectToServer() {
        try {
            System.out.println("Connecting to server...");
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ScrabblePlayer.InputHandler inHandler = new ScrabblePlayer.InputHandler();
            Thread t = new Thread(inHandler);
            t.start();
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Server is not running");
        }
    }
    public void sendMessage(String message) {
        out.println(message);
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
    // get
    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Tile> gethand() {
        return hand;
    }
    // useful functions
    public void incrementScore(int score) {
        this.score += score;
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

    public int choice()
    {   Scanner Scanner = new Scanner(System.in);
        System.out.println("Player "+id+" turn");
        System.out.println(hand);
        System.out.println("1. Place a word, 2.pass");
        return Scanner.nextInt();
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
        return "Player " + id + " score: " + score;
    }

    class InputHandler implements Runnable {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }
}
