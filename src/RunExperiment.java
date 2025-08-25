import algorithms.mazeGenerators.Maze;
            import algorithms.mazeGenerators.MyMazeGenerator;
            import algorithms.mazeGenerators.Position;

            import java.util.*;

            public class RunExperiment {

                private static final int RUNS = 10; // מספר הריצות לכל גודל מבוך
                private static final int[] SIZES = {10, 20, 50, 100}; // גדלי המבוכים

                public static void main(String[] args) {
                    System.out.println("Starting experiment: BFS vs IDDFS");

                    for (int size : SIZES) {
                        System.out.printf("Running experiments for maze size: %dx%d%n", size, size);

                        int totalBfsNodes = 0;
                        int totalIddfsNodes = 0;

                        for (int i = 0; i < RUNS; i++) {
                            long seed = System.currentTimeMillis() + i; // זרעים שונים לכל ריצה
                            Maze maze = new MyMazeGenerator().generate(size, size);

                            // הרצת BFS
                            System.out.printf("Run %d/%d (BFS)...%n", i + 1, RUNS);
                            BFS_maze_slover.solveMazeBFS(maze);
                            totalBfsNodes += BFS_maze_slover.getNodesOpened();

                            // הרצת IDDFS
                            System.out.printf("Run %d/%d (IDDFS)...%n", i + 1, RUNS);
                            IDDFS_maze_solver.solveMazeIDDFS(maze);
                            totalIddfsNodes += IDDFS_maze_solver.getNodesDeveloped();
                        }

                        // חישוב ממוצעים והשוואה
                        double avgBfsNodes = totalBfsNodes / (double) RUNS;
                        double avgIddfsNodes = totalIddfsNodes / (double) RUNS;

                        System.out.printf("Results for size %dx%d:%n", size, size);
                        System.out.printf("BFS - Average nodes opened: %.2f%n", avgBfsNodes);
                        System.out.printf("IDDFS - Average nodes developed: %.2f%n", avgIddfsNodes);
                        System.out.printf("Winner: %s%n", avgBfsNodes < avgIddfsNodes ? "BFS" : "IDDFS");
                        System.out.println("--------------------------------------------------");
                    }

                    System.out.println("Experiment completed.");
                }
            }