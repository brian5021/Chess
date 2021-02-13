import java.util.Set;

public abstract class Piece {
  private final Color color;

  public Piece(Color color) {
    this.color = color;
  }

  abstract Set<MovementOption> getMovementOptions();
  abstract Set<String> getInitialColumns();

  public Color getColor() {
    return color;
  }
}
