import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Bishop extends Piece {
  private static final MovementOption UP_ONE_RIGHT = new MovementOption(1, 1, true);
  private static final MovementOption UP_ONE_LEFT = new MovementOption(-1, 1, true);
  private static final MovementOption DOWN_ONE_RIGHT = new MovementOption(1, -1, true);
  private static final MovementOption DOWN_ONE_LEFT = new MovementOption(-1, -1, true);

  public Bishop(PieceColor pieceColor) {
    super(pieceColor);
  }

  @Override
  Set<MovementOption> getMovementOptions() {
    return ImmutableSet.of(UP_ONE_RIGHT, UP_ONE_LEFT, DOWN_ONE_RIGHT, DOWN_ONE_LEFT);
  }
}
