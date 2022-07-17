package environment;

import java.awt.Color;
import java.awt.Graphics2D;

import geometry.Polygon2d;
import math.Vec2d;
import util.GameObject;

public class Tile extends GameObject {
	
	Color color;
	Color next_c;
	public int state = 0;
	float changing = 0; //animation changing to next color
	private Polygon2d shape;
	public Polygon2d draw_shape;
	
	public Tile(Color color, int state)
	{
		this.color = color;
		this.state = state;
		shape = Polygon2d.createAsBox(new Vec2d(0.5f, 0.5f), new Vec2d(0.5f, 0.5f));
		draw_shape = (Polygon2d) shape.createCopy();
	}
	public void setColor(Color c)
	{
		color = c;
	}
	public void setNextColor(Color color)
	{
		next_c = color;
	}
	public void setChanging(float changing)
	{
		this.changing = changing;
	}
	public void draw(Graphics2D g2)
	{
		g2.setColor(color);
		shape.projectTo(projected, draw_shape);
		draw_shape.debugDraw(g2, true, color);
		
		if(changing > 0)
		{
			g2.setColor(next_c);
			Polygon2d change_shape = Polygon2d.createAsBox(new Vec2d(0.5f, 0.5f), new Vec2d(changing * 0.5f, changing * 0.5f));
			shape.projectTo(projected, change_shape);
			change_shape.debugDraw(g2, true, next_c);
		}
	}
	public void debugDraw(Graphics2D g2)
	{
		g2.setColor(color);
		shape.projectTo(projected, draw_shape);
		draw_shape.debugDraw(g2, true, color);
		
		if(changing > 0)
		{
			g2.setColor(next_c);
			Polygon2d change_shape = Polygon2d.createAsBox(new Vec2d(0.5f, 0.5f), new Vec2d(changing * 0.5f, changing * 0.5f));
			change_shape.projectTo(projected, change_shape);
			change_shape.debugDraw(g2, true, next_c);
		}
		
		super.debugDraw(g2);
	}

}
