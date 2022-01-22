import java.io.*;
import java.util.*;

public class WordleSolver
{
	private ArrayList<String> answers = new ArrayList<String>();  // possible answers given the guess history
	private ArrayList<Guess> guesses = new ArrayList<Guess>();    // guess history

	// =================================================================================

	public WordleSolver(ArrayList<String> words)
	{
		answers = new ArrayList<String>(words);
	}

	// =================================================================================
	// Requires guess.results to be filled out
	public void applyGuess(Guess guess)
	{
		System.out.println("====================================================================");
		System.out.println("Processing guess " + guess);

		ArrayList<String> remaining = new ArrayList<String>();

		for (String word : answers)
		{
			if (guess.matchesWord(word))
			{
				remaining.add(word);
			}
		}

		answers = remaining;
		guesses.add(guess);

		System.out.println();
		System.out.println("" + answers.size() + " possbilities left:");

		if (answers.size() < 200)
		{
			System.out.println(answers);
		}
		else
		{
			System.out.println("Too many to list");
		}
	}

	// =================================================================================

	public String getNextGuessWord(GuessStrategy strategy)
	{
		if (answers.size() == 0)
		{
			System.out.println("ERROR: no more possibilities... there's a bug, you idiot");
			System.exit(1);
		}

		return strategy.getBestGuess(answers, guesses);
	}

	// =================================================================================

	public final ArrayList<String> getPossibilities()
	{
		return answers;
	}

	// =================================================================================
}
