package hud;

import util.GameObject;
import util.Sprite;
import util.SpriteSheet;

import java.awt.*;
import java.util.function.Consumer;

public class HealthBar extends GameObject {
	private int lives;
	private Consumer<HealthBar> onDie;

	public HealthBar(int lives) {
		this.lives = lives;
	}
	public void init(){
	SpriteSheet.loadSpriteSheet("Heart", "/res/heart.png", 1, 1);
	//create {lives} hearts
	for (int i = 0; i < lives; i++) {
		Sprite heart = new Sprite(SpriteSheet.getSpriteSheet("Heart"), 0, 32);
		heart.setPosition(i * 1.2f, 0);
		this.addChild(heart);
	}
	System.out.println(this.children.size());
}
	public void setOnDie(Consumer<HealthBar> onDie) {
		this.onDie = onDie;
	}

	@Override
	public void draw(Graphics2D g2)
	{
		float xPos = this.transform.data[2];
		float yPos = this.transform.data[5];


	}

	@Override
	public void debugDraw(Graphics2D g2)
	{
		float xPos = this.transform.data[2];
		float yPos = this.transform.data[5];


		super.debugDraw(g2);
	}

	public void removeLife() {
		if(lives > 0) {
			lives--;
			this.children.get(lives).delete();
			if(lives == 0) {
				onDie.accept(this);
			}
		}
	}

	public int getLives() {
		return lives;
	}


	public void reset(int lives){
		this.lives = lives;
		for(int i = 0; i < children.size(); i++){
			children.get(i).delete();
		}
		init();
	}

}
