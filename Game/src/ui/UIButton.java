package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import geometry.Shape2d;
import math.Vec2d;
import util.GameObject;

public class UIButton extends GameObject implements UIElement {
	
	Shape2d bounds;
	Shape2d projected_bounds;

	Consumer<UIButton> onClick;
	
	public UIButton(Shape2d hitbox)
	{
		super();
		
		this.bounds = hitbox;
		projected_bounds = hitbox.createCopy();
	}

public void setOnClick(Consumer<UIButton> onClick)
	{
		this.onClick = onClick;
	}

	public void mousePressed(MouseEvent e)
	{
//		if(bounds.intersects(new Vec2d(e.getX(), e.getY())))
//		{
			if(onClick != null)
				onClick.accept(this);
//		}
		
	}
	public void mouseReleased(MouseEvent e)
	{
		
	}
	public void mouseMoved(MouseEvent e)
	{
		
	}
	public void keyPressed(KeyEvent e)
	{
		
	}
	public void keyReleased(KeyEvent e)
	{
		
	}
	public void debugDraw(Graphics2D g2)
	{
		updateTransform();
		
		bounds.projectTo(projected, projected_bounds);
		g2.setColor(Color.GREEN);
		projected_bounds.debugDraw(g2, false);
	}

}
