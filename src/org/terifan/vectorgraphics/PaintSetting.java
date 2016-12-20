package org.terifan.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Paint;


public class PaintSetting implements Setting
{
	private Paint mPaint;


	public PaintSetting(Paint aPaint)
	{
		mPaint = aPaint;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.setPaint(mPaint);
	}
}
