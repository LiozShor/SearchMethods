package algorithms.search;
import java.util.ArrayList;

public class Solution {
    private ArrayList<AState> solutionPath;

    public Solution(ArrayList<AState> queue) {
        this.solutionPath = queue;
    }

    public ArrayList<AState> getSolutionPath() {
        return solutionPath;
    }

    public String toString() {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (AState state : solutionPath) {
            sb.append(i).append(". ").append(state.toString()).append("\n");
            i++;
        }
        return sb.toString();
    }

//    public void printMazeWithSolution() {
//        int[][] mazeArray = maze.getMaze();
//        for (int i = 0; i < mazeArray.length; i++) {
//            for (int j = 0; j < mazeArray[i].length; j++) {
//                if (i == maze.getStartPosition().getRow() && j == maze.getStartPosition().getColumn()) {
//                    System.out.print("\033[32mS\033[0m "); // Green
//                } else if (i == maze.getGoalPosition().getRow() && j == maze.getGoalPosition().getColumn()) {
//                    System.out.print("\033[31mE\033[0m "); // Red
//                } else if (isInSolutionPath(i, j)) {
//                    System.out.print("\033[34m*\033[0m "); // Blue
//                } else {
//                    System.out.print(mazeArray[i][j] + " ");
//                }
//            }
//            System.out.println();
//
//        }
//    }

    private boolean isInSolutionPath(int i, int j) {
        for (AState state : solutionPath) {
            if (state.getRow() == i && state.getCol() == j) {
                return true;
            }
        }
        return false;
    }
}
