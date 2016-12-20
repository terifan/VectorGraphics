package test;

import javax.swing.JFrame;
import org.terifan.vectorgraphics.Anchor;
import org.terifan.vectorgraphics.Canvas;
import org.terifan.vectorgraphics.Layer;


public class Test
{
	public static void main(String ... args)
	{
		try
		{
			Canvas canvas = new Canvas();
			canvas.setAnchor(Anchor.NORTH_WEST);

			Layer layer = canvas.createLayer();
			layer.drawRect(10, 10, 100, 100);
			layer.drawString("hello world", 10, 10, 100, 100, Anchor.CENTER, true);

			JFrame frame = new JFrame();
			frame.add(canvas);
			frame.setSize(1024, 768);
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
		catch (Throwable e)
		{
			e.printStackTrace(System.out);
		}
	}
}
