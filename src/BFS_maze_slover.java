import java.util.*;
    import algorithms.mazeGenerators.Maze;
    import algorithms.mazeGenerators.Position;

    public class BFS_maze_slover {
        private static int nodesOpened = 0; // Static variable to track the number of nodes opened

        public static List<Position> solveMazeBFS(Maze maze) {
            int[][] mazeArray = maze.getMaze();
            Position start = maze.getStartPosition();
            Position end = maze.getGoalPosition();
            int rows = mazeArray.length;
            int cols = mazeArray[0].length;

            // Reset the nodesOpened counter for each new maze
            nodesOpened = 0;

            // Use boolean array to track visited positions
            boolean[][] visited = new boolean[rows][cols];
            Queue<Position> queue = new LinkedList<>();

            queue.add(start);
            visited[start.getRow()][start.getColumn()] = true;

            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            while (!queue.isEmpty()) {
                Position current = queue.poll();
                nodesOpened++;

                if (current.getRow() == end.getRow() && current.getColumn() == end.getColumn()) {
                    System.out.println("Number of nodes opened: " + nodesOpened);
                    return Collections.emptyList(); // Return an empty list since the solution path is not needed
                }

                for (int[] direction : directions) {
                    int newRow = current.getRow() + direction[0];
                    int newCol = current.getColumn() + direction[1];

                    if (isValidMove(newRow, newCol, rows, cols, mazeArray, visited)) {
                        Position neighbor = new Position(newRow, newCol);
                        queue.add(neighbor);
                        visited[newRow][newCol] = true;
                    }
                }
            }

            System.out.println("Number of nodes opened: " + nodesOpened);
            System.out.println("No solution found for the maze.");
            return Collections.emptyList();
        }

        public static int getNodesOpened() {
            return nodesOpened; // Return the number of nodes opened
        }

        private static boolean isValidMove(int row, int col, int rows, int cols, int[][] maze, boolean[][] visited) {
            return row >= 0 && row < rows && col >= 0 && col < cols &&
                    maze[row][col] == 0 && !visited[row][col];
        }
    }