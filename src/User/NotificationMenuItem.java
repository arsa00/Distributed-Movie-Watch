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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class NotificationMenuItem extends /*Canvas*/ JPanel
{
	private final int width = 30, height = 30;
	private boolean newNotification = false;
	
	public NotificationMenuItem()
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
			img = ImageIO.read(new File("Icons/notificationIcon.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		g2D.drawImage(img, this.getWidth()/2 - width/2, this.getHeight()/2 - height/2, width, height, null);
		boolean drawRect = getNotificationSatus();
		if(drawRect == true)
		{
			g2D.setColor(Color.RED);
			g2D.fillRoundRect(this.getWidth()/2 + width/2 - 4, this.getHeight()/2 - height/2 - 5, 10, 10, 10, 10);
		}
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
			img = ImageIO.read(new File("Icons/notificationIcon.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		g2D.drawImage(img, this.getWidth()/2 - width/2, this.getHeight()/2 - height/2, width, height, null);
		boolean drawRect = getNotificationSatus();
		if(drawRect == true)
		{
			g2D.setColor(Color.RED);
			g2D.fillRoundRect(this.getWidth()/2 + width/2 - 4, this.getHeight()/2 - height/2 - 5, 10, 10, 10, 10);
		}
	}
	
	public synchronized boolean getNotificationSatus()
	{
		return newNotification;
	}
	
	public synchronized void setNotificationSatus()
	{
		this.newNotification = true;
		this.repaint();
	}
	
	public synchronized void readNotifications()
	{
		this.newNotification = false;
		this.repaint();
	} 
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		NotificationMenuItem notification = new NotificationMenuItem();
		frame.setSize(new Dimension(500, 500));
		frame.add(notification);
		
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				for(int i = 0; i < 10; i++)
				{
					notification.setNotificationSatus();
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					notification.readNotifications();
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
}
