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
	 * 
	 * @param array
	 * @return
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
	 * 
	 * @param array
	 * @return
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
	 * 
	 * @param sequence
	 * @return
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
	 * 
	 * @param permutation
	 * @param sequence
	 * @param index
	 * @return
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
	 * 
	 * @param permutation
	 * @param sequence
	 * @param index
	 * @return
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
	 * 
	 * @param nrRandInts
	 * @param upTo
	 * @return
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

	/**
	 * 
	 * @param colors
	 * @param sequenceToMatch
	 * @param feedbackToMatch
	 * @return
	 */
	private static int[] amakeRandomGuessFromSequenceFeedback(
			ColorSpace colors, CodeSequence sequenceToMatch,
			Feedback feedbackToMatch)
	{
		int[] pegs = new int[sequenceToMatch.getNrPegs()];
		Random rng = new Random();
		int[] blackIndexes = new int[feedbackToMatch.getBlack()];
		int[] whiteIndexes = new int[feedbackToMatch.getWhite()];
		ColorSpace colorSpace = new ColorSpace(colors);
		int i;
		int j;
		int k;

		for (i = 0; i < pegs.length; i++)
			pegs[i] = INVALID_PEG_COLOR;

		i = 0;
		while (i < blackIndexes.length)
		{
			blackIndexes[i] = rng.nextInt(pegs.length);
			for (j = 0; j < i; j++)
				if (blackIndexes[i] == blackIndexes[j])
					break;
			if (i == j)
				i++;
			// System.out.println("A");
		}

		i = 0;
		while (i < whiteIndexes.length)
		{
			whiteIndexes[i] = rng.nextInt(pegs.length);
			for (j = 0; j < i; j++)
				if (whiteIndexes[i] == whiteIndexes[j])
					break;
			for (k = 0; k < blackIndexes.length; k++)
				if (whiteIndexes[i] == blackIndexes[k])
					break;
			if (i == j && k == blackIndexes.length)
				i++;
			// System.out.println("B");
		}

		int[] copyOfSequenceToMatch = new int[sequenceToMatch.getNrPegs()];
		for (i = 0; i < copyOfSequenceToMatch.length; i++)
			copyOfSequenceToMatch[i] = sequenceToMatch.getPegColorAt(i);

		for (i = 0; i < blackIndexes.length; i++)
		{
			pegs[blackIndexes[i]] = copyOfSequenceToMatch[blackIndexes[i]];
			copyOfSequenceToMatch[blackIndexes[i]] = INVALID_PEG_COLOR;
		}

		for (i = 0; i < whiteIndexes.length; i++)
		{
			/*
			 * System.out.print("["); for(int x = 0; x < whiteIndexes.length;
			 * x++ ) System.out.print(whiteIndexes[x] + " ");
			 * System.out.println("]");
			 */
			do
			{
				j = rng.nextInt(pegs.length);
				// System.out.println("C");

			}
			while (j == whiteIndexes[i] || pegs[j] != INVALID_PEG_COLOR);
			pegs[j] = copyOfSequenceToMatch[whiteIndexes[i]];
			copyOfSequenceToMatch[whiteIndexes[i]] = INVALID_PEG_COLOR;
		}

		for (i = 0; i < copyOfSequenceToMatch.length; i++)
			if (copyOfSequenceToMatch[i] != INVALID_PEG_COLOR
					&& colors.length() > 1)
				colorSpace.removeColor(copyOfSequenceToMatch[i]);

		for (i = 0; i < pegs.length; i++)
			if (pegs[i] == INVALID_PEG_COLOR)
			{
				// String temp = new String(pegs);
				// System.out.println( temp );
				pegs[i] = colorSpace.getRandomColor();
			}

		return pegs;
	}
}
