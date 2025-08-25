package algorithms.mazeGenerators;

import java.util.*;

public class MyMazeGenerator extends AMazeGenerator{


    public static boolean checkPosition(int[][] maze, int row, int col) {
        //counts the number of 0 around the cell
        int count = 0;
        if (row - 1 >= 0 && maze[row - 1][col] == 0) {
            count++;
        }
        if (row + 1 < maze.length && maze[row + 1][col] == 0) {
            count++;
        }
        if (col - 1 >= 0 && maze[row][col - 1] == 0) {
            count++;
        }
        if (col + 1 < maze[0].length && maze[row][col + 1] == 0) {
            count++;
        }
        return count == 1;

    }


    public Maze generate(int rows, int columns) {
        int[][] maze;
        List<int[]> potentialEnds = new ArrayList<>();
        maze = new int[rows][columns];
        boolean endPathExist = false;
        // Start with a grid full of walls
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                maze[i][j] = 1;
            }
        }
        // Create a list of visited cells
        boolean[][] visited = new boolean[rows][columns];

        Stack<int[]> stack = new Stack<>();

        // Choose the initial cell, mark it as visited and push it to the stack
        int[] start=generateStartEnd(rows,columns);
        maze[start[0]][start[1]] = 0;


        visited[start[0]][start[1]] = true;
        stack.push(start);

        // Directions for moving up, down, left, right and leftRight leftDown lightDown
         int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!stack.isEmpty()) {
            int[] cell = stack.pop();

            List<int[]> unvisitedNeighbours = new ArrayList<>();
            for (int[] direction : directions) {
                int newRow = cell[0] + direction[0];
                int newCol = cell[1] + direction[1];

                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < columns && !visited[newRow][newCol]) {
                    unvisitedNeighbours.add(new int[]{newRow, newCol});
                }
            }

            if (!unvisitedNeighbours.isEmpty()) {
                Collections.shuffle(unvisitedNeighbours, new Random());
                // Choose one of the unvisited neighbours
                    int[] neighbour = unvisitedNeighbours.get(0);
                    stack.push(neighbour);
                    int[] brokenWall = new int[]{(cell[0] + neighbour[0]) / 2, (neighbour[1] + cell[1]) / 2};
                    stack.push(brokenWall);
                    // Remove the wall between the current cell and the chosen cell
                    if (checkPosition(maze, neighbour[0], neighbour[1])) {
                        maze[neighbour[0]][neighbour[1]] = 0;
                        if ((neighbour[0] == 0 || neighbour[0] == rows - 1 || neighbour[1] == 0 || neighbour[1] == columns - 1)&& (neighbour[0] != start[0] || neighbour[1] != start[1])) {
                            potentialEnds.add(neighbour);
                        }
                }



                    // Mark the chosen cell as visited and push it to the stack
                    visited[neighbour[0]][neighbour[1]] = true;
                }
        }
        int[] end = potentialEnds.get(new Random().nextInt(potentialEnds.size()));

        return new Maze(maze, new Position(start[0], start[1]), new Position(end[0], end[1]));
    }

}
