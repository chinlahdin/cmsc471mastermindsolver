package data;

public class Feedback
{
	private static final float WHITE_VALUE = 1.0f;
	private static final float BLACK_VALUE = 1.5f;
	
	private int white;
	private int black;
	
	public Feedback( int white, int black )
	{
		this.white = white;
		this.black = black;
	}
	
	public int getWhite()
	{
		return white;
	}
	
	public int getBlack()
	{
		return black;
	}

	public int getBlackAndWhite(){
		return this.black + this.white;
	}
	
	public float getValue()
	{
		return white * WHITE_VALUE + black * BLACK_VALUE;
	}
	
	public boolean equals(Object obj)
	{
		if( this == obj )
			return true;
		if( this.getClass() != obj.getClass() )
			return false;
		Feedback otherScore = (Feedback)obj;
		return white == otherScore.white && black == otherScore.black;
	}
	
	public String toString()
	{
		String returnString = "";
		for( int i = 0; i < black; i++ )
			returnString += 'b';
		for( int i = 0; i < white; i++ )
			returnString += 'w';
		return returnString;
	}
}
