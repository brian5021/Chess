import java.util.Set;

public abstract class Piece {
  final Color color;

  public Piece(Color color) {
    this.color = color;
  }

  abstract Set<MovementOption> getMovementOptions();

  public Color getColor() {
    return color;
  }
}
