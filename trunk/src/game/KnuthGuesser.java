package game;

import java.util.ArrayList;
import java.util.Iterator;

import data.CodeSequence;
import data.Feedback;

public class KnuthGuesser implements Guesser
{
	private static final int GUESS_SPACE_SIZE = 1296;
	private static final int FEEDBACK_SPACE_SIZE = 14;
	
	private ArrayList<CodeSequence> currentGuessSpace;
	private ArrayList<CodeSequence> entireGuessSpace;
	private ArrayList<Feedback> feedbackSpace;
	private CodeSequence lastGuess;
	private Feedback lastFeedback;
	
	public KnuthGuesser()
	{
		reset();
		entireGuessSpace = createGuessSpace();
		feedbackSpace = createFeedbackSpace();
	}
	
	public void giveFeedbackForLastGuess(Feedback feedback) 
	{
		lastFeedback = feedback;
	}

	
	public CodeSequence guess() 
	{
		if( lastGuess == null )
		{
			lastGuess = new CodeSequence( new int[]{1, 1, 2, 3 } );
			return lastGuess;
		}
				
		removeImpossibleGuessesFromGuessSpace();
		
		if( currentGuessSpace.size() == 1 )
			return currentGuessSpace.get( 0 );
		
		CodeSequence bestGuess = null;
		int bestSpaceReduction = 0;
		int currentLowestReductionForGuess;
		int reductionForGuess;
		
		for( int i = 0; i < entireGuessSpace.size(); i++ )
		{
			currentLowestReductionForGuess = GUESS_SPACE_SIZE;
			for( int j = 0; j < feedbackSpace.size(); j++ )
			{
				reductionForGuess = getCountOfImpossibleGuesses( entireGuessSpace.get(i), feedbackSpace.get(j) );

				if( reductionForGuess < currentLowestReductionForGuess )
				{
					currentLowestReductionForGuess = reductionForGuess;
				}
			}
			
			if( bestGuess == null || currentLowestReductionForGuess > bestSpaceReduction )
			{
				bestGuess = entireGuessSpace.get(i);
				bestSpaceReduction = currentLowestReductionForGuess;
			}
		}
		
		lastGuess = bestGuess;
		return bestGuess;
	}

	private void removeImpossibleGuessesFromGuessSpace()
	{
		Iterator<CodeSequence> itr = currentGuessSpace.listIterator();
		CodeSequence currentCode = null;
		
		while( itr.hasNext() )
		{
			currentCode = itr.next();
			if( !currentCode.getFeedbackFor( lastGuess ).equals( lastFeedback ) )
				itr.remove();
		}
	}
	
	private int getCountOfImpossibleGuesses( CodeSequence guess, Feedback feedback )
	{
		int nrOfImpossibleGuesses = 0;
		Iterator<CodeSequence> itr = currentGuessSpace.listIterator();
		CodeSequence currentCode = null;
		
		while( itr.hasNext() )
		{
			currentCode = itr.next();
			if( !currentCode.getFeedbackFor( guess ).equals( feedback ) )
			{
				nrOfImpossibleGuesses++;
			}
		}
		
		return nrOfImpossibleGuesses;
	}
	
	public void reset() 
	{
		currentGuessSpace = createGuessSpace();
		lastGuess = null;
		lastFeedback = null;
	}
	
	private ArrayList<CodeSequence> createGuessSpace()
	{
		ArrayList<CodeSequence> guessSpace = new ArrayList<CodeSequence>();
		
		int[] code = {1, 1, 1, 1 };
		for( int i = 0; i < GUESS_SPACE_SIZE; i++ )
		{
			guessSpace.add(new CodeSequence(code));
			for( int j = 3; j >= 0; j-- )
			{
				if( code[j] == 6 )
				{
					code[j] = 1;
				}
				else
				{
					code[j]++;
					break;
				}
			}
		}
		
		return guessSpace;
	}
	
	private ArrayList<Feedback> createFeedbackSpace()
	{
		ArrayList<Feedback> feedbackSpace = new ArrayList<Feedback>();
		
		int whiteCount = 0;
		int blackCount = 0;
		for( int i = 0; i < FEEDBACK_SPACE_SIZE; i++ )
		{
			feedbackSpace.add( new Feedback( whiteCount, blackCount ) );
			blackCount++;
			if( blackCount + whiteCount > 4 || (blackCount == 3 && whiteCount == 1) )
			{
				blackCount = 0;
				whiteCount++;
			}
		}
		
		return feedbackSpace;
	}
}
