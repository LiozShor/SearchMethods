package algorithms.mazeGenerators;

import java.util.Random;

public abstract class AMazeGenerator implements IMazeGenerator {

    public long measureAlgorithmTimeMillis(int rows, int columns) {
        long startTime = System.currentTimeMillis();
        this.generate(rows, columns);
        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

    public int[] generateStartEnd(int rows, int columns) {
        int[] coordinate = new int[2];
        Random rand = new Random();
        int edge = rand.nextInt(4);
        switch (edge) {
            case 0 -> // Top edge
                    coordinate[1] = rand.nextInt(columns);
            case 1 -> { // Right edge
                coordinate[0] = rand.nextInt(rows);
                coordinate[1] = columns - 1;
            }
            case 2 -> { // Bottom edge
                coordinate[0] = rows - 1;
                coordinate[1] = rand.nextInt(columns);
            }
            case 3 -> // Left edge
                    coordinate[0] = rand.nextInt(rows);
        }
        return coordinate;
    }

}
