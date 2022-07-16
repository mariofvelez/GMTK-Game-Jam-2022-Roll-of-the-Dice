package util;

import java.awt.Color;
import java.awt.Graphics2D;

import geometry.Circle;
import geometry.Shape2d;
import math.Vec2d;
import physics.body.Body;

public class ParticleEmitter extends GameObject {
	
	EmitterShape shape;
	int max_size = 0;
	int size = 0;
	private int particles_added = 0;
	/**
	 * if the emitter plays on loop
	 */
	public boolean repeating = false;
	boolean playing = false;
	/**
	 * Emitter duration
	 */
	public float duration;
	private float emitter_age;
	/**
	 * Particle lifetime
	 */
	public float lifetime;
	public Color start_color;
	public Color end_color;
	/**
	 * speed of the particles
	 */
	public float particle_speed;
	/**
	 * particles emitted per second
	 */
	public float emission_speed;
	/**
	 * scale with gravity
	 */
	public float gravity_scale;
	/**
	 * radius of the particle
	 */
	public float radius;
	/**
	 * used for velocity
	 */
	public Body attached_body = null;
	boolean[] alive;
	float[] age;
	Vec2d[] pos;
	Vec2d[] proj;
	Vec2d[] vel;
	Color[] col;
	
	public ParticleEmitter(int size)
	{
		super();
		
		max_size = size;
		duration = 5;
		emitter_age = 0;
		lifetime = 2;
		start_color = Color.BLACK;
		end_color = Color.WHITE;
		particle_speed = 2;
		emission_speed = 10;
		
		alive = new boolean[size];
		age = new float[size];
		pos = new Vec2d[size];
		proj = new Vec2d[size];
		vel = new Vec2d[size];
		col = new Color[size];
		
		for(int i = 0; i < size; ++i)
		{
			pos[i] = new Vec2d();
			proj[i] = new Vec2d();
			vel[i] = new Vec2d();
			col[i] = start_color;
		}
	}
	public void setShape(Shape2d shape, float min_angle, float max_angle)
	{
		if(shape instanceof Circle)
		{
			this.shape = new CircleEmitterShape((Circle) shape);
			this.shape.min_angle = min_angle;
			this.shape.max_angle = max_angle;
		}
	}
	public void setLifetime(float seconds)
	{
		lifetime = seconds;
	}
	public void play()
	{
		emitter_age = 0;
		particles_added = 0;
		playing = true;
	}
	public void reset()
	{
//		emitter_age = 0;
//		for(int i = 0; i < alive.length; ++i)
//			alive[i] = false;
		size = 0;
	}
	public void step()
	{
		super.step();
		if(playing)
			emitter_age += world.physics_world.dt;
		for(int i = 0; i < size; ++i)
		{
			vel[i].x += world.physics_world.gravity.x * gravity_scale * world.physics_world.dt;
			vel[i].y += world.physics_world.gravity.y * gravity_scale * world.physics_world.dt;
			pos[i].add(vel[i]);
//			setColor(i);
			age[i] += world.physics_world.dt;
			if(age[i] > lifetime)
				alive[i] = false;
		}
		recycleParticles();
		if(playing)
		{
			float particles_to_add = (emitter_age * emission_speed) /*total emitted*/ - particles_added;
			for(int i = 0; i < particles_to_add && size < alive.length; ++i)
			{
				shape.emitParticle(pos[size], vel[size], particle_speed * world.physics_world.dt);
				if(attached_body != null)
				{
					vel[size].x += attached_body.vel.x * world.physics_world.dt;
					vel[size].y += attached_body.vel.y * world.physics_world.dt;
				}
				projected.project2D(pos[size]);
				world.inverse.project2D(pos[size]);
				alive[size] = true;
				age[size] = 0;
				size++;
				particles_added++;
			}
			if(emitter_age > duration)
			{
				playing = false;
				if(repeating)
				{
					play();
//					emitter_age = 0;
				}
			}
		}
	}
	public void debugDraw(Graphics2D g2)
	{
		super.debugDraw(g2);
		draw(g2);
	}
	public void draw(Graphics2D g2)
	{
		for(int i = 0; i < size; ++i)
		{
			proj[i].set(pos[i]);
			world.transform.project2D(proj[i]);
			setColor(i);
			g2.setColor(col[i]);
			proj[i].debugDraw(g2, (int) radius);
		}
	}
	private void recycleParticles()
	{
		int nf = 0; //next free spot
		for(int i = 0; i < alive.length; ++i)
		{
			if(alive[i])
			{
				boolean temp = alive[i];
				alive[i] = false;
				alive[nf] = temp;
				age[nf] = age[i];
				pos[nf].set(pos[i]);
				vel[nf].set(vel[i]);
				nf++;
			}
		}
		size = nf;
	}
	private void setColor(int index)
	{
		float r = lerp((float) start_color.getRed(), (float) end_color.getRed(), age[index] / lifetime);
		float g = lerp((float) start_color.getGreen(), (float) end_color.getGreen(), age[index] / lifetime);
		float b = lerp((float) start_color.getBlue(), (float) end_color.getBlue(), age[index] / lifetime);
		float a = lerp((float) start_color.getAlpha(), (float) end_color.getAlpha(), age[index] / lifetime);
		col[index] = new Color((int) r, (int) g, (int) b, (int) a);
	}
	private float lerp(float a, float b, float c)
	{
		return a + (b-a)*c;
	}

}
abstract class EmitterShape {
	
	Shape2d shape;
	
	float min_angle = 0;
	float max_angle = (float) Math.PI * 2;
	
	public EmitterShape(Shape2d shape)
	{
		this.shape = shape;
	}
	public abstract void emitParticle(Vec2d pos, Vec2d vel, float speed);
}
class CircleEmitterShape extends EmitterShape {
	
	Circle circle;
	
	public CircleEmitterShape(Circle circle)
	{
		super(circle);
		this.circle = circle;
	}
	public void emitParticle(Vec2d pos, Vec2d vel, float speed)
	{
		pos.set(circle.pos);
		float angle = (float) (Math.random()) * (max_angle - min_angle) + min_angle;
		vel.setPolar(angle, speed);
	}
	
}
