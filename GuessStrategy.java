import java.io.*;
import java.util.*;

public interface GuessStrategy
{
	// Return a name that can be displayed to the user
	public String getName();

	// Return the next word that should be used as a guess
	public String getBestGuess(Collection<String> words, Collection<Guess> guesses);
}
