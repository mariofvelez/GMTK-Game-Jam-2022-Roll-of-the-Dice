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

import environment.Tile;
import geometry.Polygon2d;
import geometry.Shape2d;
import hud.Clock;
import math.Vec2d;
import ui.UIButton;
import ui.UIText;
import util.GameObject;
import util.GameWorld;
import util.Sprite;
import util.SpriteSheet;

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
	
	Tile[] tiles;
	Color[] tile_colors; //possible colors for the tiles in order
	int tile_length = 3; //current number of colors for the level
	
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
		
		tile_colors = new Color[5];
		tile_colors[0] = new Color(230, 88, 60);
		tile_colors[1] = new Color(75, 178, 219);
		tile_colors[2] = new Color(255, 239, 66);
		tile_colors[3] = new Color(46, 232, 53);
		tile_colors[4] = new Color(99, 44, 176);
		
		
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
		//background, shadow, player, hud
		game.setDrawLayers(new int[] {100, 100, 100, 100});
		game.setPosition(size.width/2f, size.height/2f);
		game.setScale(30, -30);
		
		generateTiles(3, -5, -5, 10, 10);
		
		player = new GameObject();
		player.setPosition(5, 5);
		game.addChild(player);
		
		player_sprite = new Sprite(SpriteSheet.getSpriteSheet("Player"), 0, 32);
		player_sprite.setScale(1, -1);
		player_sprite.setPosition(-0.5f, 1f);
		player_sprite.setLayer(2);
		player.addChild(player_sprite);
		
		Sprite player_shadow = new Sprite(SpriteSheet.getSpriteSheet("Shadow"), 0, 16);
		player_shadow.setScale(1, -1);
		player_shadow.setPosition(-0.5f, 0.8f);
		player_shadow.setLayer(1);
		player.addChild(player_shadow);

		Clock timer = new Clock(5.0f, 30);
		timer.setPosition(0, 0);
		timer.setLayer(3);
		timer.setOnTimeUp((e) -> {
			System.out.println("Time up!");
		});
		game.addChild(timer);

		
		setActiveWorld(title_screen);

		play_button.setOnClick((e) -> {
			setActiveWorld(game);
			timer.start();
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
	
	public Tile createTile(Color c, int state, float x, float y)
	{
		Tile tile = new Tile(c, state);
		tile.setPosition(x, y);
		tile.setLayer(0);
		game.addChild(tile);
		
		return tile;
	}
	/**
	 * Generates a bunch of tiles
	 * @param cols - the number of colors
	 * @param sx - starting position
	 * @param sy
	 * @param width - how many tiles wide
	 * @param height - how many tiles tall
	 */
	public void generateTiles(int cols, int sx, int sy, int width, int height)
	{
		tiles = new Tile[width * height];
		Random r = new Random();
		tile_length = cols;
		for(int y = 0; y < height; ++y)
		{
			for(int x = 0; x < width; ++x)
			{
				int rand = r.nextInt(cols);
				Tile tile = createTile(tile_colors[rand], rand, sx + x, sy + y);
				tile.setNextColor(tile_colors[(rand+1) % tile_length]);
				tiles[y*width + x] = tile;
			}
		}
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
	
	float speed = 0.2f;
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
						for(int j = 0; j < 20; ++j)
						{
							for(int k = 0; k < tiles.length; ++k)
							{
								tiles[k].setChanging(j / 20f);
							}
							
							Thread.currentThread();
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						for(int k = 0; k < tiles.length; ++k)
						{
							tiles[k].state = (tiles[k].state+1) % tile_length;
							tiles[k].setColor(tile_colors[tiles[k].state]);
							tiles[k].setNextColor(tile_colors[(tiles[k].state+1) % tile_length]);
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
			
			curr.debugDraw(g2);
//			curr.draw(g2);

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