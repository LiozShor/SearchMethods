package algorithms.search;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.ArrayList;

public class SearchableMaze implements ISearchable{
    private int[][] maze;
    private boolean[][] visited;
    private int rows, columns;
    private MazeState startState, goalState;

    private Solution solution;

    public SearchableMaze(Maze maze) {
        this.maze = maze.getMaze();
        this.rows = maze.getMaze().length;
        this.columns = maze.getMaze()[0].length;
        this.startState = new MazeState(maze.getStartPosition(), null);
        this.goalState = new MazeState(maze.getGoalPosition(), null);
        this.visited = new boolean[rows][columns];
    }

    @Override
    public MazeState getStartState() {
        return startState;
    }

    public void resetVisited() {
        visited = new boolean[rows][columns];
    }

    @Override
    public AState getGoalState() {
        return goalState;
    }


    @Override
    public ArrayList<AState> getAllPossibleStates(AState state) {
        int[][] directions = {{-1,0},{0,1},{1,0},{0,-1}};
        int[][] diagonalDirections = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
        ArrayList<AState> possibleStates = new ArrayList<>();

        for (int i=0; i<4; i++) {
            int newRow = state.getRow() + directions[i][0];
            int newCol = state.getCol() + directions[i][1];
            if (isValidMove(newRow, newCol)) {
                visited[newRow][newCol] = true;
                possibleStates.add(new MazeState(newRow, newCol, (MazeState)state,10));

                int newRowDiagonal = state.getRow() + diagonalDirections[i][0];
                int newColDiagonal = state.getCol() + diagonalDirections[i][1];

                if (isValidMove(newRowDiagonal, newColDiagonal)) {
                    visited[newRowDiagonal][newColDiagonal] = true;
                    possibleStates.add(new MazeState(newRowDiagonal, newColDiagonal, (MazeState)state,15));
                }
            }
        }
        return possibleStates;
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns && !visited[row][col] && maze[row][col] == 0;
    }

    @Override
    public MazeState setStartState(AState startState) { return this.startState;


    }

    @Override
    public MazeState setGoalState(AState goalState) { return this.goalState;

    }

    @Override
    public Maze getMaze() {
        return new Maze(maze, startState.getPosition(), goalState.getPosition());
    }
}
