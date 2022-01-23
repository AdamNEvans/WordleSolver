import java.io.*;
import java.util.*;

public class NoDupesSolver implements WordleSolver
{
	private Guess lastGuess;
	private ArrayList<String> answers;
	private boolean printsEnabled;

	// =================================================================================

	public void initialize(Collection<String> possibleAnswers, boolean enablePrints)
	{
		answers = new ArrayList<String>(possibleAnswers);
		printsEnabled = enablePrints;
	}

	// =================================================================================

	public String getName()
	{
		return "Avoid duplicate letters";
	}

	// =================================================================================

	public boolean hasWon()
	{
		int exactCount = 0;
		LetterComparisonResult[] results = lastGuess.getResults();

		for (int i = 0; i < results.length; i++)
		{
			if (results[i] == LetterComparisonResult.EXACT)
			{
				exactCount++;
			}
		}

		return (exactCount == results.length);
	}

	// =================================================================================

	public String getNextGuessWord()
	{
		for (String word : answers)
		{
			if (Utilities.allCharsUnique(word))
			{
				return word;
			}
		}

		return answers.get(0);
	}

	// =================================================================================

	public void applyGuess(Guess guess)
	{
		if (printsEnabled)
		{
			System.out.print("Processing guess " + guess + " -> ");
		}

		ArrayList<String> remaining = new ArrayList<String>();

		for (String word : answers)
		{
			if (guess.matchesWord(word))
			{
				remaining.add(word);
			}
		}

		answers = remaining;
		lastGuess = guess;

		if (printsEnabled)
		{
			System.out.println("" + answers.size() + " possbilities left:");

			if (answers.size() < 200)
			{
				System.out.println(answers);
			}
			else
			{
				System.out.println("Too many to list");
			}

			System.out.println();
		}
	}

	// =================================================================================
}
