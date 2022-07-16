package util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Sprite extends GameObject {
	
	private SpriteSheet sprite_sheet;
	public BufferedImage curr_img;
	private AffineTransform atx;
	private int curr_index;
	
	int beg_index;
	int end_index;
	int ind_length;
	
	float pixels_per_unit;
	
	public Sprite(SpriteSheet sprite_sheet, int index, float pixels_per_unit)
	{
		this.sprite_sheet = sprite_sheet;
		curr_img = sprite_sheet.getSprite(index);
		atx = new AffineTransform();
		
		curr_index = index;
		beg_index = index;
		end_index = sprite_sheet.images.length-1;
		
		this.pixels_per_unit = pixels_per_unit;
	}
	public void setIndexBounds(int beg_index, int end_index)
	{
		this.beg_index = beg_index;
		this.end_index = end_index;
		
		ind_length = end_index - beg_index + 1;
	}
	public void nextImage()
	{
		curr_index++;
		if(curr_index > end_index)
			curr_index = beg_index;
		curr_img = sprite_sheet.getSprite(curr_index);
	}
	public void debugDraw(Graphics2D g2)
	{
		super.debugDraw(g2);
		
		projected.setAffineTransform(atx, 1f / pixels_per_unit, 1f / pixels_per_unit);
		g2.drawImage(curr_img, atx, null);
	}
	public void draw(Graphics2D g2)
	{
		projected.setAffineTransform(atx, 1f / pixels_per_unit, 1f / pixels_per_unit);
		g2.drawImage(curr_img, atx, null);
	}

}
