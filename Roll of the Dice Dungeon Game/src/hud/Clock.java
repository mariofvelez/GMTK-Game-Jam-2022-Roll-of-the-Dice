package hud;

import util.GameObject;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Timer class to show how much time is left in a level.
 */
public class Clock extends GameObject {
	private final double allotted_time;
	private double time_left;
	private double start_time;
	private final int radius;
	boolean running = false;
	private final float criticalThreshold = .25f;
	Consumer<Clock> onTimeUp;

	public Clock(double allotted_time, int radius) {
		super();
		this.allotted_time = allotted_time;
		this.time_left = allotted_time;
		this.start_time = System.currentTimeMillis();
		this.radius = radius;
	}

	public void setOnTimeUp(Consumer<Clock> onTimeUp) {
		this.onTimeUp = onTimeUp;
	}

	@Override
	public void draw(Graphics2D g2)
	{
		float xPos = this.transform.data[2];
		float yPos = this.transform.data[5];

		// Draw an arc representing the time left.
		if(time_left / allotted_time > criticalThreshold) {
			g2.setColor(Color.WHITE);
		} else if(time_left % 0.5f < 0.25f) {
			g2.setColor(Color.RED);
		}
		g2.fillArc((int) (xPos - radius), (int) (yPos - radius), radius * 2, radius * 2, 0, (int) (360 * (time_left / allotted_time)));
	}

	@Override
	public void debugDraw(Graphics2D g2)
	{
		float xPos = this.transform.data[2];
		float yPos = this.transform.data[5];

		// Draw an arc representing the time left.
		if((time_left / allotted_time) > criticalThreshold) {
			g2.setColor(Color.WHITE);
		} else if(time_left % 0.5f < 0.25f){
			g2.setColor(Color.RED);
		}
		g2.fillArc((int) (xPos - radius), (int) (yPos - radius), radius * 2, radius * 2, 0, (int) (360 * (time_left / allotted_time)));
		super.debugDraw(g2);
	}

	@Override
	public void step(){
		super.step();

		if(running) {
			time_left = allotted_time - (System.currentTimeMillis() - start_time) / 1000;
			if(time_left < 0) {
				time_left = 0;
				running = false;
				if(onTimeUp != null) {
					onTimeUp.accept(this);
				}
			}
		}
	}


	public void start(){
		start_time = System.currentTimeMillis();
		running = true;
	}


}
