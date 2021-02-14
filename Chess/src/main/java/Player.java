public class Player {
  private final Color color;
  private Coordinate kingPosition;
  private boolean inCheck = false;

  public Player(Color color) {
    this.color = color;
    this.kingPosition = new Coordinate("e", color.getStartRow());
  }

  public Color getColor() {
    return color;
  }

  public Coordinate getKingPosition() {
    return kingPosition;
  }

  public boolean isInCheck() {
    return inCheck;
  }

  public Player setKingPosition(Coordinate kingPosition) {
    this.kingPosition = kingPosition;
    return this;
  }

  public Player setInCheck(boolean inCheck) {
    this.inCheck = inCheck;
    return this;
  }
}
