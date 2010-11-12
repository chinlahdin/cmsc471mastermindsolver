package testing;

import java.util.ArrayList;
import java.util.Scanner;

import data.Guess;
import data.Score;

public class Tester 
{
	private static Scanner in = new Scanner( System.in );
	
	public static void main( String[] args )
	{
		Guess code;
		Guess guess = null;
		int nrColors;
		int nrPegs;
		String codeInput;
		Score score = null;
		ArrayList<Guess> guesses = new ArrayList<Guess>();
		ArrayList<Score> scores = new ArrayList<Score>();
		String colors = "";
		boolean guessMatchesAll;
		
		System.out.print( "Enter number of colors for mastermind: " );
		nrColors = in.nextInt(); in.nextLine();
		System.out.print( "Enter a mastermind code ( A - " + 	
									(char)('A' + nrColors - 1) + " ): " );
		codeInput = in.next();
		code = new Guess( codeInput.toCharArray() );
		nrPegs = codeInput.length();
		for( char c = 'A'; c < nrColors + 'A'; c++ )
			colors += c;
		
		do
		{
			if (guess == null )
				guess = new Guess( colors, nrPegs );
			else
			{
				guessMatchesAll = false;
				while(!guessMatchesAll) 
				{ 
					if( score.getBlack() + score.getWhite() == nrPegs )				
						guess = new Guess(guess);
					else
						guess = new Guess( colors, nrPegs );
					
					int i;
					for(i = 0; i < guesses.size(); i++ )
						if( guess.equals( guesses.get(i) ) ||
							(( nrPegs < 8 || score.getBlack() + score.getWhite() == nrPegs || i == guesses.size() - 1 ) &&
							 !guess.getScoreFor( guesses.get(i) ).equals( scores.get(i) ) ) )
							break;
					if( i == guesses.size() )
						guessMatchesAll = true;
				}
			}
				
			score = guess.getScoreFor( code );
			guesses.add(guess);
			scores.add(score);
			if(score.getBlack() + score.getWhite() == 0)
				for( int i = 0; i < guess.getNrPegs(); i++ ) 
					colors = colors.replace(guess.getPegColorAt(i) + "", "");
			
			System.out.print( "\nGUESS: " + guess );
			System.out.print( "\tSCORE:" + score );
			System.out.println( "\tPOSSIBLE COLORS: " + colors);
			
		} while( score.getBlack() != codeInput.length() ); 
			
		System.out.println( "WINNER!" );
	}
}
