package org.terifan.vectorgraphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.JComponent;


public class Canvas
{
	protected JComponent mComponent;
	protected ArrayList<RenderStateListener> mRenderStateListeners;
	protected HashMap<String, Layer> mMap;
	protected ArrayList<Layer> mLayers;
	protected Anchor mAnchor;
	protected Point mPosition;
	protected Rectangle mBounds;
	protected Dimension mMinimumSize;
	protected ReentrantReadWriteLock mLock;
	protected boolean mOpaque;
	protected Color mBackground;
	protected Dimension mDimension;


	public Canvas()
	{
		mMap = new HashMap<>();
		mLayers = new ArrayList<>();
		mRenderStateListeners = new ArrayList<>();
		mMinimumSize = new Dimension();
		mLock = new ReentrantReadWriteLock();
		mDimension = new Dimension();
		mBackground = Color.WHITE;

		setAnchor(Anchor.CENTER);
	}


	public Layer createLayer()
	{
		Layer layer = new Layer();
		layer.mCanvas = this;
		mLayers.add(layer);
		return layer;
	}


	public Layer createLayer(int aX, int aY, int aW, int aH)
	{
		Layer layer = new Layer();
		layer.position(aX, aY);
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


	public Canvas addRenderStateListener(RenderStateListener aListener)
	{
		mRenderStateListeners.add(aListener);
		return this;
	}


	public Canvas removeRenderStateListener(RenderStateListener aListener)
	{
		mRenderStateListeners.remove(aListener);
		return this;
	}


	public Canvas setAnchor(Anchor aConnector)
	{
		mAnchor = aConnector;
		return this;
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


	public Canvas clear()
	{
		mLayers.clear();
		mMap.clear();
		return this;
	}


	public Color getBackground()
	{
		return mBackground;
	}


	public Canvas setBackground(Color aBackground)
	{
		mBackground = aBackground;
		return this;
	}


	public boolean isOpaque()
	{
		return mOpaque;
	}


	public Canvas setOpaque(boolean aOpaque)
	{
		mOpaque = aOpaque;
		return this;
	}


	public Dimension getDimension()
	{
		return mDimension;
	}


	public int getWidth()
	{
		return mDimension.width;
	}


	public int getHeight()
	{
		return mDimension.height;
	}


	/**
	 * This method will aquire the write lock of the canvas enabling updates of the graph tree without causing concurrent errors with the
	 * renderer which is locked.
	 */
	public Canvas lock()
	{
		mLock.writeLock().lock();
		return this;
	}


	/**
	 * This method will release the write lock.
	 */
	public Canvas unlock()
	{
		mLock.writeLock().unlock();
		return this;
	}


	public boolean hasComponent()
	{
		return mComponent != null;
	}


	/**
	 * Return the Swing component
	 */
	public JComponent getComponent()
	{
		if (mComponent == null)
		{
			mComponent = new JComponent()
			{
				@Override
				protected void paintComponent(Graphics g)
				{
					render((Graphics2D)g);
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
			};
			mComponent.addMouseMotionListener(new MouseAdapter()
			{
				@Override
				public void mouseMoved(MouseEvent aEvent)
				{
					ArrayList region = intersect(aEvent.getPoint());
					System.out.println(region);
				}
			});
		}

		return mComponent;
	}


	public ArrayList intersect(Point aPoint)
	{
		ArrayList result = new ArrayList();
		for (Layer layer : mLayers)
		{
			layer.intersect(aPoint, result);
		}
		return result;
	}


	/**
	 * Will render the graph tree onto this Canvas. This method will aquire a read lock and if necessary block if any other thread is
	 * currently writing to the graphic tree.
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

			g.translate(mPosition.x, mPosition.y);

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

			g.translate(-mPosition.x, -mPosition.y);
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	/**
	 * Render the image using image format BufferedImage.TYPE_INT_ARGB.
	 *
	 * @return the rendered image.
	 */
	public BufferedImage render()
	{
		return render(BufferedImage.TYPE_INT_ARGB);
	}


	/**
	 * Render the canvas to an BufferedImage.
	 *
	 * @param aImageFormat one of the BufferedImage image format type constants.
	 * @return the rendered image
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
			aPoint.translate(-mPosition.x, -mPosition.y);

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
			mBounds.width = Math.max(mBounds.width, mMinimumSize.width - mBounds.x);
			mBounds.height = Math.max(mBounds.height, mMinimumSize.height - mBounds.y);

			mPosition = new Point();

			if (mAnchor == Anchor.CENTER || mAnchor == Anchor.NORTH || mAnchor == Anchor.SOUTH)
			{
				mPosition.x = getWidth() / 2 - mBounds.x - mBounds.width / 2;
			}
			else if (mAnchor == Anchor.EAST || mAnchor == Anchor.SOUTH_EAST || mAnchor == Anchor.NORTH_EAST)
			{
				mPosition.x = getWidth() - mBounds.x - mBounds.width - 1;
			}
			if (mAnchor == Anchor.CENTER || mAnchor == Anchor.WEST || mAnchor == Anchor.EAST)
			{
				mPosition.y = getHeight() / 2 - mBounds.y - mBounds.height / 2;
			}
			else if (mAnchor == Anchor.SOUTH || mAnchor == Anchor.SOUTH_EAST || mAnchor == Anchor.SOUTH_WEST)
			{
				mPosition.y = getHeight() - mBounds.y - mBounds.height - 1;
			}
		}
		finally
		{
			mLock.readLock().unlock();
		}
	}


	public void repaint()
	{
		if (mComponent != null)
		{
			mComponent.repaint();
		}
	}
}
