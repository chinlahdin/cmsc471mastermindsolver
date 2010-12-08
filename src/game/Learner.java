/**
 * 
 */
package game;

import java.util.ArrayList;

import data.CodeSequence;
import data.ColorSpace;
import data.ColorStatistic;
import data.Feedback;
import data.RandomGuess;

/**
 * @author curtism2
 * 
 */
public class Learner implements Guesser
{

	private final static int MAX_NR_GUESSES_TO_GENERATE = 25000;
	private ColorSpace pegColors;
	private int nrPegs;
	private ArrayList<CodeSequence> guesses;
	private ArrayList<ColorStatistic> colorStats;
	private ArrayList<Feedback> feedbackForGuesses;
	private int bestGuessIndex;
	private int gamesPlayed;
	private ColorSpace workingColorSpace;

	boolean guessMatchesAll;

	public Learner(int nrPegs, int nrPegColors)
	{
		gamesPlayed = -1;
		this.nrPegs = nrPegs;
		colorStats = new ArrayList<ColorStatistic>();
		pegColors = new ColorSpace(nrPegColors);
		
		reset();
	}

	public void reset()
	{
		gamesPlayed++;
		if (gamesPlayed > 0)
			study();
		guesses = new ArrayList<CodeSequence>();
		feedbackForGuesses = new ArrayList<Feedback>();
		bestGuessIndex = -1;
		workingColorSpace = new ColorSpace(pegColors);
	}

	private void study()
	{
		System.out.println("Last guess " + guesses.get(guesses.size() - 1));
		for (int i = 0; i < pegColors.length(); ++i)
		{
			this.colorStats.add(new ColorStatistic(""+i));
			colorStats.get(colorStats.size()-1).increaseCount();
			colorStats.get(colorStats.size()-1).getProbability(nrPegs*gamesPlayed);
		}
		
	}

	public CodeSequence guess()
	{
		if (guesses.size() != feedbackForGuesses.size())
			throw new RuntimeException("Can't guess again until feedback is received for last guess");

		CodeSequence guess = null;
		int nrOfGuessMatches = 0;
		CodeSequence bestNextGuess = null;
		int nrOfBestNextGuessMatches = 0;
		boolean guessMatchesAll = false;
		int nrGuessesGenerated = 0;

		while (!guessMatchesAll)
		{
			if (feedbackForGuesses.isEmpty()
					|| feedbackForGuesses.get(bestGuessIndex).getBlack()
							+ feedbackForGuesses.get(bestGuessIndex).getWhite() == 0)
				guess = new RandomGuess(workingColorSpace, nrPegs);
			else
				guess = new RandomGuess(workingColorSpace,
						guesses.get(bestGuessIndex),
						feedbackForGuesses.get(bestGuessIndex));

			nrOfGuessMatches = 0;

			for (int i = 0; i < guesses.size(); i++)
			{
				if (guess.equals(guesses.get(i)))
				{
					nrOfGuessMatches = 0;
					break;
				}
				if (guess.getFeedbackFor(guesses.get(i)).equals(
						feedbackForGuesses.get(i)))
					nrOfGuessMatches++;
			}

			nrGuessesGenerated++;

			if (bestNextGuess == null
					|| nrOfGuessMatches > nrOfBestNextGuessMatches)
			{
				nrOfBestNextGuessMatches = nrOfGuessMatches;
				bestNextGuess = guess;
			}

			if (nrOfGuessMatches == guesses.size()
					|| nrGuessesGenerated >= MAX_NR_GUESSES_TO_GENERATE)
				guessMatchesAll = true;

		}

		System.out.println(nrGuessesGenerated
				+ " guesses generated-- best guess matches "
				+ ((guesses.size() == nrOfGuessMatches) ? "all "
						+ guesses.size() + " previous" : nrOfGuessMatches
						+ " of " + guesses.size()) + " guesses...");
		guesses.add(bestNextGuess);
		return bestNextGuess;
	}

	public void giveFeedbackForLastGuess(Feedback feedback)
	{
		if (feedbackForGuesses.size() != guesses.size() - 1)
			throw new RuntimeException("Already gave feedback for last guess");

		feedbackForGuesses.add(feedback);

		CodeSequence lastGuess = guesses.get(guesses.size() - 1);
		if (feedback.getBlack() + feedback.getWhite() == 0)
			for (int i = 0; i < lastGuess.getNrPegs(); i++)
				workingColorSpace.removeColor(lastGuess.getPegColorAt(i));

		if (bestGuessIndex < 0
				|| feedback.getValue() > feedbackForGuesses.get(bestGuessIndex)
						.getValue())
			bestGuessIndex = feedbackForGuesses.size() - 1;
	}

	public String toString()
	{
		String returnString = "";
		for (int i = 0; i < feedbackForGuesses.size(); i++)
		{
			returnString += "GUESS: " + guesses.get(i) + "\tFEEDBACK:"
					+ feedbackForGuesses.get(i) + "\n";
		}
		return returnString;
	}
}
