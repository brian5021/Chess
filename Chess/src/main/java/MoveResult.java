import java.util.Optional;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;

import pieces.Piece;

@Immutable
public interface MoveResult {
  @Default
  default boolean isSuccess() { return true; }
  @Default
  default boolean isCheck() { return false; }
  @Default
  default boolean isCheckmate() { return false; }
  Optional<Piece> getPieceTakenMaybe();
  Piece getPieceMoved();
  Coordinate getOriginalPieceLocation();
  Coordinate getNewPieceLocation();
}
