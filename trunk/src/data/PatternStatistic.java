package data;

public class PatternStatistic
{
	private int count;
	private double probability;
	private String colorID;

	public PatternStatistic(String id)
	{
		colorID = id;
		count = 0;
		probability = 0.0;
	}

	public int getCount()
	{
		return this.count;
	}

	public String getRegex()
	{
		return colorID;
	}

	public double getProbability(int instances)
	{
		probability = (1.0 * count) / (1.0 * instances);
		return this.probability;
	}

	public void increaseCount()
	{
		this.count++;
	}

	public String toString()
	{
		return "" + colorID + ": (" + count + ")---" + probability;
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