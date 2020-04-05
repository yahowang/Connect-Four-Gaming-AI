package project8;
import java.util.*;

/**
 * This is the complete Connect 4 game with human playing with an AI.
 * Human player can choose to play first or second.
 * @author YiWang
 */
public class MainProgram {

	/**
	 * The main method runs Connect 4 game and the AI.
	 * @param args standard java main function argument
	 */
	public static void main(String[] args)
	{
		System.out.println("Welcome to Connect 4");
		System.out.println("Using Minimax with Alpha Beta Pruning\n");
		Game game = new Game();
		game.display();
		System.out.println();
		while (true)
		{
			Scanner in = new Scanner(System.in);
			System.out.print("Would you like to play first(enter \"1\") or second(enter \"2\"): ");
			if (in.hasNextInt())
			{
				int humanPlayer = in.nextInt();
				if (humanPlayer == 1)
					break;
				
				else if (humanPlayer == 2)
				{
					game.setFirstPlayer(false);;
					break;
				}
			}
			System.out.println("Invalid input. Please try again.");
		}

		while(true)
		{
			try
			{
				if(game.getTurn() == game.getHuman())
				{
					System.out.println("Human player's turn. Human is '" + game.getHuman() + "'");
					Scanner in = new Scanner(System.in);
					System.out.print("Enter column number 1-7: ");
					Integer col = null;
					if (in.hasNextInt())
						col = in.nextInt() -1;
					else
						throw new InvalidColumnException("Invalid input. Please try again.");
					game.placeDisc(col);
				}
				else
				{
					System.out.println("AI's turn. AI is '" + game.getAI() + "'");
					GameAI ai = new GameAI();
					System.out.println("Calculating...");
					System.out.println("The first few steps may process for a few seconds.");
					int chosenColumn = ai.chooseColumn(game);
					System.out.println("Done");
					System.out.println("AI chooses COLUMN " + (chosenColumn + 1));
					game.placeDisc(chosenColumn);
				}
				System.out.println();
				game.display();
				System.out.println("\n");
			}
			catch(InvalidColumnException e)
			{
				System.out.println(e.getMessage());
				System.out.println();
			}
			catch(GameOverException e)
			{
				System.out.println();
				game.display();
				System.out.println();
				System.out.println(e.getMessage());
				break;
			}
		}
		System.out.println("Thank you. Goodbye!");
	}
}
