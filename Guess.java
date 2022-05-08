import java.util.*;

public class Guess
{
	private int wordSize;
	private char[] guessChars;
	private String guess;
	private LetterComparisonResult[] results;

	// =================================================================================

	public Guess(String word, LetterComparisonResult[] inResult)
	{
		guess      = word;
		results    = inResult;
		guessChars = guess.toCharArray();
		wordSize   = guessChars.length;

		// Validate the results as a sanity test
		if (results != null)
		{
			if (results.length != wordSize)
			{
				System.out.println("ERROR: bad results length " + results.length + " for word '" + guess + "'");
			}
			else
			{
				for (int i = 0; i < results.length; i++)
				{
					if (results[i] == LetterComparisonResult.UNKNOWN)
					{
						System.out.println("ERROR: result for letter " + i + " in guess '" + guess + "' is unknown");
					}
				}
			}
		}
	}

	// =================================================================================

	public void populateResultsAgainst(String goal)
	{
		results = Utilities.compareWords(guess, goal);
	}

	// =================================================================================

	public boolean matchesWord(String input)
	{
		if (input.length() != wordSize)
		{
			return false;
		}

		char[] inputChars = input.toCharArray();
		boolean[] inputLettersUsed = new boolean[wordSize];
		boolean[] guessLettersUsed = new boolean[wordSize];

        // ------------------------------------
		// Verify input matches all the exacts and that we don't have exact matches where
		// there shouldn't be any
		for (int i = 0; i < wordSize; i++)
		{
			if (results[i] == LetterComparisonResult.EXACT)
			{
				if (guessChars[i] != inputChars[i])
				{
					// should be an exact match but there isn't
					return false;
				}

				guessLettersUsed[i] = true;
				inputLettersUsed[i] = true;
			}
			else if (guessChars[i] == inputChars[i])
			{
				// shouldn't be an exact match but there is
				return false;
			}
		}

        // ------------------------------------
		// verify input doesn't contain any of the letters we got wrong
		for (int i = 0; i < wordSize; i++)
		{
			if (results[i] == LetterComparisonResult.WRONG)
			{
				int index = 0;

				while (index < wordSize)
				{
					if ((inputChars[index] == guessChars[i]) && !inputLettersUsed[index])
					{
						break;
					}

					index++;
				}

				if (index < wordSize)
				{
					// input shouldn't contain this letter but it does
					return false;
				}

				guessLettersUsed[i] = true;
			}
		}

        // ------------------------------------
		// verify input contains all our inexact letters
		for (int i = 0; i < wordSize; i++)
		{
			if (results[i] == LetterComparisonResult.WRONG_INDEX)
			{
				for (int j = 0; j < wordSize; j++)
				{
					if (!inputLettersUsed[j] && guessChars[i] == inputChars[j])
					{
						guessLettersUsed[i] = true;
						inputLettersUsed[j] = true;
						break;
					}
				}

				if (!guessLettersUsed[i])
				{
					// input needed to contain guessChars[i], but didn't
					return false;
				}
			}
		}

        // ------------------------------------
		// double check that we accounted for every letter
		for (int i = 0; i < wordSize; i++)
		{
			if (!guessLettersUsed[i])
			{
				// there is a bug in the above logic; we didn't check for all ways matching can fail
				System.out.println("ERROR: letter " + i + " unmatched when matching guess '" +
									guess + "' against input '" + input + "'");
				System.out.println("Good job you idiot, you introduced a bug");
			}
		}

		return true;
	}

	// =================================================================================

	public String getWord()
	{
		return guess;
	}

	// =================================================================================

	public final LetterComparisonResult[] getResults()
	{
		return results;
	}

	// =================================================================================

	public String toString()
	{
		return guess + ": " + Arrays.toString(results);
	}

	// =================================================================================
}
