

public class MovementOption {
  private final int xMotion;
  private final int yMotion;
  private final boolean repeating;

  public MovementOption(int xMotion, int yMotion, boolean repeating) {
    this.xMotion = xMotion;
    this.yMotion = yMotion;
    this.repeating = repeating;
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
}
