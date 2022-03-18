package User;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MovieWatchPanel extends JPanel
{
	private class welcomePanelHoverListener extends Thread
	{
		private JPanel parentComp;
		
		public welcomePanelHoverListener(JPanel parent)
		{
			parentComp = parent;
			this.setDaemon(true);
		}
		
		@Override
		public void run()
		{
			Point e = null;
			while(true)
			{
				while(!WelcomeFrame.pageMovieWatch.equals(parentFrame.getSelectedMenuItem()))
				{
					standaloneWatchHover = false;
					roomWatchHover = false;
					synchronized (parentComp)
					{
						try
						{
							parentComp.wait();
						} catch (InterruptedException e1)
						{
							e1.printStackTrace();
						}
					}
				}

				try {
					e = parentComp.getMousePosition().getLocation();
					if(e.getY() >= welcomeImageHeight + welcomeImageVerticalOffset)
					{
						if(e.getX() < (WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2)
						{
							standaloneWatchHover = true;
							roomWatchHover = false;
						}
						else
						{
							standaloneWatchHover = false;
							roomWatchHover = true;
						}	
					}
					else
					{
						standaloneWatchHover = false;
						roomWatchHover = false;
					}
					parentComp.repaint();
				}
				catch(Exception e2) 
				{
					if(WelcomeFrame.selectHomeMenuItem.equals(parentFrame.getSelectedMenuItem()))
					{
						standaloneWatchHover = false;
						roomWatchHover = false;
						parentComp.repaint();
					}
				}
			}
		}
	}
	
	private WelcomeFrame parentFrame;
	private String user;
	private boolean standaloneWatchHover = false, roomWatchHover = false;
	private welcomePanelHoverListener hoverListener;
	
	public MovieWatchPanel(String welcomeUser, WelcomeFrame mainFrame)
	{
		parentFrame = mainFrame;
		user = welcomeUser;
		hoverListener = new welcomePanelHoverListener(this);
		hoverListener.start();
		
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isLeftMouseButton(e) && e.getY() >= (welcomeImageHeight + welcomeImageVerticalOffset))
				{
					if(e.getX() < (WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2)
					{ // standalone watch button clicked
						mainFrame.changePage(WelcomeFrame.pageConnectToServer);
					}
					else
					{ // room watch
						mainFrame.changePage(WelcomeFrame.pageMovieUpload);
					}	
				}
			}
		});

	}
	
	
	public synchronized void pageChangedBack()
	{
		this.notify(); // wake up hover listener when it's changed back to HOME page
	}
	
	
	// painting position variables
	private static final int welcomeImageVerticalOffset = -50;
	private static final int welcomeImageHorizontalOffset = -11;
	private static final int welcomeImageHeight = 481;
	//private static final int welcomeImageWidth = 500+;
	
	private static final int welcomeTextHorizontalOffset = -140;
	private static final int welcomeTextVerticalOffset = 225;
	private static final int userNameTextHorizontalOffset = -115;
	private static final int userNameTextVerticalOffset = 250;
	
	private static final int watchButtonVerticalOffset = 11;
	private static final int watchButtonHorizontalOffset = 25;
	
	private static final int uploadButtonVerticalOffset = 10;
	private static final int uploadButtonHorizontalOffset = 25; 
	
	private static final int ButtonsHeight = 200;
	private static final int ButtonsWidth = 200;
	private static final int buttonTextVerticalOffset = 28;
	
	private static final Color buttonColorText = new Color(0x1a3649);
	
	// drawing the screen (panel)
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Image img = null;
		try
		{
			img = ImageIO.read(new File("Icons/welcomePageIcon.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		g.drawImage(img, welcomeImageHorizontalOffset, welcomeImageVerticalOffset, 
				WelcomeFrame.frameWidth - WelcomeFrame.menuWidth - welcomeImageHorizontalOffset, welcomeImageHeight, null);
		g.setColor(Color.YELLOW);
		g.setFont(new Font("Lucida Console", Font.BOLD, 18)); // welcome text font
		g.drawString("Dobro došli, ", 
				(WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2 + welcomeTextHorizontalOffset, welcomeTextVerticalOffset);
		g.drawString(user, 
				(WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2 + userNameTextHorizontalOffset, userNameTextVerticalOffset);
		
		g.setFont(new Font("Lucida Console", Font.BOLD, 18)); // button text font
		try
		{
			img = ImageIO.read(new File("Icons/standaloneWatch.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(standaloneWatchHover)
		{
			g.fillRect(0, welcomeImageHeight + welcomeImageVerticalOffset, 
					(WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2, 
					WelcomeFrame.frameHeight - (welcomeImageHeight + welcomeImageVerticalOffset));
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		else if(!roomWatchHover) this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		g.drawImage(img, watchButtonHorizontalOffset, welcomeImageHeight + welcomeImageVerticalOffset + watchButtonVerticalOffset, null);

		try
		{
			img = ImageIO.read(new File("Icons/roomWatch.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(roomWatchHover)
		{
			g.fillRect((WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2, welcomeImageHeight + welcomeImageVerticalOffset, 
					(WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2, 
					WelcomeFrame.frameHeight - (welcomeImageHeight + welcomeImageVerticalOffset));
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		else if(!standaloneWatchHover) this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		g.drawImage(img, 
				watchButtonHorizontalOffset + ButtonsWidth
				+ (WelcomeFrame.frameWidth - WelcomeFrame.menuWidth - (watchButtonHorizontalOffset + ButtonsWidth))/2 - ButtonsWidth/2
				, welcomeImageHeight + welcomeImageVerticalOffset + uploadButtonVerticalOffset, null);

		g.setColor(buttonColorText);
		g.drawString("SAMOSTALNO GLEDANJE", watchButtonHorizontalOffset,
				welcomeImageHeight + welcomeImageVerticalOffset + watchButtonVerticalOffset + ButtonsHeight 
																								+ buttonTextVerticalOffset);
		
		g.drawString("PRIDRUŽITE SE SOBI", watchButtonHorizontalOffset + ButtonsWidth
				+ (WelcomeFrame.frameWidth - WelcomeFrame.menuWidth - (watchButtonHorizontalOffset + ButtonsWidth))/2 - ButtonsWidth/2
				, welcomeImageHeight + welcomeImageVerticalOffset + watchButtonVerticalOffset + ButtonsHeight 
																								+ buttonTextVerticalOffset);
	}
}
















/*
 * /*JLabel welcomeLabel = new JLabel("<html>Dobro došli,<br/>" + welcomeUser + "!</html"); // mozda bez novog reda?
		try
		{
			welcomeLabel.setIcon(new ImageIcon(ImageIO.read(new File("Icons/welcomePageIcon.png"))));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		welcomeLabel.setVerticalTextPosition(SwingConstants.CENTER);
		welcomeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeLabel.setForeground(Color.YELLOW);
		welcomeLabel.setPreferredSize(new Dimension(500, 350));
		welcomeLabel.setFont(new Font("Lucida Sans Unicode", Font.BOLD, 18));
		
		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 0, 0));
		
		Font btnFont = new Font("Lucida Console", Font.BOLD, 18); // mozda i Lucida Sans Unicode
		JLabel standaloneWatch = new JLabel();
		standaloneWatch.setText("SAMOSTALNO GLEDANJE");
		standaloneWatch.setVerticalTextPosition(SwingConstants.BOTTOM);
		standaloneWatch.setHorizontalTextPosition(SwingConstants.CENTER);
		standaloneWatch.setBackground(Color.white);
		standaloneWatch.setPreferredSize(new Dimension(250, 300));
		standaloneWatch.setOpaque(true);
		standaloneWatch.setHorizontalAlignment(SwingConstants.CENTER);
		standaloneWatch.setCursor(new Cursor(Cursor.HAND_CURSOR));
		standaloneWatch.setFont(btnFont);
		standaloneWatch.setIconTextGap(15);
		standaloneWatch.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				standaloneWatch.setBackground(Color.WHITE);
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				standaloneWatch.setBackground(new Color(0xffff4a));
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		try
		{
			standaloneWatch.setIcon(new ImageIcon(ImageIO.read(new File("Icons/standaloneWatch.png"))));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		JLabel roomWatch = new JLabel();
		roomWatch.setText("PRIDRUŽITE SE SOBI");
		roomWatch.setVerticalTextPosition(SwingConstants.BOTTOM);
		roomWatch.setHorizontalTextPosition(SwingConstants.CENTER);
		roomWatch.setBackground(Color.white);
		roomWatch.setPreferredSize(new Dimension(250, 300));
		roomWatch.setOpaque(true);
		roomWatch.setHorizontalAlignment(SwingConstants.CENTER);
		roomWatch.setVerticalAlignment(SwingConstants.CENTER);
		roomWatch.setCursor(new Cursor(Cursor.HAND_CURSOR));
		roomWatch.setFont(btnFont);
		roomWatch.setIconTextGap(15);
		roomWatch.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseReleased(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				roomWatch.setBackground(Color.WHITE);
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				roomWatch.setBackground(new Color(0xffff4a));
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		try
		{
			roomWatch.setIcon(new ImageIcon(ImageIO.read(new File("Icons/roomWatch.png"))));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		buttonsPanel.add(standaloneWatch);
		buttonsPanel.add(roomWatch);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.gridwidth = 3;
		c.gridx = 0;
		c.gridy = 0;
		this.add(welcomeLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.ipady = 5;
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridx = 0;
		c.gridy = 1;
		this.add(buttonsPanel, c);*/