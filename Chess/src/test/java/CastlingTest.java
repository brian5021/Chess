import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import pieces.King;
import pieces.Rook;
import pieces.Bishop;
import pieces.Queen;
import pieces.Piece;
import pieces.PieceColor;

public class CastlingTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  // === KINGSIDE CASTLING (O-O) ===

  @Test
  public void whiteKingsideCastleAvailable() {
    // White King e1, White Rook h1, nothing between — g1 should be in moves
    King king = new King(PieceColor.WHITE);
    Rook rook = new Rook(PieceColor.WHITE);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("h1"), rook);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertTrue("Kingside castle g1 should be available",
        moves.contains(Coordinate.from("g1")));
  }

  @Test
  public void blackKingsideCastleAvailable() {
    // Black King e8, Black Rook h8, nothing between — g8 should be in moves
    King king = new King(PieceColor.BLACK);
    Rook rook = new Rook(PieceColor.BLACK);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e8"), king);
    position.put(Coordinate.from("h8"), rook);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e8"), position);
    assertTrue("Kingside castle g8 should be available",
        moves.contains(Coordinate.from("g8")));
  }

  @Test
  public void kingsideCastleBlockedByPieceBetween() {
    // Bishop on f1 blocks castling
    King king = new King(PieceColor.WHITE);
    Rook rook = new Rook(PieceColor.WHITE);
    Bishop bishop = new Bishop(PieceColor.WHITE);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("h1"), rook);
    position.put(Coordinate.from("f1"), bishop);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertFalse("Kingside castle should be blocked when f1 is occupied",
        moves.contains(Coordinate.from("g1")));
  }

  @Test
  public void kingsideCastleBlockedWhenKingHasMoved() {
    King king = new King(PieceColor.WHITE);
    king.setHasMoved(true);
    Rook rook = new Rook(PieceColor.WHITE);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("h1"), rook);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertFalse("Kingside castle should not be available when king has moved",
        moves.contains(Coordinate.from("g1")));
  }

  @Test
  public void kingsideCastleBlockedWhenRookHasMoved() {
    King king = new King(PieceColor.WHITE);
    Rook rook = new Rook(PieceColor.WHITE);
    rook.setHasMoved(true);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("h1"), rook);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertFalse("Kingside castle should not be available when rook has moved",
        moves.contains(Coordinate.from("g1")));
  }

  // === QUEENSIDE CASTLING (O-O-O) ===

  @Test
  public void whiteQueensideCastleAvailable() {
    // White King e1, White Rook a1, nothing between — c1 should be in moves
    King king = new King(PieceColor.WHITE);
    Rook rook = new Rook(PieceColor.WHITE);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("a1"), rook);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertTrue("Queenside castle c1 should be available",
        moves.contains(Coordinate.from("c1")));
  }

  @Test
  public void blackQueensideCastleAvailable() {
    King king = new King(PieceColor.BLACK);
    Rook rook = new Rook(PieceColor.BLACK);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e8"), king);
    position.put(Coordinate.from("a8"), rook);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e8"), position);
    assertTrue("Queenside castle c8 should be available",
        moves.contains(Coordinate.from("c8")));
  }

  @Test
  public void queensideCastleBlockedByPieceBetween() {
    // Queen on d1 blocks queenside castling
    King king = new King(PieceColor.WHITE);
    Rook rook = new Rook(PieceColor.WHITE);
    Queen queen = new Queen(PieceColor.WHITE);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("a1"), rook);
    position.put(Coordinate.from("d1"), queen);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertFalse("Queenside castle should be blocked when d1 is occupied",
        moves.contains(Coordinate.from("c1")));
  }

  // === CASTLING THROUGH CHECK ===

  @Test
  public void kingsideCastleBlockedWhenPassingThroughCheck() {
    // Enemy rook attacks f1 — king would pass through check
    King king = new King(PieceColor.WHITE);
    Rook friendlyRook = new Rook(PieceColor.WHITE);
    Rook enemyRook = new Rook(PieceColor.BLACK);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("h1"), friendlyRook);
    position.put(Coordinate.from("f8"), enemyRook); // attacks f1

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertFalse("Kingside castle should be blocked when king passes through check on f1",
        moves.contains(Coordinate.from("g1")));
  }

  @Test
  public void queensideCastleBlockedWhenPassingThroughCheck() {
    // Enemy rook attacks d1 — king would pass through check
    King king = new King(PieceColor.WHITE);
    Rook friendlyRook = new Rook(PieceColor.WHITE);
    Rook enemyRook = new Rook(PieceColor.BLACK);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("a1"), friendlyRook);
    position.put(Coordinate.from("d8"), enemyRook); // attacks d1

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertFalse("Queenside castle should be blocked when king passes through check on d1",
        moves.contains(Coordinate.from("c1")));
  }

  // === BOTH SIDES AVAILABLE ===

  @Test
  public void bothCastlesAvailableSimultaneously() {
    King king = new King(PieceColor.WHITE);
    Rook kingsideRook = new Rook(PieceColor.WHITE);
    Rook queensideRook = new Rook(PieceColor.WHITE);
    Map<Coordinate, Piece> position = Maps.newHashMap();
    position.put(Coordinate.from("e1"), king);
    position.put(Coordinate.from("h1"), kingsideRook);
    position.put(Coordinate.from("a1"), queensideRook);

    Set<Coordinate> moves = board.getPotentialMoves(king, Coordinate.from("e1"), position);
    assertTrue("Kingside castle g1 should be available",
        moves.contains(Coordinate.from("g1")));
    assertTrue("Queenside castle c1 should be available",
        moves.contains(Coordinate.from("c1")));
  }
}
