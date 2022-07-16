package util;

import java.awt.Graphics2D;

import data_structures.LimitedArray;

public class DrawLayer {
	
	LimitedArray<Drawable> draws;
	
	public DrawLayer(int buff_size)
	{
		draws = new LimitedArray<>(buff_size);
		try {
			draws.setArray(new Drawable[buff_size]);
		} catch (Exception e) {
		}
	}
	public void add(Drawable drawable)
	{
		draws.add(drawable);
		drawable.setIndex(draws.size-1);
	}
	public void remove(int index)
	{
		draws.removeIndex(index);
	}
	public void recycle()
	{
		draws.recycle();
		for(int i = 0; i < draws.size; ++i)
			draws.get(i).setIndex(i);
	}
	public void draw(Graphics2D g2)
	{
		for(int i = 0; i < draws.size; ++i)
			draws.get(i).draw(g2);
	}

}
