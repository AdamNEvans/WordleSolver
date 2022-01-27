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

		//Generate the count of each letter. Use this when determining yellow/green letters
		Map<Character, Long> characterCounts = goal.chars()
				.mapToObj(c -> (char)c)
				.collect(
						groupingBy(
								Function.identity(),
								counting()
						)
				);

		char[] goalChars = goal.toCharArray();
		char[] guessChars = guess.toCharArray();
		LetterComparisonResult[] results = new LetterComparisonResult[guessSize];

		// Check for exact matches
		for (int i = 0; i < minSize; i++)
		{
			if (guessChars[i] == goalChars[i])
			{
				results[i] = LetterComparisonResult.EXACT;

				//We found a match. Decrement the count for this letter
				characterCounts.merge(
					guessChars[i],
					0L,
					(x, y) -> {
						x--;
						return x <= 0 ? null : x;
					}
				);
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
					//We're making a call to containsKey(...) here to know if there are any more counts of this letter
					//If there aren't, don't mark it yellow. Keep it gray
					if(goalChars[j] == guessChars[i] && characterCounts.containsKey(guessChars[i]))
					{
						results[i] = LetterComparisonResult.WRONG_INDEX;

						//We found a yellow letter. Decrement the count for this letter
						characterCounts.merge(
								guessChars[i],
								0L,
								(x, y) -> {
									x--;
									return x <= 0 ? null : x;
								}
						);

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
