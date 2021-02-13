

public class Coordinate {
  private final String column;
  private final int row;

  public Coordinate(String column, int row) {
    this.column = column;
    this.row = row;
  }

  public String getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }
}
