package window;

import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
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
	
	public Window(String name) throws IOException
	{
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Dimension windowSize = new Dimension(WIDTH, HEIGHT);
		this.setSize(windowSize);
		
		URL url = this.getClass().getResource("/res/testDie.png");
		this.setIconImage(ImageIO.read(url));
	}
	public static void main(String[] args) throws Exception
	{
		window = new Window("Die Dungeon");
		
		Container contentPane = window.getContentPane();
		
		Field field = new Field(window.getSize());
		contentPane.add(field);
		
		window.setResizable(false);
		window.setVisible(true);
		window.setLocationRelativeTo(null);


		
	}
	public static void close()
	{
		window.dispose();
	}
}
