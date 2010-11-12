package testing;

import game.Guesser;
import game.Oracle;

import java.util.ArrayList;
import java.util.Scanner;

import data.CodeSequence;
import data.Feedback;
import data.RandomGuess;

public class Tester 
{
	private static Scanner in = new Scanner( System.in );
	
	public static void main( String[] args )
	{
		int nrColors;
		int nrPegs;
				
		System.out.print( "Enter number of pegs for mastermind: " );
		nrPegs = in.nextInt(); in.nextLine();
		System.out.print( "Enter number of peg colors for mastermind: " );
		nrColors = in.nextInt(); in.nextLine();
		
		Oracle oracle = new Oracle(nrPegs, nrColors);
		Guesser guesser = new Guesser(nrPegs, nrColors);
		
		
		while(true) 
		{
			CodeSequence guess = guesser.guess();
			Feedback feedback = oracle.getFeedbackFor(guess);
			guesser.giveFeedbackForLastGuess(feedback);
			
			System.out.print( "\nGUESS: " + guess );
			System.out.println( "\tSCORE:" + feedback );
			
			if( feedback.getBlack() == nrPegs )
				break;
		}
			
		System.out.println( "\nWINNER!" );
	}
}
