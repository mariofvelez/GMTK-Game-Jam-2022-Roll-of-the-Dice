package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import util.GameObject;

public class UIText extends GameObject {
	
	public Color color;
	public String text;
	Font font;
	GlyphVector gv;
	AffineTransform atx;
	
	public UIText(Font font, String text)
	{
		super();
		
		this.text = text;
		this.font = font;
		atx = new AffineTransform();
	}
	public void debugDraw(Graphics2D g2)
	{
		updateTransform();
		gv = font.createGlyphVector(g2.getFontRenderContext(), text);
		projected.setAffineTransform(atx);
		font = font.deriveFont(atx);
		
		g2.setColor(color);
		g2.drawGlyphVector(gv, 0, 0);
		
	}
	public void draw(Graphics2D g2)
	{
		updateTransform();
		gv = font.createGlyphVector(g2.getFontRenderContext(), text);
		projected.setAffineTransform(atx);
		font = font.deriveFont(atx);
		
		g2.setColor(color);
		g2.drawGlyphVector(gv, 0, 0);
		
	}

}
