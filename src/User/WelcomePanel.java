package User;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PointerInfo;
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

public class WelcomePanel extends JPanel
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
				while(!WelcomeFrame.selectHomeMenuItem.equals(parentFrame.getSelectedMenuItem()))
				{
					watchBtnHover = false;
					uploadBtnHover = false;
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
					//System.out.println(e.getX() + " " + e.getY());
					if(e.getY() >= welcomeImageHeight + welcomeImageVerticalOffset)
					{
						if(e.getX() < (WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2)
						{
							watchBtnHover = true;
							uploadBtnHover = false;
						}
						else
						{
							watchBtnHover = false;
							uploadBtnHover = true;
						}	
					}
					else
					{
						watchBtnHover = false;
						uploadBtnHover = false;
					}
					parentComp.repaint();
				}
				catch(Exception e2) 
				{
					if(WelcomeFrame.selectHomeMenuItem.equals(parentFrame.getSelectedMenuItem()))
					{
						watchBtnHover = false;
						uploadBtnHover = false;
						parentComp.repaint();
					}
				}
			}
		}
	}	
	
	private WelcomeFrame parentFrame;
	private String user;
	private boolean watchBtnHover = false, uploadBtnHover = false;
	private welcomePanelHoverListener hoverListener;

	public WelcomePanel(String welcomeUser, WelcomeFrame mainFrame)
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
					{ // watch button clicked
						mainFrame.moviesAndRoomsPanel.loadMoviesAndRooms();
						mainFrame.changePage(WelcomeFrame.pageMoviesAndRooms);
					}
					else
					{ // upload button clicked
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
		
		g.setFont(new Font("Lucida Console", Font.BOLD, 22)); // button text font
		try
		{
			img = ImageIO.read(new File("Icons/watchMovieB.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(watchBtnHover)
		{
			g.fillRect(0, welcomeImageHeight + welcomeImageVerticalOffset, 
					(WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2, 
					WelcomeFrame.frameHeight - (welcomeImageHeight + welcomeImageVerticalOffset));
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		else if(!uploadBtnHover) this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		g.drawImage(img, watchButtonHorizontalOffset, welcomeImageHeight + welcomeImageVerticalOffset + watchButtonVerticalOffset, null);

		try
		{
			img = ImageIO.read(new File("Icons/movieUploadB.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(uploadBtnHover)
		{
			g.fillRect((WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2, welcomeImageHeight + welcomeImageVerticalOffset, 
					(WelcomeFrame.frameWidth - WelcomeFrame.menuWidth)/2, 
					WelcomeFrame.frameHeight - (welcomeImageHeight + welcomeImageVerticalOffset));
			this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		else if(!watchBtnHover) this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		g.drawImage(img, 
				watchButtonHorizontalOffset + ButtonsWidth
				+ (WelcomeFrame.frameWidth - WelcomeFrame.menuWidth - (watchButtonHorizontalOffset + ButtonsWidth))/2 - ButtonsWidth/2
				, welcomeImageHeight + welcomeImageVerticalOffset + uploadButtonVerticalOffset, null);

		g.setColor(buttonColorText);
		g.drawString("GLEDAJTE FILM", watchButtonHorizontalOffset,
				welcomeImageHeight + welcomeImageVerticalOffset + watchButtonVerticalOffset + ButtonsHeight 
																								+ buttonTextVerticalOffset);
		
		g.drawString("OTPREMITE FILM", watchButtonHorizontalOffset + ButtonsWidth
				+ (WelcomeFrame.frameWidth - WelcomeFrame.menuWidth - (watchButtonHorizontalOffset + ButtonsWidth))/2 - ButtonsWidth/2
				, welcomeImageHeight + welcomeImageVerticalOffset + watchButtonVerticalOffset + ButtonsHeight 
																								+ buttonTextVerticalOffset);
	}
}

























/*JLabel welcomeLabel = new JLabel("<html>Dobro došli,<br/>" + welcomeUser + "!</html"); // mozda bez novog reda?
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
*/
/*Canvas welcomeCan = new Canvas()
{
	@Override
	public void paint(Graphics g)
	{
		Image img = null;
		try
		{
			img = ImageIO.read(new File("Icons/welcomePageIcon.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println("siso");
		g.drawImage(img, 0, 0, null);
	}
};

welcomeCan.repaint();

JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 0, 0));
Font btnFont = new Font("Lucida Console", Font.BOLD, 22); // mozda i Lucida Sans Unicode
JLabel watchMovie = new JLabel();
watchMovie.setText("GLEDAJTE FILM");
watchMovie.setVerticalTextPosition(SwingConstants.BOTTOM);
watchMovie.setHorizontalTextPosition(SwingConstants.CENTER);
watchMovie.setBackground(Color.white);
watchMovie.setPreferredSize(new Dimension(250, 300));
watchMovie.setOpaque(true);
watchMovie.setHorizontalAlignment(SwingConstants.CENTER);
watchMovie.setCursor(new Cursor(Cursor.HAND_CURSOR));
watchMovie.setFont(btnFont);
watchMovie.setIconTextGap(15);
watchMovie.addMouseListener(new MouseListener()
{
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		watchMovie.setBackground(Color.WHITE);
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		watchMovie.setBackground(new Color(0xffff4a));
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(SwingUtilities.isLeftMouseButton(e))
		{
			mainFrame.changePage(WelcomeFrame.pageMovieWatch);
		}
	}
});

try
{
	watchMovie.setIcon(new ImageIcon(ImageIO.read(new File("Icons/watchMovieB.png"))));
} catch (IOException e)
{
	e.printStackTrace();
}

JLabel uploadMovie = new JLabel();
uploadMovie.setText("OTPREMITE FILM");
uploadMovie.setVerticalTextPosition(SwingConstants.BOTTOM);
uploadMovie.setHorizontalTextPosition(SwingConstants.CENTER);
uploadMovie.setBackground(Color.white);
uploadMovie.setPreferredSize(new Dimension(250, 300));
uploadMovie.setOpaque(true);
uploadMovie.setHorizontalAlignment(SwingConstants.CENTER);
uploadMovie.setCursor(new Cursor(Cursor.HAND_CURSOR));
uploadMovie.setFont(btnFont);
uploadMovie.setIconTextGap(15);
uploadMovie.addMouseListener(new MouseListener()
{
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		uploadMovie.setBackground(Color.WHITE);
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		uploadMovie.setBackground(new Color(0xffff4a));
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(SwingUtilities.isLeftMouseButton(e))
		{
			mainFrame.changePage(WelcomeFrame.pageMovieUpload);
		}
	}
});
try
{
	uploadMovie.setIcon(new ImageIcon(ImageIO.read(new File("Icons/movieUploadB.png"))));
} catch (IOException e)
{
	e.printStackTrace();
}

buttonsPanel.add(watchMovie);
buttonsPanel.add(uploadMovie);

this.setLayout(new GridBagLayout());
GridBagConstraints c = new GridBagConstraints();
c.fill = GridBagConstraints.HORIZONTAL;
c.weightx = 0.0;
c.gridwidth = 3;
c.gridx = 0;
c.gridy = 0;
this.add(welcomeCan, c);
c.fill = GridBagConstraints.HORIZONTAL;
c.weightx = 0.0;
c.ipady = 5;
c.anchor = GridBagConstraints.PAGE_END;
c.gridx = 0;
c.gridy = 1;
this.add(buttonsPanel, c);*/
//	this.add(welcomeCan);
