import java.util.Collection;

public interface WordleSolver
{
	String getName();
	boolean hasWon();
	String getNextGuessWord();

	// Requires guess.results to be filled out
	void applyGuess(Guess guess);

	void initialize(Collection<String> words);
}
