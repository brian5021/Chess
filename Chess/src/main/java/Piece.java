import java.util.Set;

public abstract class Piece {
  final PieceColor pieceColor;
  boolean hasMoved = false;

  public Piece(PieceColor pieceColor) {
    this.pieceColor = pieceColor;
  }

  abstract Set<MovementOption> getMovementOptions();

  public PieceColor getColor() {
    return pieceColor;
  }

  public boolean getHasMoved() {
    return hasMoved;
  }

  public Piece setHasMoved(boolean hasMoved) {
    this.hasMoved = hasMoved;
    return this;
  }
}
