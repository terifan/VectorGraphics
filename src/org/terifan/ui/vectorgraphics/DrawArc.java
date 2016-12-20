package org.terifan.ui.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public class DrawArc extends Rectangle implements Primitive
{
	protected int mStartAngle;
	protected int mEndAngle;


	public DrawArc()
	{
	}


	public DrawArc(int x, int y, int width, int height, int aStartAngle, int aEndAngel)
	{
		super(x, y, width, height);

		mStartAngle = aStartAngle;
		mEndAngle = aEndAngel;
	}


	public void render(Graphics2D g)
	{
		g.drawArc(x, y, width, height, mStartAngle, mEndAngle);
	}
}