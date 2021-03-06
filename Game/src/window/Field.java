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

import geometry.Polygon2d;
import math.Vec2d;
import physics.body.Body;
import physics.body.CollisionType;
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
	
	GameWorld world;
	GameObject obj;
	UIText text;
	
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
		
		world = new GameWorld();
		world.setScale(30, -30);
		world.setPosition(100, 100);
		world.physics_world.setGravity(new Vec2d(0, -9.8f));
		
		obj = new GameObject();
		obj.setPosition(1.5f, 1.5f);
		world.addChild(obj);
		
		text = new UIText(new Font("Helvetica", 0, 20), "some text");
		text.setPosition(5, -2);
		text.setScale(0.1f, -0.1f);
		world.addChild(text);
		
		Body body = new Body(new Vec2d(0, 0), CollisionType.DYNAMIC);
		Vec2d[] verts = {
				new Vec2d(600, 335),
				new Vec2d(653, 371),
				new Vec2d(597, 455),
				new Vec2d(510, 437),
				new Vec2d(532, 375)
		};
		Polygon2d polygon = new Polygon2d(verts);
		polygon.move(-545, -400);
		for(int i = 0; i < polygon.vertices.length; ++i)
			polygon.vertices[i].mult(1f / 30);
		body.setShape(polygon);
		obj.addComponent(new BodyComponent(body));
//		body.setRotationSpeed(1f);
		
		SpriteSheet.loadSpriteSheet("bee", "/res/bee_cyan.png", 1, 1);
		Sprite sprite = new Sprite(SpriteSheet.getSpriteSheet("bee"), 0, 16);
		sprite.setScale(1, -1);
		sprite.setPosition(-0.5f, 0.5f);
		obj.addChild(sprite);
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

	public void DoLogic()
	{
		if(keysDown.contains(KeyEvent.VK_A))
		{
			world.setRotationSpeed(0.05f);
			world.rotate();
		}
		if(keysDown.contains(KeyEvent.VK_D))
		{
			world.setRotationSpeed(-0.05f);
			world.rotate();
		}
		if(keysDown.contains(KeyEvent.VK_Q))
		{
			obj.setRotationSpeed(0.05f);
			obj.rotate();
		}
		if(keysDown.contains(KeyEvent.VK_E))
		{
			obj.setRotationSpeed(-0.05f);
			obj.rotate();
		}
		
		world.step();
		
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
			
			world.debugDraw(g2);
			world.physics_world.forEachShape((shape) -> {
				shape.debugDraw(g2, false, Color.ORANGE);
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