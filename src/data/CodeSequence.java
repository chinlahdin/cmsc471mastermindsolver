package data;

public class CodeSequence 
{
	protected static final char INVALID_PEG_COLOR = '!';
	
	private char[] pegs;
		
	public CodeSequence(char[] pegs) 
	{
		this.pegs = new char[pegs.length];
		System.arraycopy(pegs, 0, this.pegs, 0, pegs.length);
	}
	
	public Feedback getFeedbackFor(CodeSequence codeSequence )
	{
		if( codeSequence.pegs.length != pegs.length )
			return null;
		
		int white = 0;
		int black = 0;
		
		char[] checkedArgumentPegs = new char[pegs.length];
		char[] checkedPegs = new char[pegs.length];
		System.arraycopy(codeSequence.pegs, 0, checkedArgumentPegs, 0, pegs.length);
		System.arraycopy(pegs, 0, checkedPegs, 0, pegs.length);
		
		for( int i = 0; i < pegs.length; i++ )
			if (checkedPegs[i] == checkedArgumentPegs[i] )
			{
				checkedPegs[i] = INVALID_PEG_COLOR;
				checkedArgumentPegs[i] = INVALID_PEG_COLOR + 1;
				black++;
			}
		
		for( int i = 0; i < pegs.length; i++ )
			for( int j = 0; j < pegs.length; j++ )
				if (checkedPegs[i] == checkedArgumentPegs[j] )
				{
					checkedPegs[i] = INVALID_PEG_COLOR;
					checkedArgumentPegs[j] = INVALID_PEG_COLOR + 1;
					white++;
				}
		
		return new Feedback(white, black); 
	}
	
	public int getNrPegs()
	{
		return pegs.length;
	}
	
	public char getPegColorAt(int pegNr)
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
		CodeSequence otherGuess = (CodeSequence)obj;
		
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

