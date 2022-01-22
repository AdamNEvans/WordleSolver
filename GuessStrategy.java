import java.io.*;
import java.util.*;

public class GuessStrategy
{
	// =================================================================================

	public String getBestGuess(Collection<String> words, Collection<Guess> guesses)
	{
        for (String word : words)
        {
            if (allCharsUnique(word))
            {
                return word;
            }
        }

        return words.iterator().next();
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
