package util;

import java.awt.Graphics2D;

public interface Drawable {
	
	public void draw(Graphics2D g2);
	public void setIndex(int index);
	public int getIndex();

}
