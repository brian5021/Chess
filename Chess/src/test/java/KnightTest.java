import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import pieces.Knight;
import pieces.PieceColor;

public class KnightTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  @Test
  public void itAllowsAllKnightMoves() {
    Knight knight = new Knight(PieceColor.WHITE);
    Set<Coordinate> possibleMoveCoordinates =  board.getPotentialMoves(knight, Coordinate.from("d4"), Maps.newHashMap());
    assertThat(possibleMoveCoordinates.size()).isEqualTo(8);
    assertThat(possibleMoveCoordinates).containsExactlyInAnyOrder(Coordinate.from("e6"), Coordinate.from("c2"), Coordinate.from("f5"), Coordinate.from("b3"),
        Coordinate.from("c6"), Coordinate.from("b5"), Coordinate.from("e2"), Coordinate.from("f3"));
  }

}
