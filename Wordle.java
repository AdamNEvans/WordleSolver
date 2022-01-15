import java.io.*;
import java.util.*;

public class Wordle
{
	public static final int WORD_SIZE = 5;
	public static final int MAX_GUESSES = 6;

	// map of words to their character counts
	public static ArrayList<String> possibleWords = new ArrayList<String>();

	// map of characters to a list of their possible locations [0, WORD_SIZE - 1]
	public static int[][] locations = new int[26][5];

	// list of characters that must be in the string but we don't know their location
	public static ArrayList<Integer> requiredChars = new ArrayList<Integer>();

	// =================================================================================

	public static void main(String[] args) throws Exception
	{
		// initialize
		loadWords();
		System.out.println("Loaded " + possibleWords.size() + " words");

		for (int i = 0; i < 26; i++)
		{
			for (int j = 0; j < WORD_SIZE; j++)
			{
				locations[i][j] = j;
			}
		}

		// solve
		Scanner in = new Scanner(System.in);
		System.out.print("Enter goal word: ");
		System.out.flush();

		String goal = in.nextLine().strip().toLowerCase();
		System.out.println("Goal: '" + goal + "'");
		
		for (int i = 0; i < MAX_GUESSES; i++)
		{
//			System.out.println("words=" + possibleWords.toString());
	        System.out.println("====================================================================================");
			String guess = getNextGuess();
		    System.out.println("                          PROCESSING GUESS '" + guess + "'");
		    System.out.println("====================================================================================");

			if (guess.equals(goal))
			{
				System.out.println("SUCCESS!!!");
				System.out.println("Took " + i + " guesses");
				return;
			}

			processGuess(guess, goal);
			possibleWords.remove(guess);
		}
	}

	// =================================================================================

	public static void processGuess(String guess, String goal)
	{
		final char EXACT = 0;
		final char WRONG_LOCATION_BIT = 128;

		char[] guessChars = guess.toCharArray();
		char[] goalChars = goal.toCharArray();

		// ------------------------------------
		// check for correct location letters
		for (int i = 0; i < WORD_SIZE; i++)
		{
			if (guessChars[i] == goalChars[i])
			{
				System.out.println("Found exact match at position " + i);

				int[] list = {i};
				locations[guessChars[i] - 'a'] = list;

				// remove possibilities that don't match
				for (int j = 0; j < possibleWords.size(); j++)
				{
					if (possibleWords.get(j).charAt(i) != guessChars[i])
					{
//						System.out.println("removing '" + possibleWords.get(j) + "' because it doesn't contain '" + guessChars[i] + "' at position " + i);
						possibleWords.remove(j);
						j--;
					}
				}

				goalChars[i] = EXACT;   // clear it so we don't match it again
				guessChars[i] = EXACT;  // mark it so we don't check for it again
			}
		}

		System.out.println("Removed words that don't contain exact matches -> " + possibleWords.size() + " possibilities left");

		// ------------------------------------
		// check for correct letters in the wrong location
		int[] inexactMatchCounts = new int[26];

		for (int i = 0; i < WORD_SIZE; i++)
		{
			if (guessChars[i] == 0)
			{
				continue;
			}

			for (int j = 0; j < WORD_SIZE; j++)
			{
				if (guessChars[i] == goalChars[j] && goalChars[j] != 0)
				{
					System.out.println("Found inexact match of char " + i + " in guess and char " + j + " in goal");
					inexactMatchCounts[guessChars[i] - 'a']++;
					goalChars[j] |= WRONG_LOCATION_BIT;      // clear it so we don't match it again
					guessChars[i] |= WRONG_LOCATION_BIT;
				}
			}
		}

		// remove possibilities that don't contain all inexact match letters
		for (int i = 0; i < possibleWords.size(); i++)
		{
			boolean valid = true;
			String word = possibleWords.get(i);

			for (int j = 0; valid && j < inexactMatchCounts.length; j++)
			{
				int nextStart = 0;

				for (int k = 0; k < inexactMatchCounts[j]; k++)
				{
					int index = word.indexOf('a' + j, nextStart);

					if (index < 0)
					{
//						System.out.println("removing '" + word + "' because it doesn't contain " + inexactMatchCounts[j] + " instances of '"
//											+ (char)('a' + j) + "', which must be in the word somewhere");
						valid = false;
						break;
					}
					else
					{
						nextStart = index + 1;
					}
				}
			}

			if (!valid)
			{
				possibleWords.remove(i);
				i--;
			}
		}

		System.out.println("Removed words that don't contain inexact matches -> " + possibleWords.size() + " possibilities left");

		// ------------------------------------
		// Remove possibilities that have the letters in the same locations as guess, but we know
		// from comparing to goal that those letters can't be there. This is different from the above loop
		// because while the above loop just checks that it contains all the inexact match letters, this
		// loop checks that those letters are in legal positions.
		for (int i = 0; i < possibleWords.size(); i++)
		{
			String word = possibleWords.get(i);

			for (int j = 0; j < WORD_SIZE; j++)
			{
				if ((guessChars[j] & WRONG_LOCATION_BIT) != 0)
				{
					int index = -1;
					char c = (char)(guessChars[j] & ~WRONG_LOCATION_BIT);

					do
					{
					    index = word.indexOf(c, index + 1);
					}
					while (index < 0 || guessChars[index] == EXACT);

					if (index < 0)
					{
						possibleWords.remove(i);
						i--;
						break;
					}
				}
			}
		}

		System.out.println("Removed words that contain inexact matches in exact match positions -> " + possibleWords.size() + " possibilities left");

		// ------------------------------------
		// Remove all words containing letters that were neither exact nor inexact matches
		for (int i = 0; i < possibleWords.size(); i++)
		{
			String word = possibleWords.get(i);
//			System.out.println("---------- analyzing '" + word + "'");

			for (int j = 0; j < WORD_SIZE; j++)
			{
/*				System.out.print("char " + j + " (" + (int)guessChars[j] + ")");

				if (guessChars[j] >= 'a' && guessChars[j] <= 'z')
				{
					System.out.println("; inexactMatchCounts=" + inexactMatchCounts[guessChars[j] - 'a']);
				}
				else
				{
					System.out.println();
				} */

				if (guessChars[j] != EXACT && ((guessChars[j] & WRONG_LOCATION_BIT) == 0) &&
					inexactMatchCounts[guessChars[j] - 'a'] == 0)    // ignore the scenario where we guess a duplicate letter but only one matches
				{
					int index = word.indexOf(guessChars[j]);

					while (index >= 0 && ((goalChars[index] == EXACT) || ((goalChars[index] & WRONG_LOCATION_BIT) != 0)))
					{
						index = word.indexOf(guessChars[j], index + 1);
					}

					if (index >= 0)// && goalChars[index] != EXACT)
					{
//						System.out.println("Removing '" + word + "' because it contains unmatched letter '" + guessChars[j] + "' at index " + index);
						possibleWords.remove(i);
						i--;
						break;
					}
				}
			}
		}

		System.out.println("Removed words that contain unmatched letters -> " + possibleWords.size() + " possibilities left");
	}

	// =================================================================================

	public static String getNextGuess()
	{
		if (possibleWords.size() == 0)
		{
			System.out.println("ERROR: no more possibilities... there's a bug, you idiot");
			System.exit(1);
		}

		for (String word : possibleWords)
		{
			if (allCharsUnique(word))
			{
				return word;
			}
		}

		return possibleWords.get(0);
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
	// returns true if all letters in word were lowercase a-z. If returns false,
	// counts will not be completely populated
	public static boolean populateCounts(String word, int[] counts)
	{
		for (char c : word.toCharArray())
		{
			int index = c - 'a';

			if (index < 0 || index >= 26)
			{
				return false;
			}

			counts[index]++;
		}

		return true;
	}

	// =================================================================================

	public static void loadWords() throws Exception
	{
		Scanner in = new Scanner(new File("/usr/share/dict/words"));
		
		while (in.hasNextLine())
		{
			String word = in.nextLine().strip().toLowerCase();

			if (word.length() == WORD_SIZE)
			{
				possibleWords.add(word);
			}
		}
	}

	// =================================================================================

	public static void loadWords2()
	{
		possibleWords.add("aalii");
		possibleWords.add("abbie");
		possibleWords.add("abide");
		possibleWords.add("abidi");
		possibleWords.add("abies");
		possibleWords.add("abilo");
		possibleWords.add("thing");
	}

	// =================================================================================
}
