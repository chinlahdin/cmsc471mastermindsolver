package testing;

import java.util.Scanner;

import game.Guesser;
import game.SmartRandomGuesser;
import game.KnuthGuesser;
import game.Oracle;
import data.CodeSequence;
import data.Feedback;

public class Tester 
{
        private static final String VERBOSITY_ON = "-v";
        private static final String USE_KNUTH_4X6 = "-k";
        private static final String NR_PEGS = "-p";
        private static final String NR_COLORS = "-c";
        private static final String NR_GAMES = "-g";
        
        private static Scanner in = null;
        private static String fileName = null;
        private static boolean verbose = false;
        private static boolean knuth = false;
        private static int nrPegs = 0;
        private static int nrColors = 0;
        private static int nrGames = 0;
        
        private static void verifyArgsAndSetup( String[] args )
        {
                for( int i = 0; i < args.length; i++ )
                        if( args[i].equals( VERBOSITY_ON ) )
                                verbose = true;
                        else if( args[i].equals( USE_KNUTH_4X6 ) )
                        		knuth = true;
                        else if( args[i].equals( NR_PEGS ) )
                                nrPegs = Integer.parseInt(args[++i]);
                        else if( args[i].equals( NR_COLORS ) )
                                nrColors = Integer.parseInt(args[++i]);
                        else if( args[i].equals( NR_GAMES ) )
                                nrGames = Integer.parseInt(args[++i]);
                        else if( fileName == null )
                                fileName = args[i];
                        else
                                System.out.println( "Unknown command line argument - \"" + args[i] + "\"" );
                
                if( fileName == null )
                        in = new Scanner( System.in );
        }
        
        private static Oracle initOracle()
        {
                if( fileName != null )
                {
                	if( knuth && !(nrPegs == 4 && nrColors == 6))
                    {
                    	knuth = false;
                    }
                	
                	return new Oracle( fileName );
                }
                
                if( knuth )
                {
                	nrPegs = 4;
                	nrColors = 6;
                }
                else
                {
                	if( nrPegs <= 0 )
                	{
                        System.out.print( "Enter number of pegs for mastermind: " );
                        nrPegs = in.nextInt(); in.nextLine();
                	}
                
                	if( nrColors <= 0 )
                	{
                		System.out.print( "Enter number of peg colors for mastermind: " );
                        nrColors = in.nextInt(); in.nextLine();
                	}
                	
                	System.out.println( "" );
                }
                
                if( nrGames <= 0 )
                	nrGames = 1;
                        
                return new Oracle( nrPegs, nrColors );
        }
        
        public static void main( String[] args )
        {
                verifyArgsAndSetup(args);
                Oracle oracle = initOracle();
                Guesser guesser;
                
                if( knuth )
                	guesser = new KnuthGuesser();
                else
                	guesser = new SmartRandomGuesser(oracle.getNumPegs(),oracle.getNumPegColors());
                                
                int nrGamesPlayed = 0;
                int nrGuessesTotal = 0;
                int nrGuessesPerGame;
                int maxNrGuessesForOneGame = 0;
                int nrGamesAtMaxGuesses = 0;
                
                while(oracle.hasCodeToUseFromFile() || nrGamesPlayed < nrGames) 
                {
                        nrGuessesPerGame = 0;
                        
                        if(verbose)
                                System.out.println( "\n----------------------------------------------" );
                        
                        while(true)
                        {
                                CodeSequence guess = guesser.guess();
                                Feedback feedback = oracle.getFeedbackFor(guess);
                                guesser.giveFeedbackForLastGuess(feedback);
                                nrGuessesPerGame++;
                        
                                if(verbose)
                                {
                                        System.out.print( "GUESS: " + guess );
                                        System.out.println( "\tFEEDBACK:" + feedback );
                                }
                        
                                if(feedback.getBlack() == oracle.getNumPegs())
                                        break;
                        }
                        nrGamesPlayed++;
                        nrGuessesTotal += nrGuessesPerGame;
                        if( nrGuessesPerGame > maxNrGuessesForOneGame )
                        {
                                maxNrGuessesForOneGame = nrGuessesPerGame;
                                nrGamesAtMaxGuesses = 1;
                                //System.out.println( guesser );
                        }
                        else if( nrGuessesPerGame == maxNrGuessesForOneGame )
                        {
                                nrGamesAtMaxGuesses++;
                        }
                        
                        if(verbose)
                        {
                                System.out.println( "\nSOLVED GAME #" + nrGamesPlayed + " IN "+ nrGuessesPerGame + " GUESSES!" );
                                System.out.println( "----------------------------------------------\n" );
                        }
                        
                        oracle.generateNextCode();
                        guesser.reset();
                }
                
                System.out.println( "Played " + nrGamesPlayed + " game" + (nrGamesPlayed > 1 ? "s" : "") + " with " + oracle.getNumPegs() + 
                                                        " PEGS and " + oracle.getNumPegColors() + " COLORS..." );
                System.out.println( "\tAVERGAGE NUMBER OF GUESSES: " + nrGuessesTotal / (float)nrGamesPlayed );
                System.out.println( "\t MAXIMUM NUMBER OF GUESSES: " + maxNrGuessesForOneGame );
                System.out.println( "\t   PERCENT OF GAMES AT MAX: " + nrGamesAtMaxGuesses / (float)nrGamesPlayed );
        }
}
