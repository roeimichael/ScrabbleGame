package server;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    public String index;
    public String connectToServer() {
        try {
            System.out.println("Connecting to server...");
            client = new Socket("127.0.0.1", 9999);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            InputHandler inHandler = new InputHandler();
            index = in.readLine();
            System.out.println(index);

            Thread t = new Thread(inHandler);
            t.start();
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Server is not running");
        }
        return index;
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
