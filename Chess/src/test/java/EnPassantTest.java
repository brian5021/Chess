import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import pieces.King;
import pieces.Pawn;
import pieces.Piece;
import pieces.PieceColor;

public class EnPassantTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  @Test
  public void whiteCanCaptureEnPassantRight() {
    // White pawn on e5, black pawn just double-moved to f5
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn whitePawn = new Pawn(PieceColor.WHITE);
    whitePawn.setHasMoved(true);
    Pawn blackPawn = new Pawn(PieceColor.BLACK);
    blackPawn.setHasMoved(true);
    pieces.put(Coordinate.from("e5"), whitePawn);
    pieces.put(Coordinate.from("f5"), blackPawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);
    // Simulate that the last move was the black pawn double-moving from f7 to f5
    board.setEnPassantTarget(Coordinate.from("f6"));

    Set<Coordinate> moves = board.getPotentialMoves(whitePawn, Coordinate.from("e5"), pieces);
    assertTrue("White pawn should be able to capture en passant on f6",
        moves.contains(Coordinate.from("f6")));
  }

  @Test
  public void whiteCanCaptureEnPassantLeft() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn whitePawn = new Pawn(PieceColor.WHITE);
    whitePawn.setHasMoved(true);
    Pawn blackPawn = new Pawn(PieceColor.BLACK);
    blackPawn.setHasMoved(true);
    pieces.put(Coordinate.from("e5"), whitePawn);
    pieces.put(Coordinate.from("d5"), blackPawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);
    board.setEnPassantTarget(Coordinate.from("d6"));

    Set<Coordinate> moves = board.getPotentialMoves(whitePawn, Coordinate.from("e5"), pieces);
    assertTrue("White pawn should be able to capture en passant on d6",
        moves.contains(Coordinate.from("d6")));
  }

  @Test
  public void blackCanCaptureEnPassant() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn blackPawn = new Pawn(PieceColor.BLACK);
    blackPawn.setHasMoved(true);
    Pawn whitePawn = new Pawn(PieceColor.WHITE);
    whitePawn.setHasMoved(true);
    pieces.put(Coordinate.from("d4"), blackPawn);
    pieces.put(Coordinate.from("e4"), whitePawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.BLACK);
    board.setEnPassantTarget(Coordinate.from("e3"));

    Set<Coordinate> moves = board.getPotentialMoves(blackPawn, Coordinate.from("d4"), pieces);
    assertTrue("Black pawn should be able to capture en passant on e3",
        moves.contains(Coordinate.from("e3")));
  }

  @Test
  public void enPassantCaptureRemovesCapturedPawn() {
    // Integration test: execute the en passant move and verify captured pawn is removed
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn whitePawn = new Pawn(PieceColor.WHITE);
    whitePawn.setHasMoved(true);
    Pawn blackPawn = new Pawn(PieceColor.BLACK);
    blackPawn.setHasMoved(true);
    pieces.put(Coordinate.from("e5"), whitePawn);
    pieces.put(Coordinate.from("f5"), blackPawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);
    board.setEnPassantTarget(Coordinate.from("f6"));

    board.movePiece(Coordinate.from("e5"), Coordinate.from("f6"));

    // White pawn should be on f6
    Piece pieceOnF6 = board.getPieceAt(Coordinate.from("f6"));
    assertTrue("White pawn should be on f6 after en passant",
        pieceOnF6 instanceof Pawn && pieceOnF6.getColor() == PieceColor.WHITE);

    // Black pawn on f5 should be captured (removed)
    assertNull("Black pawn on f5 should be removed after en passant capture",
        board.getPieceAt(Coordinate.from("f5")));
  }

  @Test
  public void enPassantNotAvailableAfterOtherMove() {
    // En passant is only available immediately after the double move
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn whitePawn = new Pawn(PieceColor.WHITE);
    whitePawn.setHasMoved(true);
    Pawn blackPawn = new Pawn(PieceColor.BLACK);
    blackPawn.setHasMoved(true);
    pieces.put(Coordinate.from("e5"), whitePawn);
    pieces.put(Coordinate.from("f5"), blackPawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);
    // No en passant target set (it was cleared after another move)

    Set<Coordinate> moves = board.getPotentialMoves(whitePawn, Coordinate.from("e5"), pieces);
    assertFalse("En passant should not be available when target is not set",
        moves.contains(Coordinate.from("f6")));
  }

  @Test
  public void doubleMoveSetsEnPassantTarget() {
    // When a pawn makes a double move, the en passant target should be set
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn whitePawn = new Pawn(PieceColor.WHITE);
    pieces.put(Coordinate.from("e2"), whitePawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    board.movePiece(Coordinate.from("e2"), Coordinate.from("e4"));

    assertEquals("En passant target should be set to e3 after double pawn move",
        Coordinate.from("e3"), board.getEnPassantTarget());
  }
}
