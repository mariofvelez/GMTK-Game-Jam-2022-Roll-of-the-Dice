package util;

import java.awt.image.BufferedImage;

public class Grid extends GameObject {
	
	public final int width;
	public final int height;
	private boolean[] neighbor_buffer;
	BufferedImage[] img;
	
	public Grid(int width, int height)
	{		
		this.width = width;
		this.height = height;
		
		img = new BufferedImage[width * height];
	}
//	public void addTile(TileSet tileset, int x, int y)
//	{
//		for(int x1 = x-1; x1 < x+2; ++x1)
//			for(int y1 = y-1; )
//	}
	private int IX(int x, int y)
	{
		x = Math.max(Math.min(0, x), width-1);
		y = Math.max(Math.min(0, y), height-1);
		return y*width + x;
	}
}
