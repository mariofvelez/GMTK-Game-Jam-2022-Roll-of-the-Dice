package util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SpriteSheet {
	
private static HashMap<String, SpriteSheet> sprite_sheets = new HashMap<>();
	
	protected BufferedImage img;
	protected int[] px;
	protected BufferedImage[] images;
	
	public int width; // width of image
	public int height;
	
	public int sprites_width; // number of sprites in x direction
	public int sprites_height;
	
	public SpriteSheet(String filename, int sprites_width, int sprites_height)
	{
		URL url = this.getClass().getResource(filename);
		BufferedImage temp;
		try {
			temp = ImageIO.read(url);
		} catch (IOException e) {
			temp = new BufferedImage(sprites_width, sprites_height, BufferedImage.TYPE_INT_ARGB);
			System.err.println("Error: could not load image from file: " + filename + ", created new image instead");
			e.printStackTrace();
		}
		byte[] temp_px = ((DataBufferByte) temp.getRaster().getDataBuffer()).getData();
		img = new BufferedImage(temp.getWidth(), temp.getHeight(), BufferedImage.TYPE_INT_ARGB);
		px = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		
		for(int i = 0; i < px.length; ++i)
		{
			int col = (temp_px[i*4] << 24) + (temp_px[i*4+3] << 16) + (temp_px[i*4+2] << 8) + temp_px[i*4+1];
			px[i] = col;
		}
		
		//partition into tiles
		this.sprites_width = sprites_width;
		this.sprites_height = sprites_height;
		
		int w = img.getWidth() / sprites_width;
		int h = img.getHeight() / sprites_height;
		
		images = new BufferedImage[sprites_width * sprites_height];
		
		for(int y = 0; y < sprites_height; ++y)
		{
			for(int x = 0; x < sprites_width; ++x)
			{
				images[y*sprites_width + x] = img.getSubimage(x*w, y*w, w, h);
			}
		}
	}
	public SpriteSheet(String foldername, String prefix, int num_sprites)
	{
		images = new BufferedImage[num_sprites];
		
		for(int j = 0; j < num_sprites; ++j)
		{
			URL url = this.getClass().getResource(foldername + "/" + prefix + (j+1) + ".png");
			BufferedImage temp;
			try {
				temp = ImageIO.read(url);
			} catch (IOException e) {
				temp = new BufferedImage(sprites_width, sprites_height, BufferedImage.TYPE_INT_ARGB);
				System.err.println("Error: could not load image from file: " + foldername + "/" + prefix + j + ".png, created new image instead");
				e.printStackTrace();
			}
			byte[] temp_px = ((DataBufferByte) temp.getRaster().getDataBuffer()).getData();
			images[j] = new BufferedImage(temp.getWidth(), temp.getHeight(), BufferedImage.TYPE_INT_ARGB);
			int[] px = ((DataBufferInt) images[j].getRaster().getDataBuffer()).getData();
			
			for(int i = 0; i < px.length; ++i)
			{
				int col = (temp_px[i*4] << 24) + (temp_px[i*4+3] << 16) + (temp_px[i*4+2] << 8) + temp_px[i*4+1];
				px[i] = col;
			}
		}
		
		this.sprites_width = num_sprites;
		this.sprites_height = 1;
	}
	public BufferedImage getSprite(int index)
	{
		return images[index];
	}
	public static void loadSpriteSheet(String name, String filename, int sprites_width, int sprites_height)
	{
		sprite_sheets.put(name, new SpriteSheet(filename, sprites_width, sprites_height));
	}
	public static void loadSpriteSheet(String name, String foldername, String prefix, int num_sprites)
	{
		sprite_sheets.put(name, new SpriteSheet(foldername, prefix, num_sprites));
	}
	public static void loadTileSet(String name, String filename, int sprites_width, int sprites_height)
	{
		sprite_sheets.put(name, new TileSet(filename, sprites_width, sprites_height));
	}
	public static SpriteSheet getSpriteSheet(String name)
	{
		return sprite_sheets.get(name);
	}

}
