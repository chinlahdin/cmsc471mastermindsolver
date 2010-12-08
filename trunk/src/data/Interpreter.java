package data;

import java.util.ArrayList;

public class Interpreter {
	
	double individualProb = 0.0;
	ArrayList<Pattern> codes = null;
	
	public Interpreter(CodeSequence code, int numColors){
		individualProb = 1 / numColors;
		
		Pattern aPattern = new Pattern(code);
		aPattern.setProbablitiy(individualProb);
		codes.add(aPattern);
	}
	
	public void addAndUpdate(CodeSequence code){
		//probability of the next given your the previous code in the array
		for(int i = this.codes.size() -1 ; i >= 0; i--){
			//bayes nets 
			//double aGivenb = 
		}
	}
	//For patterns with all of the same colors i figure that 
	public void allSame(Pattern code){
		int numColors = code.getCode().getNrPegs();
		
		boolean allSame = true;
		char first = (char) code.getCode().getPegColorAt(0);
		
		for(int i = 1; i < numColors; i++){
			if(code.getCode().getPegColorAt(i) != first){
				allSame = false;
			}
		}
		
		if(allSame == true){
			code.setProbablitiy(1.0);
		}
	}
}
