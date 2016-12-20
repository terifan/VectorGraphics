package org.terifan.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public class DrawRect extends Rectangle implements Primitive
{
	public DrawRect()
	{
	}


	public DrawRect(int x, int y, int width, int height)
	{
		super(x, y, width, height);
	}


	public void render(Graphics2D g)
	{
		g.draw(this);
	}
}