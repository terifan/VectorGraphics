package org.terifan.vectorgraphics;

import java.awt.Color;
import java.awt.Graphics2D;


public class BackgroundSetting implements Setting
{
	private Color mColor;


	public BackgroundSetting(Color aColor)
	{
		mColor = aColor;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.setBackground(mColor);
	}
}
