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
	 * The method to be called in between games.
	 */
	public void reset();

	/**
	 * Default guessing method.
	 * 
	 * @return
	 */
	public CodeSequence guess();

	/**
	 * Receives feedback for the latest guess.
	 * 
	 * @param feedback
	 */
	public void giveFeedbackForLastGuess(Feedback feedback);
}
