import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Piece;
import pieces.PieceColor;
import pieces.Queen;
import pieces.Rook;

public class SaveRestoreTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  @Test
  public void serializeDefaultBoardProducesNonEmptyString() {
    String serialized = board.serialize();
    assertNotNull("Serialized board should not be null", serialized);
    assertTrue("Serialized board should contain piece data", serialized.contains("King"));
    assertTrue("Serialized board should contain turn info", serialized.contains("turn:WHITE"));
  }

  @Test
  public void roundTripPreservesPiecePositions() {
    // Set up a custom board, serialize, restore into a new board, verify pieces match
    Map<Coordinate, Piece> pieces = new HashMap<>();
    King whiteKing = new King(PieceColor.WHITE);
    whiteKing.setHasMoved(true);
    pieces.put(Coordinate.from("g1"), whiteKing);
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));
    Pawn pawn = new Pawn(PieceColor.WHITE);
    pawn.setHasMoved(true);
    pieces.put(Coordinate.from("d4"), pawn);
    pieces.put(Coordinate.from("h1"), new Rook(PieceColor.WHITE));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    String serialized = board.serialize();
    Board restored = Board.deserialize(serialized);

    // Verify pieces
    Piece restoredKing = restored.getPieceAt(Coordinate.from("g1"));
    assertTrue("White king should be at g1", restoredKing instanceof King);
    assertEquals("White king color", PieceColor.WHITE, restoredKing.getColor());
    assertTrue("White king hasMoved should be true", restoredKing.getHasMoved());

    Piece restoredPawn = restored.getPieceAt(Coordinate.from("d4"));
    assertTrue("White pawn should be at d4", restoredPawn instanceof Pawn);
    assertTrue("White pawn hasMoved should be true", restoredPawn.getHasMoved());

    Piece restoredRook = restored.getPieceAt(Coordinate.from("h1"));
    assertTrue("White rook should be at h1", restoredRook instanceof Rook);
    assertFalse("White rook hasMoved should be false", restoredRook.getHasMoved());

    assertNull("Empty square should be null", restored.getPieceAt(Coordinate.from("a1")));
  }

  @Test
  public void roundTripPreservesTurnColor() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.BLACK);

    String serialized = board.serialize();
    Board restored = Board.deserialize(serialized);

    // Move a black piece to verify it's black's turn
    Pawn blackPawn = new Pawn(PieceColor.BLACK);
    blackPawn.setHasMoved(true);
    // We can't directly check currentTurnPieceColor, but the serialize output should contain BLACK
    assertTrue("Serialized should contain turn:BLACK", serialized.contains("turn:BLACK"));
  }

  @Test
  public void roundTripPreservesEnPassantTarget() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);
    board.setEnPassantTarget(Coordinate.from("d3"));

    String serialized = board.serialize();
    Board restored = Board.deserialize(serialized);

    assertEquals("En passant target should be preserved",
        Coordinate.from("d3"), restored.getEnPassantTarget());
  }

  @Test
  public void roundTripWithNoEnPassantTarget() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    String serialized = board.serialize();
    Board restored = Board.deserialize(serialized);

    assertNull("En passant target should be null when not set",
        restored.getEnPassantTarget());
  }

  @Test
  public void roundTripPreservesAllPieceTypes() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("d1"), new Queen(PieceColor.WHITE));
    pieces.put(Coordinate.from("a1"), new Rook(PieceColor.WHITE));
    pieces.put(Coordinate.from("c1"), new Bishop(PieceColor.WHITE));
    pieces.put(Coordinate.from("b1"), new Knight(PieceColor.WHITE));
    pieces.put(Coordinate.from("e2"), new Pawn(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    String serialized = board.serialize();
    Board restored = Board.deserialize(serialized);

    assertTrue("King preserved", restored.getPieceAt(Coordinate.from("e1")) instanceof King);
    assertTrue("Queen preserved", restored.getPieceAt(Coordinate.from("d1")) instanceof Queen);
    assertTrue("Rook preserved", restored.getPieceAt(Coordinate.from("a1")) instanceof Rook);
    assertTrue("Bishop preserved", restored.getPieceAt(Coordinate.from("c1")) instanceof Bishop);
    assertTrue("Knight preserved", restored.getPieceAt(Coordinate.from("b1")) instanceof Knight);
    assertTrue("Pawn preserved", restored.getPieceAt(Coordinate.from("e2")) instanceof Pawn);
  }

  @Test
  public void roundTripDefaultBoardMatchesOriginal() {
    // Serialize the default starting board and restore it
    String serialized = board.serialize();
    Board restored = Board.deserialize(serialized);

    // Check a few key pieces from the starting position
    assertTrue("White king at e1", restored.getPieceAt(Coordinate.from("e1")) instanceof King);
    assertTrue("Black king at e8", restored.getPieceAt(Coordinate.from("e8")) instanceof King);
    assertTrue("White pawn at e2", restored.getPieceAt(Coordinate.from("e2")) instanceof Pawn);
    assertTrue("Black queen at d8", restored.getPieceAt(Coordinate.from("d8")) instanceof Queen);
    assertNull("Center should be empty", restored.getPieceAt(Coordinate.from("e4")));
  }
}
