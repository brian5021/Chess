import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Knight extends Piece {
  private static final MovementOption UP_TWO_RIGHT = new MovementOption(1, 2, false);
  private static final MovementOption UP_TWO_LEFT = new MovementOption(-1, 2, false);
  private static final MovementOption DOWN_TWO_RIGHT = new MovementOption(1, -2, false);
  private static final MovementOption DOWN_TWO_LEFT = new MovementOption(-1, -2, false);
  private static final MovementOption RIGHT_TWO_UP = new MovementOption(2, 1, false);
  private static final MovementOption RIGHT_TWO_DOWN = new MovementOption(2, -1, false);
  private static final MovementOption LEFT_TWO_UP = new MovementOption(-2, 1, false);
  private static final MovementOption LEFT_TWO_DOWN = new MovementOption(-2, -1, false);

  public Knight(Color color) {
    super(color);
  }

  @Override
  Set<MovementOption> getMovementOptions() {
    return ImmutableSet.of(UP_TWO_RIGHT, UP_TWO_LEFT, DOWN_TWO_RIGHT, DOWN_TWO_LEFT, RIGHT_TWO_UP, RIGHT_TWO_DOWN, LEFT_TWO_UP, LEFT_TWO_DOWN);
  }
}
