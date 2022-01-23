import java.io.*;
import java.util.*;

public class NoDupesStrategy implements GuessStrategy
{
	// =================================================================================

	public String getName()
	{
		return "Avoid duplicate letters";
	}

	// =================================================================================

	public String getBestGuess(Collection<String> words, Collection<Guess> guesses)
	{
        for (String word : words)
        {
            if (Utilities.allCharsUnique(word))
            {
                return word;
            }
        }

        return words.iterator().next();
    }

	// =================================================================================
}
