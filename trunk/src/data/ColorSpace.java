package data;

import java.util.ArrayList;
import java.util.Random;

/**
 * A ColorSpace holds all possible colors. It can be reduced to give a more
 * accurate inventory of the colors possible, given feedback.
 * 
 * @author M. Curtis, M. Edoror and B. Farrington
 * 
 */
public class ColorSpace
{
	ArrayList<Integer> colors;

	/**
	 * 
	 * @param nrColors
	 *            The number of colors in the color space.
	 */
	public ColorSpace(int nrColors)
	{
		colors = new ArrayList<Integer>();
		for (int i = 1; i <= nrColors; i++)
			colors.add(i);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param colorSpace
	 *            The color space to copy.
	 */
	public ColorSpace(ColorSpace colorSpace)
	{
		colors = new ArrayList<Integer>(colorSpace.colors);
	}

	/**
	 * Removes a color from the color space.
	 * 
	 * @param color
	 *            The color to remove as an integer.
	 */
	public void removeColor(int color)
	{
		colors.remove(new Integer(color));
	}

	/**
	 * @return Number of colors in the color space.
	 */
	public int length()
	{
		return colors.size();
	}

	/**
	 * 
	 * @return A random color from the color space.
	 */
	public int getRandomColor()
	{
		return colors.get(new Random().nextInt(colors.size()));
	}

	/**
	 * 
	 * @param num
	 *            Number that corresponds to the color.
	 * @return A given color.
	 */
	public int getColor(int num)
	{
		return colors.get(num % colors.size());
	}
}
