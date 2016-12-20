package org.terifan.vectorgraphics;

import java.awt.Color;
import java.awt.Graphics2D;


public class ColorSetting implements Setting
{
	private Color mColor;


	public ColorSetting(Color aColor)
	{
		mColor = aColor;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.setColor(mColor);
	}
}
