import java.util.Arrays;
import java.util.Random;

public class Minesweeper {

	private final int BOARD_SIZE;
	private final byte[][] INTERNAL_BOARD; // for internal of the game, to store mines and proximity count
	public final char[][] GAME_BOARD; // for display to the user
	public boolean shouldGameContinue; // to check weather game should continue or not.
	private boolean isFirstStep; // to check if it is the first step of the user.

	Minesweeper (int boardSize) {
		this.BOARD_SIZE = boardSize;
		this.INTERNAL_BOARD = new byte[boardSize][boardSize];
		this.GAME_BOARD = new char[boardSize][boardSize];
		this.shouldGameContinue = true;
		this.isFirstStep = true;

		for (char[] line : GAME_BOARD) {
			Arrays.fill(line, '■');
		}
	}

	private void generateBoard (int row, int col) {
		safeguardPosition (row, col);
		fillBombs ();
		fillProximity ();
	}

	// to safeguard this tile and all of its neighbouring tiles.
	private void safeguardPosition (int row, int col) {
		for (int xOffset = -1; xOffset <= 1; xOffset ++) {
			for (int yOffset = -1; yOffset <= 1; yOffset ++) {
				int xPos = row + yOffset;
				int yPos = col + xOffset;

				if (xPos < 0 || xPos >= BOARD_SIZE || yPos < 0 || yPos >= BOARD_SIZE)
					continue;

				INTERNAL_BOARD[xPos][yPos] = 101; // adding an indicator to not plant a bomb in these blocks.
			}
		}
	}

	// to fill in bombs at random places in the board.
	private void fillBombs () {
		int remainingBombCount = BOARD_SIZE;
		Random random = new Random();

		while (remainingBombCount > 0) {
			int x = random.nextInt(BOARD_SIZE);
			int y = random.nextInt(BOARD_SIZE);

			if (INTERNAL_BOARD[x][y] == -1 || INTERNAL_BOARD[x][y] == 101)
				continue; // bomb was placed already or position is safe

			INTERNAL_BOARD[x][y] = -1; // bomb now placed
			remainingBombCount --;
		}
	}

	// to fill in bomb proximity in all the remaining blocks.
	private void fillProximity () {
		for (int row = 0; row < BOARD_SIZE; row ++) {
			for (int col = 0; col < BOARD_SIZE; col ++) {
				if (INTERNAL_BOARD[row][col] == -1)
					continue; // skipping if block is a bomb

				byte bombCount = calculateBombProximity (row, col);
				INTERNAL_BOARD[row][col] = bombCount; // otherwise filling in the proximity
			}
		}
	}

	// helper function to calculate proximity of bombs
	private byte calculateBombProximity (int row, int col) {
		int closeBombCount = 0;

		for (int rowShift = -1; rowShift <= 1; rowShift ++) {
			for (int colShift = -1; colShift <= 1; colShift++) {
				if (rowShift == 0 && colShift == 0)
					continue; // if checking position is current block

				int xPosition = row + rowShift;
				int yPosition = col + colShift;

				// if the checking position is out of bound.
				if (xPosition < 0 || xPosition >= BOARD_SIZE || yPosition < 0 || yPosition >= BOARD_SIZE)
					continue;

				// if bomb is placed at checking position.
				closeBombCount += INTERNAL_BOARD[xPosition][yPosition] == -1 ? 1 : 0;
			}
		}

		return (byte) closeBombCount;
	}

	// to process user input and return result accordingly
	public String processInput (int row, int col) {
		if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE)
			return "Invalid Input : Enter valid coordinates! \n\n";

		if (isFirstStep) {
			generateBoard (row, col);
			isFirstStep = false;
		}

		if (Character.isDigit(GAME_BOARD[row][col]) || GAME_BOARD[row][col] == ' ') // each revealed block will have value b/w 0 - 9 or a space.
			return "Invalid Input : Block already revealed! \n\n";

		if (INTERNAL_BOARD[row][col] == -1) { // selected block is a bomb
			shouldGameContinue = false;
			return "GAME OVER! You pressed a mine! \n\n" + printInternalMine() + "\n" + "-".repeat(40);
		}

		else {
			updateGameField (row, col);
			if (checkWinningStatus ()) {
				shouldGameContinue = false;
				return "Congratulations, you won the game! \n\n\n" + printInternalMine();
			}
			return "\n\n";
		}
	}

	// to update the Game Field after user has provided an input.
	private void updateGameField (int row, int col) {
		// if block is unreachable, then return
		if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE)
			return;

		// if block is a bomb, then return
		if (INTERNAL_BOARD[row][col] == -1)
			return;

		// if the field was already cleared there, then all of its surrounding has already cleared so return.
		if (GAME_BOARD[row][col] == ' ')
			return;

		// if it is non-zero number, then make it visible in the game board and return.
		if (INTERNAL_BOARD[row][col] != 0) {
			GAME_BOARD[row][col] = (char) ('0' + INTERNAL_BOARD[row][col]);
			return;
		}

		// if it is a zero field, then again call this function in all of its 8 neighbours.
		GAME_BOARD[row][col] = ' ';

		for (int rowShift = -1; rowShift <= 1; rowShift ++) {
			for (int colShift = -1; colShift <= 1; colShift++) {
				if (rowShift == 0 && colShift == 0)
					continue; // if position is current block.

				int xPosition = row + rowShift;
				int yPosition = col + colShift;

				updateGameField(xPosition, yPosition);
			}
		}
	}

	// to check if the user won the game or game should continue.
	private boolean checkWinningStatus () {
		int nonRevealedCount = 0;

		for (char[] line : GAME_BOARD)
			for (char c : line)
				nonRevealedCount += c == '■' ? 1 : 0;

		// checking if the boxes which are not revealed are equal to bombCount.
		return nonRevealedCount == BOARD_SIZE;
	}

	// for printing the solution after the game is over by losing.
	private String printInternalMine () {
		StringBuilder sb = new StringBuilder();

		for (byte[] line : INTERNAL_BOARD) {
			for (byte block : line)
				sb.append("| ").append(block == -1 ? "x" : block).append(" ");
			sb.append("|").append("\n");
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (char[] line : GAME_BOARD) {
			for (char block : line)
				sb.append("| ").append(block).append(" ");
			sb.append("|").append("\n");
		}

		return sb.toString();
	}
}