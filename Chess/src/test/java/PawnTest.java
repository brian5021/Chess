import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import pieces.Pawn;
import pieces.Piece;
import pieces.PieceColor;

public class PawnTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  @Test
  public void itAllowsTwoMovesForUnmovedPawn() {
    Pawn pawn = new Pawn(PieceColor.WHITE);
    Set<Coordinate> possibleMoveCoordinates =  board.getPotentialMoves(pawn, Coordinate.from("d2"), Maps.newHashMap());
    assertThat(possibleMoveCoordinates.size()).isEqualTo(2);
    assertThat(possibleMoveCoordinates).containsExactlyInAnyOrder(Coordinate.from("d3"), Coordinate.from("d4"));
  }

  @Test
  public void itAllowsOneMoveForUMovedPawn() {
    Pawn pawn = new Pawn(PieceColor.WHITE);
    pawn.setHasMoved(true);
    Set<Coordinate> possibleMoveCoordinates =  board.getPotentialMoves(pawn, Coordinate.from("d2"), Maps.newHashMap());
    assertThat(possibleMoveCoordinates.size()).isEqualTo(1);
    assertThat(possibleMoveCoordinates).containsExactlyInAnyOrder(Coordinate.from("d3"));
  }

  @Test
  public void itAllowsPawnToTake() {
    Pawn pawn = new Pawn(PieceColor.WHITE);
    pawn.setHasMoved(true);
    Pawn opponentPawn = new Pawn(PieceColor.BLACK);
    Map<Coordinate, Piece> currentBoardPosition = Maps.newHashMap();
    currentBoardPosition.put(Coordinate.from("d4"), pawn);
    currentBoardPosition.put(Coordinate.from("e5"), opponentPawn);

    Set<Coordinate> possibleMoveCoordinates =  board.getPotentialMoves(pawn, Coordinate.from("d4"), currentBoardPosition);
    assertThat(possibleMoveCoordinates.size()).isEqualTo(2);
    assertThat(possibleMoveCoordinates).containsExactlyInAnyOrder(Coordinate.from("d5"), Coordinate.from("e5"));
  }
}
