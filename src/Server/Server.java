package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int port;
    private int listeningIntervalMS;
    private boolean stop;
    private ExecutorService threadPool;
    private IServerStrategy serverStrategy;

    public Server(int port, int listeningIntervalMS, IServerStrategy serverStrategy) {
        this.port = port;
        this.listeningIntervalMS = listeningIntervalMS;
        this.serverStrategy = serverStrategy;
        this.threadPool = Executors.newFixedThreadPool(5);
    }

    public void start() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (!stop) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        threadPool.submit(() -> {
                            try {
                                serverStrategy.handleClient(clientSocket.getInputStream(), clientSocket.getOutputStream());
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(listeningIntervalMS);
                }
                serverSocket.close();
                threadPool.shutdown();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        stop = true;
    }


        public static void main(String[] args) {
            Server serverMaze = new Server(5400, 1000,  new ServerStrategyGenerateMaze());
            Server serverSolve = new Server(5401, 1000,  new ServerStrategySolveSearchProblem());

            serverMaze.start();
            serverSolve.start();
        }

}
