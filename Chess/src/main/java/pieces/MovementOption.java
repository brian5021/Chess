package pieces;

public class MovementOption {
  private final int xMotion;
  private final int yMotion;
  private final boolean repeating;
  private final boolean requiresTake;

  public MovementOption(int xMotion, int yMotion, boolean repeating, boolean requiresTake) {
    this.xMotion = xMotion;
    this.yMotion = yMotion;
    this.repeating = repeating;
    this.requiresTake = requiresTake;
  }

  public MovementOption(int xMotion, int yMotion, boolean repeating) {
    this(xMotion, yMotion, repeating, false);
  }

  public int getXMotion() {
    return xMotion;
  }

  public int getYMotion() {
    return yMotion;
  }

  public boolean isRepeating() {
    return repeating;
  }

  public boolean isRequiresTake() {
    return requiresTake;
  }

  public MovementOption inverseDirection() {
    return new MovementOption(xMotion, yMotion * -1, repeating, requiresTake);
  }
}
