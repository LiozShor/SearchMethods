import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.*;

/**
 * IDDFS for your Maze API (getMaze(), getStartPosition(), getGoalPosition()).
 * Prints the number of nodes developed during the search.
 */
public class IDDFS_maze_solver {

    private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};
    private static int nodesDeveloped = 0; // Counter for nodes developed

    public static void solveMazeIDDFS(Maze maze) {
        nodesDeveloped = 0; // Reset the counter for each search
        Position start = maze.getStartPosition();
        Position goal  = maze.getGoalPosition();

        // sanity: start/goal must be free
        if (!isFree(maze, start.getRow(), start.getColumn()) ||
                !isFree(maze, goal.getRow(), goal.getColumn())) {
            System.out.println("Number of nodes developed: " + nodesDeveloped);
            System.out.println("No solution found for the maze.");
            return;
        }

        int maxDepth = rows(maze) * cols(maze); // safe upper bound

        for (int limit = 0; limit <= maxDepth; limit++) {
            Set<Long> onPath = new HashSet<>(); // only current recursion stack

            if (dls(maze, start, goal, limit, onPath)) {
                System.out.println("Number of nodes developed: " + nodesDeveloped);
                System.out.println("Solution found!");
                return;
            }
        }
        System.out.println("Number of nodes developed: " + nodesDeveloped);
        System.out.println("No solution found for the maze.");
    }

    public static int getNodesDeveloped() {
        return nodesDeveloped; // Return the number of nodes developed
    }

    private static boolean dls(Maze maze,
                               Position cur,
                               Position goal,
                               int limit,
                               Set<Long> onPath) {

        nodesDeveloped++; // Increment the counter for each node processed

        if (cur.getRow() == goal.getRow() && cur.getColumn() == goal.getColumn()) {
            return true;
        }
        if (limit == 0) return false;

        long id = key(cur, maze);
        onPath.add(id);

        for (Position nb : neighbors(maze, cur)) {
            long nid = key(nb, maze);
            if (onPath.contains(nid)) continue; // avoid cycles along current path

            if (dls(maze, nb, goal, limit - 1, onPath)) {
                return true;
            }
        }

        onPath.remove(id);
        return false;
    }

    private static List<Position> neighbors(Maze maze, Position p) {
        List<Position> res = new ArrayList<>(4);
        int r = p.getRow(), c = p.getColumn();
        for (int[] d : DIRS) {
            int nr = r + d[0], nc = c + d[1];
            if (inBounds(maze, nr, nc) && isFree(maze, nr, nc)) {
                res.add(new Position(nr, nc));
            }
        }
        return res;
    }

    private static boolean inBounds(Maze maze, int r, int c) {
        return r >= 0 && r < rows(maze) && c >= 0 && c < cols(maze);
    }

    private static long key(Position p, Maze maze) {
        return (long) p.getRow() * cols(maze) + p.getColumn();
    }

    private static int rows(Maze maze) { return maze.getMaze().length; }
    private static int cols(Maze maze) { return maze.getMaze()[0].length; }
    private static boolean isFree(Maze maze, int r, int c) { return maze.getMaze()[r][c] == 0; }
}