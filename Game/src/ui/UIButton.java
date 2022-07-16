package ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import geometry.Shape2d;
import util.GameObject;

public class UIButton extends GameObject implements UIElement {
	
	Shape2d bounds;
	Shape2d projected_bounds;
	
	public UIButton(Shape2d hitbox)
	{
		super();
		
		this.bounds = hitbox;
		projected_bounds = hitbox.createCopy();
	}
	public void mousePressed(MouseEvent e)
	{
		
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
