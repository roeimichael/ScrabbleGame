package server;

import server.ClientHandler;
import test.GameManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Server implements Runnable{
    protected static List<ConnectionHandler> connections= Collections.synchronizedList(new ArrayList<>()); // need to be static, use to store all the players
    private ServerSocket server;
    private static Server instance;
    private boolean done;
    private ExecutorService pool;
    public static boolean isGameInProgress = false;
    public static GameManager gameManager;

    private int turn=0;
    public Server()
    {
        connections = new ArrayList<>();
        done=false;
        gameManager = GameManager.getInstance();
    }

    public static synchronized Server getInstance()
    {
        if(instance==null)
            instance = new Server();
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void setGameStarted() {
        isGameInProgress = true;
    }
    public int getTurn()
    {
        return this.turn;
    }
    public boolean getIsGameStarted()
    {
        return isGameInProgress;
    }
    public List<ConnectionHandler> getConnections()
    {
        return connections;
    }
    public ConnectionHandler getPlayerById(int id)
    {
        return connections.get(id);
    }

    public void run()
    {
        try {
            setGameStarted();

            System.out.println("Server is running");
            server = new ServerSocket(9999);
            pool = Executors.newCachedThreadPool();
            while(!done)
            {
                System.out.println("Waiting for client to connect");
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client, connections.size());
                handler.setGameManager(gameManager);

                pool.execute(()-> {
                    try {
                        connections.add(handler);
                        handler.handleClient(client.getInputStream(),client.getOutputStream());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void broadcast(String mes)
    {
        for (ConnectionHandler ch: connections)
        {
            if(ch!=null)
                ch.sendMessage(mes);
        }
    }

    public void shutdown()
    {
        try
        {
            done=true;
            if(!server.isClosed())
            {
                server.close();
            }
            for (ConnectionHandler ch: connections)
            {
                ch.shutdown();
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }




}