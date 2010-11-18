package game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

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
		Scanner cmdFile = null;
				
		//initialize codes from file container
		codesFromFile = new ArrayList<String>();
		try
		{
			cmdFile = new Scanner(new FileInputStream(codeListFileName));
			
			populatePegColors(cmdFile.nextInt()); 

			nrPegs = cmdFile.nextInt(); 
		
			cmdFile.nextLine();
			
			//Read in third line (first line with code) as secretCode
			//secretCode = new CodeSequence(cmdFile.nextLine().toCharArray());
			//Read in all remaining lines and store as elements in codesFromFile ArrayList
			while(cmdFile.hasNextLine())
				codesFromFile.add(formatRawCode(cmdFile.nextLine()));
			
			generateNextCode();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("Cannot open file \"" + codeListFileName + "\"");
			System.exit(-1);
		}
		catch(InputMismatchException e)
		{
			System.err.println("Expected an int, but read \"" + e.getCause());
			System.exit(-1);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.err.println("File did not contain any valid codes");
			System.exit(-1);
		}
		finally
		{
			cmdFile.close();
		}
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
		//System.out.println( "CODE: " + secretCode );
		return secretCode.getFeedbackFor(codeSequence);
	}
	
	private String formatRawCode( String rawCode )
	{
		Scanner raw = new Scanner( rawCode );
		String formattedCode = "";
		while( raw.hasNextInt() )
			formattedCode += (char)('A' + raw.nextInt() - 1) + "";
		return formattedCode;
	}
	
	private void populatePegColors( int nrPegColors )
	{
		pegColors = "";
		for(int i = 0; i < nrPegColors; i++)
			pegColors += (char)('A' + i) + "";
	}
	
	public boolean hasCodeToUseFromFile()
	{
		return codesFromFile != null && codesFromFile.size() > 0;
	}
			
	public int getNumPegs()
	{
		return nrPegs;
	}
	
	public int getNumPegColors()
	{
		return pegColors.length();
	}
}
