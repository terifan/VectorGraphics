package org.terifan.ui.vectorgraphics;

import java.awt.Stroke;
import java.awt.Graphics2D;


public class StrokeSetting implements Setting
{
	private Stroke mStroke;


	public StrokeSetting(Stroke aStroke)
	{
		mStroke = aStroke;
	}


	public Stroke getStroke()
	{
		return mStroke;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.setStroke(mStroke);
	}
}
