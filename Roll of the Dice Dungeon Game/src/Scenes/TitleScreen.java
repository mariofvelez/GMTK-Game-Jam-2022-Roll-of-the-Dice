package Scenes;

import environment.Tile;
import geometry.Polygon2d;
import geometry.Shape2d;
import math.Vec2d;
import ui.UIButton;
import ui.UIText;
import util.GameWorld;
import util.Sprite;
import util.SpriteSheet;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class TitleScreen {

	GameWorld world;

	Consumer<TitleScreen> onStartGame, onStartTutorial;

	Font titleFont;

	private Tile background;


	public TitleScreen(GameWorld world, Dimension size) {


		this.world = world;
		this.world.setDrawLayers(new int[] {5, 100, 100, 100, 100});
		this.world.setPosition(size.width/2f, size.height/2f);
		this.world.setScale(30, 30);
		this.world.physics_world.setGravity(0f, 0f);
		//add text that says "Die Dungeon"
		//load font from file in res/fonts/LotsOfDotz.ttf
		//set font size to 100
		background = new Tile(new Color(65,153,105), 0);
		background.setScale(100,100);
		background.setPosition(-20,-20);

		world.addChild(background);


		try {
			titleFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(TitleScreen.class.getClassLoader().getResourceAsStream("res/fonts/LLPIXEL3.ttf"))).deriveFont(5f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		//set draw color to white
		Color titleColor = new Color(15,6,25);
		UIText die_text = new UIText(titleFont, "Die");
		die_text.color = titleColor;
		die_text.setPosition(-5, -6);
		UIText dungeon_text = new UIText(titleFont, "Dungeon");
		dungeon_text.color = titleColor;
		dungeon_text.setPosition(-11, -2);

		die_text.setLayer(3);
		dungeon_text.setLayer(3);
		this.world.addChild(die_text);
		this.world.addChild(dungeon_text);



		for(int i = 0; i < 52; i++){
			Sprite cardTest = new Sprite(SpriteSheet.getSpriteSheet("Cards"), i, 32);
			//set the cards position to a random edge of the screen
			int wall = (int) (Math.random() * 4);
			switch (wall) {
				case 0:
					cardTest.setPosition((float) (Math.random() * 34 - 34/2), -12);
					break;
				case 1:
					cardTest.setPosition((float) (Math.random() * 34 - 34/2), 12);
					break;
					case 2:
					cardTest.setPosition(-12, (float) (Math.random() * 34 - 34/2));
					break;
					case 3:
					cardTest.setPosition(12, (float) (Math.random() * 34 - 34/2));
					break;
			}

			cardTest.setLayer(1);
			cardTest.setRotation(0,0, (float) Math.toRadians(Math.random() * 360));
			cardTest.rotate();
			this.world.addChild(cardTest);

		}

		Sprite playCard = new Sprite(SpriteSheet.getSpriteSheet("PlayCard"), 0, 32);
		playCard.setLayer(2);
		playCard.setRotation(0,0, (float) Math.toRadians(-90));
		playCard.rotate();
		playCard.setPosition(0,6);
		playCard.setScale(1.5f, 1.5f);

		this.world.addChild(playCard);

		Sprite tutorialCard = new Sprite(SpriteSheet.getSpriteSheet("TutCard"), 0, 32);
		tutorialCard.setLayer(2);
		tutorialCard.setPosition(-7,0);
		tutorialCard.setScale(1.5f, 1.5f);
		this.world.addChild(tutorialCard);

		Shape2d play_shape = Polygon2d.createAsBox(new Vec2d(3.5f, 3.5f), new Vec2d(3, 2.5f));
		UIButton play_button = new UIButton(play_shape);

		this.world.addChild(play_button);
		this.world.addUIElement(play_button);

		play_button.setOnClick((e) -> {
			if (onStartGame != null) {
				onStartGame.accept(this);
			}
		});

		Shape2d tutorial_shape = Polygon2d.createAsBox(new Vec2d(-4.5f, 3), new Vec2d(2.5f, 3));
		UIButton tutorial_button = new UIButton(tutorial_shape);
		this.world.addChild(tutorial_button);
		this.world.addUIElement(tutorial_button);

		tutorial_button.setOnClick((e) -> {
			if (onStartTutorial != null) {
				onStartTutorial.accept(this);
			}
		});


	}

	public void setOnStartGame(Consumer<TitleScreen> onStartGame) {
		this.onStartGame = onStartGame;
	}

	public void setOnStartTutorial(Consumer<TitleScreen> onStartTutorial) {
		this.onStartTutorial = onStartTutorial;
	}

}
