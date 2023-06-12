package model;
import test.ClientHandler;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import test.GameManager;
import test.Tile;
import test.Word;

public class PlayerHandler implements ClientHandler{
    //private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean exit=false;
    private int playerId;
    private static GameManager gm;
    private newServer server;
    private static AtomicInteger connectedClients = new AtomicInteger(0);
    private static AtomicInteger turn = new AtomicInteger(0);
    private static AtomicBoolean gameStarted = new AtomicBoolean(false);
    private HashMap<Character, Integer> letterScores = new HashMap<>(); // temporary saves the letter and its score

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        this.assignLetterScores();
        in=new BufferedReader(new InputStreamReader(inFromclient));
        out=new PrintWriter(outToClient,true);
        int totalConnectedClients = connectedClients.incrementAndGet();
        System.out.println("[Server]Total connected clients: " + totalConnectedClients);
        server = newServer.get();
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
                System.out.println("[Server]Message from client: " + msg);
                switch (msg) {
                    case protocols.NEW_GAME -> this.startGame();
                    case protocols.SERVER_SEND_MSG-> this.serverSendMsg();
                    // gets
                    case protocols.GET_BOARD -> this.sendBoard();
                    case protocols.GET_TURN -> this.sendTurn();
                    case protocols.GET_HAND -> this.sendHand();
                    //case protocols.GET_LAST_SCORE -> this.sendLastScore();
                    case protocols.GET_SCORE -> this.sendScore();
                    case protocols.GET_CURRENT_PLAYER -> this.sendCurrentPlayer();

                    // actions
                    case protocols.PLACE_WORD -> this.placeWord();
                    case protocols.PASS -> this.pass();
                    case protocols.CHALLENGE -> this.challenge();

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



    }

    private void sendLastScore(int lastScore) {
        out.println( protocols.GET_LAST_SCORE);
        out.println(lastScore);

    }

    private void challenge() {
    }

    private void pass() {
    }

    private void placeWord() {
        try {
            String input = in.readLine();

            String[] parts = input.split(","); // example: word = "hello,1,2,true"
            String letters = parts[0];
            int size = letters.length();
            Tile[] tileArray = new Tile[size];

            char[] chars = letters.toCharArray();
            for(int i=0;i<chars.length;i++) {
                if (chars[i] == '_') {
                    //Tile t = new Tile();
                    tileArray[i] = null;
                    continue;
                }
                Tile t = new Tile(Character.toUpperCase(chars[i]), letterScores.get(Character.toUpperCase(chars[i])));
                tileArray[i] = t;
            }
            int row = Integer.parseInt(parts[1]);
            int col = Integer.parseInt(parts[2]);
            boolean isVertical = Boolean.parseBoolean(parts[3]);

            Word word = new Word(tileArray, row, col, isVertical);
            int score = gm.placeWord(word);
            this.sendLastScore(score);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void serverSendMsg() {
        try {
            String msg = in.readLine();
            this.server.sendMessagesToAllClients(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCurrentPlayer() {
        out.println(protocols.GET_CURRENT_PLAYER);
        out.println(gm.getCurrentPlayer().getId());
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
    private void assignLetterScores() {
        letterScores.put('A', 1);
        letterScores.put('B', 3);
        letterScores.put('C', 3);
        letterScores.put('D', 2);
        letterScores.put('E',1);
        letterScores.put('F',4);
        letterScores.put('G',2);
        letterScores.put('H',4);
        letterScores.put('I',1);
        letterScores.put('J',8);
        letterScores.put('K',5);
        letterScores.put('L',1);
        letterScores.put('M',3);
        letterScores.put('N',1);
        letterScores.put('O',1);
        letterScores.put('P',3);
        letterScores.put('Q',10);
        letterScores.put('R',1);
        letterScores.put('S',1);
        letterScores.put('T',1);
        letterScores.put('U',1);
        letterScores.put('V',4);
        letterScores.put('W',4);
        letterScores.put('X',8);
        letterScores.put('Y',4);
        letterScores.put('Z',10);
    }

}
