package data;

import java.util.Random;

/**
 * The random guess is the basis of our Mastermind Solver. A random guess can be
 * under different conditions: completely random, guided with feedback and
 * permuted.
 * 
 * @author M. Curtis, M. Edoror and B. Farrington
 * 
 */
public class RandomGuess extends CodeSequence
{
	/**
	 * Creates a totally random guess given the colorspace and number of pegs in
	 * the code.
	 * 
	 * @param colors
	 * @param nrPegs
	 */
	public RandomGuess(ColorSpace colors, int nrPegs)
	{
		super(makeTotallyRandomGuess(colors, nrPegs));
	}

	/**
	 * Permutes a code sequence that already has been given feedback.
	 * 
	 * @param sequenceToPermute
	 */
	public RandomGuess(CodeSequence sequenceToPermute)
	{
		super(makeRandomPermutationGuess(sequenceToPermute));
	}

	/**
	 * Makes a random guess based on the feedback.
	 * 
	 * @param colors
	 * @param sequenceToMatch
	 * @param feedbackToMatch
	 */
	public RandomGuess(ColorSpace colors, CodeSequence sequenceToMatch,
			Feedback feedbackToMatch)
	{
		super(makeRandomGuessFromSequenceFeedback(colors, sequenceToMatch,
				feedbackToMatch));
	}

	/**
	 * Makes a totally random guess and outputs it as an int array.
	 * 
	 * @param possiblePegColors
	 * @param nrPegs
	 * @return
	 */
	private static int[] makeTotallyRandomGuess(ColorSpace possiblePegColors,
			int nrPegs)
	{
		int[] pegs = new int[nrPegs];
		for (int i = 0; i < nrPegs; i++)
			pegs[i] = possiblePegColors.getRandomColor();
		return pegs;
	}

	/**
	 * Permutes a code sequence and returns it as an int array.
	 * 
	 * @param sequenceToPermute
	 * @return
	 */
	private static int[] makeRandomPermutationGuess(
			CodeSequence sequenceToPermute)
	{
		int[] pegs = new int[sequenceToPermute.getNrPegs()];
		for (int i = 0; i < pegs.length; i++)
			pegs[i] = sequenceToPermute.getPegColorAt(i);
		Random rng = new Random();
		int temp;
		int rndIndex;
		for (int i = pegs.length - 1; i >= 1; i--)
		{
			rndIndex = rng.nextInt(i + 1);
			temp = pegs[i];
			pegs[i] = pegs[rndIndex];
			pegs[rndIndex] = temp;
		}
		return pegs;
	}

	/**
	 * Makes a random guess from feedback and returns it as an int array.
	 * 
	 * @param colors
	 * @param sequenceToMatch
	 * @param feedbackToMatch
	 * @return
	 */
	private static int[] makeRandomGuessFromSequenceFeedback(ColorSpace colors,
			CodeSequence sequenceToMatch, Feedback feedbackToMatch)
	{
		int[] newGuess = new int[sequenceToMatch.getNrPegs()];
		int[] indexesToPermute = createUniqueRandIntSet(
				feedbackToMatch.getBlack() + feedbackToMatch.getWhite(),
				sequenceToMatch.getNrPegs());
		int[] permutation = new int[sequenceToMatch.getNrPegs()];
		int[] maxColorCount = getMaxColorCount(sequenceToMatch);
		int maxColorFreq = maxColorCount[0];
		char mostFreqColor = (char) maxColorCount[1];
		ColorSpace colorSpace = new ColorSpace(colors);

		// Initialize newGuess and permutation arrays with invalid values
		for (int i = 0; i < permutation.length; i++)
		{
			newGuess[i] = INVALID_PEG_COLOR;
			permutation[i] = -1;
		}

		// The first indexes will be our black pegs and the map to their own
		// index (identity)
		for (int i = 0; i < feedbackToMatch.getBlack(); i++)
		{
			while (feedbackToMatch.getWhite() > 0
					&& unusedColorsAreTheSame(permutation, sequenceToMatch,
							indexesToPermute[i])
					|| (i == 0
							&& indexesToPermute.length == sequenceToMatch.getNrPegs()
							&& sequenceToMatch.getNrPegs() <= maxColorFreq * 2 && sequenceToMatch.getPegColorAt(indexesToPermute[0]) != mostFreqColor))
			{
				int temp = indexesToPermute[indexesToPermute.length - 1];
				System.arraycopy(indexesToPermute, 0, indexesToPermute, 1,
						indexesToPermute.length - 1);
				indexesToPermute[i] = temp;
			}
			permutation[indexesToPermute[i]] = indexesToPermute[i];
		}

		// The reset of the indexes will be our white pegs, we must find a
		// mapping such that each mapping is unique and is not an identity
		for (int i = feedbackToMatch.getBlack(); i < indexesToPermute.length; i++)
			permutation[indexesToPermute[i]] = getIndexNonIdentityMapping(
					permutation, sequenceToMatch, indexesToPermute[i]);

		/*
		 * System.out.println("\nDEBUG INFO");
		 * System.out.println("------------------------------------------");
		 * System.out.println("PERMUTATION: " + arrayToString(permutation));
		 * System.out.println("FEEDBACK: " + feedbackToMatch );
		 * System.out.println("RANDOM INDEXES: " +
		 * arrayToString(indexesToPermute));
		 * System.out.println("COLORSPACE BEFORE: " + colorSpace);
		 */

		// Reduce the color space by removing colors of pegs that were not
		// chosen as having received feedback
		for (int i = 0; i < permutation.length; i++)
			if (permutation[i] < 0 && colorSpace.length() > 1)
				colorSpace.removeColor(sequenceToMatch.getPegColorAt(i));

		// System.out.println("COLORSPACE AFTER: " + colorSpace);

		// Permute the sequence as the permutation array dictates
		for (int i = 0; i < permutation.length; i++)
			if (permutation[i] >= 0)
				newGuess[permutation[i]] = sequenceToMatch.getPegColorAt(i);

		// Fill in remaining newGuess pegs with randomly chosen colored pegs
		// from remaining colorspace choices
		for (int i = 0; i < newGuess.length; i++)
			if (newGuess[i] == INVALID_PEG_COLOR)
				newGuess[i] = colorSpace.getRandomColor();

		// System.out.println( "GUESS: " + arrayToString( newGuess ) );
		// System.out.println("------------------------------------------\n");

		return newGuess;
	}

	/**
	 * Transforms a character array to a String for output.
	 * The returned String will take on the following format:
	 * "[char0, char1, char2, ... charN-1]"
	 * 
	 * @param array
	 * @return the char array as a String 
	 */
	private static String arrayToString(char[] array)
	{
		String returnString = "[";
		for (int i = 0; i < array.length - 1; i++)
			returnString += array[i] + ", ";
		returnString += array[array.length - 1] + "]";
		return returnString;
	}

	/**
	 * Transforms an integer array to a String for output.
	 * The returned String will take on the following format:
	 * "[int0, int1, int2, ... intN-1]"
	 * 
	 * @param array
	 * @return the int array as a String 
	 */
	private static String arrayToString(int[] array)
	{
		String returnString = "[";
		for (int i = 0; i < array.length - 1; i++)
			returnString += array[i] + ", ";
		returnString += array[array.length - 1] + "]";
		return returnString;
	}

	/**
	 * Calculates the most frequent color and the number of times it
	 * appears in the specified CodeSequence.
	 * 
	 * @param sequence
	 * @return an integer array with two elements, the first being the
	 * count of the most frequent color, and the second being the integer
	 * that represents the most frequent color
	 */
	private static int[] getMaxColorCount(CodeSequence sequence)
	{
		int bestCount = 0;
		int curCount = 0;
		int bestColor = INVALID_PEG_COLOR;
		int curColor = INVALID_PEG_COLOR;
		for (int i = 0; i < sequence.getNrPegs() - 1; i++)
		{
			curCount = 1;
			curColor = sequence.getPegColorAt(i);
			for (int j = i + 1; j < sequence.getNrPegs(); j++)
				if (sequence.getPegColorAt(i) == sequence.getPegColorAt(j))
					curCount++;
			if (curCount > bestCount)
			{
				bestCount = curCount;
				bestColor = curColor;
			}
		}

		return new int[] { bestCount, (int) bestColor };
	}

	/**
	 * A helper method for makeRandomGuessFromSequenceFeedback which determines
	 * if the current permutation of a CodeSequence has left all unused elements
	 * the same color.
	 * 
	 * @param permutation the current permutation of sequence
	 * @param sequence the CodeSequence which is being permuted
	 * @param index the index which will receive the next mapping and
	 * will be ignored during the calculation
	 * @return true if the remaining, not-yet-mapped-to elements of
	 * sequence are the same color; otherwise false
	 */
	private static boolean unusedColorsAreTheSame(int[] permutation,
			CodeSequence sequence, int index)
	{
		int firstUnusedIndex = 0;
		int color;
		while (firstUnusedIndex < permutation.length
				&& (permutation[firstUnusedIndex] >= 0 || firstUnusedIndex == index))
			firstUnusedIndex++;
		if (firstUnusedIndex >= permutation.length)
			return false;

		color = sequence.getPegColorAt(firstUnusedIndex);
		for (int i = firstUnusedIndex; i < permutation.length; i++)
			if (permutation[i] < 0 && sequence.getPegColorAt(i) != color
					&& i != index)
				return false;

		return true;
	}

	/**
	 * A helper method for makeRandomGuessFromSequenceFeedback which takes
	 * an integer array representing the current permutation of a guess
	 * where negative values represent unmapped guess elements, the guess
	 * as a CodeSequence, and an index in which to find a mapping for.
	 * It finds and then returns a mapping for the specified index to an
	 * unused permutation index which contains a different color.
	 * 
	 * @param permutation the current permutation of the sequence
	 * @param sequence the guess being permuted
	 * @param index the index for which to find a non-identity mapping for
	 * @return the index in sequence in which to map the specified index to
	 * or just some random index if a non-identity mapping doesn't exist
	 */
	private static int getIndexNonIdentityMapping(int[] permutation,
			CodeSequence sequence, int index)
	{
		int indexMapping = -1;
		Random rng = new Random();
		boolean isValidMapping = false;
		int availablePositions = sequence.getNrPegs();
		int[] usedMappings = new int[permutation.length];

		for (int i = 0; i < usedMappings.length; i++)
			usedMappings[i] = -1;

		for (int i = 0; i < permutation.length; i++)
		{
			if (permutation[i] >= 0)
			{
				usedMappings[permutation[i]] = i;
				availablePositions--;
			}
		}

		if (availablePositions == 1 && usedMappings[index] == -1)
			return -1;

		while (!isValidMapping)
		{
			// System.out.println( "WHITE MAPPING -- " +
			// arrayToString(permutation) + " -- " + index + " -- " + sequence
			// );
			do
			{
				indexMapping = rng.nextInt(permutation.length);
			}
			while (indexMapping == index);
			isValidMapping = true;
			for (int j = 0; j < permutation.length; j++)
			{
				if (indexMapping == permutation[j])
				{
					isValidMapping = false;
					break;
				}
			}
		}

		return indexMapping;
	}

	/**
	 * Creates and returns a unique set of random integers with a range
	 * from 0 (inclusive) to upTo (exclusive).
	 * 
	 * @param nrRandInts the number of unique random integers to create
	 * @param upTo the upper exclusive limit of the generated values
	 * @return an integer array containing the created set
	 */
	private static int[] createUniqueRandIntSet(int nrRandInts, int upTo)
	{
		int[] uniqueRandInts = new int[nrRandInts];
		Random rng = new Random();
		int randInt = 0;
		boolean isUnique = false;

		for (int i = 0; i < uniqueRandInts.length; i++)
		{
			isUnique = false;
			while (!isUnique)
			{
				randInt = rng.nextInt(upTo);
				isUnique = true;
				for (int j = 0; j < i; j++)
				{
					if (randInt == uniqueRandInts[j])
					{
						isUnique = false;
						break;
					}
				}
			}
			uniqueRandInts[i] = randInt;
		}

		return uniqueRandInts;
	}
}
