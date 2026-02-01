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
- [ ] Castling: partially implemented, commented out, needs completion
- [ ] Pawn promotion: not implemented
- [ ] En passant: not implemented
- [ ] Stalemate detection: not implemented
- [ ] Save/Restore game: GUI buttons exist but no functionality
- [ ] Debug System.out.printf statements throughout Board.java

## Build & Test

```bash
cd Chess/Chess && mvn test
```

### Remote Session (no network)

When running in a remote/sandboxed session, Maven cannot resolve dependencies (no DNS/network access).
To compile and run tests manually, use the JARs bundled with the system Gradle and Maven installations:

```bash
GUAVA=/opt/gradle-8.14.3/lib/guava-33.4.6-jre.jar
FAILACCESS=/opt/gradle-8.14.3/lib/failureaccess-1.0.3.jar
JUNIT=/opt/gradle-8.14.3/lib/junit-4.13.2.jar
HAMCREST=/opt/gradle-8.14.3/lib/hamcrest-core-1.3.jar
SRC=/home/user/Chess/Chess/src/main/java
TEST=/home/user/Chess/Chess/src/test/java
OUT=/home/user/Chess/Chess/build

# Compile main sources (need temporary stubs for ImmutableMoveResult and org.immutables.value.Value)
mkdir -p $OUT/classes $OUT/test-classes
javac -cp "$GUAVA:$FAILACCESS" -d $OUT/classes <all source files>

# Compile test
javac -cp "$GUAVA:$FAILACCESS:$JUNIT:$HAMCREST:$OUT/classes" -d $OUT/test-classes $TEST/SomeTest.java

# Run test
java -cp "$GUAVA:$FAILACCESS:$JUNIT:$HAMCREST:$OUT/classes:$OUT/test-classes" org.junit.runner.JUnitCore SomeTest
```

**Required temporary stubs** (create before compiling, delete before committing):
- `src/main/java/ImmutableMoveResult.java` — manual builder implementation of the Immutables-generated class
- `src/main/java/org/immutables/value/Value.java` — stub annotation so `MoveResult.java` compiles

**Tests must use JUnit 4 assertions** (`org.junit.Assert`) instead of AssertJ (`assertj` JAR is not available).
Existing tests that use AssertJ won't compile in this environment — only newly written tests using plain JUnit will run.

**Clean up** `$OUT`, the stubs, and any `org/` directory before committing.
