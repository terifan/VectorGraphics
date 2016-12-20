package org.terifan.vectorgraphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.JComponent;


public class Canvas extends JComponent
{
	protected HashMap<String,Layer> mMap;
	protected ArrayList<Layer> mLayers;
	protected Anchor mAnchor;
	protected ArrayList<RenderStateListener> mRenderStateListeners;
	protected Point mTranslate;
	protected Rectangle mBounds;
	protected Dimension mMinimumSize;
	protected ReentrantReadWriteLock mLock;


	public Canvas()
	{
		mMap = new HashMap<>();
		mLayers = new ArrayList<>();
		mRenderStateListeners = new ArrayList<>();
		mMinimumSize = new Dimension();
		mLock = new ReentrantReadWriteLock();

		setAnchor(Anchor.CENTER);
	}


	public Layer createLayer()
	{
		Layer layer = new Layer();
		layer.mCanvas = this;
		mLayers.add(layer);
		return layer;
	}


	public Layer createLayer(String aName)
	{
		Layer layer = new Layer();
		layer.mCanvas = this;
		mLayers.add(layer);
		mMap.put(aName, layer);
		return layer;
	}


	public Layer getLayer(String aName)
	{
		return mMap.get(aName);
	}


	public void addRenderStateListener(RenderStateListener aListener)
	{
		mRenderStateListeners.add(aListener);
	}


	public void removeRenderStateListener(RenderStateListener aListener)
	{
		mRenderStateListeners.remove(aListener);
	}


	public void setAnchor(Anchor aConnector)
	{
		mAnchor = aConnector;
	}


	public Anchor getAnchor()
	{
		return mAnchor;
	}


	public Layer getLayer(int aIndex)
	{
		return mLayers.get(aIndex);
	}


	public int getLayerIndex(Layer aLayer)
	{
		return mLayers.indexOf(aLayer);
	}


	public void clear()
	{
		mLayers.clear();
		mMap.clear();
	}


	/**
	 * Will render the graph tree onto this Canvas. This method will aquire
	 * a read lock and if necessary block if any other thread is currently
	 * writing to the graphi tree.<p>
	 *
	 * This method simply calls the <code>render</code> method.
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		render((Graphics2D)g);
	}


	/**
	 * This method will aquire the write lock of the canvas enabling updates
	 * of the graph tree without causing concurrent errors with the renderer
	 * which is locked.
	 */
	public void lock()
	{
		mLock.writeLock().lock();
	}


	/**
	 * This method will release the write lock.
	 */
	public void unlock()
	{
		mLock.writeLock().unlock();
	}


	@Override
	public void setMinimumSize(Dimension aDimension)
	{
		mMinimumSize = new Dimension(aDimension);
		super.setMinimumSize(mMinimumSize);
	}


	public void setMinimumSize(int aWidth, int aHeight)
	{
		mMinimumSize = new Dimension(aWidth, aHeight);
		super.setMinimumSize(mMinimumSize);
	}


	@Override
	public Dimension getPreferredSize()
	{
		computeBounds();
		return mBounds.getSize();
	}


	/**
	 * Will render the graph tree onto this Canvas. This method will aquire
	 * a read lock and if necessary block if any other thread is currently
	 * writing to the graphic tree.
	 */
	public void render(Graphics2D g)
	{
		mLock.readLock().lock();

		try
		{
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			for (RenderStateListener listener : mRenderStateListeners)
			{
				listener.initializeRendering(g);
			}

			if (isOpaque())
			{
				g.setColor(getBackground());
				g.fill(g.getClipBounds());
			}

			computeBounds();

			g.translate(mTranslate.x, mTranslate.y);

			for (RenderStateListener listener : mRenderStateListeners)
			{
				listener.beginRendering(g);
			}

			for (Layer layer : mLayers)
			{
				layer.render(g);
			}

			for (RenderStateListener listener : mRenderStateListeners)
			{
				listener.finishRendering(g);
			}

			g.translate(-mTranslate.x, -mTranslate.y);
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	/**
	 * Render the image using image format BufferedImage.TYPE_INT_ARGB.
	 *
	 * @return
	 *   the rendered image.
	 */
	public BufferedImage render()
	{
		return render(BufferedImage.TYPE_INT_ARGB);
	}


	/**
	 * Render the canvas to an BufferedImage.
	 *
	 * @param aImageFormat
	 *   one of the BufferedImage image format type constants.
	 * @return
	 *   the rendered image
	 */
	public BufferedImage render(int aImageFormat)
	{
		computeBounds();
		BufferedImage image = new BufferedImage(mBounds.width, mBounds.height, aImageFormat);
		Graphics2D g = image.createGraphics();
		render(g);
		g.dispose();
		return image;
	}


	public Region getRegionAt(RegionSelector aSelector, Point aPoint)
	{
		mLock.readLock().lock();

		try
		{
			aPoint = new Point(aPoint);
			aPoint.translate(-mTranslate.x, -mTranslate.y);

			for (Layer layer : mLayers)
			{
				Region region = layer.getRegionAt(aSelector, aPoint);
				if (region != null)
				{
					return region;
				}
			}

			return null;
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	private void computeBounds()
	{
		mLock.readLock().lock();

		try
		{
			mBounds = null;

			for (Layer layer : mLayers)
			{
				Rectangle b = layer.getBounds();
				if (mBounds == null)
				{
					mBounds = b;
				}
				else if (b != null)
				{
					mBounds.add(b);
				}
			}

			if (mBounds == null)
			{
				mBounds = new Rectangle();
			}
			mBounds.width = Math.max(mBounds.width, mMinimumSize.width-mBounds.x);
			mBounds.height = Math.max(mBounds.height, mMinimumSize.height-mBounds.y);

			mTranslate = new Point();

			if (mAnchor == Anchor.CENTER || mAnchor == Anchor.NORTH || mAnchor == Anchor.SOUTH)
			{
				mTranslate.x = getWidth()/2-mBounds.x-mBounds.width/2;
			}
			else if (mAnchor == Anchor.EAST || mAnchor == Anchor.SOUTH_EAST || mAnchor == Anchor.NORTH_EAST)
			{
				mTranslate.x = getWidth()-mBounds.x-mBounds.width-1;
			}
			if (mAnchor == Anchor.CENTER || mAnchor == Anchor.WEST || mAnchor == Anchor.EAST)
			{
				mTranslate.y = getHeight()/2-mBounds.y-mBounds.height/2;
			}
			else if (mAnchor == Anchor.SOUTH || mAnchor == Anchor.SOUTH_EAST || mAnchor == Anchor.SOUTH_WEST)
			{
				mTranslate.y = getHeight()-mBounds.y-mBounds.height-1;
			}
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}
}