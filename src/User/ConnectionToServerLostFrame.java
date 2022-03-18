package User;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import Errors.ErrorFatalConnectionLost;
import User.ConnectingToServerPanel.ConnectingToServerFrame;

public class ConnectionToServerLostFrame extends JFrame
{
	static String logInFrame = "LogF";
	static String welcomeFrame = "MainF";

	private ConnectingToServerFrame connecting;
	
	private void redirect(String frame, String page)
	{
		if(logInFrame.equals(frame))
		{
			UserMain.logInFrame.changePage(page);
			UserMain.logInFrame.setVisible(true);
			this.dispose();
			if(connecting != null) connecting.dispose();
		}
		else if(welcomeFrame.equals(frame))
		{
			UserMain.mainFrame.changePage(page);
			UserMain.mainFrame.setVisible(true);
			this.dispose();
			if(connecting != null) connecting.dispose();
		}
	}
	
	private void reconnectionFailed()
	{
		this.setVisible(true);
		connecting.dispose();
	}
	
	private class PageRedirectorThread extends Thread
	{
		public PageRedirectorThread()
		{
			super("PageRedirector Thread");
		}
		
		@Override
		public void run()
		{
			try
			{
				Socket conn = UserMain.connectionService.reconnectToMainServer();
				if(conn != null)
				{
					conn.close();
					redirect(previousFrame, previousPage);
				}
				else
					throw new ErrorFatalConnectionLost();
			} catch (ErrorFatalConnectionLost | IOException e1)
			{
				e1.printStackTrace();
				reconnectionFailed();
			}
		}
	}
	
	private String previousFrame, previousPage;
	
	public ConnectionToServerLostFrame(String frameRedirectedFrom, String pageRedirectedFrom)
	{
		this.previousFrame = frameRedirectedFrom;
		this.previousPage = pageRedirectedFrom;
		JPanel mainPanel = new JPanel();
		JLabel conn = new JLabel();
		ImageIcon img = null;
		img = new ImageIcon("Icons/deadServer.png");
		conn.setIcon(img);
		mainPanel.add(conn);
		JButton tryAgainBtn = new JButton("Pokušaj ponovo");
		tryAgainBtn.setPreferredSize(new Dimension(200, 50));
		tryAgainBtn.setBackground(new Color(0xffd65c));
		tryAgainBtn.setForeground(Color.BLACK);
		tryAgainBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		tryAgainBtn.setFont(new Font("Lucida Console", Font.BOLD, 18));
		tryAgainBtn.setBorder(new LineBorder(new Color(0x1a3649), 3));
		tryAgainBtn.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseExited(MouseEvent e)
			{
				tryAgainBtn.setBackground(new Color(0xffd65c));
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
				tryAgainBtn.setBackground(new Color(0xfd8087));
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				setVisible(false);
				connecting = new ConnectingToServerFrame();
				PageRedirectorThread redirector = new PageRedirectorThread();
				redirector.start();
			}
		});

		mainPanel.add(tryAgainBtn);
		
		this.setResizable(false);
		this.setBounds(35, 35, 500, 500);
		this.setTitle("Konekcija izgubljena");
		BufferedImage iconImg = null;
		try
		{
			iconImg = ImageIO.read(new File(WelcomeFrame.iconPath));
		} catch (IOException e1)
		{
		}
		this.setIconImage(iconImg);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				UserMain.closeUserProgram();
				dispose();
			}
		});
		
		this.add(mainPanel, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	public static void main(String[] args)
	{
		ConnectionToServerLostFrame frame = new ConnectionToServerLostFrame("a", "b");
	}
}
