public enum Color {
  WHITE(1, 2),
  BLACK(8, 7);

  private final int startRow;
  private final int pawnRow;

  Color(int startRow, int pawnRow) {
    this.startRow = startRow;
    this.pawnRow = pawnRow;
  }

  public int getStartRow() {
    return startRow;
  }

  public int getPawnRow() {
    return pawnRow;
  }
}
