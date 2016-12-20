package org.terifan.vectorgraphics;

import java.awt.Graphics2D;

public interface RenderStateListener
{
	public void initializeRendering(Graphics2D aGraphics);

	public void beginRendering(Graphics2D aGraphics);
	
	public void finishRendering(Graphics2D aGraphics);
}
