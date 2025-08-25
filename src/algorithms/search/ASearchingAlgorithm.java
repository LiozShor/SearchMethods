package algorithms.search;

public abstract class ASearchingAlgorithm implements ISearchingAlgorithm{
    protected int numberOfNodesEvaluated;

    public void setNumberOfNodesEvaluated(int num) {
        this.numberOfNodesEvaluated = num;
    }

    @Override
    public abstract Solution solve(ISearchable problem);


    @Override
    public abstract String getName();

    @Override
    public int getNumberOfNodesEvaluated() {
        return numberOfNodesEvaluated;
    }
}
