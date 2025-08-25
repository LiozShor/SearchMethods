package algorithms.search;

public abstract class AState {
    private int row, col;
    private AState cameFrom;

    public AState(int row,int col, AState cameFrom) {
        this.row = row;
        this.col = col;
        this.cameFrom = cameFrom;
    }

    public abstract int getCost();

    public abstract AState getState() ;


    public AState getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(AState cameFrom) {
        this.cameFrom = cameFrom;
    }

    @Override
    public String toString() {
        return "{" + row + "," + col + "}";

    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public boolean equals(AState state) {
        if (state == null) {
            return false;
        }
        return state.getRow() == this.getRow() && state.getCol() == this.getCol();
    }

}
