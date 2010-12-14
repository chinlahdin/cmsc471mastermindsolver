package data;

/**
 * A Feedback object stores the number of correct colors (whites) and number of
 * correct colors and locations (blacks).
 * 
 *@author M. Curtis, M. Edoror and B. Farrington
 * 
 */
public class Feedback
{
	private static final float WHITE_VALUE = 1.0f;
	private static final float BLACK_VALUE = 1.5f;

	private int white;
	private int black;

	/**
	 * Constructs a feedback with a specific number of blacks and whites.
	 * 
	 * @param white
	 *            Number of whites.
	 * @param black
	 *            Number of blacks.
	 */
	public Feedback(int white, int black)
	{
		this.white = white;
		this.black = black;
	}

	/**
	 * 
	 * @return The number of whites.
	 */
	public int getWhite()
	{
		return white;
	}

	/**
	 * 
	 * @return The number of blacks.
	 */
	public int getBlack()
	{
		return black;
	}

	/**
	 * Return the number of both blacks and whites.
	 * 
	 * @return The number of blacks and whites.
	 */
	public int getBlackAndWhite()
	{
		return this.black + this.white;
	}

	/**
	 * Using the constants for white values and black values, returns a value.
	 * 
	 * @return Computed value using number of whites and blacks and their
	 *         respective constants.
	 */
	public float getValue()
	{
		return white * WHITE_VALUE + black * BLACK_VALUE;
	}

	/**
	 * Overridden equivalence test specifically for Feedback objects. Two
	 * Feedbacks are the same if they contain the same number of blacks and
	 * whites.
	 */
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (this.getClass() != obj.getClass())
			return false;
		Feedback otherScore = (Feedback) obj;
		return white == otherScore.white && black == otherScore.black;
	}

	/**
	 * Overridden method that displays the number of blacks and whites for this
	 * feedback.
	 */
	public String toString()
	{
		String returnString = "";
		for (int i = 0; i < black; i++)
			returnString += 'b';
		for (int i = 0; i < white; i++)
			returnString += 'w';
		return returnString;
	}
}
