package algorithms.mazeGenerators;

public class EmptyMazeGenerator extends AMazeGenerator{
    public Maze generate(int rows, int columns) {
        int[][] maze = new int[rows][columns];
        return new Maze(maze, new Position(0, 0), new Position(rows - 1, columns - 1));
    }
}
