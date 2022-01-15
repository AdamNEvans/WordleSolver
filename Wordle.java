import java.io.*;
import java.util.*;

public class Wordle
{
	public static final int WORD_SIZE      = 5;
	public static final int MAX_GUESSES    = 6;

	public static final int STATUS_EXACT   = -1;
	public static final int STATUS_WRONG   = -2;
	public static final int STATUS_UNKNOWN = -3;
//	public static final int STATUS_INEXACT;      // inexact matches are indicated by the index of the letter in the other string it corresponds to

	// map of words to their character counts
	public static ArrayList<String> possibleWords = new ArrayList<String>();

	// list of characters that must be in the string but we don't know their location
	public static ArrayList<Integer> requiredChars = new ArrayList<Integer>();

	// =================================================================================

	public static void main(String[] args) throws Exception
	{
		// initialize
		loadWords();
		System.out.println("Loaded " + possibleWords.size() + " words");

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
		char[] guessChars = guess.toCharArray();  // letters of guess
		char[] goalChars = goal.toCharArray();    // letters of goal
		int[] guessStatus = new int[WORD_SIZE];   // status of each letter in guess
		int[] goalStatus = new int[WORD_SIZE];    // status of each letter in goal
		int[] inexactMatchCounts = new int[26];   // [x] = number of inexact matches for letter x

		for (int i = 0; i < WORD_SIZE; i++)
		{
			guessStatus[i] = STATUS_UNKNOWN;
			goalStatus[i] = STATUS_UNKNOWN;
		}

		// -----------------------------------------------------------------------
		// Get the statuses of every letter in the guess and goal
		// -----------------------------------------------------------------------
		// check for correct location letters
		for (int i = 0; i < WORD_SIZE; i++)
		{
			if (guessChars[i] == goalChars[i])
			{
				System.out.println("Found exact match at position " + i);
				goalStatus[i] = STATUS_EXACT;
				guessStatus[i] = STATUS_EXACT;
			}
		}

		// ------------------------------------
		// check for correct letters in the wrong location
		for (int i = 0; i < WORD_SIZE; i++)
		{
			if (guessChars[i] == STATUS_EXACT)
			{
				continue;
			}

			for (int j = 0; j < WORD_SIZE; j++)
			{
				if (guessChars[i] == goalChars[j] && goalStatus[j] != STATUS_EXACT)
				{
					System.out.println("Found inexact match of char " + i + " in guess and char " + j + " in goal");
					inexactMatchCounts[guessChars[i] - 'a']++;
					goalStatus[j] = i;
					guessStatus[i] = j;
				}
			}
		}

		// ------------------------------------
		// mark the rest of the letters wrong
		for (int i = 0; i < WORD_SIZE; i++)
		{
			if (guessStatus[i] == STATUS_UNKNOWN)
			{
				guessStatus[i] = STATUS_WRONG;
			}

			if (goalStatus[i] == STATUS_UNKNOWN)
			{
				goalStatus[i] = STATUS_WRONG;
			}
		}

		// -----------------------------------------------------------------------
		// Start removing possibilities based on how well our guess did
		// -----------------------------------------------------------------------
		// Remove possibilities that don't contain all the exact matches
		for (int i = 0; i < possibleWords.size(); i++)
		{
			String word = possibleWords.get(i);

			for (int j = 0; j < WORD_SIZE; j++)
			{
				if (guessStatus[j] == STATUS_EXACT && word.charAt(j) != guessChars[j])
				{
//					System.out.println("removing '" + word + "' because it doesn't contain '" + guessChars[j] + "' at position " + j);
					possibleWords.remove(i);
					i--;
					break;
				}
			}
		}

		System.out.println("Removed words that don't contain all exact matches -> " + possibleWords.size() + " possibilities left");

		// ------------------------------------
		// Remove possibilities containing letters that were neither exact nor inexact matches
		for (int i = 0; i < possibleWords.size(); i++)
		{
			String word = possibleWords.get(i);

			for (int j = 0; j < WORD_SIZE; j++)
			{
				if (guessStatus[j] == STATUS_WRONG &&
					inexactMatchCounts[guessChars[j] - 'a'] == 0)    // ignore the scenario where we guess a duplicate letter but only one matches
				{
					int index = word.indexOf(guessChars[j]);

					while (index >= 0 && ((goalStatus[index] == STATUS_EXACT) || (goalStatus[index] >= 0)))
					{
						index = word.indexOf(guessChars[j], index + 1);
					}

					if (index >= 0)
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

		// ------------------------------------
		// Remove possibilities that don't contain all inexact match letters
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

		System.out.println("Removed words that don't contain all inexact matches -> " + possibleWords.size() + " possibilities left");

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
				if (guessStatus[j] >= 0)
				{
					int index = -1;

					do
					{
					    index = word.indexOf(guessChars[j], index + 1);
					}
					while (index < 0 || guessStatus[index] == STATUS_EXACT);

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
