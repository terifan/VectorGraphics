package org.terifan.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public class DrawString extends Rectangle implements Primitive
{
	private String mText;
	private Anchor mAnchor;
	private boolean mMultiline;


	public DrawString(String aLabel, int x, int y, int w, int h, Anchor aAnchor, boolean aMultiline)
	{
		super(x, y, w, h);
		mText = aLabel;
		mAnchor = aAnchor;
		mMultiline = aMultiline;
	}


	@Override
	public void render(Graphics2D g)
	{
		Utilities.drawString(g, mText, x, y, width, height, mAnchor, g.getColor(), g.getBackground(), mMultiline);
	}
}
