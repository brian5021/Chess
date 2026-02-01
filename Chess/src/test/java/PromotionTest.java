import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pieces.King;
import pieces.Pawn;
import pieces.Piece;
import pieces.PieceColor;
import pieces.Queen;

public class PromotionTest {
  Board board;

  @Before
  public void setup() {
    board = new Board();
  }

  @Test
  public void whitePawnPromotesToQueenOnRank8() {
    // Set up minimal board: white pawn on e7 about to promote, both kings present
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn pawn = new Pawn(PieceColor.WHITE);
    pawn.setHasMoved(true);
    pieces.put(Coordinate.from("e7"), pawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    // Move pawn to e8 (but king is there, use a different file)
    // Actually, let's use a clear path
    pieces.put(Coordinate.from("a8"), new King(PieceColor.BLACK));
    pieces.remove(Coordinate.from("e8"));
    board.clearAndSetPieces(pieces);

    board.movePiece(Coordinate.from("e7"), Coordinate.from("e8"));

    Piece promotedPiece = board.getPieceAt(Coordinate.from("e8"));
    assertTrue("Pawn should promote to Queen when reaching rank 8",
        promotedPiece instanceof Queen);
  }

  @Test
  public void blackPawnPromotesToQueenOnRank1() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn pawn = new Pawn(PieceColor.BLACK);
    pawn.setHasMoved(true);
    pieces.put(Coordinate.from("d2"), pawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.BLACK);

    board.movePiece(Coordinate.from("d2"), Coordinate.from("d1"));

    Piece promotedPiece = board.getPieceAt(Coordinate.from("d1"));
    assertTrue("Black pawn should promote to Queen when reaching rank 1",
        promotedPiece instanceof Queen);
  }

  @Test
  public void promotedQueenRetainsSameColor() {
    Map<Coordinate, Piece> pieces = new HashMap<>();
    Pawn pawn = new Pawn(PieceColor.WHITE);
    pawn.setHasMoved(true);
    pieces.put(Coordinate.from("c7"), pawn);
    pieces.put(Coordinate.from("e1"), new King(PieceColor.WHITE));
    pieces.put(Coordinate.from("e8"), new King(PieceColor.BLACK));

    board.clearAndSetPieces(pieces);
    board.setCurrentTurn(PieceColor.WHITE);

    board.movePiece(Coordinate.from("c7"), Coordinate.from("c8"));

    Piece promotedPiece = board.getPieceAt(Coordinate.from("c8"));
    assertTrue("Promoted piece should be a Queen", promotedPiece instanceof Queen);
    assertTrue("Promoted queen should retain white color",
        promotedPiece.getColor() == PieceColor.WHITE);
  }
}
