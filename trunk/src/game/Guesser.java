package game;

import java.util.ArrayList;

import data.CodeSequence;
import data.Feedback;
import data.RandomGuess;

public class Guesser 
{
	private String pegColors;
	private int nrPegs;
	private ArrayList<CodeSequence> guesses;
	private ArrayList<Feedback> feedbackForGuesses;
	private int bestGuessIndex;
	private String workingColorSpace;
	
	boolean guessMatchesAll;
	
	public Guesser(int nrPegs, int nrPegColors)
	{
		this.nrPegs = nrPegs;
		populatePegColors(nrPegColors);
		reset();
	}
	
	public void reset()
	{
		guesses = new ArrayList<CodeSequence>();
		feedbackForGuesses = new ArrayList<Feedback>();
		bestGuessIndex = -1;
		workingColorSpace = pegColors;
	}
	
	private void populatePegColors(int nrPegColors)
	{
		pegColors = "";
		for(int i = 0; i < nrPegColors; i++)
			pegColors += (char)('A' + i) + "";
	}
	
	public CodeSequence guess()
	{
		if( guesses.size() != feedbackForGuesses.size() )
			throw new RuntimeException("Can't guess again until feedback is received for last guess");
		
		CodeSequence guess = null;
		CodeSequence lastGuess = null;
		Feedback lastFeedback = null;
		boolean guessMatchesAll;
		
		if (guesses.size() == 0 )
			guess = new RandomGuess( pegColors, nrPegs );
		else
		{	
			guessMatchesAll = false;
			while(!guessMatchesAll) 
			{ 
				lastFeedback = feedbackForGuesses.get(feedbackForGuesses.size() - 1);
				lastGuess = guesses.get(guesses.size() - 1 );
				
				if( lastFeedback.getBlack() + lastFeedback.getWhite() == nrPegs )				
					guess = new RandomGuess(lastGuess);
				else if( feedbackForGuesses.size() < 1 || 
						(feedbackForGuesses.get(bestGuessIndex).getBlack() + feedbackForGuesses.get(bestGuessIndex).getWhite() == 0 ) )
					guess = new RandomGuess( workingColorSpace, nrPegs );
				else
					guess = new RandomGuess( workingColorSpace, guesses.get(bestGuessIndex), feedbackForGuesses.get(bestGuessIndex) );
		
				int i;
				for(i = 0; i < guesses.size(); i++ )
					if( guess.equals( guesses.get(i) ) ||
							( //( nrPegs < 8 || lastFeedback.getBlack() + lastFeedback.getWhite() == nrPegs || i == guesses.size() - 1 ) &&
									!guess.getFeedbackFor( guesses.get(i) ).equals( feedbackForGuesses.get(i) ) ) )
						break;
				if( i == guesses.size() )
					guessMatchesAll = true;
			}
		}
			
		guesses.add(guess);
		return guess;
	}
	
	public void giveFeedbackForLastGuess(Feedback feedback)
	{
		if(feedbackForGuesses.size() != guesses.size() - 1)
			throw new RuntimeException("Already gave feedback for last guess");
		
		feedbackForGuesses.add(feedback);
		
		CodeSequence lastGuess = guesses.get(guesses.size() - 1);
		if(feedback.getBlack() + feedback.getWhite() == 0)
			for( int i = 0; i < lastGuess.getNrPegs(); i++ ) 
				workingColorSpace = workingColorSpace.replace(lastGuess.getPegColorAt(i) + "", "");
		
		if(bestGuessIndex < 0 || feedback.getValue() > feedbackForGuesses.get(bestGuessIndex).getValue())
			bestGuessIndex = feedbackForGuesses.size() - 1;
	}
	
	public String toString()
	{
		String returnString = "";
		for( int i = 0; i < feedbackForGuesses.size(); i++ )
		{
			returnString += "GUESS: " + guesses.get(i) + "\tFEEDBACK:" + feedbackForGuesses.get(i) + "\n";
		}
		return returnString;
	}
}
