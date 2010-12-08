package game;

import data.CodeSequence;
import data.Feedback;

public interface Guesser 
{
	public void reset();

	public CodeSequence guess();
	
	public void giveFeedbackForLastGuess(Feedback feedback);
}
