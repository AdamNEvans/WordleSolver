import java.io.*;
import java.util.*;

public interface WordleSolver
{
	public String getName();
	public boolean hasWon();
	public String getNextGuessWord();

	// Requires guess.results to be filled out
	public void applyGuess(Guess guess);
}
