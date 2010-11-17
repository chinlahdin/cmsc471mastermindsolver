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
	//private static Scanner in = new Scanner( System.in );
	
	public static void main( String[] args )
	{
		/*int nrColors;
		int nrPegs;
		int nrGuesses = 0;
				
		System.out.print( "Enter number of pegs for mastermind: " );
		nrPegs = in.nextInt(); in.nextLine();
		System.out.print( "Enter number of peg colors for mastermind: " );
		nrColors = in.nextInt(); in.nextLine();
		
		Oracle oracle = new Oracle(nrPegs, nrColors);
		Guesser guesser = new Guesser(nrPegs, nrColors);*/
		
		//start reading from the file
		Oracle oracle= new Oracle(args[0]);
		Guesser guesser = new Guesser(oracle.getNumPegs(),oracle.getNumPegColors());
		//test to see if the codes are there
		//System.out.println(oracle.getCodes());
		
		while(!oracle.hasCodeToUseFromFile()){
		
			int nrGuesses = 0;
			while(true) 
			{	
				nrGuesses++;
				CodeSequence guess = guesser.guess();
				Feedback feedback = oracle.getFeedbackFor(guess);
				guesser.giveFeedbackForLastGuess(feedback);
			
				System.out.print( "\nGUESS: " + guess );
				System.out.println( "\tSCORE:" + feedback );
			
				if( feedback.getBlack() == oracle.getNumPegs())
					break;
			}
			
		System.out.println( "\nSOLVED IN " + nrGuesses + " GUESSES!" );
		}
	}
}
