package game;

import java.util.ArrayList;

import data.CodeSequence;
import data.ColorSpace;
import data.Feedback;
import data.RandomGuess;

public class SmartRandomGuesser implements Guesser 
{
        private ColorSpace pegColors;
        private int nrPegs;
        private ArrayList<CodeSequence> guesses;
        private ArrayList<Feedback> feedbackForGuesses;
        private int bestGuessIndex;
        private ColorSpace workingColorSpace;
        
        boolean guessMatchesAll;
        
        public SmartRandomGuesser(int nrPegs, int nrPegColors)
        {
                this.nrPegs = nrPegs;
                pegColors = new ColorSpace(nrPegColors);
                reset();
        }
        
        public void reset()
        {
                guesses = new ArrayList<CodeSequence>();
                feedbackForGuesses = new ArrayList<Feedback>();
                bestGuessIndex = -1;
                workingColorSpace = new ColorSpace(pegColors);
        }
 
        public CodeSequence guess()
    	{
    		if( guesses.size() != feedbackForGuesses.size() )
    			throw new RuntimeException("Can't guess again until feedback is received for last guess");
    		
    		CodeSequence guess = null;
    		boolean guessMatchesAll = false;
    		int nrGuessesGenerated = 0;
    		
   			while(!guessMatchesAll) 
   			{ 
   				if( feedbackForGuesses.isEmpty() || feedbackForGuesses.get( bestGuessIndex).getBlack() + feedbackForGuesses.get(bestGuessIndex).getWhite() == 0 )
   					guess = new RandomGuess( workingColorSpace, nrPegs );
   				else
    				guess = new RandomGuess( workingColorSpace, guesses.get(bestGuessIndex), feedbackForGuesses.get(bestGuessIndex) );
    		
   				int i;
   				for(i = 0; i < guesses.size(); i++ )
   					if( guess.equals( guesses.get(i) ) || !guess.getFeedbackFor( guesses.get(i) ).equals( feedbackForGuesses.get(i) ) )
   						break;
   				if( i == guesses.size() )
   					guessMatchesAll = true;
   				nrGuessesGenerated++;
   			}
 
   			System.out.println( nrGuessesGenerated + " guesses generated..." );
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
                               workingColorSpace.removeColor(lastGuess.getPegColorAt(i));
                
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
