package org.terifan.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Polygon;


public class FillPolygon extends Polygon implements Primitive
{
	public FillPolygon()
	{
	}


	public FillPolygon(int[] xpoints, int[] ypoints, int npoints)
	{
		super(xpoints, ypoints, npoints);
	}



	public FillPolygon(int ... aCoords)
	{
		for (int i = 0; i < aCoords.length; i+=2)
		{
			super.addPoint(aCoords[i], aCoords[i+1]);
		}
	}


	public void render(Graphics2D g)
	{
		g.fillPolygon(this);
	}
}