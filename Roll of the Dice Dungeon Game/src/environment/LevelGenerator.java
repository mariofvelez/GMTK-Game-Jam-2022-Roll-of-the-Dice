package environment;

import java.awt.Color;
import java.util.Random;

import geometry.Polygon2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import util.GameObject;
import util.GameWorld;
import util.Sprite;
import util.SpriteSheet;
import util.components.BodyComponent;

public class LevelGenerator {
	
	GameWorld world;
	
	public Tile[] tiles;
	public Color[] tile_colors; //possible colors for the tiles in order
	public int tile_length = 4; //current number of colors for the level
	
	public LevelGenerator(GameWorld world)
	{
		this.world = world;
		
		tile_colors = new Color[5];
		tile_colors[0] = new Color(230, 88, 60);
		tile_colors[1] = new Color(75, 178, 219);
		tile_colors[2] = new Color(255, 239, 66);
		tile_colors[3] = new Color(46, 232, 53);
		tile_colors[4] = new Color(99, 44, 176);
	}
	public void generateLevel(int colors, float sx, float sy, int width, int height)
	{
		generateTiles(colors, sx, sy, width, height);
		
		float w_width = width/4f-0.5f; //wall width
		float w_height = height/4f-0.5f; //wall height
		
		//bottom
		createWall(new Vec2d(sx + w_width, sy), new Vec2d(w_width, 0.1f));
		createWall(new Vec2d(sx + width - w_width, sy), new Vec2d(w_width, 0.1f));
		//top
		createWall(new Vec2d(sx + w_width, sy + height), new Vec2d(w_width, 0.1f));
		createWall(new Vec2d(sx + width - w_width, sy + height), new Vec2d(w_width, 0.1f));
		//left
		createWall(new Vec2d(sx, sy + w_height), new Vec2d(0.1f, w_height));
		createWall(new Vec2d(sx, sy + height - w_height), new Vec2d(0.1f, w_height));
		//right
		createWall(new Vec2d(sx + width, sy + w_height), new Vec2d(0.1f, w_height));
		createWall(new Vec2d(sx + width, sy + height - w_height), new Vec2d(0.1f, w_height));
		
		//corners
		Sprite tl = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 6, 32);
		tl.setPosition(sx - 1, sy - 1);
		world.addChild(tl);
		
		Sprite tr = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 7, 32);
		tr.setPosition(sx + width, sy - 1);
		world.addChild(tr);
		
		Sprite bl = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 18, 32);
		bl.setPosition(sx - 1, sy + height);
		world.addChild(bl);
		
		Sprite br = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 19, 32);
		br.setPosition(sx + width, sy + height);
		world.addChild(br);
		
		//entrances
		Sprite t1 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 38, 32);
		t1.setPosition(sx + width/2f - 2, sy - 1);
		world.addChild(t1);
		
		Sprite t2 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 36, 32);
		t2.setPosition(sx + width/2f + 1, sy - 1);
		world.addChild(t2);
		
		Sprite b1 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 38, 32);
		b1.setPosition(sx + width/2f - 2, sy + height);
		world.addChild(b1);
		
		Sprite b2 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 36, 32);
		b2.setPosition(sx + width/2f + 1, sy + height);
		world.addChild(b2);
		
		Sprite r1 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 27, 32);
		r1.setPosition(sx - 1, sy + height/2 - 2);
		world.addChild(r1);
		
		Sprite r2 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 3, 32);
		r2.setPosition(sx - 1, sy + height/2 + 1);
		world.addChild(r2);
		
		Sprite l1 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 27, 32);
		l1.setPosition(sx + width, sy + height/2 - 2);
		world.addChild(l1);
		
		Sprite l2 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 3, 32);
		l2.setPosition(sx + width, sy + height/2 + 1);
		world.addChild(l2);
		
		//edges
		for(int i = 0; i < width; ++i)
		{
			if(i < width/2-1 || i > width/2+1)
			{
				Sprite wall = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 37, 32);
				wall.setPosition(sx + i, sy + height);
				world.addChild(wall);
				
				Sprite wall2 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 37, 32);
				wall2.setPosition(sx + i, sy - 1);
				world.addChild(wall2);
			}
		}
		for(int i = 0; i < height; ++i)
		{
			if(i < height/2-2 || i > height/2+1)
			{
				Sprite wall = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 15, 32);
				wall.setPosition(sx - 1, sy + i);
				world.addChild(wall);
				
				Sprite wall2 = new Sprite(SpriteSheet.getSpriteSheet("Walls"), 15, 32);
				wall2.setPosition(sx + width, sy + i);
				world.addChild(wall2);
			}
		}
	}
	public Tile createTile(Color c, int state, float x, float y)
	{
		Tile tile = new Tile(c, state);
		tile.setPosition(x, y);
		tile.setLayer(0);
		world.addChild(tile);
		
		return tile;
	}
	/**
	 * Generates a bunch of tiles
	 * @param cols - the number of colors
	 * @param sx - starting position
	 * @param sy
	 * @param width - how many tiles wide
	 * @param height - how many tiles tall
	 */
	public void generateTiles(int cols, float sx, float sy, int width, int height)
	{
		tiles = new Tile[width * height];
		Random r = new Random();
		tile_length = cols;
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				int rand = r.nextInt(cols); //maybe use perlin noise for easier levels
				
				Tile tile = createTile(tile_colors[rand], rand, sx + x, sy + y);
				tile.setNextColor(tile_colors[(rand+1) % tile_length]);
				tiles[y*width + x] = tile;
			}
		}
	}
	public GameObject createWall(Vec2d pos, Vec2d dim)
	{
		GameObject wall = new GameObject();
		wall.setPosition(pos.x, pos.y);
		
		Body body = new Body(new Vec2d(0, 0), CollisionType.STATIC);
		body.setShape(Polygon2d.createAsBox(new Vec2d(), dim));
		BodyComponent comp = new BodyComponent(body);
		world.addChild(wall);
		
		wall.addComponent(comp);
		
		return wall;
	}
	public boolean checkIntersected(Vec2d pos, int state)
	{
		for(int i = 0; i < tiles.length; ++i)
		{
			Tile tile = tiles[i];
			if(tile.state != state)
			{
				if(tile.draw_shape.intersects(pos))
					return true;
			}
		}
		return false;
	}

}
