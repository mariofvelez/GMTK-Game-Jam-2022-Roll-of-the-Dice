package util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.function.Consumer;

import math.Transform;
import math.Vec2d;
import util.components.GameComponent;

public class GameObject implements DebugDraw, Drawable {
	
	public int ID;
	private int draw_layer = 0;
	private int draw_index = -1;
	public GameWorld world;
	public GameObject parent;
	public ArrayList<GameObject> children;
	public ArrayList<GameComponent> components;
	public Transform transform; //local coordinates
	public Transform projected; //world coordinates
	public Transform rotate;
	
	public GameObject()
	{
		ID = 0;
		parent = null;
		children = new ArrayList<>();
		components = new ArrayList<>();
		transform = new Transform(3, true);
		projected = new Transform(3, false);
		rotate = new Transform(3, true);
	}
	public void addChild(GameObject child)
	{
		children.add(child);
		child.parent = this;
		child.world = world;
		
		world.addToLayer(child, child.draw_layer);
	}
	public void addComponent(GameComponent component)
	{
		component.parent = this;
		components.add(component);
		component.onAdd();
	}
	/**
	 * deletes from the world along with all its children and components
	 */
	public void delete()
	{
		for(int i = 0; i < children.size(); ++i)
			children.get(i).delete();
		for(int i = 0; i < components.size(); ++i)
			components.get(i).onRemove();
		
		world.removeFromLayer(this);
		world.deleteFromWorld(this);
	}
	/**
	 * goes through this GameObject and all of its children, checks its ID, and calls the function
	 * @param f - the function to apply
	 * @param ID - the ID
	 */
	public void forEachID(Consumer<GameObject> f, int ID)
	{
		if(this.ID == ID)
			f.accept(this);
		for(int i = 0; i < children.size(); ++i)
			children.get(i).forEachID(f, ID);
	}
	public void step()
	{
//		world.step();
		updateTransform();
		
		for(int i = 0; i < components.size(); ++i)
			components.get(i).onUpdate();
		for(int i = 0; i < children.size(); ++i)
			children.get(i).step();
	}
	public void updateTransform()
	{
		if(parent != null)
		{
			Transform.mult(parent.projected, transform, projected);
		}
		else
			projected.setData(transform.data);
	}
	public void debugDraw(Graphics2D g2)
	{	
		Vec2d c = new Vec2d(projected.data[2], projected.data[5]); // origin
		Vec2d a = new Vec2d(projected.data[0], projected.data[3]); // x
		Vec2d b = new Vec2d(projected.data[1], projected.data[4]); // y
		
		g2.setColor(Color.RED);
		g2.drawLine((int) c.x, (int) c.y, (int) (c.x + a.x), (int) (c.y + a.y));
		g2.setColor(Color.GREEN);
		g2.drawLine((int) c.x, (int) c.y, (int) (c.x + b.x), (int) (c.y + b.y));
		g2.setColor(Color.BLACK);
		c.debugDraw(g2, 2);
		
		for(int i = 0; i < children.size(); ++i)
		{
			children.get(i).debugDraw(g2);
		}
	}
	public void draw(Graphics2D g2)
	{
		
	}
	public void setIndex(int index)
	{
		draw_index = index;
	}
	public int getIndex()
	{
		return draw_index;
	}
	public void setLayer(int layer)
	{
		draw_layer = layer;
	}
	public void changeLayer(int layer)
	{
		world.removeFromLayer(this);
		world.addToLayer(this, layer);
	}
	public int getLayer()
	{
		return draw_layer;
	}
	public void setPosition(float x, float y)
	{
		transform.data[2] = x;
		transform.data[5] = y;
	}
	public void move(float x, float y)
	{
		transform.data[2] += x;
		transform.data[5] += y;
	}
	public void setScale(float x, float y)
	{
		transform.data[0] *= x;
		transform.data[3] *= x;
		transform.data[1] *= y;
		transform.data[4] *= y;
	}
	public void setRotationSpeed(float angle)
	{
		rotate.setRotationInstance(angle);
	}
	public void setRotationSpeed(Vec2d pos, float angle)
	{
		rotate.setRotationInstance(pos, angle);
	}
	public void setRotationSpeed(float x, float y, float angle)
	{
		rotate.setRotationInstance(x, y, angle);
	}
	public void setRotationSpeedLocal(Vec2d local, float angle)
	{
		Vec2d copy = new Vec2d(local);
		transform.project2D(copy);
		rotate.setRotationInstance(copy, angle);
	}
	public void setRotationSpeedLocal(float x, float y, float angle)
	{
		Vec2d copy = new Vec2d(x, y);
		transform.project2D(copy);
		rotate.setRotationInstance(copy, angle);
	}
	public void setRotation(float x, float y, float angle)
	{
		float a = (float) Math.atan2(transform.data[3], transform.data[0]);
		rotate.setRotationInstance(x, y, -a);
		rotate();
		rotate.setRotationInstance(x, y, angle);
	}
	public void rotate()
	{
		transform.mult(rotate);
	}

}
