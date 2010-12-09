package data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternStatistic
{
	private int count;
	private double probability;
	private String regex;
	private int length;

	public PatternStatistic(String id)
	{
		Pattern pattern;
		Matcher matcher;
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

	public int getCount()
	{
		return this.count;
	}

	public String getRegex()
	{
		return regex;
	}

	public double getProbability(int instances)
	{
		probability = (1.0 * count) / (1.0 * instances / length);
		return this.probability;
	}
	public double getProbability()
	{
		return this.probability;
	}

	public void increaseCount()
	{
		this.count++;
	}
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
}