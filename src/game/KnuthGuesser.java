package game;

import java.util.ArrayList;
import java.util.Iterator;

import data.CodeSequence;
import data.Feedback;

/**
 * The Knuth guessing algorithm guesser. This algorithm only works for the
 * default case of 4 pegs and 6 colors.
 * 
 * @author M. Curtis, M. Edoror and B. Farrington
 * 
 */
public class KnuthGuesser implements Guesser
{
	private static final int GUESS_SPACE_SIZE = 1296;
	private static final int FEEDBACK_SPACE_SIZE = 14;

	private ArrayList<CodeSequence> currentGuessSpace;
	private ArrayList<CodeSequence> entireGuessSpace;
	private ArrayList<Feedback> feedbackSpace;
	private CodeSequence lastGuess;
	private Feedback lastFeedback;

	/**
	 * Default constructor.
	 */
	public KnuthGuesser()
	{
		reset();
		entireGuessSpace = createGuessSpace();
		feedbackSpace = createFeedbackSpace();
	}

	/**
	 * Receives feedback for last guess.
	 */
	public void giveFeedbackForLastGuess(Feedback feedback)
	{
		lastFeedback = feedback;
	}

	/**
	 * Generates a first guess which can eliminate at most 3 colors and at least
	 * 1.
	 */
	public CodeSequence guess()
	{
		if (lastGuess == null)
		{
			lastGuess = new CodeSequence(new int[] { 1, 1, 2, 3 });
			return lastGuess;
		}

		removeImpossibleGuessesFromGuessSpace();

		if (currentGuessSpace.size() == 1)
			return currentGuessSpace.get(0);

		CodeSequence bestGuess = null;
		int bestSpaceReduction = 0;
		int currentLowestReductionForGuess;
		int reductionForGuess;

		for (int i = 0; i < entireGuessSpace.size(); i++)
		{
			currentLowestReductionForGuess = GUESS_SPACE_SIZE;
			for (int j = 0; j < feedbackSpace.size(); j++)
			{
				reductionForGuess = getCountOfImpossibleGuesses(
						entireGuessSpace.get(i), feedbackSpace.get(j));

				if (reductionForGuess < currentLowestReductionForGuess)
				{
					currentLowestReductionForGuess = reductionForGuess;
				}
			}

			if (bestGuess == null
					|| currentLowestReductionForGuess > bestSpaceReduction)
			{
				bestGuess = entireGuessSpace.get(i);
				bestSpaceReduction = currentLowestReductionForGuess;
			}
		}

		lastGuess = bestGuess;
		return bestGuess;
	}

	/**
	 * Removes guesses for which gave no feedback.
	 */
	private void removeImpossibleGuessesFromGuessSpace()
	{
		Iterator<CodeSequence> itr = currentGuessSpace.listIterator();
		CodeSequence currentCode = null;

		while (itr.hasNext())
		{
			currentCode = itr.next();
			if (!currentCode.getFeedbackFor(lastGuess).equals(lastFeedback))
				itr.remove();
		}
	}

	/**
	 * Counts the number of impossible guesses.
	 * 
	 * @param guess
	 * @param feedback
	 * @return
	 */
	private int getCountOfImpossibleGuesses(CodeSequence guess,
			Feedback feedback)
	{
		int nrOfImpossibleGuesses = 0;
		Iterator<CodeSequence> itr = currentGuessSpace.listIterator();
		CodeSequence currentCode = null;

		while (itr.hasNext())
		{
			currentCode = itr.next();
			if (!currentCode.getFeedbackFor(guess).equals(feedback))
			{
				nrOfImpossibleGuesses++;
			}
		}

		return nrOfImpossibleGuesses;
	}

	/**
	 * Resets the state for the guesser.
	 */
	public void reset()
	{
		currentGuessSpace = createGuessSpace();
		lastGuess = null;
		lastFeedback = null;
	}

	/**
	 * Enumerates the entire guess space.
	 * 
	 * @return
	 */
	private ArrayList<CodeSequence> createGuessSpace()
	{
		ArrayList<CodeSequence> guessSpace = new ArrayList<CodeSequence>();

		int[] code = { 1, 1, 1, 1 };
		for (int i = 0; i < GUESS_SPACE_SIZE; i++)
		{
			guessSpace.add(new CodeSequence(code));
			for (int j = 3; j >= 0; j--)
			{
				if (code[j] == 6)
				{
					code[j] = 1;
				}
				else
				{
					code[j]++;
					break;
				}
			}
		}

		return guessSpace;
	}

	/**
	 * Creates the feedback space.
	 * 
	 * @return
	 */
	private ArrayList<Feedback> createFeedbackSpace()
	{
		ArrayList<Feedback> feedbackSpace = new ArrayList<Feedback>();

		int whiteCount = 0;
		int blackCount = 0;
		for (int i = 0; i < FEEDBACK_SPACE_SIZE; i++)
		{
			feedbackSpace.add(new Feedback(whiteCount, blackCount));
			blackCount++;
			if (blackCount + whiteCount > 4
					|| (blackCount == 3 && whiteCount == 1))
			{
				blackCount = 0;
				whiteCount++;
			}
		}

		return feedbackSpace;
	}
}
