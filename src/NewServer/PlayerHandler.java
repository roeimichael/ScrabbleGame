package NewServer;
import test.ClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerHandler implements ClientHandler{
    //private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean exit=false;
    private int PlayerId;
    private static AtomicInteger connectedClients = new AtomicInteger(0);
    private static AtomicInteger turn = new AtomicInteger(0);
    private static AtomicBoolean startGame = new AtomicBoolean(false);

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {

        in=new BufferedReader(new InputStreamReader(inFromclient));
        out=new PrintWriter(outToClient,true);
        miniGameManager mgm = miniGameManager.get();
        //mgm.addObserver(this);
        int totalConnectedClients = connectedClients.incrementAndGet();
        System.out.println("Total connected clients: " + totalConnectedClients);
        // first the server sends the new player his id
        String msgFromPlayer= null;
        try {
            msgFromPlayer=in.readLine();
            PlayerId= Integer.parseInt(msgFromPlayer);
            System.out.println("client "+msgFromPlayer);
            mgm.setPlayerLetters(PlayerId,""); // a new player is connected, first we give him an empty string until game starts

            // start game method
            // host needs to press a button to start the game
            // guest needs to wait for the host to start the game
            if(PlayerId==0) // host
            {
                out.println("you are the host");
//                while(!startGame){
                out.println("Press anything to start the game");
                msgFromPlayer = in.readLine();
                if (msgFromPlayer.equals(protocols.HOST_START_GAME)) {
                    startGame.set(true);
                    out.println(protocols.HOST_START_GAME);
                }

                miniGameManager.get().startGame();
            }
            else
            {
                //guest
                out.println("you are a guest");
                out.println("Waiting for the host to start the game");
                do {
                    mgm = miniGameManager.get();
                } while (!startGame.get());
//                out.println("game started");
                // delay of 1 second to make sure the host has already started the game
                Thread.sleep(1000);
            }
            mgm=miniGameManager.get();
//            mgm.setPlayerLetters(PlayerId,randomLetters()); // a new player is connected, so we give him 7 random letters
            out.println(mgm.getPlayerLetters(PlayerId));
            System.out.println("client "+PlayerId+" has letters: "+mgm.getPlayerLetters(PlayerId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // game loop
        // server runs infinitely
        // server waits for a message from the client
        while(!exit)
        {
            // first send the turn to the client
            out.println(turn.get());
            System.out.println("turn: "+turn.get());
            while(turn.get()!=PlayerId) {
            }
            // then send the board to the client
            mgm=miniGameManager.get();
            out.println(mgm.getBoard());
            System.out.println("getBoard");

            try {
                msgFromPlayer= in.readLine();
            } catch (IOException e) {
                msgFromPlayer="wait";
            }

            switch (msgFromPlayer) {
                case (protocols.CONFIRM) -> //confirm
                {
                    mgm=miniGameManager.get();
                    mgm.addLetter(0,"a");
                    out.println(protocols.CONFIRM);
                    System.out.println("confirm");
                }
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
                case (protocols.GET_HAND) -> { // getHand
                    out.println(protocols.GET_HAND);
                    //mgm.setPlayerLetters(clientID, letters);
                    out.println(miniGameManager.get().getPlayerLetters(PlayerId));
                    System.out.println("getHand");
                }
                case (protocols.ADD_WORD) -> {
                    mgm=miniGameManager.get();
                    turn.set(mgm.getTurn());
                    out.println(protocols.ADD_WORD);
                    if(turn.get()!=PlayerId)
                    {
                        out.println("not your turn");
                        break;
                    }
                    out.println("Choose a letter to add: ");
                    String letter= null;
                    try {
                        letter = in.readLine();
                        out.println("Choose a position to add: ");  // 0-8
                        int position=Integer.parseInt(in.readLine());
                        mgm.addLetter(position,letter);
                        mgm.changeTurn();
                        turn.set(mgm.getTurn());

                        mgm.printBoard();// print board in the server
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("addWord");
                }
                case (protocols.GET_BOARD) -> { // getBoard
                    out.println(protocols.GET_BOARD);
                    mgm=miniGameManager.get();
                    out.println(mgm.getBoard());
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
