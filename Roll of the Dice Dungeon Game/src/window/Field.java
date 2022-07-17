package window;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.Random;

import environment.LevelGenerator;
import environment.Tile;
import geometry.Circle;
import geometry.Polygon2d;
import geometry.Shape2d;
import hud.Clock;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
import ui.UIButton;
import ui.UIText;
import util.GameObject;
import util.GameWorld;
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
	
	LevelGenerator level_gen;
	
	GameObject player;
	Sprite player_sprite;
	float player_z = 0;
	float player_vz = 0;
	int player_index = 0; //dice roll
	
	public Field(Dimension size) throws Exception {
		this.setPreferredSize(size);
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);

		this.thread = new Thread(this);
		running = true;
		runTime = 0;
		seconds = 0;
		refreshTime = (int) (1f/50 * 1000);

		keysDown = new ArrayList<Integer>();
		
		SpriteSheet.loadSpriteSheet("Tiles", "/res/castle_tileset_full.png", 12, 4);
		SpriteSheet.loadSpriteSheet("Player", "/res/player/dice", "die_", 6);
		SpriteSheet.loadSpriteSheet("Shadow", "/res/shadow.png", 1, 1);
		SpriteSheet.loadSpriteSheet("Tutorial", "/res/tutorial.png", 1, 1);
		
		title_screen = new GameWorld();
		//background, graphics, buttons, graphics, foreground
		title_screen.setDrawLayers(new int[] {5, 100, 100, 100, 100});
		title_screen.setPosition(size.width/2f, size.height/2f);
		title_screen.setScale(30, 30);
		title_screen.physics_world.setGravity(0f, 0f);
		
		//FIXME - title for the game
		UIText name_text = new UIText(new Font("Helvetica", 0, 3), "Filler Text");
		name_text.setPosition(-6, 0);
		title_screen.addChild(name_text);
		
		Shape2d play_shape = Polygon2d.createAsBox(new Vec2d(0, 4), new Vec2d(5, 2));
		UIButton play_button = new UIButton(play_shape);

		title_screen.addChild(play_button);
		title_screen.addUIElement(play_button);
		
		
		//Game scene
		game = new GameWorld();
		level_gen = new LevelGenerator(game);
		//background, shadow, player, hud
		game.setDrawLayers(new int[] {200, 100, 100, 100});
		game.setPosition(size.width/2f, size.height/2f);
		game.setScale(45, -45);
		
		player = new GameObject();
		player.setPosition(-7.5f, 0);
		game.addChild(player);
		
		player_sprite = new Sprite(SpriteSheet.getSpriteSheet("Player"), 0, 32);
		player_sprite.setScale(1, -1);
		player_sprite.setPosition(-0.5f, 1f);
		player_sprite.setLayer(2);
		player.addChild(player_sprite);
		
		Body player_body = new Body(new Vec2d(), CollisionType.DYNAMIC);
		player_body.setShape(new Circle(new Vec2d(), 0.5f));
		BodyComponent player_body_comp = new BodyComponent(player_body);
		player.addComponent(player_body_comp);
		
		Sprite player_shadow = new Sprite(SpriteSheet.getSpriteSheet("Shadow"), 0, 16);
		player_shadow.setScale(1, -1);
		player_shadow.setPosition(-0.5f, 0.8f);
		player_shadow.setLayer(1);
		player.addChild(player_shadow);

		Clock timer = new Clock(15.0f, 30);
		timer.setPosition(size.width - 60, 50);
		timer.setLayer(3);
		timer.setOnTimeUp((e) -> {
			System.out.println("Time up!");
		});
		game.addChild(timer);
		
		level_gen.generateLevel(3, -7.5f, -5, 15, 10);
		
		setActiveWorld(title_screen);

		play_button.setOnClick((e) -> {
			setActiveWorld(game);
			timer.start();
		});
		
		tutorial = new GameWorld();
		tutorial.setScale(0.9f, 0.9f);
		//just the image
		tutorial.setDrawLayers(new int[] {5});
		
		Sprite tutorial_sprite = new Sprite(SpriteSheet.getSpriteSheet("Tutorial"), 0, 1);
		tutorial_sprite.setPosition(30, 50);
		tutorial.addChild(tutorial_sprite);
		
		Shape2d tutorial_shape = Polygon2d.createAsBox(new Vec2d(-10, 4), new Vec2d(2.5f, 1));
		UIButton tutorial_button = new UIButton(tutorial_shape);
		title_screen.addChild(tutorial_button);
		title_screen.addUIElement(tutorial_button);
		tutorial_button.setOnClick((e) -> {
			setActiveWorld(tutorial);
		});
		
		Shape2d tutorial_back_shape = Polygon2d.createAsBox(new Vec2d(50, 20), new Vec2d(40, 15));
		UIButton tutorial_back_button = new UIButton(tutorial_back_shape);
		tutorial.addChild(tutorial_back_button);
		tutorial.addUIElement(tutorial_back_button);
		tutorial_back_button.setOnClick((e) -> {
			setActiveWorld(title_screen);
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
		
		curr = to_set;
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
		
		if(keysDown.contains(KeyEvent.VK_A))
			player.move(-speed, 0f);
		if(keysDown.contains(KeyEvent.VK_D))
			player.move(speed, 0f);
		if(keysDown.contains(KeyEvent.VK_W))
			player.move(0f, speed);
		if(keysDown.contains(KeyEvent.VK_S))
			player.move(0f, -speed);
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
						player_index = r.nextInt(6);
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
		
		player_sprite.setPosition(-0.5f, 1 + player_z);
		
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
			
//			curr.debugDraw(g2);
			curr.draw(g2);
			//debug physics shapes draw
			curr.physics_world.forEachShape((shape) -> {
				Shape2d proj = shape.createCopy();
				shape.projectTo(curr.projected, proj);
				proj.debugDraw(g2, true);
			});

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