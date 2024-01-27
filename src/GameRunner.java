import java.util.InputMismatchException;
import java.util.Scanner;

class GameRunner {
	public static void main(String[] args) {
		System.out.print("""
				Welcome to the Legendary Minesweeper game!

				About the game:
				Each game starts with a block containing a grid of squares which have mines embedded in them,
				your job in short terms is to NOT PRESS A MINE. Basically, when you click a square it will
				reveal whether it consists a mine or is it a safe one. If it is a safe one, then it will reveal
				a number denoting the number one mines this square is in contact with (its neighbouring 8
				blocks). using that number you have to decide your next step. To win the game you need to safely
				press and reveal all the blocks which do not have a mine in them.
				
				To play the game you need to input the row and column number which you need to press.
				Columns go from 1 - Size of Board, horizontally, left to right.
				Rows go from 1 - Size of Board, vertically, top to bottom.
				
				So, to click a block which is 2nd from left and 3rd from top. You need to input
				Row - 3
				Col - 2
				
				Enjoy the game!
				""");

		Scanner sc = new Scanner(System.in);
		boolean restartGame = true;

		while (restartGame) {
			System.out.print("\nEnter board Size (> 3) : ");
			int boardSize = 0;

			try {
				boardSize = sc.nextInt();
				if (boardSize < 4)
					continue;
			} catch (InputMismatchException e) {
				System.out.println("Invalid Input ! Restart Program");
				System.exit(1);
			}

			Minesweeper mine = new Minesweeper(boardSize);
			int row = 0;
			int col = 0;

			while (mine.shouldGameContinue) {
				System.out.println("-".repeat(40) + "\n");
				System.out.println(mine);

				try {
					System.out.print("Enter Row : ");
					row = sc.nextInt();
					System.out.print("Enter Col : ");
					col = sc.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("Invalid Input! Restart Program");
					System.exit(1);
				}

				System.out.println("-".repeat(40) + "\n");
				System.out.print(mine.processInput(row - 1, col - 1));
			}

			System.out.print("\n\nAnother game? (Y | N) : ");
			char inp = sc.next().toLowerCase().charAt(0);

			restartGame = inp == 'y';
		}

		System.out.println("Goodbye!");
	}
}