public class Player {
  private final PieceColor pieceColor;
  private Coordinate kingPosition;
  private boolean inCheck = false;

  public Player(PieceColor pieceColor) {
    this.pieceColor = pieceColor;
    this.kingPosition = new Coordinate("e", pieceColor.getStartRow());
  }

  public PieceColor getColor() {
    return pieceColor;
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
