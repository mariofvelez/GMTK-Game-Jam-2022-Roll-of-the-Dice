package environment;

import java.awt.Color;
import java.awt.Graphics2D;

import geometry.Polygon2d;
import math.Vec2d;
import util.GameObject;

public class Tile extends GameObject {
	
	Color color;
	Color next_c;
	private Polygon2d shape;
	private Polygon2d draw_shape;
	
	public Tile(Color color)
	{
		this.color = color;
		shape = Polygon2d.createAsBox(new Vec2d(0.5f, 0.5f), new Vec2d(0.5f, 0.5f));
		draw_shape = (Polygon2d) shape.createCopy();
	}
	public void setNextColor(Color color)
	{
		next_c = color;
	}
	public void draw(Graphics2D g2)
	{
		g2.setColor(color);
		shape.projectTo(projected, draw_shape);
		draw_shape.debugDraw(g2, true, color);
	}
	public void debugDraw(Graphics2D g2)
	{
		g2.setColor(color);
		shape.projectTo(projected, draw_shape);
		draw_shape.debugDraw(g2, true, color);
		super.debugDraw(g2);
	}

}
