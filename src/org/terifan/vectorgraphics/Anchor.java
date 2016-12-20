package org.terifan.vectorgraphics;

import java.awt.Rectangle;

public enum Anchor
{
	NORTH_WEST, 
	NORTH,
	NORTH_EAST,
	CENTER,
	SOUTH_WEST,
	SOUTH,
	SOUTH_EAST,
	WEST,
	EAST;
	
	public void translate(Rectangle aBounds, Rectangle aOuter)
	{
		switch (this)
		{
			case WEST:
			case NORTH_WEST:
			case SOUTH_WEST:
				aBounds.x = aOuter.x;
				break;
			case CENTER:
			case NORTH:
			case SOUTH:
				aBounds.x = aOuter.x + (aOuter.width - aBounds.width) / 2;
				break;
			default:
				aBounds.x = aOuter.x + aOuter.width - aBounds.width;
				break;
		}
		switch (this)
		{
			case NORTH_WEST:
			case NORTH:
			case NORTH_EAST:
				aBounds.y = aOuter.y;
				break;
			case WEST:
			case CENTER:
			case EAST:
				aBounds.y = aOuter.y + (aOuter.height - aBounds.height) / 2;
				break;
			default:
				aBounds.y = aOuter.y + aOuter.height - aBounds.height;
				break;
		}
	}
}
