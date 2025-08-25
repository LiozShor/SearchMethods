package Server;

import algorithms.mazeGenerators.*;
import IO.MyCompressorOutputStream;

import java.io.*;

public class ServerStrategyGenerateMaze implements IServerStrategy {
    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);

            int[] mazeDimensions = (int[]) fromClient.readObject();
            int rows = mazeDimensions[0];
            int cols = mazeDimensions[1];
            AMazeGenerator mazeGenerator = new MyMazeGenerator();
            Maze maze = mazeGenerator.generate(rows, cols);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MyCompressorOutputStream compressorOutputStream = new MyCompressorOutputStream(byteArrayOutputStream);
            compressorOutputStream.write(maze.toByteArray());
            compressorOutputStream.flush();
            byte[] compressedMaze = byteArrayOutputStream.toByteArray();
            toClient.writeObject(compressedMaze);
            fromClient.close();
            toClient.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
