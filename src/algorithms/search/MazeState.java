package algorithms.search;

import algorithms.mazeGenerators.Position;

public class MazeState extends AState {
    private int row;
    private int col;
    private int cost;
    private MazeState parent;

    public MazeState(Position pos, MazeState parent) {
        super(pos.getRow(), pos.getColumn(),  parent);
        this.row = pos.getRow();
        this.col = pos.getColumn();
    }

    public MazeState(int row, int col, MazeState state,int cost) {
        super(row, col, state);
        this.row = row;
        this.col = col;
        this.cost = cost;

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public MazeState getParent() {
        return parent;
    }

    @Override
    public AState getState() {
        return this;
    }

    @Override
    public String toString() {
        return "{" + row + "," + col + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MazeState) {
            MazeState other = (MazeState) obj;
            return row == other.row && col == other.col;
        }
        return false;
    }

    public void setParent(MazeState parent) {
        this.parent = parent;
    }

    public Position getPosition() {
        return new Position(row, col);
    }

    @Override
    public int getCost() {
        return 0;
    }
}
