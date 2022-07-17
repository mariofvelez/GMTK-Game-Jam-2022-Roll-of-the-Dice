package util;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import data_structures.LimitedArray;
import math.Transform;
import physics.World;
import ui.UIElement;

public class GameWorld extends GameObject implements DebugDraw, MouseListener, KeyListener, MouseMotionListener {
	
	public World physics_world;
	public long start_time;
	public Transform inverse;
	
	DrawLayer[] draw_layers;
	LimitedArray<DrawableReference> drawable_remove_list;
	LimitedArray<GameObject> object_delete_list;
	ArrayList<UIElement> ui_elements;
	
	public GameWorld()
	{
		this.world = this;
		
		physics_world = new World();
		start_time = System.currentTimeMillis();
		
		inverse = new Transform(3, true);
		drawable_remove_list = new LimitedArray<>(500);
		object_delete_list = new LimitedArray<>(500);
		ui_elements = new ArrayList<>();
		try {
			drawable_remove_list.setArray(new DrawableReference[500]);
			object_delete_list.setArray(new GameObject[500]);
		} catch (Exception e) {}
	}
	public void addUIElement(UIElement e)
	{
		ui_elements.add(e);
	}
	/**
	 * Sets the shape of the layers. Must be called before drawing anything
	 * @param shape - an array containing the maximum sizes for each layer
	 */
	public void setDrawLayers(int[] shape)
	{
		draw_layers = new DrawLayer[shape.length];
		for(int i = 0; i < shape.length; ++i)
			draw_layers[i] = new DrawLayer(shape[i]);
	}
	/**
	 * Adds an object to a layer
	 * @param obj - the object to add
	 * @param index - the index of the layer
	 */
	public void addToLayer(GameObject obj, int index)
	{
		draw_layers[index].add(obj);
		obj.setLayer(index);
	}
	/**
	 * Does not get removed until next step() is called
	 * @param obj - the object to remove
	 */
	public void removeFromLayer(GameObject obj)
	{
		drawable_remove_list.add(new DrawableReference(obj.getLayer(), obj.getIndex()));
	}
	private void updateRemoved()
	{
		for(int i = 0; i < drawable_remove_list.size; ++i)
		{
			DrawableReference remove = drawable_remove_list.get(i);
			draw_layers[remove.layer].remove(remove.index);
		}
		// recycle the layers if anything was removed
		if(drawable_remove_list.size > 0)
		{
			for(int i = 0; i < draw_layers.length; ++i)
				draw_layers[i].recycle();
		}
		drawable_remove_list.clear();
	}
	public void delete()
	{
		
	}
	/**
	 * Does not get deleted until next step() is called
	 * @param obj - the object to remove
	 */
	public void deleteFromWorld(GameObject obj)
	{
		object_delete_list.add(obj);
	}
	private void updateDeleted()
	{
		for(int i = 0; i < object_delete_list.size; ++i)
		{
			GameObject remove = object_delete_list.get(i);
			remove.parent.children.remove(remove);
		}
		// recycle the layers if anything was removed
		if(object_delete_list.size > 0)
		{
//			object_delete_list.recycle();
			object_delete_list.clear();
		}
	}
	public void addChild(GameObject child)
	{
		super.addChild(child);
		child.world = this;
	}
	public void step()
	{
		updateRemoved();
		updateDeleted();
		transform.invert3x3(inverse);
		physics_world.step();
		super.step();
	}
	public void draw(Graphics2D g2)
	{
		for(int i = 0; i < draw_layers.length; ++i)
			draw_layers[i].draw(g2);
	}
	public void keyPressed(KeyEvent e)
	{
		for(int i = 0; i < ui_elements.size(); ++i)
			ui_elements.get(i).keyPressed(e);
	}
	public void keyReleased(KeyEvent e)
	{
		for(int i = 0; i < ui_elements.size(); ++i)
			ui_elements.get(i).keyReleased(e);
	}
	public void keyTyped(KeyEvent e)
	{
//		for(int i = 0; i < ui_elements.size(); ++i)
//			ui_elements.get(i).keyTyped(e);
	}
	public void mouseClicked(MouseEvent e)
	{
		
	}
	public void mouseEntered(MouseEvent e)
	{
		
	}
	public void mouseExited(MouseEvent e)
	{
		
	}
	public void mousePressed(MouseEvent e)
	{
		for(int i = 0; i < ui_elements.size(); ++i)
			ui_elements.get(i).mousePressed(e);
	}
	public void mouseReleased(MouseEvent e)
	{
		for(int i = 0; i < ui_elements.size(); ++i)
			ui_elements.get(i).mouseReleased(e);
	}
	public void mouseDragged(MouseEvent e)
	{
		
	}
	public void mouseMoved(MouseEvent e)
	{
		for(int i = 0; i < ui_elements.size(); ++i)
			ui_elements.get(i).mouseMoved(e);
	}

}
class DrawableReference {
	int layer;
	int index;
	
	public DrawableReference(int layer, int index)
	{
		this.layer = layer;
		this.index = index;
	}
}