import java.util.Set;

public abstract class Piece {
  final PieceColor pieceColor;

  public Piece(PieceColor pieceColor) {
    this.pieceColor = pieceColor;
  }

  abstract Set<MovementOption> getMovementOptions();

  public PieceColor getColor() {
    return pieceColor;
  }
}
