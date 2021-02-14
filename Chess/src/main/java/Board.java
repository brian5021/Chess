import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;

public class Board {
  private static final Set<Integer> ROWS = ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8);
  private static final BiMap<String, Integer> COLUMNS = ImmutableBiMap.<String, Integer>builder().put("a", 1).put("b", 2).put("c", 3).put("d", 4).put("e", 5).put("f", 6).put("g", 7).put("h", 8).build();
  private static final Player WHITE_PLAYER = new Player(Color.WHITE);
  private static final Player BLACK_PLAYER = new Player(Color.BLACK);
  private final Map<Coordinate, Piece> piecePositionMap = new HashMap<>();
  private Player currentTurnPlayer = WHITE_PLAYER;

  public Board() {
    initializePieces();
  }

  public void movePiece(Coordinate currentPosition, Coordinate targetPosition) {
    Piece piece = getPieceAtCoordinate(currentPosition);
    System.out.printf("Attempting to move %s from %s to %s%n", piece.getClass().getSimpleName(), currentPosition, targetPosition);
    Set<Coordinate> potentialMoves = getPotentialMoves(piece, currentPosition);
    if (!potentialMoves.contains(targetPosition)) {
      throw new RuntimeException("Not valid move");
    }
    piecePositionMap.remove(currentPosition);
    Piece takenPiece = piecePositionMap.remove(targetPosition);
    if (takenPiece != null) {
      System.out.printf("The %s piece was taken%n", takenPiece.getClass().getSimpleName());
    }
    piecePositionMap.put(targetPosition, piece);
    System.out.println("successfully moved piece");
    endTurn();
  }

  private Set<Coordinate> getPotentialMoves(Piece piece, Coordinate currentPosition) {
    Set<Coordinate> potentialMoves = new HashSet<>();
    for (MovementOption movementOption : piece.getMovementOptions()) {
      Coordinate startingPosition = currentPosition;
      Coordinate potentialMove;
      boolean potentialMoveOnBoard;
      boolean potentialMoveOccupied;
      do {
        potentialMove = new Coordinate(COLUMNS.inverse().get(COLUMNS.get(startingPosition.getColumn()) + movementOption.getXMotion()), startingPosition.getRow() + movementOption.getYMotion());
        potentialMoveOnBoard = isOnBoard(potentialMove);
        potentialMoveOccupied = isOccupied(potentialMove);
        if (!potentialMoveOnBoard) {
          continue;
        }
        if (movementOption.isRequiresTake() && (!potentialMoveOccupied || !canTake(piecePositionMap.get(potentialMove)))) {
          continue;
        }
        if (potentialMoveOccupied && canTake(piecePositionMap.get(potentialMove))) {
          potentialMoves.add(potentialMove);
        } else if (!potentialMoveOccupied) {
          potentialMoves.add(potentialMove);
        }
        startingPosition = potentialMove;
      } while (movementOption.isRepeating() && !potentialMoveOccupied && potentialMoveOnBoard);
    }
    System.out.printf("Potential moves for piece: %s%n", potentialMoves);
    return potentialMoves;
  }

  private boolean canTake(Piece piece) {
    if (piece.getColor() == currentTurnPlayer.getColor()) {
      return false;
    }
    if (piece instanceof King) {
      throw new RuntimeException("Missed check");
    }
    return true;
  }

  private boolean isOnBoard(Coordinate coordinate) {
    return COLUMNS.containsKey(coordinate.getColumn()) && ROWS.contains(coordinate.getRow());
  }

  private boolean isOccupied(Coordinate coordinate) {
    return piecePositionMap.containsKey(coordinate);
  }

  private Piece getPieceAtCoordinate(Coordinate coordinate) {
    if (!isOccupied(coordinate)) {
      throw new RuntimeException("No piece at coordinate");
    } else {
      Piece piece = piecePositionMap.get(coordinate);
      if (piecePositionMap.get(coordinate).getColor() != currentTurnPlayer.getColor()) {
        throw new RuntimeException(String.format("Piece is not owned by %s", currentTurnPlayer.getColor().name()));
      }
      return piece;
    }
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
    for (String column : COLUMNS.keySet()) {
      piecePositionMap.put(new Coordinate(column, color.getPawnRow()), new Pawn(color));
    }
  }

  private void initializeRooks(Color color) {
    piecePositionMap.put(new Coordinate("a", color.getStartRow()), new Rook(color));
    piecePositionMap.put(new Coordinate("h", color.getStartRow()), new Rook(color));
  }

  private void initializeKnights(Color color) {
    piecePositionMap.put(new Coordinate("b", color.getStartRow()), new Knight(color));
    piecePositionMap.put(new Coordinate("g", color.getStartRow()), new Knight(color));
  }

  private void initializeBishops(Color color) {
    piecePositionMap.put(new Coordinate("c", color.getStartRow()), new Bishop(color));
    piecePositionMap.put(new Coordinate("f", color.getStartRow()), new Bishop(color));
  }

  private void initializeQueens(Color color) {
    piecePositionMap.put(new Coordinate("d", color.getStartRow()), new Queen(color));
  }

  private void initializeKings(Color color) {
    piecePositionMap.put(new Coordinate("e", color.getStartRow()), new King(color));
  }

  private void endTurn() {
    currentTurnPlayer = currentTurnPlayer.getColor() == Color.WHITE ? BLACK_PLAYER : WHITE_PLAYER;
  }
}
