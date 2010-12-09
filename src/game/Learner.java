/**
 * 
 */
package game;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.CodeSequence;
import data.ColorSpace;
import data.Feedback;
import data.PatternStatistic;
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
	private ArrayList<String> codes;
	private ArrayList<PatternStatistic> patternStats;
	private ArrayList<Feedback> feedbackForGuesses;
	private int bestGuessIndex;
	private int gamesPlayed;
	private ColorSpace workingColorSpace;

	boolean guessMatchesAll;

	public Learner(int nrPegs, int nrPegColors)
	{
		gamesPlayed = -1;
		this.nrPegs = nrPegs;
		patternStats = new ArrayList<PatternStatistic>();
		codes = new ArrayList<String>();
		pegColors = new ColorSpace(nrPegColors);
		// This defines the basic color space
		for (int i = 1; i < nrPegColors; ++i)
		{
			this.patternStats.add(new PatternStatistic("" + i));
		}
		// Any other expression we wish to look for:

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
		Pattern pattern;
		Matcher matcher;
		String previousCode;
		PatternStatistic newColorStat;
		boolean breakout;
		String subcode;
		ArrayList<String> oldSubcodes;

		oldSubcodes = new ArrayList<String>();
		previousCode = guesses.get(guesses.size() - 1).toString();
		codes.add(previousCode);

		// Study current code and extract regex's from it
		for (int len = 2; len < previousCode.length(); ++len)
		{
			for (int i = 0; i < previousCode.length() - len; ++i)
			{
				breakout = false;
				subcode = previousCode.substring(i, i + len);
				while (subcode.endsWith(" "))
					subcode = previousCode.substring(i, i + (len + 1));

				for (String oldSubcode : oldSubcodes)
				{
					if (subcode.equalsIgnoreCase(oldSubcode))
					{
						breakout = true;
						break;
					}
				}
				if (breakout)
					break;
				if (subcode.indexOf(" ") == 0)
					continue;

				System.out.println("Current subcode = " + subcode);
				newColorStat = new PatternStatistic(subcode);
				if (!patternStats.contains(newColorStat))
					patternStats.add(newColorStat);
				oldSubcodes.add(subcode);
			}
		}
		// Evaluate all probabilities for regex's
		for (PatternStatistic stat : patternStats)
		{
			pattern = Pattern.compile(stat.getRegex());
			matcher = pattern.matcher(previousCode);
			while (matcher.find())
			{
				stat.increaseCount();
			}
			stat.getProbability(nrPegs * gamesPlayed);
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
