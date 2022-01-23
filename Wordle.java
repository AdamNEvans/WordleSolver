import java.io.*;
import java.util.*;

public class Wordle
{
	public static final int MAX_GUESSES = 6;

	// =================================================================================

	public static void main(String[] args) throws Exception
	{
		Scanner in = new Scanner(System.in);
		GuessStrategy strategy = getStrategy(in);
		ArrayList<String> wordList = loadWords(new File("words5.txt"));
		WordleSolver solver = new WordleSolver(wordList);

		System.out.print("Enter goal word: ");
		System.out.flush();

		String goal = in.nextLine().strip().toLowerCase();
		System.out.println("Goal: '" + goal + "'");

		for (int i = 0; i < MAX_GUESSES; i++)
		{
			System.out.println();
			Guess guess = new Guess(solver.getNextGuessWord(strategy), null);

			if (guess.getWord().equals(goal))
			{
				System.out.println("SUCCESS!!!");
				System.out.println("Took " + (i + 1) + " guesses");
				System.exit(0);
			}

			guess.populateResultsAgainst(goal);
			solver.applyGuess(guess);
		}

		System.out.println();
		System.out.println("FAILED");
		System.out.println("Remaining possibilities:");
		System.out.println(solver.getPossibilities().toString());
	}

	// =================================================================================

	public static GuessStrategy getStrategy(Scanner in)
	{
		GuessStrategy[] strategies = {
			new NoDupesStrategy(),
		};

		while (true)
		{
			System.out.println("What guess strategy should I use?");

			for (int i = 0; i < strategies.length; i++)
			{
				String defaultText = (i == 0 ? " (default)" : "");
				System.out.println("" + i + ": " + strategies[i].getName() + defaultText);
			}

			try
			{
				int choice = Integer.parseInt(in.nextLine());

				if (choice >= 0 && choice < strategies.length)
				{
					return strategies[choice];
				}
			}
			catch (Exception e) {}   // ignore the exception if we fail to parse the int

			System.out.println("ERROR: Invalid strategy");
		}
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
