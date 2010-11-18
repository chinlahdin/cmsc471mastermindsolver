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
	
	public RandomGuess(String colors, CodeSequence sequenceToMatch, Feedback feedbackToMatch)
	{
		super(makeRandomGuessFromSequenceFeedback(colors, sequenceToMatch, feedbackToMatch));
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
	
	private static char[] makeRandomGuessFromSequenceFeedback(String colors, CodeSequence sequenceToMatch, Feedback feedbackToMatch)
	{
		char[] pegs = new char[sequenceToMatch.getNrPegs()];
		Random rng = new Random();
		int[] blackIndexes = new int[feedbackToMatch.getBlack()];
		int[] whiteIndexes = new int[feedbackToMatch.getWhite()];
		int i;
		int j;
		int k;
		
		for(i = 0; i < pegs.length; i++)
			pegs[i] = INVALID_PEG_COLOR;
		
		i = 0;
		while(i < blackIndexes.length)
		{
			blackIndexes[i] = rng.nextInt(pegs.length);
			for(j = 0; j < i; j++)
				if(blackIndexes[i] == blackIndexes[j])
					break;
			if(i == j)
				i++;
			//System.out.println("A");
		}
		
		i = 0;
		while(i < whiteIndexes.length)
		{
			whiteIndexes[i] = rng.nextInt(pegs.length);
			for(j = 0; j < i; j++)
				if(whiteIndexes[i] == whiteIndexes[j])
					break;
			for(k = 0; k < blackIndexes.length; k++)
				if(whiteIndexes[i] == blackIndexes[k])
					break;
			if(i == j && k == blackIndexes.length)
				i++;
			//System.out.println("B");
		}
		
		char[] copyOfSequenceToMatch = new char[sequenceToMatch.getNrPegs()];
		for(i = 0; i < copyOfSequenceToMatch.length; i++)
			copyOfSequenceToMatch[i] = sequenceToMatch.getPegColorAt(i);
		
		for(i = 0; i < blackIndexes.length; i++)
		{
			pegs[blackIndexes[i]] = copyOfSequenceToMatch[blackIndexes[i]];
			copyOfSequenceToMatch[blackIndexes[i]] = INVALID_PEG_COLOR;
		}
		
		for(i = 0; i < whiteIndexes.length; i++)
		{
			/*System.out.print("[");
			for(int x = 0; x < whiteIndexes.length; x++ )
				System.out.print(whiteIndexes[x] + " ");
			System.out.println("]");*/
			do 
			{
				j = rng.nextInt(pegs.length);
				//System.out.println("C");
				
			} while(j == whiteIndexes[i] || pegs[j] != INVALID_PEG_COLOR);
			pegs[j] = copyOfSequenceToMatch[whiteIndexes[i]];
			copyOfSequenceToMatch[whiteIndexes[i]] = INVALID_PEG_COLOR;
		}
		
		for(i = 0; i < copyOfSequenceToMatch.length; i++)
			if(copyOfSequenceToMatch[i] != INVALID_PEG_COLOR && colors.length() > 1 )
				colors = colors.replace(copyOfSequenceToMatch[i] + "", "");
		
		for(i = 0; i < pegs.length; i++ )
			if(pegs[i] == INVALID_PEG_COLOR)
			{
				//String temp = new String(pegs);
				//System.out.println( temp );
				pegs[i] = colors.charAt(rng.nextInt(colors.length()));
			}
		
		return pegs;
	}
}
