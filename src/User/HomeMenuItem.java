package User;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class HomeMenuItem extends /*Canvas*/ JPanel
{
	private final int width = 30, height = 30;
	
	public HomeMenuItem()
	{
		this.setPreferredSize(new Dimension(50, 50));
	}
	
	/*
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2D = (Graphics2D)g;
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File("Icons/homeIcon.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		g2D.drawImage(img, this.getWidth()/2 - width/2, this.getHeight()/2 - height/2, width, height, null);
	}
	*/
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D)g;
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File("Icons/homeIcon.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		g2D.drawImage(img, this.getWidth()/2 - width/2, this.getHeight()/2 - height/2, width, height, null);
	}
	
}
