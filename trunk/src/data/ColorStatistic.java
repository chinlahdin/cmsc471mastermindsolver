package data;

public class ColorStatistic 
{
	private int count;
	private double probability;
	private String colorID;
	
	public ColorStatistic(String id)
	{
		colorID = id;
		count = 0;
		probability = 0.0;
	}
	
	public int getCount()
	{
		return this.count;
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
		return ""+colorID + ": ("+ count + ")---"+probability;
	}
}