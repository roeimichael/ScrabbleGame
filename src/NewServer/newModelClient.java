package NewServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

public class newModelClient extends Observable {
    private String ip;
    private int port;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private int id;
    private String letterList;
    private boolean isHost = false;
    private static miniGameManager mgm;

    public newModelClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public void getPlayerID() throws IOException {
        // messages: server -> client -> PlayerHandler

        // first step: when the client connects to the server, the server sends him his id
        this.id = Integer.parseInt(in.readLine());
        System.out.println("Connected to server, client " + id);
        isHost = id == 0;
        // second step: sending it back to the playerHandler
        out.println(id);
    }

    public void connectToServer() {
        try {
            System.out.println("Connecting to server...");
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // first step: when the client connects to the server, the server sends him his id
            getPlayerID();
            // getting a message from the server and printing it

            String msgFromServer = null;
            //System.out.println("[Server]: " + msgFromServer); // "you are the host" or "you are a guest"

            System.out.println("Game Started");
            letterList = in.readLine();
            System.out.println("Client " + this.id + ": " + letterList);

            System.out.println("Game Started");
            Thread t = new Thread(()->{ // listen to the server, and acts accordingly
               while(true)
               {
                   try {
                       String msg = in.readLine();
                       switch (msg) {
                           case protocols.START_GAME:
                               this.setGameStarted();
                               break;
                           case protocols.GET_BOARD:
                                  out.println(mgm.getBoard());
                                  break;
                           case protocols.GET_HAND:
                                  out.println();
                                  break;

                       }

                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
               }
            });
            t.start();

            int turn;
            // while loop until we get the exit message
//            while (!msgFromServer.equals("bye")) {
//                turn = Integer.parseInt(in.readLine());
//                System.out.println("Turn: " + turn);
//                if (turn == id) {
//                    System.out.println("Your turn");
//                } else {
//                    System.out.println("Waiting for other players");
//                }
//                // get the updated board
//                String board = in.readLine();
//                for (int i = 0; i < 9; i++) {
//                    System.out.print(board.charAt(i));
//                    if (i % 3 == 2) {
//                        System.out.println();
//                    }
//                }
//
//                // getting a message from the user and sending it to the server
//                System.out.println("Choose your move:\n 1.confirm\n 2.exit\n 3.challenge\n 4.query\n 5.getHand\n 6.addWord\n 7.getBoard ");
//                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                String msgFromUser = br.readLine();
//                msgFromUser = translateMsgToProtocol(msgFromUser);
//                out.println(msgFromUser);
//
//                if ((msgFromServer = in.readLine()) != null) {
//                    System.out.println("Server: " + msgFromServer);
//                    chooseAction(msgFromServer);
////                    switch (msgFromServer) {
////                        case (protocols.CONFIRM) -> //confirm
////                        {
////                            System.out.println("confirm");
////                        }
////                        case (protocols.EXIT) -> { // exit
////                            out.println(id); // exising client sends his id to the server
////                            System.out.println("Client "+id+" is exiting");
////                            return;
////                        }
////                        case (protocols.CHALLENGE) -> { // challenge
////                            System.out.println("challenge");
////                        }
////                        case (protocols.QUERY) -> { // query
////                            System.out.println("query");
////                        }
////                        case (protocols.GET_HAND) -> { // getHand
////                            System.out.println("getHand");
////                            letterList=in.readLine();
////                            System.out.println("Client "+this.id+": "+letterList);
////                        }
////                        case (protocols.ADD_WORD) -> {
////                            System.out.println("addWord");
////                            String msg=in.readLine();
////                            if(msg.equals("not your turn"))
////                            {
////                                System.out.println("not your turn");
////                                break;
////                            }
////                            System.out.println(msg);//Choose a letter to add
////                            System.out.println(letterList);
////                            String letter=br.readLine();
////                            while(!letterList.contains(letter))
////                            {
////                                System.out.println("You don't have this letter, choose again");
////                                System.out.println(letterList);
////                                letter=br.readLine();
////
////                            }
////                            out.println(letter);
////                            System.out.println(in.readLine());//Choose a position to add
////                            int position=Integer.parseInt(br.readLine());
////                            out.println(position);
////
////
////                        }
////                        case(protocols.GET_BOARD) -> {
////                            board=in.readLine();
////                            for(int i=0;i<9;i++){
////                                System.out.print(board.charAt(i));
////                                if(i%3==2){
////                                    System.out.println();
////                                }
////                            }
////                        }
////                        default -> {
////                            msgFromServer="wait";
////                            System.out.println("Server is not running");
////                        }
////                    }
//                }
//            }
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Server is not running");

        }


    }

    public String translateMsgToProtocol(String msgFromUser) {
        switch (msgFromUser) {
            case ("1") -> //confirm
            {
                return protocols.CONFIRM;
            }
            case ("2") -> { // exit
                return protocols.EXIT;
            }
            case ("3") -> { // challenge
                return protocols.CHALLENGE;
            }
            case ("4") -> { // query
                return protocols.QUERY;
            }
            case ("5") -> { // getHand
                return protocols.GET_HAND;
            }
            case ("6") -> { // addWord
                return protocols.ADD_WORD;
            }
            case ("7") -> { // getBoard
                return protocols.GET_BOARD;
            }
        }
        return protocols.EXIT;
    }

    public void setGameStarted() {

        mgm=miniGameManager.get();
        mgm.startGame();
        setChanged();
        notifyObservers(protocols.START_GAME);

    }



    public String getMessageFromServer() {
        try {
            String msg = in.readLine();
            return msg;
        } catch (IOException e) {
            return "problem";
        }
    }

    public void chooseAction(String msgFromServer) throws IOException {
        System.out.println("Server: " + msgFromServer);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        switch (msgFromServer) {
            case (protocols.CONFIRM) -> //confirm
            {
                System.out.println("confirm");
            }
            case (protocols.EXIT) -> { // exit
                out.println(id); // exising client sends his id to the server
                System.out.println("Client " + id + " is exiting");
                return;
            }
            case (protocols.CHALLENGE) -> { // challenge
                System.out.println("challenge");
            }
            case (protocols.QUERY) -> { // query
                System.out.println("query");
            }
            case (protocols.GET_HAND) -> { // getHand
                System.out.println("getHand");
                letterList = in.readLine();
                System.out.println("Client " + this.id + ": " + letterList);
            }
            case (protocols.ADD_WORD) -> {
                System.out.println("addWord");
                String msg = in.readLine();
                if (msg.equals("not your turn")) {
                    System.out.println("not your turn");
                    break;
                }
                System.out.println(msg);//Choose a letter to add
                System.out.println(letterList);
                String letter = br.readLine();
                while (!letterList.contains(letter)) {
                    System.out.println("You don't have this letter, choose again");
                    System.out.println(letterList);
                    letter = br.readLine();

                }
                out.println(letter);
                System.out.println(in.readLine());//Choose a position to add
                int position = Integer.parseInt(br.readLine());
                out.println(position);


            }
            case (protocols.GET_BOARD) -> {
                String board = in.readLine();
                for (int i = 0; i < 9; i++) {
                    System.out.print(board.charAt(i));
                    if (i % 3 == 2) {
                        System.out.println();
                    }
                }
            }
            default -> {
                msgFromServer = "wait";
                System.out.println("Server is not running");
            }
        }


    }
}

