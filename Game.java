package project8;

/** A customized Exception class dealing with invalid columns
 */
class InvalidColumnException extends IllegalArgumentException
{
	/** Default constructor */
	public InvalidColumnException() {}
	
	/** Constructor with message 
	 * @param message the error message */
	public InvalidColumnException(String message) {super(message);}
}

/**
 * A customized Exception class dealing with game over scenarios
 */
class GameOverException extends IllegalArgumentException
{
	/** Default constructor */
	public GameOverException() {}
	
	/** Constructor with message
	 * @param message the error message */
	public GameOverException(String message) {super(message);};
}

/** 
 * This class represents the game logic and the UI of Connect 4.
 * It is a 6 row x 7 column board game.
 * @author YiWang
 */
public class Game {
	private char FIRST;							// who is the first player
	private char TURN;							// who should place the disc now
	private char[][] board;						// 2-D array of char representing the board
	private static final char EMPTY = ' ';		// an empty cell on the board
	private static final char AI = 'O';			// AI's disc
	private static final char HUMAN = 'X';		// Human's disc
	private static final int ROW = 6;			// number of rows of the board
	private static final int COLUMN = 7;			// number of columns of the board
	
	/**
	 * Default constructor, setting human playing first and AI playing second
	 */
	public Game()
	{
		TURN = HUMAN;
		FIRST = TURN;
		board = new char[ROW][COLUMN];
		for (int i = 0; i < ROW; ++i)
		{
			char[] rowArray = new char[COLUMN];
			for (int j = 0; j < COLUMN; ++j)
				rowArray[j] = EMPTY;
			board[i] = rowArray;
		}
	}
	
	/**
	 * Set who plays first. Should be called in the very beginning of the game.
	 * @param humanFirst true if human player wants to play first, otherwise AI plays first
	 */
	public void setFirstPlayer(boolean humanFirst)
	{
		TURN = humanFirst? HUMAN:AI;
		FIRST = TURN;
	}
	
	/**
	 * Return the current board
	 * @return the current board
	 */
	public char[][] getBoard()
	{
		return board;
	}
	
	/**
	 * Return who is the first player
	 * @return the first player
	 */
	public char getFirst()
	{
		return FIRST;
	}
	
	/**
	 * Get the current player
	 * @return char representing the current player
	 */
	public char getTurn()
	{
		return TURN;
	}
	
	/**
	 * Get the HUMAN disc
	 * @return char representing HUMAN
	 */
	public static char getHuman()
	{
		return HUMAN;
	}
	
	/**
	 * Get the AI disc
	 * @return char representing AI
	 */
	public static char getAI()
	{
		return AI;
	}
	
	/**
	 * Get the EMPTY cell
	 * @return char representing EMPTY
	 */
	public static char getEmpty()
	{
		return EMPTY;
	}
	
	/**
	 * Get the total number of rows
	 * @return the total number of rows
	 */
	public static int getRow()
	{
		return ROW;
	}
	
	/**
	 * Get the total number of columns
	 * @return the total number of columns
	 */
	public static int getColumn()
	{
		return COLUMN;
	}

	/**
	 * Display the current game board to the console.
	 */
	public void display()
	{
		for (char[] rowArray: board)
		{
			System.out.print("|");
			for (char cell: rowArray)
				System.out.print(cell + "|");
			System.out.println();
		}
		System.out.println("---------------");	
		System.out.println(" 1 2 3 4 5 6 7");
	}
	
	/**
	 * place a disc to the column. InvalidColumnException will be thrown
	 * if the column is out of bounds or the column is full
	 * @param col the column the current player wants to drop. Assume to be in the range between 0 to 6 inclusively.
	 */
	public void placeDisc(int col) throws InvalidColumnException, GameOverException
	{
		if (col < 0 || col > COLUMN - 1)
			throw new InvalidColumnException("Invalid Column Index");
		for (int row = ROW - 1; row > -1; --row)
		{
			if(board[row][col] == EMPTY)
			{
				board[row][col] = TURN;
				int gameOver = isGameOver(board);
				if (gameOver > -1)
				{
					if(gameOver == 1)
					{
						if (TURN  == HUMAN)
							throw new GameOverException("Game Over! The winner is Human Player.");
						throw new GameOverException("Game Over! The winner is AI.");
					}
					throw new GameOverException("Game Over! It is a tie.");
				}
				flipTurn();
				return;
			}
		}
		throw new InvalidColumnException("Column is already full.");
	}

	/**
	 * Determine whether the game is over on the game board
	 * @param board representing the game board
	 * @return 0 if the game board is full; 1 if current player wins; -1 if the game is not over
	 */
	public static int isGameOver(char[][] board)
	{
		for(int i = 0; i < ROW; ++i)
			for(int j = 0; j< COLUMN; ++j)
				if(numberLinked(i, j, board) != 0)
					return 1;
		if (isBoardFull(board))
			return 0;
		return -1;
	}

	/**
	 * Helper method used for changing the turn of the game.
	 */
	private void flipTurn()
	{
		if (TURN == HUMAN)
			TURN = AI;
		else
			TURN = HUMAN;
	}
	
	/**
	 * Helper method used for determining whether the board is full or not
	 * @param board char[][] representing the game board
	 * @return true if the board is full, otherwise false.
	 */
	private static boolean isBoardFull(char[][] board)
	{
		for (char topCell: board[0])
			if (topCell == EMPTY)
				return false;
		return true;
	}
	
	/**
	 * Helper method used for determining how many directions of 
	 * the current position is linked (same 4 player's discs) on the game board.
	 * There are 8 possible directions for any position.
	 * @param row the row of the current position
	 * @param col the column of the current position
	 * @param board char[][] representing the game board
	 * @return the number of links of the current position
	 */
	private static int numberLinked(int row, int col, char[][] board)
	{
		int result = 0;
		if (isDirectionLinked(board, row, col, 1, 1))
			result += 1;
		if (isDirectionLinked(board, row, col, 1, 0))
			result += 1;
		if (isDirectionLinked(board, row, col, 1, -1))
			result += 1;
		if (isDirectionLinked(board, row, col, 0, 1))
			result += 1;
		if (isDirectionLinked(board, row, col, 0, -1))
			result += 1;
		if (isDirectionLinked(board, row, col, -1, 1))
			result += 1;
		if (isDirectionLinked(board, row, col, -1, 0))
			result += 1;
		if (isDirectionLinked(board, row, col, -1, -1))
			result += 1;
		return result;
	}
	
	/**
	 * Helper method used for checking if there are same 4 discs linked at specified direction
	 * @param board char[][] representing the game board
	 * @param row the row of the current position
	 * @param col the column of the current position
	 * @param delRow row direction, should be -1, 0, or 1
	 * @param delCol column direction, should be -1, 0, or 1
	 * @return true if there are 4 discs linked, otherwise false
	 */
	private static boolean isDirectionLinked(char[][] board, int row, int col, int delRow, int delCol)
	{
		int actualLinked = 0;
		for (int i = row, j = col; i > -1 && j > -1 && i < ROW && j < COLUMN && actualLinked < 4; i += delRow, j += delCol, ++actualLinked)
			if (board[i][j] == EMPTY || board[i][j] != board[row][col])
				return false;
		if (actualLinked == 4)
			return true;
		return false;
	}
}
