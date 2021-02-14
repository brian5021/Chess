import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Pawn extends Piece {
  private static final MovementOption UP_ONE = new MovementOption(0, 1, false);
  private static final MovementOption UP_TWO = new MovementOption(0, 2, false);
  private static final MovementOption TAKE_RIGHT = new MovementOption(1, 1, false, true);
  private static final MovementOption TAKE_LEFT = new MovementOption(-1, 1, false, true);

  public Pawn(PieceColor pieceColor) {
    super(pieceColor);
  }

  @Override
  Set<MovementOption> getMovementOptions() {
    Set<MovementOption> movementOptions = new HashSet<>();
    movementOptions.add(UP_ONE);
    movementOptions.add(TAKE_RIGHT);
    movementOptions.add(TAKE_LEFT);
    if (!getHasMoved()) {
      movementOptions.add(UP_TWO);
    }
    if (pieceColor == PieceColor.BLACK) {
      return movementOptions.stream()
          .map(MovementOption::inverseDirection)
          .collect(Collectors.toSet());
    }

    return movementOptions;
  }
}
