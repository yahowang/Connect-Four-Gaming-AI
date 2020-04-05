package project8;
import java.util.*;

/**
 * This class is the Connect 4 AI.
 * It uses minimax algorithm with alpha-beta pruning
 * as game strategies. The max depth to look up is 9.
 * @author YiWang
 */
public class GameAI {
	private static final char AI = Game.getAI();			// AI's disc
	private static final char HUMAN =  Game.getHuman();	// Human's disc
	private static final char EMPTY = Game.getEmpty();	// an empty cell on the board
	private static final int ROW = Game.getRow();			// number of rows of the board
	private static final int COLUMN = Game.getColumn();	// number of columns of the board
	private static final int MAXDEPTH = 9;				// How deep should the minimax with alpha-beta pruning go
	
	/**
	 * Default constructor
	 */
	public GameAI(){}
	
	/**
	 * AI chooses a column to place its disc according to the game
	 * @param game connect four Game object
	 * @return the column where the AI chooses to place it disc
	 */
	public int chooseColumn(Game game)
	{
		char[][] board = game.getBoard();
		int numDisc = numberDisc(board);
		if (numDisc < (game.getFirst() == AI? 4 : 3))			// try to occupy the middle column
			return COLUMN/2;
		if (numDisc == 4 && board[ROW-4][COLUMN/2] != EMPTY)	// special case when the first 4 discs stack in the middle
			return COLUMN/2;	
		if (numDisc == 6 && board[ROW-5][COLUMN/2] != EMPTY && game.getFirst() == AI)	// special case when the first 5 discs stack in the middle
		{
			int count = 0;
			char player = AI;
			for(int i = ROW-1; i > ROW-6; --i)
			{
				if (board[i][COLUMN/2] != player)
					break;
				count++;
				player = player == AI? HUMAN:AI;
			}
			if (count == 5)
			{
				int specialMove = specialCaseMove(board);
				if (specialMove != -1)
					return specialMove;
			}
		}
		return (int) minMaxBoard(board, numDisc, MAXDEPTH, AI, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Helper method.
	 * Recursively find the min score or max score of the game board.
	 * If the depth is MAXDEPTH - 1, it will return the best column to place.
	 * Otherwise it returns the max score if the current player is AI,
	 * or it returns the min score if the current player is HUMAN.
	 * This method also uses alpha-beta pruning for boosting the minimax algorithm.
	 * 
	 * @param board the current game board
	 * @param numDisc number of discs on the current game board
	 * @param depth how deep should the AI consider
	 * @param player who needs to place a disc on the current board now
	 * @param alpha should set to Integer.MIN_VALUE initially
	 * @param beta should set to Integer.MAX_VALUE initially
	 * @return the best column to place if depth == MAXDEPTH-1, otherwise returns the best score according to the player.
	 */
	private long minMaxBoard(char[][] board, int numDisc, int depth, char player, long alpha, long beta)
	{
		depth--;		// In a deeper level
		numDisc++;
		long bestScore;
		// initialize the bestScore
		if (player == AI)
			bestScore = alpha;
		else
			bestScore = beta;
		// record the score for each possible column chosen by the current player
		ArrayList<Integer> columns = new ArrayList<Integer>();
		ArrayList<Long> scores = new ArrayList<Long>();
		ArrayList<Integer> legalMoves = legalMove(board);
		if (depth == MAXDEPTH - 1)
		{
			// quick access to winning states
			for (int col: legalMoves)
				if (Game.isGameOver(copyMutateBoard(board, col, player)) == 1)  // Winning happens in the current player's very next move
					return col;		// the winning column
		}
		for (int col: legalMoves)	// each legal column
		{
			char[][] childBoard = copyMutateBoard(board, col, player); // the sub board
			columns.add(col);
			 // gameOver meaning: -1 means game is not over; 0 means a draw; 1 means current player wins
			int gameOver = Game.isGameOver(childBoard);
			if (gameOver > -1 || depth == 0)
			{
				if (gameOver == 1)	// someone wins
				{
					if (player == AI)
						return Integer.MAX_VALUE - numDisc; // the best score of AI taking account of number of discs
					else
						return Integer.MIN_VALUE + numDisc; // the best score of HUMAN taking account of number of discs
				}
				if (gameOver == 0)  // The game ends with a draw
					return 0;
				if (depth == 0)		// The game is not over, simply reached the specified depth
				{
					long childBoardScore = evaluateBoard(childBoard);  // The score of the sub-board
					if (childBoardScore > bestScore)  // if the new score is better than the old best score
					{
						bestScore = childBoardScore;
						alpha = bestScore;
					}
					if (alpha >= beta)	// for alpha beta pruning
						return bestScore; // do not need to compute siblings.
					scores.add(childBoardScore); // no pruning happened, record the sub-board score
					continue;
				}
			}
			long childBoardScore;
			// not at the leaf
			if (player == AI)
			{	
				// get the sub-board's best child's score
				childBoardScore = minMaxBoard(childBoard, numDisc, depth, HUMAN, alpha, beta);

				if (childBoardScore > bestScore) // if the new score is better than the old best score
				{
					bestScore = childBoardScore;
					alpha = bestScore;
				}
				if (alpha >= beta && depth != MAXDEPTH -1) // for alpha-beta pruning
					return bestScore;
			}
			else
			{
				// get the sub-board's best child's score
				childBoardScore = minMaxBoard(childBoard, numDisc, depth, AI, alpha, beta);
				if (childBoardScore < bestScore) // if the new score is better than the old best score
				{
					bestScore = childBoardScore;
					beta = bestScore;
				}
				if (alpha >= beta && depth != MAXDEPTH -1) // for alpha-beta pruning
					return bestScore;
			}
			scores.add(childBoardScore);
		}
		if (depth == MAXDEPTH -1) // the very next position of the current player
			return bestColumn(scores, columns); // find the best column to choose
		if (player == AI)
			return Collections.max(scores);  // find the max score among the sub-boards
		return Collections.min(scores);		// find the min score among the sub-boards
	}

	/**
	 * Helper method. This method deals with a special case when there are only 6 discs on the board
	 * and 5 of them are lined-up only in the middle with the order of AI-HUMAN-AI-HUMAN-AI from the bottom of the board.
	 * @param board the current game board
	 * @return the column that the AI should prefer to drop its disc. -1 means special case does not apply.
	 */
	private int specialCaseMove(char[][] board)
	{
		if (board[ROW-1][1] != EMPTY)
			return 1;
		if (board[ROW-6][COLUMN/2] != EMPTY)
			return 4;
		int[] choose4 = {0, 4, 6};
		for (int col: choose4)
			if (board[ROW-1][col] != EMPTY)
				return 4;
		int[] choose5 = {2, 5};
		for (int col: choose5)
			if (board[ROW-1][col] != EMPTY)
				return 5;
		return -1;
	}

	/**
	 * Helper method. Count the number of discs on the board
	 * @param board the game board
	 * @return the number of discs on the board
	 */
	private int numberDisc(char[][] board)
	{
		int result = 0;
		for (char[] rowArray: board)
			for(char cell: rowArray)
				if(cell != EMPTY)
					result++;
		return result;
	}
	
	/**
	 * Helper method. Evaluate the performance of the board.
	 * @param board the game board
	 * @return the number of possible links for AI minus the number of possible links for HUMAN
	 */
 	private long evaluateBoard(char[][] board)
	{
		long linked = 0;
		for (int i = 0; i < ROW; ++i)
			for (int j = 0; j < COLUMN; ++j)
			{
				if (board[i][j] != EMPTY)
				{
					if (board[i][j] == AI)
					{
						linked += directionLinked(board, i, j, 1, 1);
						linked += directionLinked(board, i, j, 1, 0);
						linked += directionLinked(board, i, j, 1, -1);
						linked += directionLinked(board, i, j, 0, 1);
						linked += directionLinked(board, i, j, 0, -1);
						linked += directionLinked(board, i, j, -1, 1);
						linked += directionLinked(board, i, j, -1, 0);
						linked += directionLinked(board, i, j, -1, -1);
					}
					else
					{
						linked -= directionLinked(board, i, j, 1, 1);
						linked -= directionLinked(board, i, j, 1, 0);
						linked -= directionLinked(board, i, j, 1, -1);
						linked -= directionLinked(board, i, j, 0, 1);
						linked -= directionLinked(board, i, j, 0, -1);
						linked -= directionLinked(board, i, j, -1, 1);
						linked -= directionLinked(board, i, j, -1, 0);
						linked -= directionLinked(board, i, j, -1, -1);
					}
				}
			}
		return linked;	
	}

 	/**
 	 * Helper method. Calculate if there is a possible link of the position in interest at a specified direction on the board 
 	 * @param board the game board
 	 * @param row the position in interest
 	 * @param col the position in interest
 	 * @param delRow the row direction; should be -1, 0, or 1
 	 * @param delCol the column direction; should be -1, 0, or 1
 	 * @return 1 if it is possible to have this direction linked; Integer.MAX_VALUE if this direction is a winning direction; otherwise 0.
 	 */
	private long directionLinked(char[][] board, int row, int col, int delRow, int delCol)
	{
		int k = 0;
		int actual = 0;
		int horizontalSpecial = 0;
		for (int i = row, j = col; isInBound(i, j) && k < 4; i += delRow, j += delCol, ++k)
		{
			if (board[i][j] != EMPTY && board[i][j] != board[row][col])
				return 0;
			if (board[i][j] == board[row][col])
			{
				actual++;
				if (delRow == 0 && k == 2 && actual == 3 && isInBound(row, col-delCol) && isInBound(i, j+delCol) && board[row][col-delCol] == EMPTY && board[i][j+delCol] == EMPTY)
					horizontalSpecial += row + 1; // Taking account of odd even row advantage
			}
		}
		if (k == 4)
			return actual == 4? Integer.MAX_VALUE : (1 + horizontalSpecial);
		return 0;
	}

	/**
	 * Helper method. Determine if a position is in bound
	 * @param row the row coordinate
	 * @param col the column coordinate
	 * @return true if the position if in bound, otherwise false.
	 */
	private boolean isInBound(int row, int col)
	{
		return row > -1 && col > -1 && row < ROW && col < COLUMN;
	}
	
	/**
	 * Helper method. get the best column for the AI to choose
	 * @param scores the scores of the possible sub-boards
	 * @param columns the columns to reach the possible sub-boards
	 * @return the best column for the AI
	 */
	private int bestColumn(ArrayList<Long> scores, ArrayList<Integer> columns)
	{
		long resultScore = scores.get(0);
		int resultColumn = columns.get(0);
		for (int i = 0; i < scores.size(); ++i)
		{
			if (scores.get(i) > resultScore)
			{
				resultScore = scores.get(i);
				resultColumn = columns.get(i);
			}
		}
		return resultColumn;
	}

	/**
	 * Helper method. Get all legal columns to choose on the current game board
	 * @param board the game board
	 * @return an ArrayList of Integer representing the legal columns
	 */
	private ArrayList<Integer> legalMove(char[][] board)
	{
		ArrayList<Integer> result = new ArrayList<Integer>(0);
		int[] order = {3, 2, 4, 0, 6, 1, 5};
		for (int j: order)
			if (board[0][j] == EMPTY)
				result.add(j);
		return result;
	}

	/**
	 * Helper method. place a disc to the copy of the board and return the mutated board.
	 * @param board the current game board
	 * @param columnToPlace a legal column to place a disc
	 * @param player the current player
	 * @return a copy of the board with a new disc on the specified column
	 */
	private char[][] copyMutateBoard(char[][] board, int columnToPlace, char player)
	{
		boolean placed = false;
		char[][] copyBoard = new char[ROW][COLUMN];
		for (int i = ROW - 1; i > -1; --i)
		{
			char[] copyRow = new char[COLUMN];
			for (int j = 0; j < COLUMN; ++j)
			{
				copyRow[j] = board[i][j];
				if (j == columnToPlace && !placed && copyRow[j] == EMPTY)
				{
					copyRow[j] = player;
					placed = true;
				}
			}
			copyBoard[i] = copyRow;
		}
		return copyBoard;
	}
}
