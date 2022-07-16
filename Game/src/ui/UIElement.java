package ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface  UIElement {
	
	public void mousePressed(MouseEvent e);
	public void mouseReleased(MouseEvent e);
	public void mouseMoved(MouseEvent e);
	public void keyPressed(KeyEvent e);
	public void keyReleased(KeyEvent e);

}
