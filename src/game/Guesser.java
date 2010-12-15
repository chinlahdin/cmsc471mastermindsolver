package game;

import data.CodeSequence;
import data.Feedback;

/**
 * The common interface for all guessers in our Mastermind solver.
 * 
 * @author M. Curtis, M. Edoror and B. Farrington
 * 
 */
public interface Guesser
{
	/**
	 * The method to be called in between games which
	 * should clear out any cached data not required for the
	 * next game.
	 */
	public void reset();

	/**
	 * Default guessing method that returns the Guesser's next
	 * guess.
	 * 
	 * @return
	 */
	public CodeSequence guess();

	/**
	 * Receives feedback for the latest guess which should be called
	 * after each guess call.
	 * 
	 * @param feedback
	 */
	public void giveFeedbackForLastGuess(Feedback feedback);
}
