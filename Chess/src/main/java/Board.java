import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import pieces.Bishop;
import pieces.King;
import pieces.Knight;
import pieces.MovementOption;
import pieces.Pawn;
import pieces.Piece;
import pieces.PieceColor;
import pieces.Queen;
import pieces.Rook;

public class Board {
  private static final Set<Integer> ROWS = ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8);
  private static final BiMap<String, Integer> COLUMNS = ImmutableBiMap.<String, Integer>builder().put("a", 1).put("b", 2).put("c", 3).put("d", 4).put("e", 5).put("f", 6).put("g", 7).put("h", 8).build();
  private static final Player WHITE_PLAYER = new Player(PieceColor.WHITE);
  private static final Player BLACK_PLAYER = new Player(PieceColor.BLACK);
  private static final Map<PieceColor, Player> COLOR_TO_PLAYER = ImmutableMap.of(PieceColor.WHITE, WHITE_PLAYER, PieceColor.BLACK, BLACK_PLAYER);
  private static final Map<PieceColor, Player> COLOR_TO_OPPONENT = ImmutableMap.of(PieceColor.WHITE, BLACK_PLAYER, PieceColor.BLACK, WHITE_PLAYER);

  private final Map<Coordinate, Piece> piecePositionMap = new HashMap<>();
  private PieceColor currentTurnPieceColor = PieceColor.WHITE;
  private Coordinate enPassantTarget = null; // square behind a pawn that just double-moved


  public Board() {
    initializePieces();
  }

  public ImmutableMoveResult movePiece(Coordinate currentPosition, Coordinate targetPosition) {
    ImmutableMoveResult.Builder moveResult = ImmutableMoveResult.builder();
    Piece piece = getPieceAtCoordinate(currentPosition);
    Set<Coordinate> potentialMoves = getPotentialMoves(piece, currentPosition, piecePositionMap);
    if (!potentialMoves.contains(targetPosition)) {
      throw new RuntimeException("Not valid move");
    }
    Optional<Piece> takenPieceMaybe = executePieceMove(currentPosition, targetPosition, piece, piecePositionMap);

    // En passant capture: remove the opponent's pawn that was passed
    if (piece instanceof Pawn && targetPosition.equals(enPassantTarget)) {
      Coordinate capturedPawnPos = new Coordinate(targetPosition.getColumn(), currentPosition.getRow());
      Piece capturedPawn = piecePositionMap.remove(capturedPawnPos);
      if (capturedPawn != null) {
        takenPieceMaybe = Optional.of(capturedPawn);
      }
    }

    // Track en passant target: if a pawn double-moves, record the square it skipped
    if (piece instanceof Pawn && Math.abs(targetPosition.getRow() - currentPosition.getRow()) == 2) {
      int skippedRow = (currentPosition.getRow() + targetPosition.getRow()) / 2;
      enPassantTarget = new Coordinate(currentPosition.getColumn(), skippedRow);
    } else {
      enPassantTarget = null;
    }

    // If this was a castling move, also move the rook
    if (piece instanceof King && Math.abs(COLUMNS.get(targetPosition.getColumn()) - COLUMNS.get(currentPosition.getColumn())) == 2) {
      int direction = COLUMNS.get(targetPosition.getColumn()) > COLUMNS.get(currentPosition.getColumn()) ? 1 : -1;
      int rookCol = direction == 1 ? 8 : 1;
      Coordinate rookFrom = new Coordinate(COLUMNS.inverse().get(rookCol), currentPosition.getRow());
      Coordinate rookTo = new Coordinate(incrementAndGetColumn(currentPosition, direction), currentPosition.getRow());
      Piece rook = piecePositionMap.remove(rookFrom);
      piecePositionMap.put(rookTo, rook);
      rook.setHasMoved(true);
    }

    // Pawn promotion: replace pawn with queen when it reaches the back rank
    if (piece instanceof Pawn) {
      int promotionRank = piece.getColor() == PieceColor.WHITE ? 8 : 1;
      if (targetPosition.getRow() == promotionRank) {
        Queen promotedQueen = new Queen(piece.getColor());
        promotedQueen.setHasMoved(true);
        piecePositionMap.put(targetPosition, promotedQueen);
        piece = promotedQueen;
      }
    }

    if (moveExposesCheck(piecePositionMap)) {
      undoPieceMove(currentPosition, targetPosition, piece, takenPieceMaybe);
      throw new RuntimeException("Move exposes check on the king");
    }
    if (moveCausesCheck()) {
      moveResult.isCheck(true);
      if (moveCausesCheckMate()) {
        moveResult.isCheckmate(true);
      }
    } else if (opponentHasNoLegalMoves()) {
      moveResult.isStalemate(true);
    }
    piece.setHasMoved(true);
    endTurn();

    return moveResult.pieceMoved(piece)
        .isSuccess(true)
        .originalPieceLocation(currentPosition)
        .newPieceLocation(targetPosition)
        .pieceTakenMaybe(takenPieceMaybe)
        .build();
  }

  private Optional<Piece> executePieceMove(Coordinate currentPosition, Coordinate targetPosition, Piece piece, Map<Coordinate, Piece> piecePositionMapToUpdate) {
    piecePositionMapToUpdate.remove(currentPosition);
    Optional<Piece> takenPieceMaybe = Optional.ofNullable(piecePositionMapToUpdate.remove(targetPosition));
    piecePositionMapToUpdate.put(targetPosition, piece);
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

  private boolean moveExposesCheck(Map<Coordinate, Piece> currentPiecePositionMap) {
    Player player = COLOR_TO_PLAYER.get(currentTurnPieceColor);
    return moveResultsInCheck(player.getColor(), currentPiecePositionMap, player.getKingPosition());
  }

  private boolean moveCausesCheck() {
    Player player = COLOR_TO_OPPONENT.get(currentTurnPieceColor);
    return moveResultsInCheck(player.getColor(), piecePositionMap, player.getKingPosition());
  }

  private boolean moveCausesCheckMate() {
    return opponentHasNoLegalMoves();
  }

  private boolean opponentHasNoLegalMoves() {
    Player opponent = COLOR_TO_OPPONENT.get(currentTurnPieceColor);
    Multimap<Entry<Coordinate, Piece>, Coordinate> opponentPotentialMovesByPiece = piecePositionMap.entrySet().stream()
        .filter(entry -> entry.getValue().getColor() == opponent.getColor())
        .collect(Multimaps.flatteningToMultimap(entry -> entry,
            entry -> getPotentialMoves(entry.getValue(), entry.getKey(), piecePositionMap).stream(),
            HashMultimap::create));

    for (Entry<Entry<Coordinate, Piece>, Coordinate> entry : opponentPotentialMovesByPiece.entries()) {
      Map<Coordinate, Piece> potentialPiecePositionMap = new HashMap<>(piecePositionMap);
      potentialPiecePositionMap.remove(entry.getKey().getKey());
      potentialPiecePositionMap.remove(entry.getValue());
      potentialPiecePositionMap.put(entry.getValue(), entry.getKey().getValue());
      Coordinate currentKingPosition = entry.getKey().getValue() instanceof King ? entry.getValue() : opponent.getKingPosition();
      if (!moveResultsInCheck(opponent.getColor(), potentialPiecePositionMap, currentKingPosition)) {
        return false;
      }
    }
    return true;
  }

  private boolean moveResultsInCheck(PieceColor pieceColor, Map<Coordinate, Piece> currentPositionMap, Coordinate currentKingPosition) {
    Set<Coordinate> opponentPotentialMoves = currentPositionMap.entrySet().stream()
        .filter(entry -> entry.getValue().getColor() == COLOR_TO_OPPONENT.get(pieceColor).getColor())
        .map(entry -> getPotentialMoves(entry.getValue(), entry.getKey(), currentPositionMap))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    return opponentPotentialMoves.contains(currentKingPosition);
  }

  @VisibleForTesting
  Set<Coordinate> getPotentialMoves(Piece piece, Coordinate currentPosition, Map<Coordinate, Piece> currentPiecePositionMap) {
    Set<Coordinate> potentialMoves = new HashSet<>();
    for (MovementOption movementOption : piece.getMovementOptions()) {
      Coordinate startingPosition = currentPosition;
      Coordinate potentialMove;
      boolean potentialMoveOnBoard;
      boolean potentialMoveOccupied;
      do {
        potentialMove = new Coordinate(COLUMNS.inverse().get(COLUMNS.get(startingPosition.getColumn()) + movementOption.getXMotion()), startingPosition.getRow() + movementOption.getYMotion());
        potentialMoveOnBoard = isOnBoard(potentialMove);
        potentialMoveOccupied = isOccupied(potentialMove, currentPiecePositionMap);
        if (!potentialMoveOnBoard) {
          continue;
        }
        if (movementOption.isRequiresTake() && (!potentialMoveOccupied || !canTake(piece, currentPiecePositionMap.get(potentialMove)))) {
          continue;
        }
        if (potentialMoveOccupied && canTake(piece, currentPiecePositionMap.get(potentialMove))) {
          potentialMoves.add(potentialMove);
        } else if (!potentialMoveOccupied) {
          potentialMoves.add(potentialMove);
        }
        startingPosition = potentialMove;
      } while (movementOption.isRepeating() && !potentialMoveOccupied && potentialMoveOnBoard);
    }
    // En passant
    if (piece instanceof Pawn && enPassantTarget != null) {
      int colDiff = Math.abs(COLUMNS.get(enPassantTarget.getColumn()) - COLUMNS.get(currentPosition.getColumn()));
      int expectedRow = piece.getColor() == PieceColor.WHITE ? 5 : 4; // en passant capture rank
      if (colDiff == 1 && currentPosition.getRow() == expectedRow
          && enPassantTarget.getRow() == (piece.getColor() == PieceColor.WHITE ? 6 : 3)) {
        potentialMoves.add(enPassantTarget);
      }
    }

    // Castling
    if (canCastle(currentPosition, piece, currentPiecePositionMap, 1)) {
      potentialMoves.add(new Coordinate(incrementAndGetColumn(currentPosition, 2), currentPosition.getRow()));
    }
    if (canCastle(currentPosition, piece, currentPiecePositionMap, -1)) {
      potentialMoves.add(new Coordinate(incrementAndGetColumn(currentPosition, -2), currentPosition.getRow()));
    }

    return potentialMoves;
  }

  /**
   * Check if castling is legal in the given direction.
   * @param direction 1 for kingside, -1 for queenside
   */
  private boolean canCastle(Coordinate kingPos, Piece piece, Map<Coordinate, Piece> positionMap, int direction) {
    if (!isKingAndHasNotMoved(piece)) {
      return false;
    }

    // Find the rook position: h-file (col 8) for kingside, a-file (col 1) for queenside
    int rookCol = direction == 1 ? 8 : 1;
    Coordinate rookPos = new Coordinate(COLUMNS.inverse().get(rookCol), kingPos.getRow());
    Piece rookPiece = positionMap.get(rookPos);
    if (rookPiece == null || !isRookAndHasNotMoved(rookPiece)) {
      return false;
    }

    // Check all squares between king and rook are empty
    int kingCol = COLUMNS.get(kingPos.getColumn());
    int startCol = Math.min(kingCol, rookCol) + 1;
    int endCol = Math.max(kingCol, rookCol);
    for (int col = startCol; col < endCol; col++) {
      Coordinate between = new Coordinate(COLUMNS.inverse().get(col), kingPos.getRow());
      if (isOccupied(between, positionMap)) {
        return false;
      }
    }

    // King must not currently be in check
    if (moveResultsInCheck(piece.getColor(), positionMap, kingPos)) {
      return false;
    }

    // King must not pass through check (the square it crosses)
    Coordinate throughSquare = new Coordinate(incrementAndGetColumn(kingPos, direction), kingPos.getRow());
    Map<Coordinate, Piece> throughMap = Maps.newHashMap(positionMap);
    throughMap.remove(kingPos);
    throughMap.put(throughSquare, piece);
    if (moveResultsInCheck(piece.getColor(), throughMap, throughSquare)) {
      return false;
    }

    // King must not land in check (the destination square)
    Coordinate destSquare = new Coordinate(incrementAndGetColumn(kingPos, 2 * direction), kingPos.getRow());
    Map<Coordinate, Piece> destMap = Maps.newHashMap(positionMap);
    destMap.remove(kingPos);
    destMap.put(destSquare, piece);
    if (moveResultsInCheck(piece.getColor(), destMap, destSquare)) {
      return false;
    }

    return true;
  }

  private String incrementAndGetColumn(Coordinate coordinate, int increment) {
    return COLUMNS.inverse().get(COLUMNS.get(coordinate.getColumn()) + increment);
  }

  private boolean isKingAndHasNotMoved(Piece piece) {
    return piece instanceof King && !piece.getHasMoved();
  }

  private boolean isRookAndHasNotMoved(Piece piece) {
    return piece instanceof Rook && !piece.getHasMoved();
  }

  private boolean canTake(Piece piece, Piece pieceToTake) {
    return piece.getColor() != pieceToTake.getColor();
  }

  private boolean isOnBoard(Coordinate coordinate) {
    return COLUMNS.containsKey(coordinate.getColumn()) && ROWS.contains(coordinate.getRow());
  }

  private boolean isOccupied(Coordinate coordinate, Map<Coordinate, Piece> currentPiecePositionMap) {
    return currentPiecePositionMap.containsKey(coordinate);
  }

  private Piece getPieceAtCoordinate(Coordinate coordinate) {
    if (!isOccupied(coordinate, piecePositionMap)) {
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

  @VisibleForTesting
  Piece getPieceAt(Coordinate coordinate) {
    return piecePositionMap.get(coordinate);
  }

  @VisibleForTesting
  void clearAndSetPieces(Map<Coordinate, Piece> pieces) {
    piecePositionMap.clear();
    piecePositionMap.putAll(pieces);
    // Update king positions
    for (Map.Entry<Coordinate, Piece> entry : pieces.entrySet()) {
      if (entry.getValue() instanceof King) {
        COLOR_TO_PLAYER.get(entry.getValue().getColor()).setKingPosition(entry.getKey());
      }
    }
  }

  @VisibleForTesting
  void setCurrentTurn(PieceColor color) {
    currentTurnPieceColor = color;
  }

  @VisibleForTesting
  void setEnPassantTarget(Coordinate target) {
    enPassantTarget = target;
  }

  @VisibleForTesting
  Coordinate getEnPassantTarget() {
    return enPassantTarget;
  }
}
