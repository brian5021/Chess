import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Queen extends Piece {
  private static final MovementOption UP = new MovementOption(0, 1, true, false);
  private static final MovementOption DOWN = new MovementOption(0, -1, true, false);
  private static final MovementOption LEFT = new MovementOption(-1, 0, true, false);
  private static final MovementOption RIGHT = new MovementOption(1, 0, true, false);
  private static final MovementOption UP_RIGHT = new MovementOption(1, 1, true, false);
  private static final MovementOption UP_LEFT = new MovementOption(-1, -1, true, false);
  private static final MovementOption DOWN_RIGHT = new MovementOption(1, -1, true, false);
  private static final MovementOption DOWN_LEFT = new MovementOption(-1, -1, true, false);

  public Queen(Color color) {
    super(color);
  }

  @Override
  Set<MovementOption> getMovementOptions() {
    return ImmutableSet.of(UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT);
  }
}
