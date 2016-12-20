package org.terifan.vectorgraphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;


public final class Utilities
{
	protected final static FileSystemView mFileSystemView = FileSystemView.getFileSystemView();
	protected final static FontRenderContext mFontRenderContext = new FontRenderContext(new AffineTransform(), false, false);
	protected final static WeakHashMap<String,Icon> mFileIcons = new WeakHashMap<>();


	private Utilities()
	{
	}


	public static String clipString(String aString, Font aFont, int aLength)
	{
		aString = aString.trim();

		if (aString.isEmpty() || aLength == 0)
		{
			return "";
		}

		if (aFont.getStringBounds(aString, mFontRenderContext).getWidth() < aLength)
		{
			return aString;
		}

		char[] chars = (aString + "..").toCharArray();
		int len = aString.length() + 2;

		for (;len > 0; len--)
		{
			if (len > 3)
			{
				chars[len - 3] = '.';
			}

			if (aFont.getStringBounds(chars, 0, len, mFontRenderContext).getWidth() < aLength)
			{
				break;
			}
		}

		return new String(chars, 0, len);
	}


	public static void drawString(Graphics aGraphics, String aString, Rectangle aBounds, Anchor aAnchor, Color aTextColor, Color aBackground, boolean aMultiline)
	{
		drawString(aGraphics, aString, aBounds.x, aBounds.y, aBounds.width, aBounds.height, aAnchor, aTextColor, aBackground, aMultiline);
	}


	/**
	 * Draws a multipline string within the bounds specified.
	 *
	 * @param aGraphics
	 *   draw on this context
	 * @param aString
	 *   the string to draw
	 * @param aPositionX
	 *   x offset
	 * @param aPositionY
	 *   y offset
	 * @param aWidth
	 *   width of the bounds to draw within
	 * @param aHeight
	 *   height of the bounds to draw within
	 * @param aAnchor
	 *   specifying the orientation of the text.
	 * @param aTextColor
	 *   Text color to use or null
	 * @param aBackground
	 *   Text background or null
	 */
	public static void drawString(Graphics aGraphics, String aString, int aPositionX, int aPositionY, int aWidth, int aHeight, Anchor aAnchor, Color aTextColor, Color aBackground, boolean aMultiline)
	{
		if (aString == null || aString.isEmpty())
		{
			return;
		}

		Font font = aGraphics.getFont();

		if (aTextColor == null && aBackground != null)
		{
			aTextColor = aGraphics.getColor();
		}

		ArrayList<String> list;

		if (aMultiline)
		{
			list = lineBreakText(aString, font, aWidth);
		}
		else if (aWidth > 0)
		{
			list = new ArrayList<>();
			list.add(clipString(aString, font, aWidth));
		}
		else
		{
			list = new ArrayList<>();
			list.add(aString);
		}

		int lineHeight = (int)font.getStringBounds(aString, mFontRenderContext).getHeight();

		LineMetrics lm = font.getLineMetrics("Adgj", mFontRenderContext);
		int ascent = (int)lm.getAscent();

		int lineCount;
		if (aHeight <= 0)
		{
			lineCount = list.size();
			aHeight = lineCount * lineHeight;

			if (aAnchor == Anchor.SOUTH_EAST || aAnchor == Anchor.SOUTH || aAnchor == Anchor.SOUTH_WEST)
			{
				aPositionY -= aHeight;
			}
			if (aAnchor == Anchor.WEST || aAnchor == Anchor.CENTER || aAnchor == Anchor.EAST)
			{
				aPositionY -= aHeight/2;
			}
		}
		else
		{
			lineCount = Math.min(list.size(), aHeight / lineHeight);
		}

		if (aAnchor == Anchor.SOUTH_EAST || aAnchor == Anchor.SOUTH || aAnchor == Anchor.SOUTH_WEST)
		{
			aPositionY += Math.max(0, aHeight-lineCount*lineHeight);
		}
		else if (aAnchor == Anchor.CENTER || aAnchor == Anchor.WEST || aAnchor == Anchor.EAST)
		{
			aPositionY += Math.max(0, (aHeight-lineCount*lineHeight)/2);
		}

		for (int i = 0; i < lineCount; i++)
		{
			String str = list.get(i);

			int x = aPositionX, w = -1;

			if (aAnchor == Anchor.NORTH || aAnchor == Anchor.CENTER || aAnchor == Anchor.SOUTH)
			{
				w = getStringLength(str, font);
				x += (aWidth-w)/2;
			}
			else if (aAnchor == Anchor.NORTH_EAST || aAnchor == Anchor.EAST || aAnchor == Anchor.SOUTH_EAST)
			{
				w = getStringLength(str, font);
				x += aWidth-w;
			}

			int y = aPositionY+i*lineHeight;

			if (aBackground != null)
			{
				if (w == -1)
				{
					w = getStringLength(str, font);
				}
				aGraphics.setColor(aBackground);
				aGraphics.fillRect(x, y, w+1, lineHeight+1);
			}
			if (aTextColor != null)
			{
				aGraphics.setColor(aTextColor);
			}

			aGraphics.drawString(str, x, y+ascent);
		}
	}


	public static int getStringLength(String aString, Font aFont)
	{
		return (int)aFont.getStringBounds(aString, mFontRenderContext).getWidth();
	}


	public static ArrayList<String> lineBreakText(String aString, Font aFont, int aWidth)
	{
		ArrayList<String> list = new ArrayList<>();

		for (String str : aString.split("\n"))
		{
			do
			{
				int w = getStringLength(str, aFont);
				String tmp;

				if (w > aWidth)
				{
					int offset = findStringLimit(str, aFont, aWidth);
					int temp = Math.max(str.lastIndexOf(' ', offset), Math.max(str.lastIndexOf('.', offset), Math.max(str.lastIndexOf('-', offset), str.lastIndexOf('_', offset))));
					offset = Math.max(1, temp > 1 ? temp : offset);

					tmp = str.substring(0,offset);
					str = str.substring(offset).trim();
				}
				else
				{
					tmp = str.trim();
					str = "";
				}

				list.add(tmp.trim());
			}
			while (str.length() > 0);
		}

		return list;
	}


	private static int findStringLimit(String aString, Font aFont, int aWidth)
	{
		int min = 0;
		int max = aString.length();

		while (Math.abs(min-max) > 1)
		{
			int mid = (max+min)/2;

			int w = getStringLength(aString.substring(0,mid), aFont);

			//System.out.printf("%d\t%d\t%d\t%d\t%d\n", min, max, mid, aWidth, w);

			if (w > aWidth)
			{
				max = mid;
			}
			else
			{
				min = mid;
			}
		}

		return min;
	}
}