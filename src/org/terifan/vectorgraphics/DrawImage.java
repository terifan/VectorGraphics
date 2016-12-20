package org.terifan.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class DrawImage extends Rectangle implements Primitive
{
	private BufferedImage mBitmap;


	public DrawImage(BufferedImage aBitmap, int x, int y)
	{
		this(aBitmap, x, y, aBitmap.getWidth(), aBitmap.getHeight());
	}


	public DrawImage(BufferedImage aBitmap, int x, int y, int w, int h)
	{
		super(x, y, w, h);
		mBitmap = aBitmap;
	}


	@Override
	public void render(Graphics2D g)
	{
		g.drawImage(mBitmap, x, y, width, height, null);
	}


	@Override
	public Rectangle getBounds()
	{
		return super.getBounds();
	}
}
