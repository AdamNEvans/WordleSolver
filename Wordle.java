import java.io.*;
import java.util.*;

public class Wordle
{
	public static final int MAX_GUESSES = 6;

	// =================================================================================

	public static void main(String[] args) throws Exception
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Is the goal word known?");

		String answer = in.nextLine();
		boolean autoplay = (answer.equals("y") || answer.equals("yes"));
		WordleSolver solver = getSolver(in);

		if (autoplay)
		{
			autoplayKnownGoal(solver);
		}
		else
		{
			playInteractiveWithUnknownGoal(solver);
		}
	}

	// =================================================================================

	public static void autoplayKnownGoal(WordleSolver solver) throws Exception
	{
		Scanner in = new Scanner(System.in);

		System.out.print("Enter goal word: ");
		System.out.flush();

		String goal = in.nextLine().strip().toLowerCase();
		System.out.println("Goal: '" + goal + "'");

		for (int i = 0; i < MAX_GUESSES; i++)
		{
			Guess guess = new Guess(solver.getNextGuessWord(), null);
			guess.populateResultsAgainst(goal);
			solver.applyGuess(guess);

			if (solver.hasWon())
			{
				System.out.println("SUCCESS!!!");
				System.out.println("Took " + (i + 1) + " guesses");
				System.exit(0);
			}
		}

		System.out.println();
		System.out.println("FAILED");
	}

	// =================================================================================

	public static void playInteractiveWithUnknownGoal(WordleSolver solver) throws Exception
	{
		Scanner in = new Scanner(System.in);

		System.out.println();
		System.out.println("When entering guess results, enter one character per letter, using the key:");
		System.out.println("  '.' -> letter is not in the goal word (gray letter)");
		System.out.println("  '#' -> letter is in the correct position (green letter)");
		System.out.println("  '?' -> letter is in the wrong position (yellow letter)");
		System.out.println();

		for (int i = 0; i < MAX_GUESSES; i++)
		{
			String recommendedGuessWord = solver.getNextGuessWord();
			System.out.println("Recommended guess: '" + recommendedGuessWord + "'");
			System.out.print("Enter your guess: ");
			System.out.flush();
			String guessWord = in.nextLine();

			System.out.print("Enter the result: ");
			System.out.flush();
			String resultString = in.nextLine();
			Guess guess = new Guess(guessWord, Utilities.resultsFromString(resultString));

			solver.applyGuess(guess);

			if (solver.hasWon())
			{
				System.out.println("SUCCESS!");
				break;
			}
		}
	}

	// =================================================================================

	public static WordleSolver getSolver(Scanner in) throws Exception
	{
		// first solver is default
		WordleSolver[] solvers = {
			new NoDupesSolver(),
		};

		int choice = 0;

		while (true)
		{
			System.out.println("What solver/guess strategy should I use?");

			for (int i = 0; i < solvers.length; i++)
			{
				String defaultText = (i == 0 ? " (default)" : "");
				System.out.println("" + i + ": " + solvers[i].getName() + defaultText);
			}

			try
			{
				String input = in.nextLine();

				if (input.equals(""))
				{
					// use the default solver
					choice = 0;
					break;
				}
				else
				{
					choice = Integer.parseInt(input);

					if (choice >= 0 && choice < solvers.length)
					{
						// valid choice
						break;
					}
				}
			}
			catch (Exception e) {}   // ignore the exception if we fail to parse the int

			System.out.println("ERROR: Invalid solver");
		}

		// initialize the chosen solver
		if (choice == 0)
		{
			NoDupesSolver solver = (NoDupesSolver)solvers[choice];
			solver.initialize(loadWords(new File("words5.txt")), true);
		}

		return solvers[choice];
	}

	// =================================================================================

	public static ArrayList<String> loadWords(File file) throws Exception
	{
		Scanner in = new Scanner(file);
		ArrayList<String> words = new ArrayList<String>(20000);
		
		while (in.hasNextLine())
		{
			words.add(in.nextLine().strip().toLowerCase());
		}

		return words;
	}

	// =================================================================================
	// use for debugging with custom lists
	public static ArrayList<String> loadWords2(File unused)
	{
		ArrayList<String> words = new ArrayList<String>();
		words.add("abhor");
		words.add("abide");
		words.add("abmho");
		words.add("abnet");
		words.add("abbey");

		return words;
	}

	// =================================================================================
}
