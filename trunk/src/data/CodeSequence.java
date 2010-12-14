package data;

/**
 * A CodeSequence acts as a guess for the Mastermind game. It contains an array
 * of pegs using numbers mapped to the color space.
 * 
 * @author M. Curtis, M. Edoror and B. Farrington
 * 
 */
public class CodeSequence
{
	protected static final int INVALID_PEG_COLOR = -1;

	private int[] pegs;

	/**
	 * Constructs a CodeSequence, using an integer array for initializing
	 * values.
	 * 
	 * @param pegs
	 *            Integer array of the guess, mapping to the color space.
	 */
	public CodeSequence(int[] pegs)
	{
		this.pegs = new int[pegs.length];
		System.arraycopy(pegs, 0, this.pegs, 0, pegs.length);
	}

	/**
	 * 
	 * @param codeSequence
	 *            Using the codeSequence as a guess, get the feedback in terms
	 *            of black and white.
	 * @return A feedback object with the amount of blacks and whites for the
	 *         given guess.
	 */
	public Feedback getFeedbackFor(CodeSequence codeSequence)
	{
		if (codeSequence.pegs.length != pegs.length)
			return null;

		int white = 0;
		int black = 0;

		int[] checkedArgumentPegs = new int[pegs.length];
		int[] checkedPegs = new int[pegs.length];
		System.arraycopy(codeSequence.pegs, 0, checkedArgumentPegs, 0,
				pegs.length);
		System.arraycopy(pegs, 0, checkedPegs, 0, pegs.length);

		for (int i = 0; i < pegs.length; i++)
			if (checkedPegs[i] == checkedArgumentPegs[i])
			{
				checkedPegs[i] = INVALID_PEG_COLOR;
				checkedArgumentPegs[i] = INVALID_PEG_COLOR + 1;
				black++;
			}

		for (int i = 0; i < pegs.length; i++)
			for (int j = 0; j < pegs.length; j++)
				if (checkedPegs[i] == checkedArgumentPegs[j])
				{
					checkedPegs[i] = INVALID_PEG_COLOR;
					checkedArgumentPegs[j] = INVALID_PEG_COLOR + 1;
					white++;
				}

		return new Feedback(white, black);
	}

	/**
	 * 
	 * @return The number of pegs in the sequence.
	 */
	public int getNrPegs()
	{
		return pegs.length;
	}

	/**
	 * 
	 * @param pegNr
	 *            The slot number of the peg being queried.
	 * @return The color at the corresponding slot number.
	 */
	public int getPegColorAt(int pegNr)
	{
		if (pegNr < 0 || pegNr >= pegs.length)
			return INVALID_PEG_COLOR;
		return pegs[pegNr];
	}

	/**
	 * Overridden method for equivalence between two CodeSequences.
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (this.getClass() != obj.getClass())
			return false;
		CodeSequence otherGuess = (CodeSequence) obj;

		for (int i = 0; i < pegs.length; i++)
			if (pegs[i] != otherGuess.pegs[i])
				return false;
		return true;
	}

	/**
	 * Overridden method for displaying a CodeSequence's important properties.
	 * 
	 */
	public String toString()
	{
		String returnString = "";
		for (int i = 0; i < pegs.length - 1; i++)
			returnString += pegs[i] + " ";
		return returnString + pegs[pegs.length - 1];
	}

}
