package User;

import java.awt.Desktop.Action;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import User.NotificationPanel.NotificationItem;

public class MovieUploadPanel extends JPanel
{
	
	private File movie = null;
	private WelcomeFrame parentFrame;
	
	private static final int chooseMovieWidth = 250, chooseMovieHeight = 70, verticalOffset = 10;
	private static final int uploadMovieWidth = 200, uploadMovieHeight = 45;
	
	public MovieUploadPanel(WelcomeFrame mainFrame)
	{
		parentFrame = mainFrame;
		this.setLayout(new GridLayout(0 ,1));
		JPanel btn2Panel = new JPanel();
		JPanel moviePanel = new JPanel();
		JFileChooser fileChooser = new JFileChooser();
		
		/*Canvas chooseMovie = new Canvas() {
			@Override
			public void paint(Graphics g)
			{
				Image img = null;
				try
				{
					img = ImageIO.read(new File("Icons/movieBrowseBtn.png"));
				} catch (IOException e)
				{
					e.printStackTrace();
				};
				g.drawImage(img, WelcomeFrame.frameWidth/2 - chooseMovieWidth/2 - WelcomeFrame.menuWidth, verticalOffset, 
						chooseMovieWidth, chooseMovieHeight, null);
			}
		};*/
		
		JPanel chooseMovie = new JPanel()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Image img = null;
				try
				{
					img = ImageIO.read(new File("Icons/movieBrowseBtnNew.png"));
				} catch (IOException e)
				{
					e.printStackTrace();
				};
				g.drawImage(img, WelcomeFrame.frameWidth/2 - chooseMovieWidth/2 - WelcomeFrame.menuWidth, verticalOffset, 
						chooseMovieWidth, chooseMovieHeight, null);
			}
		};
		
		chooseMovie.setPreferredSize(new Dimension(chooseMovieWidth + 1, 100));
		chooseMovie.setCursor(new Cursor(Cursor.HAND_CURSOR));
		chooseMovie.repaint();
		
		JButton uploadMovie = new JButton("OTPREMITE FILM");
		uploadMovie.setFont(new Font("Lucida Console", Font.BOLD, 18));
		uploadMovie.setCursor(new Cursor(Cursor.HAND_CURSOR));
		uploadMovie.setBackground(new Color(0x4eff45));
		uploadMovie.setForeground(Color.BLACK);
		uploadMovie.setPreferredSize(new Dimension(uploadMovieWidth, uploadMovieHeight));
		
		JLabel movieLabel = new JLabel("Movie name...");
		movieLabel.setFont(new Font("Lucida Sans Unicode", Font.BOLD, 15));
		movieLabel.setHorizontalAlignment(SwingConstants.CENTER);
		movieLabel.setVerticalAlignment(SwingConstants.CENTER);
		
		movieLabel.setVisible(false);
		uploadMovie.setVisible(false);
		
		btn2Panel.add(uploadMovie);
		moviePanel.add(movieLabel);
		this.add(chooseMovie);
		this.add(moviePanel);
		this.add(btn2Panel);
		
		chooseMovie.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isLeftMouseButton(e) && 
						e.getY() >= verticalOffset && e.getY() <= verticalOffset + chooseMovieHeight &&
						e.getX() >= WelcomeFrame.frameWidth/2 - chooseMovieWidth/2 - WelcomeFrame.menuWidth && 
						e.getX() <= WelcomeFrame.frameWidth/2 + chooseMovieWidth/2 - WelcomeFrame.menuWidth)
				{
					int status = fileChooser.showOpenDialog(parentFrame);
					if(status == JFileChooser.APPROVE_OPTION)
					{
						movie = fileChooser.getSelectedFile();
						movieLabel.setText(movie.getName());
						movieLabel.setHorizontalTextPosition(SwingConstants.CENTER);
						movieLabel.setVerticalTextPosition(SwingConstants.TOP);
						try
						{
							movieLabel.setIcon(new ImageIcon(ImageIO.read(new File("Icons/movie.png"))));
						} catch (IOException e1)
						{
							e1.printStackTrace();
						}
						movieLabel.setPreferredSize(new Dimension(250, 250));
						
						movieLabel.setVisible(true);
						uploadMovie.setVisible(true);
						
					}
				}
			}
		});
		
		uploadMovie.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isLeftMouseButton(e))
				{
					// ClientUploader(movie).start();
					NotificationItem notification = new NotificationItem();
					notification.addItem(new JLabel("<html>OTPREMANJE:<br/>" + movie.getName() + "</html>"));
					JProgressBar uploadBar = new JProgressBar();
					uploadBar.setPreferredSize(new Dimension(WelcomeFrame.frameWidth, 20));
					notification.addItem(uploadBar);
					mainFrame.addNotification(notification);
					movieLabel.setVisible(false);
					uploadMovie.setVisible(false);
					mainFrame.changePage(WelcomeFrame.selectNotificationMenuItem);
				}
			}
		});
	}
	
}
