package game;

import java.util.ArrayList;

import data.*;

public class Oracle 
{
	private final static int DEFAULT_NR_PEGS = 4;
	private final static int DEFAULT_NR_PEG_COLORS = 6;
	
	private CodeSequence secretCode;
	private String pegColors;
	private int nrPegs;
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
		
		//Read in first line as nrPegs
		//Read in second line and populate pegColors
		
		//Read in third line (first line with code) as secretCode
		//Read in all remaining lines and store as elements in codesFromFile ArrayList
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
	
	private boolean hasCodeToUseFromFile()
	{
		return codesFromFile != null && codesFromFile.size() == 0;
	}
}
