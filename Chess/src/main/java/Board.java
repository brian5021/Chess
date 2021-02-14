import java.awt.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class Board {
  private static final Set<Integer> ROWS = ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8);
  private static final BiMap<String, Integer> COLUMNS = ImmutableBiMap.<String, Integer>builder().put("a", 1).put("b", 2).put("c", 3).put("d", 4).put("e", 5).put("f", 6).put("g", 7).put("h", 8).build();
  private static final Player WHITE_PLAYER = new Player(PieceColor.WHITE);
  private static final Player BLACK_PLAYER = new Player(PieceColor.BLACK);
  private static final Map<PieceColor, Player> COLOR_TO_PLAYER = ImmutableMap.of(PieceColor.WHITE, WHITE_PLAYER, PieceColor.BLACK, BLACK_PLAYER);
  private static final Map<PieceColor, Player> COLOR_TO_OPPONENT = ImmutableMap.of(PieceColor.WHITE, BLACK_PLAYER, PieceColor.BLACK, WHITE_PLAYER);

  private final Map<Coordinate, Piece> piecePositionMap = new HashMap<>();
  private PieceColor currentTurnPieceColor = PieceColor.WHITE;

  private final JPanel gui = new JPanel(new BorderLayout(3, 3));
  private JPanel chessBoard;
  private final JLabel message = new JLabel("Chess Champ is ready to play!");
  private static final String COLS = "ABCDEFGH";


  public Board() {
    initializePieces();
  }

  public boolean movePiece(Coordinate currentPosition, Coordinate targetPosition) {
    Piece piece = getPieceAtCoordinate(currentPosition);
    System.out.printf("Attempting to move %s from %s to %s%n", piece.getClass().getSimpleName(), currentPosition, targetPosition);
    Set<Coordinate> potentialMoves = getPotentialMoves(piece, currentPosition);
    if (!potentialMoves.contains(targetPosition)) {
      throw new RuntimeException("Not valid move");
    }
    Optional<Piece> takenPieceMaybe = executePieceMove(currentPosition, targetPosition, piece);
    if (moveExposesCheck()) {
      undoPieceMove(currentPosition, targetPosition, piece, takenPieceMaybe);
      throw new RuntimeException("Move exposes check on the king");
    }
    if (moveCausesCheck()) {
      System.out.println("CHECK!");
    }
    takenPieceMaybe.ifPresent(value -> System.out.printf("The %s piece was taken%n", value.getClass().getSimpleName()));
    System.out.println("successfully moved piece");
    endTurn();
    return true;
  }

  private Optional<Piece> executePieceMove(Coordinate currentPosition, Coordinate targetPosition, Piece piece) {
    piecePositionMap.remove(currentPosition);
    Optional<Piece> takenPieceMaybe = Optional.ofNullable(piecePositionMap.remove(targetPosition));
    piecePositionMap.put(targetPosition, piece);
    if (piece instanceof King) {
      COLOR_TO_PLAYER.get(currentTurnPieceColor).setKingPosition(targetPosition);
    }
    return takenPieceMaybe;
  }

  private void undoPieceMove(Coordinate currentPosition, Coordinate targetPosition, Piece piece, Optional<Piece> takenPieceMaybe) {
    piecePositionMap.remove(targetPosition);
    piecePositionMap.put(currentPosition, piece);
    takenPieceMaybe.ifPresent(takenPiece -> piecePositionMap.put(targetPosition, takenPiece));
    if (piece instanceof King) {
      COLOR_TO_PLAYER.get(currentTurnPieceColor).setKingPosition(currentPosition);
    }
  }

  private boolean moveExposesCheck() {
    System.out.printf("Checking if move exposes check - King %s%n", COLOR_TO_PLAYER.get(currentTurnPieceColor).getKingPosition());
    return moveResultsInCheck(currentTurnPieceColor);
  }

  private boolean moveCausesCheck() {
    System.out.printf("Checking if move causes check - King %s%n", COLOR_TO_OPPONENT.get(currentTurnPieceColor).getKingPosition());
    return moveResultsInCheck(COLOR_TO_OPPONENT.get(currentTurnPieceColor).getColor());
  }

  private boolean moveResultsInCheck(PieceColor pieceColor) {
    Coordinate kingPosition = COLOR_TO_PLAYER.get(pieceColor).getKingPosition();
    Set<Coordinate> opponentPotentialMoves = piecePositionMap.entrySet().stream()
        .filter(entry -> entry.getValue().getColor() == COLOR_TO_OPPONENT.get(pieceColor).getColor())
        .map(entry -> getPotentialMoves(entry.getValue(), entry.getKey()))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    return opponentPotentialMoves.contains(kingPosition);
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
        if (movementOption.isRequiresTake() && (!potentialMoveOccupied || !canTake(piece, piecePositionMap.get(potentialMove)))) {
          continue;
        }
        if (potentialMoveOccupied && canTake(piece, piecePositionMap.get(potentialMove))) {
          potentialMoves.add(potentialMove);
        } else if (!potentialMoveOccupied) {
          potentialMoves.add(potentialMove);
        }
        startingPosition = potentialMove;
      } while (movementOption.isRepeating() && !potentialMoveOccupied && potentialMoveOnBoard);
    }
    System.out.printf("Potential moves for piece %s [%s]: %s%n", piece.getClass().getSimpleName(), currentPosition, potentialMoves.stream().sorted(Comparator.comparing(Coordinate::toString)).collect(Collectors.toList()));
    return potentialMoves;
  }

  private boolean canTake(Piece piece, Piece pieceToTake) {
    return piece.getColor() != pieceToTake.getColor();
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
      if (piecePositionMap.get(coordinate).getColor() != currentTurnPieceColor) {
        throw new RuntimeException(String.format("Piece is not owned by %s", currentTurnPieceColor));
      }
      return piece;
    }
  }

  private void initializePieces() {
    for (PieceColor pieceColor : PieceColor.values()) {
      initializePawns(pieceColor);
      initializeRooks(pieceColor);
      initializeKnights(pieceColor);
      initializeBishops(pieceColor);
      initializeQueens(pieceColor);
      initializeKings(pieceColor);
    }
  }

  private void initializePawns(PieceColor pieceColor) {
    for (String column : COLUMNS.keySet()) {
      piecePositionMap.put(new Coordinate(column, pieceColor.getPawnRow()), new Pawn(pieceColor));
    }
  }

  private void initializeRooks(PieceColor pieceColor) {
    piecePositionMap.put(new Coordinate("a", pieceColor.getStartRow()), new Rook(pieceColor));
    piecePositionMap.put(new Coordinate("h", pieceColor.getStartRow()), new Rook(pieceColor));
  }

  private void initializeKnights(PieceColor pieceColor) {
    piecePositionMap.put(new Coordinate("b", pieceColor.getStartRow()), new Knight(pieceColor));
    piecePositionMap.put(new Coordinate("g", pieceColor.getStartRow()), new Knight(pieceColor));
  }

  private void initializeBishops(PieceColor pieceColor) {
    piecePositionMap.put(new Coordinate("c", pieceColor.getStartRow()), new Bishop(pieceColor));
    piecePositionMap.put(new Coordinate("f", pieceColor.getStartRow()), new Bishop(pieceColor));
  }

  private void initializeQueens(PieceColor pieceColor) {
    piecePositionMap.put(new Coordinate("d", pieceColor.getStartRow()), new Queen(pieceColor));
  }

  private void initializeKings(PieceColor pieceColor) {
    piecePositionMap.put(new Coordinate("e", pieceColor.getStartRow()), new King(pieceColor));
  }

  private void endTurn() {
    currentTurnPieceColor = currentTurnPieceColor == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
  }
}
