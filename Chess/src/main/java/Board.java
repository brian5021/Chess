import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Board {
  private static final Set<Integer> ROWS = ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8);
  private static final Set<String> COLUMNS = ImmutableSet.of("a", "b", "c", "d", "e", "f", "g", "h");
  private final Map<String, Map<Integer, Piece>> piecePositionMap = new HashMap<>();

  public Color turnColor = Color.WHITE;


  public boolean movePiece(Coordinate currentPosition, Coordinate targetPosition) {
    Piece piece = getPieceAtCoordinate(currentPosition);
    Set<Coordinate> potentialMoves = getPotentialMoves(piece, targetPosition);
    if (!potentialMoves.contains(targetPosition)) {
      return false;
    }
  }

  private Set<Coordinate> getPotentialMoves(Piece piece, Coordinate currentPosition) {
    Set<Coordinate> potentialMoves = new HashSet<>();
    for (MovementOption movementOption : piece.getMovementOptions()) {
      Coordinate potentialMove;
      boolean potentialMoveOnBoard;
      boolean potentialMoveOccupied;
      do {
        potentialMove = new Coordinate(currentPosition.getColumn() + movementOption.getXMotion(), currentPosition.getRow() + movementOption.getYMotion());
        potentialMoveOnBoard = isOnBoard(potentialMove);
        potentialMoveOccupied = isOccupied(potentialMove);
        if (!potentialMoveOccupied || canTake(piecePositionMap.get(potentialMove.getColumn()).get(potentialMove.getRow()))) {
          potentialMoves.add(potentialMove);
        }
      } while (movementOption.isRepeating() && !potentialMoveOccupied && potentialMoveOnBoard);
    }
  }

  private boolean canTake(Piece piece) {
    if (piece.getColor() == turnColor) {
      return false;
    }
    if (piece instanceof King) {
      throw new RuntimeException("Missed check");
    }
    return true;
  }

  private boolean isOnBoard(Coordinate coordinate) {
    return COLUMNS.contains(coordinate.getColumn()) && ROWS.contains(coordinate.getRow());
  }

  private boolean isOccupied(Coordinate coordinate) {
    return piecePositionMap.get(coordinate.getColumn()) != null && piecePositionMap.get(coordinate.getColumn()).get(coordinate.getRow()) != null;
  }

  private Piece getPieceAtCoordinate(Coordinate coordinate) {
    if (!isOccupied(coordinate)) {
      throw new RuntimeException("No piece at coordinate");
    } else {
      Piece piece = piecePositionMap.get(coordinate.getColumn()).get(coordinate.getRow());
      if (piecePositionMap.get(coordinate.getColumn()).get(coordinate.getRow()).getColor() != turnColor) {
        throw new RuntimeException(String.format("Piece is not owned by %s", turnColor.name()));
      }
      return piece;
    }
  }

  private void initializeBoard() {
    initializePieces();
  }

  private void initializePieces() {
    for (Color color : Color.values()) {
      initializePawns(color);
      initializeRooks(color);
      initializeKnights(color);
      initializeBishops(color);
      initializeQueens(color);
      initializeKings(color);
    }
  }

  private void initializePawns(Color color) {
    for (String column : COLUMNS) {
        piecePositionMap.get(column).put(color.getPawnRow(), new Pawn(color));
      }
  }

  private void initializeRooks(Color color) {
    piecePositionMap.get("a").put(color.getStartRow(), new Rook(color));
    piecePositionMap.get("h").put(color.getStartRow(), new Rook(color));
  }

  private void initializeKnights(Color color) {
    piecePositionMap.get("b").put(color.getStartRow(), new Knight(color));
    piecePositionMap.get("g").put(color.getStartRow(), new Knight(color));
  }

  private void initializeBishops(Color color) {
    piecePositionMap.get("c").put(color.getStartRow(), new Bishop(color));
    piecePositionMap.get("f").put(color.getStartRow(), new Bishop(color));
  }

  private void initializeQueens(Color color) {
    piecePositionMap.get("d").put(color.getStartRow(), new Queen(color));
  }

  private void initializeKings(Color color) {
    piecePositionMap.get("e").put(color.getStartRow(), new King(color));
  }

  private void endTurn() {
    turnColor = turnColor == Color.WHITE ? Color.BLACK : Color.WHITE;
  }
}
