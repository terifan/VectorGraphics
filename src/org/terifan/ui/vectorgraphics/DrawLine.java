package org.terifan.ui.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;


public class DrawLine implements Primitive
{
	private Point [] mPoints;


	public DrawLine(Point ... aPoints)
	{
		mPoints = aPoints;
	}


	public DrawLine(int ... aCoords)
	{
		mPoints = new Point[aCoords.length/2];

		for (int i = 0, j = 0; i < aCoords.length; i+=2, j++)
		{
			mPoints[j] = new Point(aCoords[i], aCoords[i+1]);
		}
	}


	@Override
	public void render(Graphics2D g)
	{
		for (int i = 1; i < mPoints.length; i++)
		{
			g.drawLine(mPoints[i-1].x, mPoints[i-1].y, mPoints[i].x, mPoints[i].y);
		}
	}


	public Rectangle getBounds()
	{
		Rectangle bounds = new Rectangle(mPoints[0]);
		for (Point p : mPoints)
		{
			bounds.add(p);
		}
		return bounds;
	}
}