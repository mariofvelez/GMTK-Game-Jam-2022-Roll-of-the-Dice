package environment;

import java.awt.Color;
import java.util.Random;

import geometry.Polygon2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import util.GameObject;
import util.GameWorld;
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

}
