package window;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JFrame;

/**
 * 
 * @author Mario Velez
 * 
 *
 */
public class Window extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3494605708565768482L;
	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	
	public static Window window;
	
	public Window(String name)
	{
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension windowSize = new Dimension(WIDTH, HEIGHT);
		this.setSize(windowSize);
		//get a buffer image from a file
		BufferedImage img = null;
		try
		{
			URL url = this.getClass().getResource("/res/testDie.png");
			img = javax.imageio.ImageIO.read(url);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(img != null) {
			this.setIconImage(img);
		}
	}
	public static void main(String[] args) throws Exception
	{
		window = new Window("Die Dungeon");
		
		Container contentPane = window.getContentPane();
		
		Field field = new Field(window.getSize());
		contentPane.add(field);
		
		window.setVisible(true);
		window.setLocation(200, 100);
	}
	public static void close()
	{
		window.dispose();
	}
}
