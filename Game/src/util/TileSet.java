package util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TileSet extends SpriteSheet {
	
	private ArrayList<TileRule> tile_rules;
	
	public TileSet(String filename, int tile_width, int tile_height)
	{
		super(filename, tile_width, tile_height);
		tile_rules = new ArrayList<>();
	}
	public void addTileRule(int tile_x, int tile_y, int[] type)
	{
		TileRule tile_rule = new TileRule(images[tile_y * sprites_width + tile_x]);
		for(int i = 0; i < type.length; ++i)
			tile_rule.setRule(i, type[i]);
		tile_rules.add(tile_rule);
	}
	public BufferedImage getImageByNeighbors(boolean[] neighbors)
	{
		for(int i = 0; i < tile_rules.size(); ++i)
		{
			if(tile_rules.get(i).checkValid(neighbors))
				return tile_rules.get(i).img;
		}
		System.out.println("here");
		return images[0];
	}
//	private static Comparator<TileRule> comp = (a, b) -> {
//		int a_weight = 0;
//		for(int i = 0; i < a.rule.length; ++i)
//			a_weight += a.rule[i] != ValidNeighborType.Either? 1 : 0;
//		
//		int b_weight = 0;
//		for(int i = 0; i < b.rule.length; ++i)
//			b_weight += b.rule[i] != ValidNeighborType.Either? 1 : 0;
//		
//		if(a_weight > b_weight)
//			return -1;
//		if(b_weight > a_weight)
//			return 1;
//		return 0;
//	};
//	private void sortTileRules()
//	{
//		tile_rules.sort(comp);
//	}
	public static void setDefaultTileRule(TileSet tileset)
	{
		//top basic
		tileset.addTileRule(0, 0, new int[] {
				0, 2, 0,
				2,    1,
				0, 1, 1
		});
		tileset.addTileRule(1, 0, new int[] {
				0, 2, 0,
				1,    1,
				1, 1, 1
		});
		tileset.addTileRule(2, 0, new int[] {
				0, 2, 0,
				1,    2,
				1, 1, 0
		});
		tileset.addTileRule(3, 0, new int[] {
				0, 2, 0,
				2,    2,
				0, 1, 0
		});
		
		//middle basic
		tileset.addTileRule(0, 1, new int[] {
				0, 1, 1,
				2,    1,
				0, 1, 1
		});
		tileset.addTileRule(1, 1, new int[] {
				1, 1, 1,
				1,    1,
				1, 1, 1
		});
		tileset.addTileRule(2, 1, new int[] {
				1, 1, 0,
				1,    2,
				1, 1, 0
		});
		tileset.addTileRule(3, 1, new int[] {
				0, 1, 0,
				2,    2,
				0, 1, 0
		});
		
		//bottom basic
		tileset.addTileRule(0, 2, new int[] {
				0, 1, 1,
				2,    1,
				0, 2, 0
		});
		tileset.addTileRule(1, 2, new int[] {
				1, 1, 1,
				1,    1,
				0, 2, 0
		});
		tileset.addTileRule(2, 2, new int[] {
				1, 1, 0,
				1,    2,
				0, 2, 0
		});
		tileset.addTileRule(3, 2, new int[] {
				0, 1, 0,
				2,    2,
				0, 2, 0
		});
		
		//4th basic
		tileset.addTileRule(0, 3, new int[] {
				0, 2, 0,
				2,    1,
				0, 2, 0
		});
		tileset.addTileRule(1, 3, new int[] {
				0, 2, 0,
				1,    1,
				0, 2, 0
		});
		tileset.addTileRule(2, 3, new int[] {
				0, 2, 0,
				1,    2,
				0, 2, 0
		});
		tileset.addTileRule(3, 3, new int[] {
				0, 2, 0,
				2,    2,
				0, 2, 0
		});
		
		//not corners
		tileset.addTileRule(4, 0, new int[] {
				1, 1, 1,
				1,    1,
				1, 1, 2
		});
		tileset.addTileRule(5, 0, new int[] {
				1, 1, 1,
				1,    1,
				2, 1, 1
		});
		tileset.addTileRule(4, 1, new int[] {
				1, 1, 2,
				1,    1,
				1, 1, 1
		});
		tileset.addTileRule(5, 1, new int[] {
				2, 1, 1,
				1,    1,
				1, 1, 1
		});
		
		//not corners not outside
		tileset.addTileRule(6, 0, new int[] {
				0, 2, 0,
				2,    1,
				0, 1, 2
		});
		tileset.addTileRule(7, 0, new int[] {
				0, 2, 0,
				1,    2,
				2, 1, 0
		});
		tileset.addTileRule(6, 1, new int[] {
				0, 1, 2,
				2,    1,
				0, 2, 0
		});
		tileset.addTileRule(7, 1, new int[] {
				2, 1, 0,
				1,    2,
				0, 2, 0
		});
		
		//t shape
		tileset.addTileRule(4, 2, new int[] {
				1, 1, 2,
				1,    1,
				1, 1, 2
		});
		tileset.addTileRule(5, 2, new int[] {
				1, 1, 1,
				1,    1,
				2, 1, 2
		});
		tileset.addTileRule(4, 3, new int[] {
				2, 1, 2,
				1,    1,
				1, 1, 1
		});
		tileset.addTileRule(5, 3, new int[] {
				2, 1, 1,
				1,    1,
				2, 1, 1
		});
		
		//t shape not outside
		tileset.addTileRule(6, 2, new int[] {
				0, 1, 2,
				2,    1,
				0, 1, 2
		});
		tileset.addTileRule(7, 2, new int[] {
				0, 2, 0,
				1,    1,
				2, 1, 2
		});
		tileset.addTileRule(6, 3, new int[] {
				2, 1, 2,
				1,    1,
				0, 2, 0
		});
		tileset.addTileRule(7, 3, new int[] {
				2, 1, 0,
				1,    2,
				2, 1, 0
		});
		
		//corner ledges horizontal
		tileset.addTileRule(8, 0, new int[] {
				0, 2, 0,
				1,    1,
				2, 1, 1
		});
		tileset.addTileRule(9, 0, new int[] {
				0, 2, 0,
				1,    1,
				1, 1, 2
		});
		tileset.addTileRule(8, 1, new int[] {
				2, 1, 1,
				1,    1,
				0, 2, 0
		});
		tileset.addTileRule(9, 1, new int[] {
				1, 1, 2,
				1,    1,
				0, 2, 0
		});
		
		//corner ledges vertical
		tileset.addTileRule(8, 2, new int[] {
				0, 1, 2,
				2,    1,
				0, 1, 1
		});
		tileset.addTileRule(9, 2, new int[] {
				2, 1, 0,
				1,    2,
				1, 1, 0
		});
		tileset.addTileRule(8, 3, new int[] {
				0, 1, 1,
				2,    1,
				0, 1, 2
		});
		tileset.addTileRule(9, 3, new int[] {
				1, 1, 0,
				1,    2,
				2, 1, 0
		});
		
		//corner ledges both
		tileset.addTileRule(10, 0, new int[] {
				2, 1, 2,
				1,    1,
				2, 1, 1
		});
		tileset.addTileRule(11, 0, new int[] {
				2, 1, 2,
				1,    1,
				1, 1, 2
		});
		tileset.addTileRule(10, 1, new int[] {
				2, 1, 1,
				1,    1,
				2, 1, 2
		});
		tileset.addTileRule(11, 1, new int[] {
				1, 1, 2,
				1,    1,
				2, 1, 2
		});
		
		//not opposite corners
		tileset.addTileRule(10, 2, new int[] {
				2, 1, 1,
				1,    1,
				1, 1, 2
		});
		tileset.addTileRule(10, 3, new int[] {
				1, 1, 2,
				1,    1,
				2, 1, 1
		});
		
		//no corners
		tileset.addTileRule(11, 2, new int[] {
				2, 1, 2,
				1,    1,
				2, 1, 2
		});
		
//		tileset.sortTileRules();
	}
}
enum ValidNeighborType {
	Yes,
	No,
	Either
}
class TileRule {
	
	ValidNeighborType[] rule;
	BufferedImage img;
	
	public TileRule(BufferedImage img)
	{
		this.img = img;
		rule = new ValidNeighborType[8];
		for(int i = 0; i < rule.length; ++i)
			rule[i] = ValidNeighborType.Either;
	}
	public void setRule(int index, int type)
	{
		if(type == 0)
			rule[index] = ValidNeighborType.Either;
		else if(type == 1)
			rule[index] = ValidNeighborType.Yes;
		else if(type == 2)
			rule[index] = ValidNeighborType.No;
	}
	public boolean checkValid(boolean[] neighbors)
	{
		for(int i = 0; i < rule.length; ++i)
		{
			if(rule[i] == ValidNeighborType.Either)
				continue;
			if(rule[i] == ValidNeighborType.Yes && !neighbors[i]) // there must be a room here
				return false;
			else if(rule[i] == ValidNeighborType.No && neighbors[i]) // there must not be a room here
				return false;
		}
		return true;
	}
}

