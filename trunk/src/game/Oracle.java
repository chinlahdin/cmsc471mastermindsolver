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
	private int numCodesLeft;
	
	private CodeSequence secretCode;
	private ColorSpace pegColors;
	private int nrPegs;
	private ArrayList<int[]> codesFromFile;
	
	public Oracle()
	{
		this(DEFAULT_NR_PEG_COLORS, DEFAULT_NR_PEGS);
	}
		
	public Oracle( int nrPegs, int nrPegColors )
	{
		this.nrPegs = nrPegs;
		pegColors = new ColorSpace(nrPegColors);
		generateNextCode();
		numCodesLeft = 0;
	}
	
	public Oracle(String codeListFileName)
	{
		//Open and read a file to generate codes
		Scanner cmdFile = null;
				
		//initialize codes from file container
		codesFromFile = new ArrayList<int[]>();
		try
		{
			cmdFile = new Scanner(new FileInputStream(codeListFileName));
			
			pegColors = new ColorSpace(cmdFile.nextInt()); 

			nrPegs = cmdFile.nextInt(); 
		
			cmdFile.nextLine();
			
			//Read in third line (first line with code) as secretCode
			//secretCode = new CodeSequence(cmdFile.nextLine().toCharArray());
			//Read in all remaining lines and store as elements in codesFromFile ArrayList
			while(cmdFile.hasNextLine())
				codesFromFile.add(formatRawCode(cmdFile.nextLine()));
			
			numCodesLeft = codesFromFile.size() + 1;
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
		numCodesLeft--;
		if(codesFromFile != null && codesFromFile.size() > 0 )
			secretCode = new CodeSequence(codesFromFile.remove(0));
		else
			secretCode = new RandomGuess(pegColors, nrPegs);
	}

	public Feedback getFeedbackFor(CodeSequence codeSequence)
	{
		//System.out.println( "CODE: " + secretCode );
		return secretCode.getFeedbackFor(codeSequence);
	}
	
	private int[] formatRawCode( String rawCode )
	{
		Scanner raw = new Scanner( rawCode );
		int[] formattedCode = new int[nrPegs];
		for( int i = 0; i < nrPegs && raw.hasNextInt(); i++ )
			formattedCode[i] = raw.nextInt();
		return formattedCode;
	}

	public boolean hasCodeToUseFromFile()
	{
		return numCodesLeft > 0;
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
