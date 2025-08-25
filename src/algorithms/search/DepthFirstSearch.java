package algorithms.search;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class DepthFirstSearch extends ASearchingAlgorithm {

    private int numberOfNodesEvaluated;
    public void setNumberOfNodesEvaluated(int num) {
        this.numberOfNodesEvaluated = num;
    }

    @Override
    public Solution solve(ISearchable maze) {
        int counter = 0;
        maze.resetVisited();
        HashMap<AState, AState> parentMap = new HashMap<>();
        Stack<AState> stack = new Stack<>();
        stack.push(maze.getStartState());

        while (!stack.isEmpty()) {
            AState currentState = stack.pop();
            counter++;
            if (currentState.equals(maze.getGoalState())) {
                ArrayList<AState> path  = new ArrayList<>();
                for (AState state = currentState; state != null; state = parentMap.get(state)) {
                    path.add( 0,state);
                }
                setNumberOfNodesEvaluated(counter);
                return new Solution(path);
            }
            for (AState neighbor : maze.getAllPossibleStates(currentState)) {
                if (!parentMap.containsKey(neighbor)) {
                    stack.push(neighbor);
                    parentMap.put(neighbor, currentState);
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "DepthFirstSearch";
    }

    @Override
    public int getNumberOfNodesEvaluated() {
        return numberOfNodesEvaluated;
    }
}
