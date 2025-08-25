package algorithms.search;
import java.util.*;

public class BreadthFirstSearch extends ASearchingAlgorithm {
    public static Queue<AState> queue = new LinkedList<AState>();


    private int numberOfNodesEvaluated=0;

    @Override
    public Solution solve(ISearchable problem) {
        problem.resetVisited();
        int counter=0;
        HashMap<AState, AState> parentMap = new HashMap<>();
        queue.add(problem.getStartState());
        while (!queue.isEmpty()) {
            AState currentState = queue.poll();
            counter++;
            if (currentState.equals(problem.getGoalState())) {
                ArrayList<AState> path = new ArrayList<>();
                for (AState state = currentState; state != null; state = parentMap.get(state)) {
                    path.add(0, state);
                }
                setNumberOfNodesEvaluated(counter);
                return new Solution(path);
            }
            for (AState neighbor : problem.getAllPossibleStates(currentState)) {
                if (!parentMap.containsKey(neighbor)) {
                    queue.add(neighbor);
                    parentMap.put(neighbor, currentState);
                }
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "BreadthFirstSearch";
    }

    @Override
    public int getNumberOfNodesEvaluated() {
        return numberOfNodesEvaluated;
    }

    public void setNumberOfNodesEvaluated(int num) {
        this.numberOfNodesEvaluated = num;
    }
}
