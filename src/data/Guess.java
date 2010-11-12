package data;

import java.util.Random;

public class Guess {
	private char[] pegs;
	private static final char INVALID_PEG_COLOR = '!';
	
	public Guess(String colors, int nrPegs) {
		pegs = new char[nrPegs];
		Random rng = new Random();
		for( int i = 0; i < nrPegs; i++ )
			pegs[i] = colors.charAt(rng.nextInt(colors.length()));
	}
	
	public Guess(char[] pegs) 
	{
		this.pegs = new char[pegs.length];
		System.arraycopy(pegs, 0, this.pegs, 0, pegs.length);
	}
	
	public Guess(Guess guess) 
	{
		this.pegs = new char[guess.pegs.length];
		System.arraycopy(guess.pegs, 0, pegs, 0, pegs.length);
		Random rng = new Random();
		char temp;
		int rndIndex;
		for( int i = pegs.length - 1; i >= 1; i-- )
		{
			rndIndex = rng.nextInt(i + 1);
			temp = pegs[i];
			pegs[i] = pegs[rndIndex];
			pegs[rndIndex] = temp;
		}
	}

	public Score getScoreFor(Guess solution) 
	{
		if( solution.pegs.length != pegs.length )
			return null;
		
		int white = 0;
		int black = 0;
		
		char[] checkedSolutionPegs = new char[pegs.length];
		char[] checkedGuessPegs = new char[pegs.length];
		System.arraycopy(solution.pegs, 0, checkedSolutionPegs, 0, pegs.length);
		System.arraycopy(pegs, 0, checkedGuessPegs, 0, pegs.length);
		
		for( int i = 0; i < pegs.length; i++ )
			if (checkedGuessPegs[i] == checkedSolutionPegs[i] )
			{
				checkedGuessPegs[i] = INVALID_PEG_COLOR;
				checkedSolutionPegs[i] = INVALID_PEG_COLOR + 1;
				black++;
			}
		
		for( int i = 0; i < pegs.length; i++ )
			for( int j = 0; j < pegs.length; j++ )
				if (checkedGuessPegs[i] == checkedSolutionPegs[j] )
				{
					checkedGuessPegs[i] = INVALID_PEG_COLOR;
					checkedSolutionPegs[j] = INVALID_PEG_COLOR + 1;
					white++;
				}
		
		return new Score( white, black ); 
	}
	
	
	public int getNrPegs()
	{
		return pegs.length;
	}
	
	public char getPegColorAt( int pegNr )
	{
		if( pegNr < 0 || pegNr >= pegs.length )
			return INVALID_PEG_COLOR;
		return pegs[pegNr];
	}
	
	
	public boolean equals(Object obj)
	{
		if( this == obj )
			return true;
		if( this.getClass() != obj.getClass() )
			return false;
		Guess otherGuess = (Guess)obj;
		
		for( int i = 0; i < pegs.length; i++ )
			if( pegs[i] != otherGuess.pegs[i] )
				return false;
		return true;
	}
	
	public String toString()
	{
		return new String(pegs);
	}
}
