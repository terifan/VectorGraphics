package test;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
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
			BufferedImage image = ImageIO.read(Test.class.getResource("image.jpg"));

			Canvas canvas = new Canvas();
			canvas.setAnchor(Anchor.NORTH_WEST);

			for (int y = 0; y < 6; y++)
			{
				for (int x = 0; x < 6; x++)
				{
					Layer layer = canvas.createLayer(10 + (256+5) * x, 10 + (170+5) * y, 256, 170);
					layer.setFont(new Font("arial", Font.PLAIN, 9));

					Layer imageLayer = layer.createLayer(0, 0, 256, 170);
					Layer textLayer = layer.createLayer(0, 150, 256, 20);
					Layer borderLayer = layer.createLayer(0, 0, 256, 170);

					borderLayer.setColor(new Color(0,0,0));
					borderLayer.drawRect(0, 0, 256, 170);
					borderLayer.onMouseEnter(e->{textLayer.setFont(new Font("arial",Font.PLAIN,15)); canvas.repaint();});
					borderLayer.onMouseExit(e->{textLayer.setFont(null); canvas.repaint();});

					imageLayer.drawImage(image, 0, 0, 256, 170);

					textLayer.setColor(new Color(220,220,220,128));
					textLayer.fillRect(0, 0, 256, 20);
					textLayer.setColor(new Color(50,50,50));
					textLayer.setBackground(null);
					textLayer.drawString("Tiger goes grrrr", Anchor.CENTER);
				}
			}

			JFrame frame = new JFrame();
			frame.add(canvas.getComponent());
			frame.setSize(1650, 1150);
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
