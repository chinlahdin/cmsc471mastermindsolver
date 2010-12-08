package data;

import java.util.ArrayList;
import java.util.Random;

public class ColorSpace 
{
	ArrayList<Integer> colors;
	
	public ColorSpace( int nrColors )
	{
		colors = new ArrayList<Integer>();
		for( int i = 1; i <= nrColors; i++ )
			colors.add(i);
	}
	
	public ColorSpace( ColorSpace colorSpace )
	{
		colors = new ArrayList<Integer>( colorSpace.colors );
	}
	
	public void removeColor( int color )
	{
		colors.remove(new Integer(color));
	}
	
	public int length()
	{
		return colors.size();
	}
	
	public int getRandomColor()
	{
		return colors.get(new Random().nextInt(colors.size()));
	}
}
