import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

enum LetterComparisonResult
{
	UNKNOWN('x'),
	EXACT('#'),
	WRONG('.'),
	WRONG_INDEX('?');

	private char charRepresentation;

	LetterComparisonResult(char charRepresentation)
	{
		this.charRepresentation = charRepresentation;
	}

	char getCharRepresentation()
	{
		return charRepresentation;
	}

	public static Optional<LetterComparisonResult> fromStringRepresentation(char stringRepresentation)
	{
		return Arrays.stream(values())
				.filter(result -> result.getCharRepresentation() == stringRepresentation)
				.findFirst();
	}
};

public class Utilities
{
	// =================================================================================

	public static LetterComparisonResult[] compareWords(String guess, String goal)
	{
		int goalSize = goal.length();
		int guessSize = guess.length();
		int minSize = Math.min(goalSize, guessSize);

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
				goalLettersUsed[i] = true;
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
						break;
					}
				}
			}
		}

		return results;
	}

	// =================================================================================
	// @param resultString A string where each char represents the comparison result for
	// that letter.
	// '-' -> wrong
	// '#' -> exact
	// '?' -> inexact
	public static LetterComparisonResult[] resultsFromString(String resultString)
	{
		char[] chars = resultString.toCharArray();
		LetterComparisonResult[] results = new LetterComparisonResult[resultString.length()];

		for (int i = 0; i < chars.length; i++)
		{
			switch (chars[i])
			{
				case '.':
					results[i] = LetterComparisonResult.WRONG;
					break;

				case '#':
					results[i] = LetterComparisonResult.EXACT;
					break;

				case '?':
					results[i] = LetterComparisonResult.WRONG_INDEX;
					break;

				default:
					System.out.println("ERROR: Invalid result char '" + chars[i] + "'");
					break;
			}
		}

		return results;
	}

	public static String stringFromResults(LetterComparisonResult[] result)
	{
		return Arrays.stream(result)
				.map(LetterComparisonResult::getCharRepresentation)
				.map(String::valueOf)
				.collect(joining());
	}

	// =================================================================================

	public static boolean allCharsUnique(String word)
	{
		boolean[] found = new boolean[256];

		for (char c : word.toCharArray())
		{
			if (found[c])
			{
				return false;
			}

			found[c] = true;
		}

		return true;
	}

	// =================================================================================
}
