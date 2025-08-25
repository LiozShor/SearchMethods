package algorithms.search;
import algorithms.mazeGenerators.Maze;

import java.util.ArrayList;

public interface ISearchable {
    public AState getStartState();
    public AState getGoalState();
    public ArrayList<AState> getAllPossibleStates(AState state) ;
    public AState setStartState(AState startState);
    public AState setGoalState(AState goalState);

    public void resetVisited();

    Maze getMaze();

//    public Maze getMaze();
}
