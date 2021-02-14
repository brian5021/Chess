import java.util.Objects;

public class Coordinate {
  private final String column;
  private final int row;

  public Coordinate(String column, int row) {
    this.column = column;
    this.row = row;
  }

  public static Coordinate from(String column, int row) {
    return new Coordinate(column, row);
  }

  public String getColumn() {
    return column;
  }

  public int getRow() {
    return row;
  }

  @Override
  public String toString() {
    return getColumn() + getRow();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Coordinate) {
      final Coordinate that = (Coordinate) obj;
      return Objects.equals(this.row, that.row) &&
          Objects.equals(this.column, that.column);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(column, row);
  }
}
