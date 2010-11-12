package data;

public class Score
{
	private int white;
	private int black;
	
	public Score( int white, int black )
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
	
	public boolean equals(Object obj)
	{
		if( this == obj )
			return true;
		if( this.getClass() != obj.getClass() )
			return false;
		Score otherScore = (Score)obj;
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
