import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import pieces.King;
import pieces.PieceColor;

public class KingTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  @Test
  public void itAllowsAllKingMoves() {
    King king = new King(PieceColor.WHITE);
    Set<Coordinate> possibleMoveCoordinates = board.getPotentialMoves(king, Coordinate.from("d4"), Maps.newHashMap());

    Set<Coordinate> expected = new HashSet<>(Arrays.asList(
        Coordinate.from("d5"),  // UP
        Coordinate.from("d3"),  // DOWN
        Coordinate.from("c4"),  // LEFT
        Coordinate.from("e4"),  // RIGHT
        Coordinate.from("e5"),  // UP_RIGHT
        Coordinate.from("c5"),  // UP_LEFT  -- this is the bug: King defines UP_LEFT as (-1,-1) instead of (-1,1)
        Coordinate.from("e3"),  // DOWN_RIGHT
        Coordinate.from("c3")   // DOWN_LEFT
    ));

    assertEquals("King should have exactly 8 moves from d4 on empty board", 8, possibleMoveCoordinates.size());
    assertEquals("King moves should match all 8 surrounding squares", expected, possibleMoveCoordinates);
  }
}
