package data;

import java.util.Random;

public class RandomGuess extends CodeSequence
{
	public RandomGuess(String colors, int nrPegs) 
	{
		super(makeTotallyRandomGuess(colors, nrPegs));
	}
	
	public RandomGuess(CodeSequence sequenceToPermute) 
	{
		super(makeRandomPermutationGuess(sequenceToPermute));
	}
	
	private static char[] makeTotallyRandomGuess(String possiblePegColors, int nrPegs)
	{
		char[] pegs = new char[nrPegs];
		Random rng = new Random();
		for( int i = 0; i < nrPegs; i++ )
			pegs[i] = possiblePegColors.charAt(rng.nextInt(possiblePegColors.length()));
		return pegs;
	}
	
	private static char[] makeRandomPermutationGuess(CodeSequence sequenceToPermute)
	{
		char[] pegs = new char[sequenceToPermute.getNrPegs()];
		for( int i = 0; i < pegs.length; i++ )
			pegs[i] = sequenceToPermute.getPegColorAt(i);
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
		return pegs;
	}
}
