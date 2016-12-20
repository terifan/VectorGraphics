package org.terifan.ui.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public interface Primitive extends Element
{
	public void render(Graphics2D aGraphics);

	public Rectangle getBounds();
}