package test;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Client.*;
import Client.IClientStrategy;
import Server.*;
import algorithms.mazeGenerators.*;
import algorithms.search.AState;
import algorithms.search.Solution;
import IO.MyDecompressorInputStream;

public class RunCommunicateWithServers {
    public static void main(String[] args) {
        // Initializing servers
        Server mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        Server solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        // Server stringReverserServer = new Server(5402, 1000, 10, new ServerStrategyStringReverser());

        // Starting servers
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
        // stringReverserServer.start();

        // Communicating with servers
        CommunicateWithServer_MazeGenerating();
        CommunicateWithServer_SolveSearchProblem();
        // CommunicateWithServer_StringReverser();

        // Stopping all servers
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
        // stringReverserServer.stop();
    }

    private static void CommunicateWithServer_MazeGenerating() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{50, 50};
                        toServer.writeObject(mazeDimensions); // send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); // read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000]; // allocate byte[] for the decompressed maze
                        is.read(decompressedMaze); // Fill decompressedMaze with bytes
                        Maze maze = new Maze(decompressedMaze);
                        maze.print();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void applyStrategy(InputStream inFromServer, OutputStream outToServer) {
                    return;
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        MyMazeGenerator mg = new MyMazeGenerator();
                        Maze maze = mg.generate(50, 50);
                        maze.print();
                        toServer.writeObject(maze); // send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); // read solution from server
                        System.out.println("Solution steps:");
                        for (AState step : mazeSolution.getSolutionPath()) {
                            System.out.println(step);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void applyStrategy(InputStream inFromServer, OutputStream outToServer) {

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void CommunicateWithServer_StringReverser() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5402, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        BufferedReader fromServer = new BufferedReader(new InputStreamReader(inFromServer));
                        PrintWriter toServer = new PrintWriter(outToServer);
                        String message = "Client Message";
                        toServer.write(message + "\n");
                        toServer.flush();
                        String serverResponse = fromServer.readLine();
                        System.out.println("Server response: " + serverResponse);
                        fromServer.close();
                        toServer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void applyStrategy(InputStream inFromServer, OutputStream outToServer) {

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
