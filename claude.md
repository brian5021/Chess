# Chess Project - Development Guidelines

## Development Workflow: Test-Driven Development (TDD)

All bug fixes and new features should follow the Red-Green-Refactor cycle:

1. **Red** - Write a test that demonstrates the bug or specifies the new behavior. Run it to confirm it fails.
2. **Green** - Make the minimal code change to make the test pass. Run the test to confirm it passes.
3. **Refactor** - Clean up if needed, re-run tests to ensure nothing broke.

### Commit Strategy

- Each fix/feature should be a single commit containing both the test and the code change.
- Commit message should describe what was fixed/added and why.

## Project Structure

- `Chess/Chess/` - Maven project root
- `src/main/java/` - Source code (Board, pieces, GUI, etc.)
- `src/test/java/` - Tests (JUnit 4 + AssertJ)
- Tests use `Board.getPotentialMoves()` with isolated piece position maps for unit testing movement logic.

## Testing Conventions

- Test class per piece type (e.g., `KingTest.java`, `PawnTest.java`)
- `BoardTest.java` for integration-level game logic tests (check, checkmate, etc.)
- Use `Maps.newHashMap()` for empty boards when testing piece movement in isolation.
- Use `Coordinate.from("e4")` for board positions.
- AssertJ for assertions (`assertThat(...)`).

## Known Issues to Fix

- [x] King.java: UP_LEFT movement is (-1, -1) but should be (-1, 1) — duplicates DOWN_LEFT
- [x] Castling: rewritten canCastle() for kingside and queenside, with rook movement (11 tests)
- [x] Pawn promotion: auto-promotes to queen on back rank (3 tests)
- [x] En passant: target tracking, capture logic, move generation (6 tests)
- [x] Stalemate detection: draw when opponent has no legal moves but not in check (3 tests)
- [x] Save/Restore game: serialize/deserialize board state, GUI file dialogs (7 tests)
- [x] Debug System.out.printf statements throughout Board.java — all removed

## Build & Test

```bash
cd Chess/Chess && mvn test
```
