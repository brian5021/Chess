import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Rook extends Piece {
  private static final MovementOption MOVEMENT_UP = new MovementOption(0, 1, true);
  private static final MovementOption MOVEMENT_DOWN = new MovementOption(0, -1, true);
  private static final MovementOption MOVEMENT_LEFT = new MovementOption(-1, 0, true);
  private static final MovementOption MOVEMENT_RIGHT = new MovementOption(1, 0, true);
  private static final Set<MovementOption> MOVEMENT_OPTIONS = ImmutableSet.of(MOVEMENT_DOWN, MOVEMENT_UP, MOVEMENT_LEFT, MOVEMENT_RIGHT);

  public Rook(Color color) {
    super(color);
  }

  @Override
  Set<MovementOption> getMovementOptions() {
    return MOVEMENT_OPTIONS;
  }

}
