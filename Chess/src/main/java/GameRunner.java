import java.util.Scanner;

public class GameRunner {


  public static void main(String[] args) {
    Board board = new Board();
    while(true) {
      Scanner sc = new Scanner(System.in);
      System.out.println("Enter piece coordinate");
      String pieceCoordinateString = sc.nextLine();
      System.out.println("Enter target coordinate");
      String targetCoordinateString = sc.nextLine();

      Coordinate pieceCoordinate = new Coordinate(pieceCoordinateString.substring(0, 1), Integer.parseInt(pieceCoordinateString.substring(1, 2)));
      Coordinate targetCoordinate = new Coordinate(targetCoordinateString.substring(0, 1), Integer.parseInt(targetCoordinateString.substring(1, 2)));

      try {
        board.movePiece(pieceCoordinate, targetCoordinate);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
