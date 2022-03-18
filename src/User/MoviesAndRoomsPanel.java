package User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import Common.ConnectionConstants;

public class MoviesAndRoomsPanel extends JPanel
{
	private static class PopUpMenu extends JPopupMenu
	{
		private JMenuItem seeFullNameItem;
		private boolean isMovie;
		public PopUpMenu(JLabel item, boolean isMovie)
		{
			this.isMovie = isMovie;
			seeFullNameItem = new JMenuItem("Pročitaj celo ime");
			this.add(seeFullNameItem);
			
			JPopupMenu menu = this;
			seeFullNameItem.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					menu.setVisible(false);
					if(isMovie)
						JOptionPane.showMessageDialog(item, item.getText(), "Puno ime filma", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(item, item.getText(), "Puno ime sobe", JOptionPane.INFORMATION_MESSAGE);
				}
			});

		}
	}
	
	
	private JPanel mainPanel;
	private JScrollPane scroll;
	private GridBagConstraints gridC;
	private Dimension itemSize;
	private WelcomeFrame parentFrame;
	
	private int items;
	private static final String filePath = ConnectionConstants.configDir + ConnectionConstants.moviesAndRoomsfileName 
																							+ ConnectionConstants.configFileType; 
	
	public MoviesAndRoomsPanel()
	{
		this(null);
	}
	
	public MoviesAndRoomsPanel(WelcomeFrame mainFrame)
	{
		this.parentFrame = mainFrame;
		try
		{
			new File(filePath).createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		items = 0;
		itemSize = new Dimension(160,  170);
		mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);
		gridC = new GridBagConstraints();
		gridC.weighty = 1;
		gridC.weightx = 1;
		gridC.ipady = 20;
		gridC.anchor = GridBagConstraints.NORTH;
		scroll = new JScrollPane(mainPanel);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(WelcomeFrame.frameWidth - 65, WelcomeFrame.frameHeight - 40));
		
		this.add(scroll);
	}
	
	private void addMovie(String movieName)
	{
		items++;
		ImageIcon movieIcon = new ImageIcon("Icons/movieIcon.png");
		
		JLabel movieItem = new JLabel(movieName);
		movieItem.setPreferredSize(itemSize);
		movieItem.setIcon(movieIcon);
		movieItem.setVerticalTextPosition(SwingConstants.BOTTOM);
		movieItem.setHorizontalTextPosition(SwingConstants.CENTER);
		movieItem.setHorizontalAlignment(SwingConstants.CENTER);
		
		// movieItem position calculation
		if(items % 3 == 1)
		{ // first in next row
			gridC.gridy++;
		}
		
		gridC.gridx = (items + 2) % 3; // position in current row
		
		// movie item design
		movieItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
		movieItem.setOpaque(true);
		movieItem.setBackground(Color.WHITE);
		movieItem.setBorder(new LineBorder(Color.WHITE, 3));
		movieItem.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					PopUpMenu rightClickMenu = new PopUpMenu(movieItem, true);
					rightClickMenu.show(movieItem, e.getX(), e.getY());
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				movieItem.setBackground(Color.WHITE);
				movieItem.setBorder(new LineBorder(Color.WHITE, 3));
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				movieItem.setBackground(new Color(0xffcb6a));
				movieItem.setBorder(new LineBorder(Color.BLACK, 3));
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// TODO: add action when clicked on movie
				if(SwingUtilities.isLeftMouseButton(e))
					System.out.println("Movie clicked: " + movieItem.getText());
			}
		});		
		
		mainPanel.add(movieItem, gridC);
	}
	
	private void addRoom(String roomName)
	{
		items++;
		ImageIcon roomIcon = new ImageIcon("Icons/roomIcon.png");
		
		JLabel roomItem = new JLabel(roomName);
		roomItem.setPreferredSize(itemSize);
		roomItem.setIcon(roomIcon);
		roomItem.setVerticalTextPosition(SwingConstants.BOTTOM);
		roomItem.setHorizontalTextPosition(SwingConstants.CENTER);
		roomItem.setHorizontalAlignment(SwingConstants.CENTER);
		
		// movieItem position calculation
		if(items % 3 == 1)
		{ // first in next row
			gridC.gridy++;
		}
		
		gridC.gridx = (items + 2) % 3; // position in current row
		
		// movie item design
		roomItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
		roomItem.setOpaque(true);
		roomItem.setBackground(Color.WHITE);
		roomItem.setBorder(new LineBorder(Color.WHITE, 3));
		roomItem.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e))
				{
					PopUpMenu rightClickMenu = new PopUpMenu(roomItem, false);
					rightClickMenu.show(roomItem, e.getX(), e.getY());
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
				roomItem.setBackground(Color.WHITE);
				roomItem.setBorder(new LineBorder(Color.WHITE, 3));
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				roomItem.setBackground(new Color(0xff71a1));
				roomItem.setBorder(new LineBorder(Color.BLACK, 3));
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// TODO: add action when clicked on movie
				if(SwingUtilities.isLeftMouseButton(e))
				{
					System.out.println("Room clicked: " + roomItem.getText());
					Player p = new Player(true);
					UserMain.mainFrame.setEnabled(false);
				}
			}
		});		
		
		mainPanel.add(roomItem, gridC);
	}
	
	private void loadMoviesAndRoomsFromFile()
	{
		File file = new File(filePath);
		
		try(BufferedReader fileReader = new BufferedReader(new FileReader(file)))
		{ // file construction: list of movie titles... | delimiter text "ROOMS:" | list of room names...
			String temp;
			boolean stillMovies = true;
			while((temp = fileReader.readLine()) != null)
			{
				if(ConnectionConstants.moviesAndRoomsDelimiterWord.equals(temp))
				{
					stillMovies = false;
					continue;
				}
				
				if(temp.isBlank()) continue;
				
				if(stillMovies) this.addMovie(temp);
				else this.addRoom(temp);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(items == 0)
		{
			mainPanel.add(new JLabel("Nema filmova ni soba za prikazati..."));
		}
	}
	
	void loadMoviesAndRooms()
	{
		// clear all previously loaded movies and rooms
		mainPanel.removeAll();
		items = 0;
		// TODO: ask server for a file
		// load all movies and rooms from a file
		loadMoviesAndRoomsFromFile();
	}
	
	void close()
	{
		new File(filePath).delete();
	}
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(35, 35, WelcomeFrame.frameWidth, WelcomeFrame.frameHeight);
		
		MoviesAndRoomsPanel panel = new MoviesAndRoomsPanel();
		panel.loadMoviesAndRoomsFromFile();
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
}
