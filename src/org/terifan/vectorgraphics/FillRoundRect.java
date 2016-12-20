package org.terifan.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public class FillRoundRect extends Rectangle implements Primitive
{
	private int r0, r1;


	public FillRoundRect(int x, int y, int w, int h, int r0, int r1)
	{
		super(x, y, w, h);
		this.r0 = r0;
		this.r1 = r1;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.fillRoundRect(x, y, width, height, r0, r1);
	}
}
