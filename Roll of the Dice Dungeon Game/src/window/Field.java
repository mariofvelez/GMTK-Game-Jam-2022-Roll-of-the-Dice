package window;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import Scenes.TitleScreen;
import environment.LevelGenerator;
import environment.Tile;
import geometry.Circle;
import geometry.Polygon2d;
import geometry.Shape2d;
import hud.Clock;
import hud.HealthBar;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import ui.UIButton;
import ui.UIText;
import util.GameObject;
import util.GameWorld;
import util.ParticleEmitter;
import util.Sprite;
import util.SpriteSheet;
import util.components.BodyComponent;

/**
 * 
 * @author Mario Velez
 *
 */
public class Field extends Canvas
		implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener,
				   Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -796167392411348854L;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Graphics bufferGraphics; // graphics for backbuffer
	private BufferStrategy bufferStrategy;
	
	public static int mousex = 0; // mouse values
	public static int mousey = 0;

	public static ArrayList<Integer> keysDown; // holds all the keys being held down
	boolean leftClick;

	private Thread thread;

	private boolean running;
	private int runTime;
	private float seconds;
	private int refreshTime;
	
	public static int[] anchor = new  int[2];
	public static boolean dragging;
	
	GameWorld curr; //holds the current GameWorld that's active
	GameWorld title_screen; //title screen scene
	GameWorld game; //game scene
	GameWorld tutorial;
	HealthBar health_bar;
	Clock timer;
	
	LevelGenerator level_gen;

	TitleScreen title_screen_scene;
	
	GameObject player;
	Sprite player_sprite;
	float player_z = 0;
	float player_vz = 0;
	int player_index = 0; //dice roll
	
	ParticleEmitter player_death;
	
	int safe_col = 0;
	Vec2d[] sides = new Vec2d[] {
			new Vec2d(-8.5f, 0f),
			new Vec2d(0f, 6f),
			new Vec2d(8.5f, 0f),
			new Vec2d(0f, -6f)
	};
	Vec2d restart_pos = sides[0];
	int last_side = 0;
	int max_roll = 2;
	int curr_level = 1;
	UIText level_text;
	
	public Field(Dimension size) throws Exception {
		this.setPreferredSize(size);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
//		this.setBackground(new Color(54, 37, 37));

		this.thread = new Thread(this);
		running = true;
		runTime = 0;
		seconds = 0;
		refreshTime = (int) (1f/50 * 1000);

		keysDown = new ArrayList<Integer>();
		
		SpriteSheet.loadSpriteSheet("Walls", "/res/castle_tileset_full.png", 12, 4);
		SpriteSheet.loadSpriteSheet("Player", "/res/player/dice", "die_", 6);
		SpriteSheet.loadSpriteSheet("Shadow", "/res/shadow.png", 1, 1);
		SpriteSheet.loadSpriteSheet("Tutorial", "/res/tutorial.png", 1, 1);
		SpriteSheet.loadSpriteSheet("Felt", "/res/feltPixelArt.png", 1, 1);
		SpriteSheet.loadSpriteSheet("Cards", "/res/cards", "card_", 60);

		SpriteSheet.loadSpriteSheet("PlayCard", "/res/PlayCard.png", 1, 1);
		SpriteSheet.loadSpriteSheet("TutCard", "/res/TutorialCard.png", 1, 1);


		title_screen = new GameWorld();

		title_screen_scene = new TitleScreen(title_screen, size);
		
		
		//Game scene
		game = new GameWorld();
		level_gen = new LevelGenerator(game);
		//background, shadow, player, hud
		game.setDrawLayers(new int[] {500, 500, 100, 100});
		game.setPosition(size.width/2f, size.height/2f - 15);
		game.setScale(45, -45);
		
		player = new GameObject();
		player.setPosition(restart_pos.x, restart_pos.y);
		game.addChild(player);
		
		player_sprite = new Sprite(SpriteSheet.getSpriteSheet("Player"), 0, 32);
		player_sprite.setScale(1, -1);
		player_sprite.setPosition(-0.5f, 0.5f);
		player_sprite.setLayer(2);
		player.addChild(player_sprite);
		
		Body player_body = new Body(new Vec2d(), CollisionType.DYNAMIC);
		player_body.setShape(new Circle(new Vec2d(), 0.3f));
		BodyComponent player_body_comp = new BodyComponent(player_body);
		player.addComponent(player_body_comp);
		
		Sprite player_shadow = new Sprite(SpriteSheet.getSpriteSheet("Shadow"), 0, 16);
		player_shadow.setScale(1, -1);
		player_shadow.setPosition(-0.5f, 0.8f);
		player_shadow.setLayer(1);
		player.addChild(player_shadow);
		
		player_death = new ParticleEmitter(500);
		player_death.setShape(new Circle(new Vec2d(0, 0), 1f), 0, (float) Math.PI*2f);
		player_death.radius = 3f;
		player_death.lifetime = 1f;
		player_death.emission_speed = 200;
		player_death.start_color = new Color(255, 255, 255, 255);
		player_death.end_color = new Color(255, 255, 255, 0);
		player_death.particle_speed = 5f;
		player_death.repeating = false;
		player_death.duration = 0.1f;
		player_death.gravity_scale = 0f;
		player_death.attached_body = player_body;
		player_death.setLayer(1);
		player.addChild(player_death);

		timer = new Clock(45.0f, 30);
		timer.setPosition(size.width - 60, 50);
		timer.setLayer(3);
		timer.setOnTimeUp((e) -> {
			System.out.println("Time up!");
			loseLife();
		});
		game.addChild(timer);
		
		level_text = new UIText(title_screen_scene.titleFont.deriveFont(1f), "Level: " + curr_level);
		level_text.setPosition(-8f, 5f);
		level_text.setScale(1, -1);
		level_text.setLayer(3);
		game.addChild(level_text);
		
		level_gen.generateLevel(2, -7.5f, -5, 15, 10);
		this.setBackground(level_gen.tile_colors[safe_col]);
		
		setActiveWorld(title_screen);

		title_screen_scene.setOnStartGame((e) -> {
			setActiveWorld(game);
			timer.start();
		});
		
		tutorial = new GameWorld();
//		tutorial.setScale(0.9f, 0.9f);
		//just the image
		tutorial.setDrawLayers(new int[] {5,5});
		
		Sprite tutorial_sprite = new Sprite(SpriteSheet.getSpriteSheet("Tutorial"), 0, 1f);
		tutorial_sprite.setPosition(-10, 50);
		tutorial.addChild(tutorial_sprite);
		

		title_screen_scene.setOnStartTutorial((e) -> {
			setActiveWorld(tutorial);
		});
		
		Shape2d tutorial_back_shape = Polygon2d.createAsBox(new Vec2d(50, 30), new Vec2d(40, 40));
		UIButton tutorial_back_button = new UIButton(tutorial_back_shape);
		tutorial.addChild(tutorial_back_button);
		tutorial.addUIElement(tutorial_back_button);
		tutorial_back_button.setOnClick((e) -> {
			setActiveWorld(title_screen);
		});

		Sprite backSprite = new Sprite(SpriteSheet.getSpriteSheet("PlayCard"), 0, 1);

		backSprite.setRotation(0,0, (float) Math.toRadians(90));
		backSprite.rotate();
		backSprite.setScale(.5f, .5f);
		backSprite.setPosition(80, 15);
		backSprite.setLayer(1);
		tutorial.addChild(backSprite);

		health_bar = new HealthBar(5);
		health_bar.setPosition(3, -5);
		health_bar.setLayer(3);
		health_bar.setScale(1, -1);
		game.addChild(health_bar);
		health_bar.init();
		health_bar.setOnDie((e) -> {
			System.out.println("You died!");
			
			Thread after_die_thread = new Thread(new Runnable()
			{
				public void run()
				{
					Thread.currentThread();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					setActiveWorld(title_screen);
					health_bar.reset(5);
					safe_col = 0;
					max_roll = 2;
					curr_level = 1;
					level_text.text = "Level: 1";
					level_gen.tile_length = 2;
				}
			});
			after_die_thread.start();
			
		});

	}

	public void setActiveWorld(GameWorld to_set)
	{
		this.removeMouseListener(curr);
		this.removeKeyListener(curr);
		this.removeMouseMotionListener(curr);
		
		this.addMouseListener(to_set);
		this.addKeyListener(to_set);
		this.addMouseMotionListener(to_set);
		
		if(to_set == tutorial)
			this.setBackground(new Color(230, 230, 230));
		if(to_set == game)
			this.setBackground(level_gen.tile_colors[safe_col]);
		
		curr = to_set;
	}
	
	public void loseLife()
	{
		if(curr != game)
			return;
		
		player_death.play();
		health_bar.removeLife();
		player.setPosition(restart_pos.x, restart_pos.y);
		timer.start();
	}
	
	public void nextLevel()
	{
		curr_level++;
		level_text.text = "Level: " + curr_level;
		
		timer.start();
		
		game.forEachID((obj) -> {
			obj.delete();
		}, 5);
		
		if(Math.random() < 0.1f)
			max_roll++;
		if(max_roll > 6)
			max_roll = 6;
		
		if(Math.random() < 0.3f)
			level_gen.tile_length++;
		if(level_gen.tile_length > 5)
			level_gen.tile_length = 5;
		
		player.setPosition(restart_pos.x, restart_pos.y);
		
		safe_col = (int) (Math.random() * level_gen.tile_length);
		
		level_gen.generateTiles(level_gen.tile_length, -7.5f, -5f, 15, 10);
		
		this.setBackground(level_gen.tile_colors[safe_col]);
	}

	public void paint(Graphics g) {


		if (bufferStrategy == null) {
			this.createBufferStrategy(2);
			bufferStrategy = this.getBufferStrategy();
			bufferGraphics = bufferStrategy.getDrawGraphics();

			this.thread.start();
		}
	}
	@Override
	public void run() {
		// what runs when editor is running
		
		while (running) {
			long t1 = System.currentTimeMillis();
			
			DoLogic();
			Draw();

			DrawBackbufferToScreen();

			Thread.currentThread();
			try {
				Thread.sleep(refreshTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long t2 = System.currentTimeMillis();
			
			if(t2 - t1 > 16)
			{
				if(refreshTime > 0)
					refreshTime --;
			}
			else
				refreshTime ++;
			
			seconds += refreshTime/1000f;
			//System.out.println(t2 - t1);
			

		}
	}

	public void DrawBackbufferToScreen() {
		bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();
	}
	
	float speed = 0.1f;
	public void DoLogic() {
		
		if(keysDown.contains(KeyEvent.VK_A) || keysDown.contains(KeyEvent.VK_LEFT))
			player.move(-speed, 0f);
		if(keysDown.contains(KeyEvent.VK_D) || keysDown.contains(KeyEvent.VK_RIGHT))
			player.move(speed, 0f);
		if(keysDown.contains(KeyEvent.VK_W )|| keysDown.contains(KeyEvent.VK_UP))
			player.move(0f, speed);
		if(keysDown.contains(KeyEvent.VK_S )|| keysDown.contains(KeyEvent.VK_DOWN))
			player.move(0f, -speed);
		
		Vec2d p_pos = new Vec2d(player.projected.data[2], player.projected.data[5]);
		boolean hit = level_gen.checkIntersected(p_pos, safe_col);
		if(hit && player_z == 0)
		{
			loseLife();
		}
		
		Vec2d pos = new Vec2d(player.transform.data[2], player.transform.data[5]);
		if(pos.x > 8.5f && last_side != 2)
		{
			last_side = 0;
			restart_pos = sides[last_side];
			nextLevel();
		}
		if(pos.x < -8.5f && last_side != 0)
		{
			last_side = 2;
			restart_pos = sides[last_side];
			nextLevel();
		}
		if(pos.y > 6f && last_side != 1)
		{
			last_side = 3;
			restart_pos = sides[last_side];
			nextLevel();
		}
		if(pos.y < -6f && last_side != 3)
		{
			last_side = 1;
			restart_pos = sides[last_side];
			nextLevel();
		}
		
		if(keysDown.contains(KeyEvent.VK_SPACE) && player_z == 0)
		{
			//jump, roll dice again
			player_vz = 0.4f;
			player_z = 0.0001f;
			
			Thread tile_thread = new Thread(new Runnable()
			{
				public void run()
				{
					int index = player_index;
					
					for(int i = 0; i < index+1; ++i) //change color for each number on the dice roll
					{
						for(int j = 0; j < 0; ++j)
						{
							for(int k = 0; k < level_gen.tiles.length; ++k)
							{
								level_gen.tiles[k].setChanging(j / 20f);
							}
							
							
						}
						for(int k = 0; k < level_gen.tiles.length; ++k)
						{
							level_gen.tiles[k].state = (level_gen.tiles[k].state+1) % level_gen.tile_length;
							level_gen.tiles[k].setColor(level_gen.tile_colors[level_gen.tiles[k].state]);
							level_gen.tiles[k].setNextColor(level_gen.tile_colors[(level_gen.tiles[k].state+1) % level_gen.tile_length]);
							level_gen.tiles[k].setChanging(0);
						}
						Thread.currentThread();
						try {
							Thread.sleep(0);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			tile_thread.start();
			
			Thread roll_thread = new Thread(new Runnable()
			{
				public void run()
				{
					Random r = new Random();
					
					while(player_z > 0)
					{
						player_index = r.nextInt(max_roll);
						player_sprite.setImage(player_index);
						
						Thread.currentThread();
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			roll_thread.start();
		}
		
		player_vz -= 0.03f;
		player_z += player_vz;
		
		if(player_z < 0)
		{
			player_z = 0;
			player_vz = 0;
		}
		
		player_sprite.setPosition(-0.5f, 0.8f + player_z);
		
		curr.step();
		runTime++;
	}
	
	public void Draw() // titleScreen
	{
		// clears the backbuffer
		bufferGraphics = bufferStrategy.getDrawGraphics();
		try {
			bufferGraphics.clearRect(0, 0, this.getSize().width, this.getSize().height);
			// where everything will be drawn to the backbuffer
			Graphics2D g2 = (Graphics2D) bufferGraphics;
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			curr.draw(g2);
//			curr.debugDraw(g2);
			//debug physics shapes draw
//			curr.physics_world.forEachShape((shape) -> {
//				Shape2d proj = shape.createCopy();
//				shape.projectTo(curr.projected, proj);
//				proj.debugDraw(g2, true);
//			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bufferGraphics.dispose();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!keysDown.contains(e.getKeyCode()) && e.getKeyCode() != 86)
			keysDown.add(new Integer(e.getKeyCode()));
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(new Integer(e.getKeyCode()));
		
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1)
		{
			leftClick = true;
		}
		else if(e.getButton() == 2)
		{
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1)
			leftClick = false;
		if(e.getButton() == 2)
			dragging = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(leftClick)
			leftClick = true;
		mousex = e.getX();
		mousey = e.getY();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mousex = e.getX();
		mousey = e.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		
	}

}