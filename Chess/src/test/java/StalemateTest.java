import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pieces.King;
import pieces.Queen;
import pieces.Rook;
import pieces.Pawn;
import pieces.Piece;
import pieces.PieceColor;

public class StalemateTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  @Test
  public void detectsStalemateWhenOpponentHasNoLegalMoves() {
    // Classic stalemate position: Black king on a8, White queen on b6, White king on c1
    // After white moves queen to b6, black has no legal moves but is not in check
    Map<Coordinate, Piece> pieces = new HashMap<>();
    pieces.put(Coordinate.from("a8"), new King(PieceColor.BLACK));
    pieces.put(Coordinate.from("a6"), new Queen(PieceColor.WHITE));
    pieces.put(Coordinate.from("c1"), new King(PieceColor.WHITE));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    // Move queen from a6 to b6 — this creates stalemate for black
    ImmutableMoveResult result = board.movePiece(Coordinate.from("a6"), Coordinate.from("b6"));

    assertTrue("Should detect stalemate", result.isStalemate());
    assertFalse("Stalemate is not checkmate", result.isCheckmate());
    assertFalse("Stalemate means not in check", result.isCheck());
  }

  @Test
  public void noStalemateWhenOpponentHasLegalMoves() {
    // Normal position — not stalemate
    Map<Coordinate, Piece> pieces = new HashMap<>();
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));
    pieces.put(Coordinate.from("e2"), new Pawn(PieceColor.WHITE));
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    ImmutableMoveResult result = board.movePiece(Coordinate.from("e2"), Coordinate.from("e3"));

    assertFalse("Should not be stalemate when opponent has moves", result.isStalemate());
  }

  @Test
  public void checkmateIsNotStalemate() {
    // Black king cornered, white queen + king deliver mate
    // White king on f6 protects g7, so queen on g7 is checkmate
    Map<Coordinate, Piece> pieces = new HashMap<>();
    pieces.put(Coordinate.from("h8"), new King(PieceColor.BLACK));
    pieces.put(Coordinate.from("g6"), new Queen(PieceColor.WHITE));
    King whiteKing = new King(PieceColor.WHITE);
    whiteKing.setHasMoved(true);
    pieces.put(Coordinate.from("f6"), whiteKing);

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    // Move queen to g7 — checkmate (king on f6 protects g7)
    ImmutableMoveResult result = board.movePiece(Coordinate.from("g6"), Coordinate.from("g7"));

    assertTrue("Should be checkmate", result.isCheckmate());
    assertFalse("Checkmate should not be stalemate", result.isStalemate());
  }
}
