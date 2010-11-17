package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import data.*;

public class Oracle 
{
	private final static int DEFAULT_NR_PEGS = 4;
	private final static int DEFAULT_NR_PEG_COLORS = 6;
	
	private CodeSequence secretCode;
	private String pegColors;
	private int nrPegs;
	private int numPegColors;
	private ArrayList<String> codesFromFile;
	
	public Oracle()
	{
		this(DEFAULT_NR_PEG_COLORS, DEFAULT_NR_PEGS);
	}
		
	public Oracle( int nrPegs, int nrPegColors )
	{
		this.nrPegs = nrPegs;
		populatePegColors(nrPegColors);
		generateNextCode();
	}
	
	public Oracle(String codeListFileName)
	{
		//Open and read a file to generate codes
		Scanner cmdFile = null;
		
		//initialize codes from file container
		codesFromFile = new ArrayList<String>();
		try{
		cmdFile = new Scanner(new FileInputStream(codeListFileName));
		
		}
		catch(FileNotFoundException e){
			System.err.println("Cannot open file "+codeListFileName);
			System.exit(-1);
		}
		//Read in first line as nrPegs
		nrPegs = cmdFile.nextInt(); 
		System.out.println("Pegs: " +nrPegs);
		//Read in second line and populate pegColors
		numPegColors = cmdFile.nextInt();cmdFile.nextLine();
		System.out.println("Num peg colors: "+numPegColors);
		populatePegColors(numPegColors);
		//Read in third line (first line with code) as secretCode
		//secretCode = new CodeSequence(cmdFile.nextLine().toCharArray());
		//Read in all remaining lines and store as elements in codesFromFile ArrayList
		while(cmdFile.hasNext()){
			
			codesFromFile.add(cmdFile.nextLine());
		}
		
		cmdFile.close();
		generateNextCode();
	}
	
	public void generateNextCode()
	{
		if(hasCodeToUseFromFile())
			secretCode = new CodeSequence(codesFromFile.remove(0).toCharArray());
		else
			secretCode = new RandomGuess(pegColors, nrPegs);
	}

	public Feedback getFeedbackFor(CodeSequence codeSequence)
	{
		return secretCode.getFeedbackFor(codeSequence);
	}
	
	private void populatePegColors( int nrPegColors )
	{
		pegColors = "";
		for(int i = 0; i < nrPegColors; i++)
			pegColors += (char)('A' + i) + "";
	}
	
	public boolean hasCodeToUseFromFile()
	{
		return codesFromFile != null && codesFromFile.size() == 0;
	}
	
	public String getCodes(){
	
		String text ="";
		for(int i = 0; i < codesFromFile.size(); i++){
			text+=codesFromFile.get(i).toString();
		}
		return text;
	}
	
	public ArrayList<String> getCodeList(){
		return codesFromFile;
	}
	
	public int getNumPegs(){
		return nrPegs;
	}
	
	public int getNumPegColors(){
		return numPegColors;
	}
}
