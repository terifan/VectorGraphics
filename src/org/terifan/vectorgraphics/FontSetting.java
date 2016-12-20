package org.terifan.vectorgraphics;

import java.awt.Font;
import java.awt.Graphics2D;


public class FontSetting implements Setting
{
	private Font mFont;


	public FontSetting(Font aFont)
	{
		mFont = aFont;
	}


	public Font getFont()
	{
		return mFont;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.setFont(mFont);
	}
}
