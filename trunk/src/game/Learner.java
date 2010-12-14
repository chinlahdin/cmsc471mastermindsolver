package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data.CodeSequence;
import data.ColorSpace;
import data.Feedback;
import data.PatternStatistic;
import data.RandomGuess;

/**
 * @author M. Curtis, M. Edoror and B. Farrington
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
	private ArrayList<PatternStatistic> singleColorStats;
	private ArrayList<Feedback> feedbackForGuesses;
	private int bestGuessIndex;
	private int gamesPlayed;
	private double gamma;
	private double colorsPerGame;
	private ColorSpace workingColorSpace;
	private boolean bias1Flag;
	private double threshold;

	boolean guessMatchesAll;

	public Learner(int nrPegs, int nrPegColors)
	{
		threshold = 0.0;
		bias1Flag = true;
		gamesPlayed = -1;
		gamma = 0.1;
		this.nrPegs = nrPegs;
		patternStats = new ArrayList<PatternStatistic>();
		singleColorStats = new ArrayList<PatternStatistic>();
		codes = new ArrayList<String>();
		pegColors = new ColorSpace(nrPegColors);
		// This defines the basic color space
		for (int i = 1; i < nrPegColors; ++i)
		{
			singleColorStats.add(new PatternStatistic("" + i));
		}
		// Any other expression we wish to look for:

		reset();
	}

	public void reset()
	{
		gamesPlayed++;
		if (gamesPlayed > 0)
			threshold = study();
		guesses = new ArrayList<CodeSequence>();
		feedbackForGuesses = new ArrayList<Feedback>();
		bestGuessIndex = -1;
		workingColorSpace = new ColorSpace(pegColors);
	}

	private double study()
	{
		if (gamma < pegColors.length())
			gamma += Math.pow(gamma, 1.5);
		Pattern pattern;
		Matcher matcher;
		String previousCode;
		PatternStatistic newColorStat;
		boolean breakout;
		String subcode;
		ArrayList<String> oldSubcodes;
		double util1 = 0.0, util2 = 0.0;

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

				newColorStat = new PatternStatistic(subcode);
				if (!patternStats.contains(newColorStat)
						&& !singleColorStats.contains(newColorStat))
					patternStats.add(newColorStat);
				oldSubcodes.add(subcode);
			}
		}
		TreeSet<Character> uniqueColors = new TreeSet<Character>();
		for (Character c : previousCode.toCharArray())
		{
			if (c != 32)
				uniqueColors.add(c);
		}
		colorsPerGame += uniqueColors.size() * 1.0;
		System.out.println("Colors per game: " + colorsPerGame);

		colorsPerGame /= gamesPlayed;
		System.out.println("Colors per game: " + colorsPerGame);

		// Evaluate all probabilities for regex's
		for (PatternStatistic stat : patternStats)
		{
			pattern = Pattern.compile(stat.getRegex());
			matcher = pattern.matcher(previousCode);
			while (matcher.find())
			{
				stat.increaseCount();
			}
			stat.calcProbability(nrPegs * gamesPlayed);
		}
		for (PatternStatistic stat : singleColorStats)
		{
			pattern = Pattern.compile(stat.getRegex());
			matcher = pattern.matcher(previousCode);
			while (matcher.find())
			{
				stat.increaseCount();
			}
			stat.calcProbability(nrPegs * gamesPlayed);
		}

		PatternStatistic mostSignificant = singleColorStats.get(0);
		for (PatternStatistic stat : singleColorStats)
		{
			util1 = gamma * (stat.getProbability() + (stat.length() * 1.2));
			util2 = gamma
					* (mostSignificant.getProbability() + (mostSignificant.length() * 1.5));

			if (util1 > util2 && stat.getCount() > 1)
			{
				mostSignificant = stat;
			}
		}
		for (PatternStatistic patt : patternStats)
		{
			util1 = gamma * (patt.getProbability() + (patt.length() * 1.5));
			util2 = gamma
					* (mostSignificant.getProbability() + (mostSignificant.length() * 1.5));

			if (util1 > util2 && patt.getCount() > 1)
			{
				mostSignificant = patt;
			}
		}
		System.out.println("Most significant pattern: (" + util1 + ") "
				+ mostSignificant);

		for (int i = 0; i < oldSubcodes.size(); i++)
		{

			char compare = oldSubcodes.get(i).charAt(0);
			for (int j = 0; j < oldSubcodes.get(i).length(); j++)
			{
				if (compare != oldSubcodes.get(i).charAt(j))
					bias1Flag = false;
			}
		}
		System.out.println("The most probable color is "
				+ findProbableColor(999));
		System.out.println("The least probable color is "
				+ findProbableColor(0));
		System.out.println("Colors per game: " + colorsPerGame);
		return util1;
	}

	public boolean biasCheck2()
	{

		if (threshold >= (3 * (1.0 / nrPegs)))
			return true;
		return false;
	}

	public boolean biasCheck1()
	{
		// if three games have been played and all three codes have same color
		// for each
		// put in the guess to solve for bias 1
		if (gamesPlayed >= 3 && bias1Flag)
		{
			return true;
		}
		return false;
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
		int[] guessArray = new int[this.nrPegs];

		while (!guessMatchesAll)
		{
			if (guesses.isEmpty() && biasCheck2())
			{
				guessArray = new int[this.nrPegs];
				for (int z = 0; z < guessArray.length; z++)
				{
					if (z == 2)
					{
						guessArray[z] = guessArray[0];
						continue;
					}
					String potentialGuess = findProbableColor(
							(int) Math.round(z * gamma)).getRegex();
					if (potentialGuess.contains(" "))
					{
						guessArray[z] = Integer.parseInt(potentialGuess.substring(
								0, potentialGuess.indexOf(" ")));
					}
					else
					{
						guessArray[z] = Integer.parseInt(potentialGuess);
					}
				}

				guess = new CodeSequence(guessArray);
			}

			else if (biasCheck1())
			{
				int halfWay = this.nrPegs / 2;
				int value = 1;
				for (int y = 0; y < guessArray.length; y++)
				{
					if (y >= halfWay)
					{
						guessArray[y] = value++;
					}
					else
					{
						guessArray[y] = value;
						if (y % 2 == 1)
						{
							value += 1;
						}
					}
				}
				guess = new CodeSequence(guessArray);

			}
			else if (feedbackForGuesses.isEmpty()
					|| feedbackForGuesses.get(bestGuessIndex).getBlack()
							+ feedbackForGuesses.get(bestGuessIndex).getWhite() == 0)
			{
				if (guesses.isEmpty())
				{
					guessArray = new int[this.nrPegs];
					for (int z = 0; z < guessArray.length; z++)
					{
						String potentialGuess = findProbableColor(
								(int) Math.round(z * gamma)).getRegex();
						if (potentialGuess.contains(" "))
						{
							guessArray[z] = Integer.parseInt(potentialGuess.substring(
									0, potentialGuess.indexOf(" ")));
						}
						else
						{
							guessArray[z] = Integer.parseInt(potentialGuess);
						}
					}

					guess = new CodeSequence(guessArray);
				}
				else
					guess = new RandomGuess(workingColorSpace, nrPegs);

			}
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

	private PatternStatistic findProbableColor(int rank)
	{
		if (rank >= this.singleColorStats.size())
			rank = singleColorStats.size() - 1;
		if (rank < 0)
			rank = 0;
		Collections.sort(this.singleColorStats);
		return this.singleColorStats.get(rank);
	}

	public void giveFeedbackForLastGuess(Feedback feedback)
	{
		if (feedbackForGuesses.size() != guesses.size() - 1)
			throw new RuntimeException("Already gave feedback for last guess");

		feedbackForGuesses.add(feedback);

		CodeSequence lastGuess = guesses.get(guesses.size() - 1);
		/*
		 * if (feedback.getBlack() + feedback.getWhite() == 0) for (int i = 0; i
		 * < lastGuess.getNrPegs(); i++)
		 * workingColorSpace.removeColor(lastGuess.getPegColorAt(i));
		 */

		if (guesses.size() == 1 && biasCheck1())
		{
			if (feedback.getBlackAndWhite() == 2)
			{
				for (int a = lastGuess.getNrPegs() / 2; a < lastGuess.getNrPegs(); a++)
				{
					workingColorSpace.removeColor(lastGuess.getPegColorAt(a));
				}
			}
			if (feedback.getBlackAndWhite() == 1)
			{
				for (int a = 0; a < lastGuess.getNrPegs() / 2; a++)
				{
					workingColorSpace.removeColor(lastGuess.getPegColorAt(a));
				}
			}
		}
		if (feedback.getBlackAndWhite() == 0)
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
