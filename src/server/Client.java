package server;

import NewServer.protocols;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private Socket client;
    private BufferedReader in;
    private static Client instance;
    private PrintWriter out;
    public String index;
    public String connectToServer() {
        try {
            System.out.println("Connecting to server...");
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//            InputHandler inHandler = new InputHandler();
//            Thread t = new Thread(inHandler);
//            t.start();
            index = in.readLine();
            System.out.println(index);
            out.println(protocols.REFILL_HAND);



            String msg= in.readLine();
            while(!msg.equals(protocols.EXIT))
            {
                //msg= in.readLine();
                msg=protocols.EXIT;
            }
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Server is not running");
        }
        return index;
    }

    public static Client getClientInstance(){
        if(instance == null)
            instance = new Client();
        return instance;
    }
    public void sendMessage(String message) {
        out.println(message);
    }
    public ArrayList<String> getClientHand(){
        out.println(protocols.GET_HAND);
        ArrayList<String> hand=new ArrayList<>();
        //TODO: make sure that we will get in the following format: A,B,C,D,E,F,G
        try {
            String[] letters=in.readLine().split(",");
            for(String letter:letters)
                hand.add(letter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return hand;
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

    public String getIndex() {
        return index;
    }

    class InputHandler implements Runnable {
        public void run() {
            try {
                String message= in.readLine();
                while (message  != null) {
                    System.out.println(message);
                    message= in.readLine();
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

}
