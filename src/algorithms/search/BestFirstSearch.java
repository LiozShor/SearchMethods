package algorithms.search;

import java.util.Comparator;
import java.util.PriorityQueue;

public class BestFirstSearch extends BreadthFirstSearch {

    public BestFirstSearch() {
        super();
        queue = new PriorityQueue<>(Comparator.comparingInt(AState::getCost));
    }
    @Override
    public String getName() {
        return "BestFirstSearch";
    }
}
