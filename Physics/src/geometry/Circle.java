package geometry;

import java.awt.Color;
import java.awt.Graphics2D;

import debug.DebugDrawInfo;
import math.MathConstant;
import math.Transform;
import math.Transform2d;
import math.Vec2d;
import math.Vec3d;
import physics.AABB;

public class Circle extends Shape2d {
	public Vec2d pos;
	public float radius;
	public float radius2;
	
	public Circle(Vec2d pos, float radius)
	{
		this.pos = pos;
		this.radius = radius;
		radius2 = radius*radius;
		area = MathConstant.PI*radius2;
	}
	public void setRadius(float radius)
	{
		this.radius = radius;
		radius2 = radius*radius;
		area = MathConstant.PI*radius2;
	}
	public void debugDraw(Graphics2D g2, boolean fill)
	{
		if(fill)
		{
//			g2.setColor(new Color(255, 127, 0, 100));
			g2.setColor(Color.WHITE);
			g2.fillOval((int) (pos.x-radius), (int) (pos.y-radius), (int) (radius*2), (int) (radius*2));
			g2.setColor(Color.BLACK);
		}
		g2.drawOval((int) (pos.x-radius), (int) (pos.y-radius), (int) (radius*2), (int) (radius*2));
	}
	public void debugDraw(Graphics2D g2, boolean fill, Color color)
	{
		if(fill)
		{
			g2.setColor(color);
			g2.fillOval((int) (pos.x-radius), (int) (pos.y-radius), (int) (radius*2), (int) (radius*2));
		}
		g2.setColor(Color.BLACK);
		g2.drawOval((int) (pos.x-radius), (int) (pos.y-radius), (int) (radius*2), (int) (radius*2));
	}
	public void debugDraw(Graphics2D g2, DebugDrawInfo info)
	{
		if(!info.wireframe)
		{
			g2.setColor(info.col);
			g2.fillOval((int) (pos.x-radius), (int) (pos.y-radius), (int) (radius*2), (int) (radius*2));
		}
		g2.setColor(Color.BLACK);
		g2.drawOval((int) (pos.x-radius), (int) (pos.y-radius), (int) (radius*2), (int) (radius*2));
		
	}
	public void debugDraw(Graphics2D g2, Transform2d transform, boolean fill, Color color)
	{
		Vec2d pos = transform.projectToTransform(this.pos);
		if(fill)
		{
			g2.setColor(color);
			g2.fillOval((int) (pos.x-radius*transform.tx.x), (int) (pos.y-radius*transform.tx.x), (int) (radius*2*transform.tx.x), (int) (radius*2*transform.tx.x));
		}
		else
		{
			g2.setColor(Color.BLACK);
			g2.drawOval((int) (pos.x-radius*transform.tx.x), (int) (pos.y-radius*transform.tx.x), (int) (radius*2*transform.tx.x), (int) (radius*2*transform.tx.x));
		}
	}
	public boolean intersects(Vec2d point)
	{
		float dist_x = point.x - pos.x;
		float dist_y = point.y - pos.y;
		
		return dist_x*dist_x + dist_y*dist_y < radius2;
	}
	public boolean intersects(LineSegment ls)
	{
		Vec2d dir = Vec2d.subtract(ls.a, ls.b);
		float len = dir.length();
		dir.normalize();
		Vec2d normal = dir.leftNormal();
		Vec2d v = Vec2d.subtract(pos, ls.b);
		float along_dir = Vec2d.dotProduct(v, dir);
		float along_normal = Vec2d.dotProduct(v, normal);
		if((along_dir < len && along_dir >= 0 && Math.abs(along_normal) < radius) || intersects(ls.a) || intersects(ls.b))
			return true;
		return false;
	}
	public Vec2d projectedBounds(Vec2d axis, Vec2d pos)
	{
		Vec2d dir = Vec2d.subtract(this.pos, pos);
		float p = Vec2d.dotProduct(dir, axis);
		Vec2d v = new Vec2d(p-radius, p+radius);
		return v;
	}
	public void support(Vec2d axis, Vec2d point)
	{
		point.set(axis.x * radius, axis.y * radius);
	}
	public Vec2d distance(Vec2d point, boolean inside)
	{
		Vec2d to_center = Vec2d.subtract(point, pos);
		to_center.normalize();
		float dist = Vec2d.subtract(point, pos).length() - radius;
		to_center.mult(dist);
		return to_center;
	}
	public void move(Vec2d dist)
	{
		pos.add(dist);
	}
	public Shape2d projectTo(Transform2d tf2d)
	{
		Circle proj = new Circle(tf2d.projectToTransform(pos), radius * tf2d.tx.length());
		return proj;
	}
	private static Vec3d axis = new Vec3d(1, 0, 1);
	public void projectTo(Transform transform, Shape2d shape)
	{
		Circle circle = (Circle) shape;
		circle.pos.set(pos);
		transform.project2D(circle.pos);
		axis.set(1, 0, 1);
		transform.project3D(axis);
		circle.radius = radius;
		circle.radius2 = radius2;
		circle.area = area;
	}
	public void setAABB(AABB aabb)
	{
		aabb.min_x = pos.x - radius;
		aabb.max_x = pos.x + radius;
		aabb.min_y = pos.y - radius;
		aabb.max_y = pos.y + radius;
	}
	public float computeArea()
	{
		area = MathConstant.PI*radius2;
		return area;
	}
	public void computeCentroid(Vec2d centroid)
	{
		centroid.set(pos);
	}
	public float computeInertia(float density)
	{
		return density * MathConstant.PI * radius2 * radius2 / 4;
	}
	public float computeInertia(Vec2d center, float density)
	{
		return computeInertia(density) - area*Vec2d.dist2(center, pos);
	}

}
