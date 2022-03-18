package User;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class ConnectingToServerPanel extends JPanel
{
	static class ConnectingToServerFrame extends JFrame
	{
		public ConnectingToServerFrame()
		{
			this.setResizable(false);
			this.setBounds(35, 35, 500, 500);
			this.setTitle("Konektovanje");
			BufferedImage img = null;
			try
			{
				img = ImageIO.read(new File(WelcomeFrame.iconPath));
			} catch (IOException e1)
			{
			}
			this.setIconImage(img);
			
			this.addWindowListener(new WindowAdapter()
			{
				@Override
				public void windowClosing(WindowEvent e)
				{
					UserMain.closeUserProgram();
					dispose();
				}
			});
			
			ConnectingToServerPanel panel = new ConnectingToServerPanel();
			this.add(panel, BorderLayout.CENTER);
			this.setVisible(true);
		}
	}
	
	public ConnectingToServerPanel()
	{
		this.setLayout(new FlowLayout());
		JLabel conn = new JLabel();
		ImageIcon gif = null;
		gif = new ImageIcon("Icons/connecting.gif");
		conn.setIcon(gif);
		this.add(conn);
	}
	
	public static void main(String[] args)
	{
		ConnectingToServerFrame frame = new ConnectingToServerFrame();
		ConnectingToServerPanel f = new ConnectingToServerPanel();
		JOptionPane.showMessageDialog(frame, "Warning: ne moze", "UPOZORENJE", JOptionPane.WARNING_MESSAGE);
		frame.dispose(); // dispose doesn't call widnow closing!
	}
}
