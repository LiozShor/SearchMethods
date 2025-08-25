package algorithms.mazeGenerators;
import java.util.Random;

public class SimpleMazeGenerator extends AMazeGenerator {


    public Maze generate(int rows, int columns) {
        int[] randStart = generateStartEnd(rows, columns);
        int[] randEnd;
        do {
            randEnd = generateStartEnd(rows, columns); // Ensure start and end are distinct
        } while (randStart[0] == randEnd[0] && randStart[1] == randEnd[1]);

        int[][] maze = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                maze[i][j] = 1;
            }
        }

        // Modify path in a straight line from start to end
        modifyPath(randStart[0], randStart[1], randEnd[0], randEnd[1], maze);
        addZeros(maze);


        return new Maze(maze, new Position(randStart[0], randStart[1]), new Position(randEnd[0], randEnd[1]));
    }

    public void modifyPath(int startRow, int startCol, int endRow, int endCol, int[][] matrix) {
        // Ensure start and end points are distinct
        if (startRow == endRow && startCol == endCol) {
            return;
        }

        int dx = endRow - startRow;
        int dy = endCol - startCol;

        // Determine step direction for x and y
        int stepX = dx > 0 ? 1 : -1;
        int stepY = dy > 0 ? 1 : -1;

        // Convert dx and dy to positive values
        dx = Math.abs(dx);
        dy = Math.abs(dy);

        // Start modifying the path in a straight line
        int x = startRow;
        int y = startCol;

        // Ensure the start point is set to 0
        matrix[x][y] = 0;

        if (dx > dy) {
            int error = dx / 2;
            for (int i = 0; i < dx; i++) {
                x += stepX;
                error -= dy;
                if (error < 0) {
                    y += stepY;
                    error += dx;
                }
                matrix[x][y] = 0;
            }
        } else {
            int error = dy / 2;
            for (int i = 0; i < dy; i++) {
                y += stepY;
                error -= dx;
                if (error < 0) {
                    x += stepX;
                    error += dy;
                }
                matrix[x][y] = 0;
            }
        }
    }

    public void addZeros(int[][] matrix) {
        Random rand = new Random();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 1 && rand.nextInt(10) < 3) {
                    matrix[i][j] = 0;
                }
            }
        }
    }
}
