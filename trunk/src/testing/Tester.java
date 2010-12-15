package testing;

import game.Guesser;
import game.KnuthGuesser;
import game.Learner;
import game.Oracle;
import game.SmartRandomGuesser;

import java.util.Scanner;

import data.CodeSequence;
import data.Feedback;

/**
 * This is the main driver for our project which runs games between
 * an Oracle and a Guesser.  There are several optional
 * command-line arguments for altering the number of pegs,
 * colors, games, degree of output, whether or not to use
 * Knuth's five guess algorithm as the Guesser, whether or not
 * to use a Guesser which learns, and whether or not to have
 * the Oracle use codes from a file or randomly generate its own.
 * 
 *  Usage:
 *  -v       	(Turns on verbose output)
 *  -k	     	(Limits the number of colors and pegs to the standard 6 and 4, and uses the Knuth Guesser)
 *  -l		 	(Uses the Learner Guesser which takes into account information across Games)
 *  -p <int> 	(Sets the number of pegs to the specified value)
 *  -c <int> 	(Sets the number of colors to the specified value)
 *  -g <int> 	(Sets the number of games to be played to the specified value)
 *  -b <String>	(Turns on bias logic for the specified bias by way of a bias tag String-- was used for testing)
 *  <String>	(If a valid Oracle file, sets the Oracle to use codes from this file-- the number of pegs, colors, and
 *               games will be determined by the file and the corresponding command-line arguments will be ignored; also
 *               overrides the Knuth Guesser option if the Oracle file doesn't contain standard (4x6) game codes)
 * 
 * @author M. Curtis, M. Edoror and B. Farrington
 */
public class Tester
{
	private static final String VERBOSITY_ON = "-v";
	private static final String USE_KNUTH_4X6 = "-k";
	private static final String LEARNING = "-l";
	private static final String NR_PEGS = "-p";
	private static final String NR_COLORS = "-c";
	private static final String NR_GAMES = "-g";
	private static final String BIAS_NUM = "-b";

	private static Scanner in = null;
	private static String fileName = null;
	private static boolean verbose = false;
	private static boolean knuth = false;
	private static boolean learn = false;
	private static int nrPegs = 0;
	private static int nrColors = 0;
	private static int nrGames = 0;
	private static String bias = "";

	/**
	 * Sets up the command line arguments.
	 * 
	 * @param args
	 *            VERBOSITY_ON = "-v"; USE_KNUTH_4X6 = "-k"; LEARNING = "-l";
	 *            NR_PEGS = "-p"; NR_COLORS = "-c"; NR_GAMES = "-g";BIAS_NUM =
	 *            "-b";
	 */
	private static void verifyArgsAndSetup(String[] args)
	{
		for (int i = 0; i < args.length; i++)
			if (args[i].equals(VERBOSITY_ON))
				verbose = true;
			else if (args[i].equals(USE_KNUTH_4X6))
				knuth = true;
			else if (args[i].equals(LEARNING))
				learn = true;
			else if (args[i].equals(NR_PEGS))
				nrPegs = Integer.parseInt(args[++i]);
			else if (args[i].equals(NR_COLORS))
				nrColors = Integer.parseInt(args[++i]);
			else if (args[i].equals(NR_GAMES))
				nrGames = Integer.parseInt(args[++i]);
			else if (args[i].equals(BIAS_NUM))
				bias = args[++i];
			else if (fileName == null)
				fileName = args[i];
			else
				System.out.println("Unknown command line argument - \""
						+ args[i] + "\"");

		if (fileName == null)
			in = new Scanner(System.in);
	}

	/**
	 * Creates the code maker.
	 * 
	 * @return
	 */
	private static Oracle initOracle()
	{
		if (fileName != null)
		{
			Oracle oracle = new Oracle(fileName);
			if (knuth
					&& !(oracle.getNumPegs() == 4 && oracle.getNumPegColors() == 6))
			{
				knuth = false;
			}

			return oracle;
		}

		if (knuth)
		{
			nrPegs = 4;
			nrColors = 6;
		}
		else
		{
			if (nrPegs <= 0)
			{
				System.out.print("Enter number of pegs for mastermind: ");
				nrPegs = in.nextInt();
				in.nextLine();
			}

			if (nrColors <= 0)
			{
				System.out.print("Enter number of peg colors for mastermind: ");
				nrColors = in.nextInt();
				in.nextLine();
			}

			System.out.println("");
		}

		if (nrGames <= 0)
			nrGames = 1;

		return new Oracle(nrPegs, nrColors);
	}

	/**
	 * Main method for execution.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		double elapsedTime = 0.0;
		long start = System.currentTimeMillis();
		// long elapsed = 0;
		verifyArgsAndSetup(args);
		Oracle oracle = initOracle();
		Guesser guesser;

		if (knuth)
			guesser = new KnuthGuesser();
		else if (learn)
			guesser = new Learner(oracle.getNumPegs(), oracle.getNumPegColors());
		else
			guesser = new SmartRandomGuesser(oracle.getNumPegs(),
					oracle.getNumPegColors(),
					bias);

		int nrGamesPlayed = 0;
		int nrGuessesTotal = 0;
		int nrGuessesPerGame;
		int maxNrGuessesForOneGame = 0;
		int nrGamesAtMaxGuesses = 0;

		while (oracle.hasCodeToUseFromFile() || nrGamesPlayed < nrGames)
		{
			nrGuessesPerGame = 0;

			if (verbose)
				System.out.println("\n----------------------------------------------");

			while (true)
			{
				CodeSequence guess = guesser.guess();
				Feedback feedback = oracle.getFeedbackFor(guess);
				guesser.giveFeedbackForLastGuess(feedback);
				nrGuessesPerGame++;

				if (verbose)
				{
					System.out.print("GUESS: " + guess);
					System.out.println("\tFEEDBACK:" + feedback + "\n");
				}

				if (feedback.getBlack() == oracle.getNumPegs())
					break;
			}
			nrGamesPlayed++;
			nrGuessesTotal += nrGuessesPerGame;
			if (nrGuessesPerGame > maxNrGuessesForOneGame)
			{
				maxNrGuessesForOneGame = nrGuessesPerGame;
				nrGamesAtMaxGuesses = 1;
				// System.out.println( guesser );
			}
			else if (nrGuessesPerGame == maxNrGuessesForOneGame)
			{
				nrGamesAtMaxGuesses++;
			}

			if (verbose)
			{
				System.out.println("\nSOLVED GAME #" + nrGamesPlayed + " IN "
						+ nrGuessesPerGame + " GUESSES!");
				System.out.println("----------------------------------------------\n");
			}

			oracle.generateNextCode();
			guesser.reset();
			elapsedTime += (System.currentTimeMillis() - start) / 1000.0;
			start = System.currentTimeMillis();
		}


		System.out.println("CPU Time: " + elapsedTime + " in seconds");
		System.out.println("Average CPU Time " + elapsedTime
				/ (float) nrGamesPlayed + " in seconds");
		System.out.println("Played " + nrGamesPlayed + " game"
				+ (nrGamesPlayed > 1 ? "s" : "") + " with "
				+ oracle.getNumPegs() + " PEGS and " + oracle.getNumPegColors()
				+ " COLORS...");
		System.out.println("\tAVERGAGE NUMBER OF GUESSES: " + nrGuessesTotal
				/ (float) nrGamesPlayed);
		System.out.println("\t MAXIMUM NUMBER OF GUESSES: "
				+ maxNrGuessesForOneGame);
		System.out.println("\t   PERCENT OF GAMES AT MAX: "
				+ nrGamesAtMaxGuesses / (float) nrGamesPlayed);
	}
}
