public class Pawn extends Piece {
  public boolean firstMove = true;

  public Pawn(Color color) {
    super(color, positionX, positionY, movementRules);
  }
}
