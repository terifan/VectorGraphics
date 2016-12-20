package org.terifan.ui.vectorgraphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;


public class Region implements Primitive
{
	private String mIdentity;
	private Rectangle mBounds;
	private Object mUserObject;


	public Region(String aIdentity, int x, int y, int w, int h)
	{
		this(aIdentity, new Rectangle(x,y,w,h), null);
	}


	public Region(String aIdentity, int x, int y, int w, int h, Object aUserObject)
	{
		this(aIdentity, new Rectangle(x,y,w,h), aUserObject);
	}


	public Region(String aIdentity, Rectangle aRectangle)
	{
		this(aIdentity, aRectangle, null);
	}

 
	public Region(String aIdentity, Rectangle aRectangle, Object aUserObject)
	{
		mIdentity = aIdentity;
		mBounds = aRectangle;
		mUserObject = aUserObject;
	}


	public String getIdentity()
	{
		return mIdentity;
	}


	public void setIdentity(String aIdentity)
	{
		mIdentity = aIdentity;
	}


	@Override
	public Rectangle getBounds()
	{
		return mBounds;
	}


	public void setBounds(Rectangle aBounds)
	{
		mBounds = aBounds;
	}


	public Object getUserObject()
	{
		return mUserObject;
	}


	public void setUserObject(Object aUserObject)
	{
		mUserObject = aUserObject;
	}


	@Override
	public void render(Graphics2D aGraphics)
	{
	}
}