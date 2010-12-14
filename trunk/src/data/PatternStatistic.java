package data;

/**
 * A PatternStatistic object holds the statistics of occurrence and counts for a
 * particular regular expression. These objects can be compared by their
 * occurrence values, the probability variable.
 * 
 * @author M. Curtis, M. Edoror and B. Farrington
 * 
 */
public class PatternStatistic implements Comparable<PatternStatistic>
{
	private int count;
	private double probability;
	private String regex;
	private int length;

	/**
	 * Constructs a pattern statistic with a regex.
	 * 
	 * @param id
	 *            Regex to identify this pattern.
	 */
	public PatternStatistic(String id)
	{
		regex = id;
		count = 0;
		int whitespace = 0;
		probability = 0.0;
		char[] chars = id.toCharArray();
		for (int i = 0; i < id.length(); ++i)
		{
			if (chars[i] == ' ')
				whitespace++;
		}
		length = id.length() - whitespace;
	}

	/**
	 * 
	 * @return The number of times this regular expression has been witnessed.
	 */
	public int getCount()
	{
		return this.count;
	}

	/**
	 * 
	 * @return The pattern for this statistic.
	 */
	public String getRegex()
	{
		return regex;
	}

	/**
	 * Divides current count by number of possible occurrences, which is given.
	 * 
	 * @param instances
	 *            Number of maximum occurrences possible for this pattern.
	 * @return The occurrence rate.
	 */
	public double calcProbability(int instances)
	{
		probability = (1.0 * count) / (1.0 * instances / length);
		return this.probability;
	}

	/**
	 * 
	 * @return The current occurrence rate.
	 */
	public double getProbability()
	{
		return this.probability;
	}

	/**
	 * Increases the count of this pattern-statistic.
	 */
	public void increaseCount()
	{
		this.count++;
	}

	/**
	 * 
	 * @return The length of the regex, which is the number of pegs it occupies.
	 */
	public int length()
	{
		return this.length;
	}

	public String toString()
	{
		return "" + regex + ": (" + count + ")---" + probability;
	}

	public boolean equals(Object o)
	{
		if (o.getClass().equals(this.getClass()))
		{
			return ((PatternStatistic) o).getRegex().equalsIgnoreCase(
					this.getRegex()) ? true : false;

		}
		return false;

	}

	@Override
	public int compareTo(PatternStatistic o)
	{

		if (o.probability == this.probability)
			return 0;
		else
		{
			return (o.probability > this.probability) ? -1 : 1;
		}
	}
}