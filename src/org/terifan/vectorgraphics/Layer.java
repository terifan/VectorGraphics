package org.terifan.vectorgraphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Layer
{
	private static FontRenderContext FRC = new FontRenderContext(new AffineTransform(), false, false);

	protected ArrayList<Element> mElements;
	protected ArrayList<Layer> mLayers;
	protected Point mTranslate;
	protected HashMap<String,Element> mIdentities;
	protected Layer mParent;
	protected Canvas mCanvas;
	protected Color mHighlight;


	Layer()
	{
		mElements = new ArrayList<>();
		mLayers = new ArrayList<>();
		mIdentities = new HashMap<>();
		mTranslate = new Point();

		add(new Font("Tahoma", Font.PLAIN, 11));
	}


	/**
	 * Creates and adds a new Layer to this Layer.
	 *
	 * @return
	 *  the new Layer instance.
	 */
	public Layer createLayer()
	{
		Layer layer = new Layer();
		layer.mCanvas = mCanvas;
		layer.mParent = this;
		mLayers.add(layer);
		return layer;
	}


	public Layer getParent()
	{
		return mParent;
	}


	public Canvas getCanvas()
	{
		return mCanvas;
	}


	public Layer add(Font aFont)
	{
		add(new FontSetting(aFont));
		return this;
	}


	public Layer add(String aIdentity, Font aFont)
	{
		add(aIdentity, new FontSetting(aFont));
		return this;
	}


	public Layer add(Color aColor)
	{
		add(new ColorSetting(aColor));
		return this;
	}


	public Layer add(String aIdentity, Color aColor)
	{
		add(aIdentity, new ColorSetting(aColor));
		return this;
	}


	public Layer add(Stroke aStroke)
	{
		add(new StrokeSetting(aStroke));
		return this;
	}


	public Layer add(String aIdentity, Stroke aStroke)
	{
		add(aIdentity, new StrokeSetting(aStroke));
		return this;
	}


	public Layer add(Paint aPaint)
	{
		add(new PaintSetting(aPaint));
		return this;
	}


	public Layer add(String aIdentity, Paint aPaint)
	{
		add(aIdentity, new PaintSetting(aPaint));
		return this;
	}


	public Layer add(Setting ... aSettings)
	{
		mElements.addAll(Arrays.asList(aSettings));
		return this;
	}


	public Layer add(Primitive ... aPrimitives)
	{
		mElements.addAll(Arrays.asList(aPrimitives));
		return this;
	}


	public Layer add(String aIdentity, Setting aSetting)
	{
		mIdentities.put(aIdentity, aSetting);
		mElements.add(aSetting);
		return this;
	}


	public Layer add(String aIdentity, Primitive aPrimitive)
	{
		mIdentities.put(aIdentity, aPrimitive);
		mElements.add(aPrimitive);
		return this;
	}


	public void clear()
	{
		mElements.clear();
	}


	public void translate(int x, int y)
	{
		mTranslate.x = x;
		mTranslate.y = y;
	}


	public void render(Graphics2D aGraphics)
	{
		aGraphics.translate(mTranslate.x, mTranslate.y);

		for (Element element : mElements)
		{
			if (element instanceof HighlightSetting)
			{
				mHighlight = ((HighlightSetting)element).getColor();
			}
			else if(element instanceof Setting)
			{
				((Setting)element).render(aGraphics);
			}
			else
			{
				if (mHighlight != null)
				{
					Color c = aGraphics.getColor();
					aGraphics.setColor(mHighlight);
					Rectangle b = ((Primitive)element).getBounds();
					aGraphics.fill(b);
					aGraphics.draw(b);
					aGraphics.setColor(c);
				}

				((Primitive)element).render(aGraphics);
			}
		}

		for (Layer layer : mLayers)
		{
			layer.render(aGraphics);
		}

		aGraphics.translate(-mTranslate.x, -mTranslate.y);
	}


	public Rectangle getBounds()
	{
		Rectangle bounds = null;

		for (Element element : mElements)
		{
			if (element instanceof Primitive)
			{
				Primitive p = (Primitive)element;
				if (bounds == null)
				{
					bounds = p.getBounds();
				}
				else
				{
					bounds.add(p.getBounds());
				}
			}
		}

		for (Layer layer : mLayers)
		{
			if (bounds == null)
			{
				bounds = layer.getBounds();
			}
			else
			{
				bounds.add(layer.getBounds());
			}
		}

		return bounds;
	}


	public void setFont(Font aFont)
	{
		add(aFont);
	}


	public Font getFont()
	{
		for (int i = mElements.size(); --i >= 0;)
		{
			if (mElements.get(i) instanceof FontSetting)
			{
				return ((FontSetting)mElements.get(i)).getFont();
			}
		}
		return new Font("arial", Font.PLAIN, 10);
	}


	public void setColor(Color aColor)
	{
		add(aColor);
	}


	public void setBackground(Color aColor)
	{
		add(new BackgroundSetting(aColor));
	}


	public void setStroke(Stroke aStroke)
	{
		add(aStroke);
	}


	public Stroke getStroke()
	{
		for (int i = mElements.size(); --i >= 0;)
		{
			if (mElements.get(i) instanceof StrokeSetting)
			{
				return ((StrokeSetting)mElements.get(i)).getStroke();
			}
		}
		return new BasicStroke();
	}


	public void setPaint(Paint aPaint)
	{
		add(aPaint);
	}


	public void setRenderingHint(RenderingHints.Key key, Object value)
	{
		add(new RenderingHint(key, value));
	}


	public void drawImage(BufferedImage aImage, int x, int y, Object a)
	{
		add(new DrawImage(aImage, x, y));
	}


	public void drawImage(BufferedImage aImage, int x, int y, int w, int h, Object a)
	{
		add(new DrawImage(aImage, x, y, w, h));
	}


	public void drawArc(int x, int y, int w, int h, int startAngel, int endAngel)
	{
		add(new DrawArc(x, y, w, h, startAngel, endAngel));
	}


	public void drawLine(int ... aCoords)
	{
		add(new DrawLine(aCoords));
	}


	public void drawLine(Point ... aCoords)
	{
		add(new DrawLine(aCoords));
	}


	public void drawRoundRect(int x, int y, int w, int h, int r0, int r1)
	{
		add(new DrawRoundRect(x, y, w, h, r0, r1));
	}


	public void fillRoundRect(int x, int y, int w, int h, int r0, int r1)
	{
		add(new FillRoundRect(x, y, w, h, r0, r1));
	}


	public void drawPolygon(int ... aCoords)
	{
		add(new DrawPolygon(aCoords));
	}


	public void fillPolygon(int ... aCoords)
	{
		add(new FillPolygon(aCoords));
	}


	public void drawPolygon(int [] x, int [] y, int n)
	{
		add(new DrawPolygon(x, y, n));
	}


	public void fillPolygon(int [] x, int [] y, int n)
	{
		add(new FillPolygon(x, y, n));
	}


	public void drawRect(Rectangle aRectangle)
	{
		drawRect(aRectangle.x, aRectangle.y, aRectangle.width, aRectangle.height);
	}


	public void drawRect(int x, int y, int w, int h)
	{
		add(new DrawRect(x, y, w, h));
	}


	public void fillRect(Rectangle aRectangle)
	{
		fillRect(aRectangle.x, aRectangle.y, aRectangle.width, aRectangle.height);
	}


	public void fillRect(int x, int y, int w, int h)
	{
		add(new FillRect(x, y, w, h));
	}


	public void drawString(String aString, Rectangle aRectangle, Anchor aAnchor, boolean aMultiline)
	{
		drawString(aString, aRectangle.x, aRectangle.y, aRectangle.width, aRectangle.height, aAnchor, aMultiline);
	}


	public void drawString(String aString, int x, int y, int w, int h, Anchor aAnchor, boolean aMultiline)
	{
		add(new DrawString(aString, x, y, w, h, aAnchor, aMultiline));
	}


	public Rectangle getTextBounds(String aString, int aRectX, int aRectY, int aRectWidth, int aRectHeight, Anchor aAnchor, boolean aMultiline, boolean aLimitLines)
	{
		Font font = getFont();

		ArrayList<String> list;

		if (aMultiline)
		{
			list = Utilities.lineBreakText(aString, font, aRectWidth);
		}
		else
		{
			list = new ArrayList<>();
			list.add(Utilities.clipString(aString, font, aRectWidth));
		}

		LineMetrics lm = font.getLineMetrics("Adgj", FRC);
		int lineHeight = (int)lm.getHeight();

		Rectangle bounds = null;

		int lineCount = list.size();

		if (aLimitLines)
		{
			lineCount = Math.min(lineCount, aRectHeight / lineHeight);
		}

		if (aAnchor == Anchor.SOUTH_EAST || aAnchor == Anchor.SOUTH || aAnchor == Anchor.SOUTH_WEST)
		{
			aRectY += Math.max(0, aRectHeight-lineCount*lineHeight);
		}
		else if (aAnchor == Anchor.CENTER || aAnchor == Anchor.WEST || aAnchor == Anchor.EAST)
		{
			aRectY += Math.max(0, (aRectHeight-lineCount*lineHeight)/2);
		}

		for (int i = 0; i < lineCount; i++)
		{
			String str = list.get(i);

			int x = aRectX;
			int w = Utilities.getStringLength(str, font);

			if (aAnchor == Anchor.NORTH || aAnchor == Anchor.CENTER || aAnchor == Anchor.SOUTH)
			{
				x += (aRectWidth-w)/2;
			}
			else if (aAnchor == Anchor.NORTH_EAST || aAnchor == Anchor.EAST || aAnchor == Anchor.SOUTH_EAST)
			{
				x += aRectWidth-w;
			}

			int y = aRectY+i*lineHeight;

			if (bounds == null)
			{
				bounds = new Rectangle(x, y, w, lineHeight);
			}
			else
			{
				bounds.add(x, y);
				bounds.add(x+w, y+lineHeight);
			}
		}

//		if (aAnchor == Anchor.SOUTH_EAST || aAnchor == Anchor.SOUTH || aAnchor == Anchor.SOUTH_WEST)
//		{
//			bounds.y -= bounds.height;
//		}
//		else if (aAnchor == Anchor.CENTER || aAnchor == Anchor.WEST || aAnchor == Anchor.EAST)
//		{
//			bounds.y -= bounds.height/2;
//		}

		return bounds;
	}


	public Region getRegionAt(RegionSelector aSelector, Point aPoint)
	{
		try
		{
			aPoint.x -= mTranslate.x;
			aPoint.y -= mTranslate.y;

			for (Layer layer : mLayers)
			{
				Region region = layer.getRegionAt(aSelector, aPoint);
				if (region != null)
				{
					return region;
				}
			}

			for (Element element : mElements)
			{
				if (element instanceof Region)
				{
					Region region = (Region)element;
					if (region.getBounds().contains(aPoint) && aSelector.match(region))
					{
						return region;
					}
				}
			}

			return null;
		}
		finally
		{
			aPoint.x += mTranslate.x;
			aPoint.y += mTranslate.y;
		}
	}
}