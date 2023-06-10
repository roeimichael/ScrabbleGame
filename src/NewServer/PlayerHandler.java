package NewServer;
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
    private int PlayerId;
    private GameManager gm;
    private static AtomicInteger connectedClients = new AtomicInteger(0);
    private static AtomicInteger turn = new AtomicInteger(0);
    private static AtomicBoolean startGame = new AtomicBoolean(false);

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
            msgFromPlayer=in.readLine();
            PlayerId= Integer.parseInt(msgFromPlayer);
            System.out.println("[Server]client "+msgFromPlayer);
            gm.addPlayer(PlayerId);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // game loop
        // server runs infinitely
        // server waits for a message from the client
        while(!exit)
        {
            // first send the turn to the client
            out.println(protocols.GET_TURN);
            out.println(turn.get());
//            System.out.println("turn: "+turn.get());
            while(turn.get()!=PlayerId) {
            }
            // then send the board to the client
            GameManager gm= GameManager.get();
            out.println(gm.getBoard());
//            System.out.println("getBoard");

            try {
                msgFromPlayer= in.readLine();
            } catch (IOException e) {
                msgFromPlayer="wait";
            }

            switch (msgFromPlayer) {
                case (protocols.EXIT) -> { // exit
                    out.println(protocols.EXIT);
                    System.out.println("exit");
                    exit=true;
                }
                case (protocols.CHALLENGE) -> { // challenge
                    out.println(protocols.CHALLENGE);
                    System.out.println("challenge");
                }
                case (protocols.QUERY) -> { // query
                    out.println(protocols.QUERY);
                    System.out.println("query");
                }
//                case (protocols.GET_HAND) -> { // getHand
//                    out.println(protocols.GET_HAND);
//                    //mgm.setPlayerLetters(clientID, letters);
//                    out.println(GameManager.get().getPlayerLetters(PlayerId));
//                    System.out.println("getHand");
//                }
//                case (protocols.ADD_WORD) -> {
//                    gm= GameManager.get();
//                    turn.set(gm.getCurrentTurn());
//                    out.println(protocols.ADD_WORD);
//                    if(turn.get()!=PlayerId)
//                    {
//                        out.println("not your turn");
//                        break;
//                    }
//                    out.println("Choose a letter to add: ");
//                    String letter= null;
//                    try {
//                        letter = in.readLine();
//                        out.println("Choose a position to add: ");  // 0-8
//                        int position=Integer.parseInt(in.readLine());
//                        gm.addLetter(position,letter);
//                        gm.changeTurn();
//                        turn.set(gm.getTurn());
//
//                        gm.printBoard();// print board in the server
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println("addWord");
//                }
                case (protocols.GET_BOARD) -> { // getBoard
                    out.println(protocols.GET_BOARD);
                    gm= GameManager.get();
                    out.println(gm.getBoard());
                    System.out.println("getBoard");
                }
            }
        }
        try {
            int exitId = Integer.parseInt(in.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void close() {

    }

    public String randomLetters()
    {
        String letters="";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 7; i++) {
            char randomLetter = (char) (random.nextInt(26) + 'a');
            stringBuilder.append(randomLetter);
            if(i!=6)
                stringBuilder.append(',');
        }
        String randomLetters = stringBuilder.toString();
        return randomLetters;
    }

}
