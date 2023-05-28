package server;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.protocols;
public class Client extends Application {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    private Stage primaryStage;
    private Scene menuScene;
    private Scene gameScene;
    private TextField wordField;
    private Server server; // New server instance for hosting

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        createMenu();
        createGameScreen();

        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Game Menu");
        primaryStage.show();
    }

    private void createMenu() {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        Button hostButton = new Button("Start Game as Host");
        Button joinButton = new Button("Join Game");

        hostButton.setOnAction(e -> {
            startServer();
            sendMessage(protocols.NEW_GAME_AS_HOST);
            System.out.println("Host button pressed");
            primaryStage.setScene(gameScene);
        });

        joinButton.setOnAction(e -> {
            connectToServer();
            sendMessage(protocols.JOIN_GAME_AS_CLIENT);
            primaryStage.setScene(gameScene);
        });

        root.getChildren().addAll(hostButton, joinButton);

        menuScene = new Scene(root, 200, 150);
    }

    private void createGameScreen() {
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        wordField = new TextField();
        Button confirmButton = new Button("Confirm");
        Button challengeButton = new Button("Challenge");

        confirmButton.setOnAction(e -> {
            String word = wordField.getText();
            sendMessage(word);
        });

        challengeButton.setOnAction(e -> sendMessage(protocols.CHALLENGE));

        root.getChildren().addAll(wordField, confirmButton, challengeButton);

        gameScene = new Scene(root, 200, 150);
    }
    public void startServer()
    {
        if (Server.isGameInProgress) {
            // Show error message
            System.out.println("A game is already in progress. Cannot start a new game as host.");
            // Return to the main menu
            primaryStage.setScene(menuScene);
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
            InputHandler inHandler = new InputHandler();
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
