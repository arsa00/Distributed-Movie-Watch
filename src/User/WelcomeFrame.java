package User;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.Border;

import User.NotificationPanel.NotificationItem;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class WelcomeFrame extends JFrame
{
	public static final String programName = "ETF Bioskop";
	public static final String iconPath = "Icons/programIcon.png";
	public static final String etfIconPath = "Icons/ETF.png";
	
	public static final String selectHomeMenuItem = "HOME";
	public static final String selectNotificationMenuItem = "NOTIFICATION";
	public static final String pageMovieWatch = "MOVIE_WATCH";
	public static final String pageMoviesAndRooms = "MOVIES_ROOMS";
	public static final String pageMovieUpload = "MOVIE_UPLOAD";
	public static final String pageConnectToServer = "CONNECTING_TO_SERVER";
	
	public static final int frameWidth = 565;
	public static final int frameHeight = 693+31;
	public static final int menuWidth = 50;
	
	private static final Color defColorMenu = new Color(0x1a3649);
	private static final Color hoverColorMenu = new Color(0xffff4a);
	private static final Color selectedColorMenu = new Color(0xffffff);
	
	private String userName = "";
	private String activePage = selectHomeMenuItem;
	private HomeMenuItem homeIcon;
	private NotificationMenuItem notificationIcon;
	
	JPanel cardPanel;
	MovieWatchPanel movieWatchPage;
	MovieUploadPanel movieUploadPanel;
	MoviesAndRoomsPanel moviesAndRoomsPanel;
	NotificationPanel notificationPanel;
	WelcomePanel welcomePanel;
	
	public WelcomeFrame(String user)
	{
		userName = user;
		// settings of frame
		BufferedImage iconImage = null;
		try
		{
			iconImage = ImageIO.read(new File(iconPath));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		this.setTitle(programName);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setIconImage(iconImage);
		this.getContentPane().setBackground(Color.WHITE);
		
		// fill frame
		fillFrame();
		
		this.setBounds(25, 25, frameWidth, frameHeight);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private void fillFrame()
	{
		cardPanel = new JPanel(new CardLayout());
		//menu
	    homeIcon = new HomeMenuItem();
		homeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		homeIcon.setBackground(defColorMenu);
		if(selectHomeMenuItem.equals(activePage))
			homeIcon.setBackground(selectedColorMenu);
		else
			homeIcon.setBackground(defColorMenu);
		
		notificationIcon = new NotificationMenuItem();
		notificationIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
		notificationIcon.setBackground(defColorMenu);
		if(selectNotificationMenuItem.equals(activePage))
			notificationIcon.setBackground(selectedColorMenu);
		else
			notificationIcon.setBackground(defColorMenu);
		
		
		homeIcon.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(selectHomeMenuItem.equals(activePage))
					homeIcon.setBackground(selectedColorMenu);
				else
					homeIcon.setBackground(defColorMenu);
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				homeIcon.setBackground(hoverColorMenu);
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isLeftMouseButton(e))
				{
					changePage(WelcomeFrame.selectHomeMenuItem);
				}
			}
		});
		
		notificationIcon.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				if(selectNotificationMenuItem.equals(activePage))
					notificationIcon.setBackground(selectedColorMenu);
				else
					notificationIcon.setBackground(defColorMenu);
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				notificationIcon.setBackground(hoverColorMenu);
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isLeftMouseButton(e))
				{
					changePage(WelcomeFrame.selectNotificationMenuItem);
				}
			}
		});

		homeIcon.repaint();
		notificationIcon.repaint();
		
		JPanel menuPanel = new JPanel(new GridBagLayout());
		menuPanel.setBackground(defColorMenu);
		menuPanel.setPreferredSize(new Dimension(menuWidth, 0));
		GridBagConstraints c1 = new GridBagConstraints();
		c1.anchor = GridBagConstraints.FIRST_LINE_START;
		c1.gridx = 0;
		c1.gridy = 0;
		menuPanel.add(homeIcon, c1);
		c1.anchor = GridBagConstraints.FIRST_LINE_START;
		c1.gridx = 0;
		c1.gridy = 1;
		menuPanel.add(notificationIcon, c1);
		c1.gridy = 2;
		c1.gridx = 0;
		c1.weightx = 1;
		c1.weighty = 1;
		JPanel filler = new JPanel();
		filler.setOpaque(false);
		menuPanel.add(filler, c1);
		
		this.add(menuPanel, BorderLayout.WEST);

		// create home (welcome) page and add it to card layout
		welcomePanel = new WelcomePanel(userName, this);
		cardPanel.add(welcomePanel, WelcomeFrame.selectHomeMenuItem);
				
		// create notifications page and add it to card layout
		notificationPanel = new NotificationPanel(this, notificationIcon);
		cardPanel.add(notificationPanel, WelcomeFrame.selectNotificationMenuItem);
		
		// create watch movie page and add it to card layout
		movieWatchPage = new MovieWatchPanel(userName, this);
		cardPanel.add(movieWatchPage, WelcomeFrame.pageMovieWatch);
		
		// create upload movie page and add it to card layout 
		movieUploadPanel = new MovieUploadPanel(this);
		cardPanel.add(movieUploadPanel, WelcomeFrame.pageMovieUpload);
		
		// create page where are all movies and rooms and add it to card layout 
		moviesAndRoomsPanel = new MoviesAndRoomsPanel(this);
		cardPanel.add(moviesAndRoomsPanel, WelcomeFrame.pageMoviesAndRooms);
		
		// create connecting to server page and add it to card layout 
		ConnectingToServerPanel connectingToServerPanel = new ConnectingToServerPanel();
		cardPanel.add(connectingToServerPanel, WelcomeFrame.pageConnectToServer);
		
		//add card panel to WelcomeFrame (that is MainFrame)
		this.add(cardPanel, BorderLayout.CENTER);
	}
	
	public synchronized String getSelectedMenuItem()
	{
		return activePage;
	}
	
	public synchronized void addNotification(NotificationItem item)
	{
		this.notificationPanel.addNotification(item);
	}
	
	public synchronized void changePage(String newPage)
	{
		if(newPage.equals(WelcomeFrame.selectHomeMenuItem))
		{ // home page
			activePage = newPage;
			homeIcon.setBackground(selectedColorMenu);
			notificationIcon.setBackground(defColorMenu);
			welcomePanel.pageChangedBack();  // wake up hover listener thread
		}
		else if(newPage.equals(WelcomeFrame.selectNotificationMenuItem))
		{ // notification page
			activePage = newPage;
			homeIcon.setBackground(defColorMenu);
			notificationIcon.setBackground(selectedColorMenu);
			notificationIcon.readNotifications();
		}
		else if(newPage.equals(WelcomeFrame.pageMoviesAndRooms))
		{// movies & rooms page
			activePage = newPage;
			homeIcon.setBackground(defColorMenu);
			notificationIcon.setBackground(defColorMenu);
		}
		else if(newPage.equals(WelcomeFrame.pageMovieWatch))
		{ // movie watch page
			activePage = newPage;
			homeIcon.setBackground(defColorMenu);
			notificationIcon.setBackground(defColorMenu);
			movieWatchPage.pageChangedBack();  // wake up hover listener thread
		}
		else if(newPage.equals(WelcomeFrame.pageMovieUpload))
		{ // movie upload page
			activePage = newPage;
			homeIcon.setBackground(defColorMenu);
			notificationIcon.setBackground(defColorMenu);
		}
		else if(newPage.equals(WelcomeFrame.pageConnectToServer))
		{ // server connecting page
			activePage = newPage;
			homeIcon.setBackground(defColorMenu);
			notificationIcon.setBackground(defColorMenu);
		}
		CardLayout cTemp = (CardLayout) cardPanel.getLayout();
		cTemp.show(cardPanel, newPage);
	}
	
	synchronized void fatalConnectionLost()
	{
		this.setVisible(false);
		ConnectionToServerLostFrame connLostFrame = new ConnectionToServerLostFrame(ConnectionToServerLostFrame.welcomeFrame, activePage);
	}
	
	public synchronized void close() 
	{
		moviesAndRoomsPanel.close();
		// TODO: add other close methods to elements (where needed)
		this.dispose();
	}
	
	public static void main(String[] args)
	{
		WelcomeFrame main = new WelcomeFrame("Maja");
	}
}
