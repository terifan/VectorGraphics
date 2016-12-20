package org.terifan.ui.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class RenderingHint implements Setting
{
	private RenderingHints.Key key;
	private Object value;


	public RenderingHint(RenderingHints.Key key, Object value)
	{
		this.key = key;
		this.value = value;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.setRenderingHint(key, value);
	}
}
