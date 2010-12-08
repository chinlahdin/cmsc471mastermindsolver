package data;

public class Pattern {

	private CodeSequence code;
	private double probability;
	
	public Pattern(CodeSequence secretCode){
		//should initalize the code
		code = secretCode;
		probability = 0.0;
	}
	
	public double getProbability(){
		return probability;
	}
	
	public void setProbablitiy(double num){
		probability = num;
	}
	
	public CodeSequence getCode(){
		return code;
	}
}
