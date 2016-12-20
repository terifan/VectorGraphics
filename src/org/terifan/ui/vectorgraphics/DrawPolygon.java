package org.terifan.ui.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Polygon;


public class DrawPolygon extends Polygon implements Primitive
{
	public DrawPolygon()
	{
	}


	public DrawPolygon(int[] xpoints, int[] ypoints, int npoints)
	{
		super(xpoints, ypoints, npoints);
	}



	public DrawPolygon(int ... aCoords)
	{
		for (int i = 0; i < aCoords.length; i+=2)
		{
			super.addPoint(aCoords[i], aCoords[i+1]);
		}
	}


	public void render(Graphics2D g)
	{
		g.drawPolygon(this);
	}
}