import java.util.Optional;
import pieces.Piece;

public final class ImmutableMoveResult implements MoveResult {
  private final boolean isSuccess;
  private final boolean isCheck;
  private final boolean isCheckmate;
  private final boolean isStalemate;
  private final Optional<Piece> pieceTakenMaybe;
  private final Piece pieceMoved;
  private final Coordinate originalPieceLocation;
  private final Coordinate newPieceLocation;

  private ImmutableMoveResult(Builder builder) {
    this.isSuccess = builder.isSuccess;
    this.isCheck = builder.isCheck;
    this.isCheckmate = builder.isCheckmate;
    this.isStalemate = builder.isStalemate;
    this.pieceTakenMaybe = builder.pieceTakenMaybe;
    this.pieceMoved = builder.pieceMoved;
    this.originalPieceLocation = builder.originalPieceLocation;
    this.newPieceLocation = builder.newPieceLocation;
  }

  @Override public boolean isSuccess() { return isSuccess; }
  @Override public boolean isCheck() { return isCheck; }
  @Override public boolean isCheckmate() { return isCheckmate; }
  @Override public boolean isStalemate() { return isStalemate; }
  @Override public Optional<Piece> getPieceTakenMaybe() { return pieceTakenMaybe; }
  @Override public Piece getPieceMoved() { return pieceMoved; }
  @Override public Coordinate getOriginalPieceLocation() { return originalPieceLocation; }
  @Override public Coordinate getNewPieceLocation() { return newPieceLocation; }

  public static Builder builder() { return new Builder(); }

  public static final class Builder {
    private boolean isSuccess = true;
    private boolean isCheck = false;
    private boolean isCheckmate = false;
    private boolean isStalemate = false;
    private Optional<Piece> pieceTakenMaybe = Optional.empty();
    private Piece pieceMoved;
    private Coordinate originalPieceLocation;
    private Coordinate newPieceLocation;

    public Builder isSuccess(boolean isSuccess) { this.isSuccess = isSuccess; return this; }
    public Builder isCheck(boolean isCheck) { this.isCheck = isCheck; return this; }
    public Builder isCheckmate(boolean isCheckmate) { this.isCheckmate = isCheckmate; return this; }
    public Builder isStalemate(boolean isStalemate) { this.isStalemate = isStalemate; return this; }
    public Builder pieceTakenMaybe(Optional<Piece> pieceTakenMaybe) { this.pieceTakenMaybe = pieceTakenMaybe; return this; }
    public Builder pieceMoved(Piece pieceMoved) { this.pieceMoved = pieceMoved; return this; }
    public Builder originalPieceLocation(Coordinate loc) { this.originalPieceLocation = loc; return this; }
    public Builder newPieceLocation(Coordinate loc) { this.newPieceLocation = loc; return this; }

    public ImmutableMoveResult build() { return new ImmutableMoveResult(this); }
  }
}
