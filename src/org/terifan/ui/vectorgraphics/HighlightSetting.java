package org.terifan.ui.vectorgraphics;

import java.awt.Color;
import java.awt.Graphics2D;


public class HighlightSetting implements Setting
{
	private Color mColor;


	public HighlightSetting(Color aColor)
	{
		mColor = aColor;
	}


	public Color getColor()
	{
		return mColor;
	}


	public void render(Graphics2D aGraphics)
	{
	}
}
