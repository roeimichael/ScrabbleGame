package NewServer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
public class miniGameWindow extends Application {

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Game Window");

            // Create grid pane for layout
            GridPane gridPane = new GridPane();
            gridPane.setPadding(new Insets(10));
            gridPane.setHgap(10);
            gridPane.setVgap(10);

            // Add labels and text fields for IP and port
            Label ipLabel = new Label("IP:");
            TextField ipTextField = new TextField("127.0.0.1");
            Label portLabel = new Label("Port:");
            TextField portTextField = new TextField("9999");

            gridPane.add(ipLabel, 0, 0);
            gridPane.add(ipTextField, 1, 0);
            gridPane.add(portLabel, 0, 1);
            gridPane.add(portTextField, 1, 1);

            // Add "Start Game as Host" button
            Button startHostButton = new Button("Start Game as Host");
            // start game
            startHostButton.setOnAction(event -> {
                String ip = ipTextField.getText();
                int port = Integer.parseInt(portTextField.getText());
                newServer server = new newServer(port, new PlayerHandler());
                server.start();
                runClient(ip, port);
                // Start game as host logic here
                System.out.println("Starting game as host with IP: " + ip + " and port: " + port);
            });

            // Add "Join Game as Guest" button
            Button joinGuestButton = new Button("Join Game as Guest");
            // join game
            joinGuestButton.setOnAction(event -> {
                String ip = ipTextField.getText();
                int port = Integer.parseInt(portTextField.getText());
                runClient(ip, port);
                // Join game as guest logic here
                System.out.println("Joining game as guest with IP: " + ip + " and port: " + port);
            });

            gridPane.add(startHostButton, 0, 2);
            gridPane.add(joinGuestButton, 1, 2);

            Scene scene = new Scene(gridPane, 300, 150);
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public void runClient(String ip, int port){
            newClient client = new newClient(ip,port);
            client.connectToServer();
        }
    }


