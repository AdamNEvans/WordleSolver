import java.util.Collection;

public interface WordleSolver
{
	String getName();
	boolean hasWon();
	String getNextGuessWord();

	// Requires guess.results to be filled out
	void applyGuess(Guess guess);

	default void initialize(Collection<String> possibleAnswers)
	{
		initialize(possibleAnswers, true);
	}

	void initialize(Collection<String> words, boolean enablePrints);
}
