import java.io.*;
import java.util.*;

enum LetterComparisonResult
{
	UNKNOWN,
	EXACT,
	WRONG,
	WRONG_INDEX,
};

public class Utilities
{
	// =================================================================================

	public static LetterComparisonResult[] compareWords(String guess, String goal)
	{
		int goalSize = goal.length();
		int guessSize = guess.length();
		int minSize = (goalSize < guessSize ? goalSize : guessSize);

		char[] goalChars = goal.toCharArray();
		char[] guessChars = guess.toCharArray();
		boolean[] goalLettersUsed = new boolean[goalSize];
		LetterComparisonResult[] results = new LetterComparisonResult[guessSize];

		// Check for exact matches
		for (int i = 0; i < minSize; i++)
		{
			if (guessChars[i] == goalChars[i])
			{
				results[i] = LetterComparisonResult.EXACT;
			}
		}

		// Check for inexact matches and wrong letters
		for (int i = 0; i < guessSize; i++)
		{
			if (results[i] == null)
			{
				results[i] = LetterComparisonResult.WRONG;

				for (int j = 0; j < goalSize; j++)
				{
					if (!goalLettersUsed[j] && goalChars[j] == guessChars[i])
					{
						results[i] = LetterComparisonResult.WRONG_INDEX;
						goalLettersUsed[j] = true;
					}
				}
			}
		}

		return results;
	}

	// =================================================================================
}
